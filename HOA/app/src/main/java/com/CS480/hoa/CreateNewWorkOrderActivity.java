package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewWorkOrderActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.createNewWorkOrder";
    public static final int requestImageCapture = 1;
    public static final int MAXPHOTOS = 5;

    private int counter = 0;

    private String currentPhotoPath;

    private WorkOrder workOrder;
    private User user;
    private Bitmap[] attachedPhotos;
    private String[] attachedPhotosPaths;
    private String[] photoUrlList;

    private EditText descriptionInput;
    private Button saveButton;
    private Button cancelButton;
    private Button attachPhotoButton;
    private ProgressBar loadingSpinner;

    private AsyncTask<Void, Void, String> sendPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_work_order);

        //maximum of 5 photos for each work order
        attachedPhotos = new Bitmap[MAXPHOTOS];
        attachedPhotosPaths = new String[MAXPHOTOS];
        photoUrlList = new String[MAXPHOTOS];

        //retrieve the user from previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        //assign objects to variables
        descriptionInput = findViewById(R.id.workOrderCreateDescriptionInput);
        saveButton = findViewById(R.id.workOrderCreateSaveButton);
        cancelButton = findViewById(R.id.workOrderCreateCancelButton);
        attachPhotoButton = findViewById(R.id.workOrderCreateAttachPhotoButton);
        loadingSpinner = findViewById(R.id.workOrderCreateProgressBar);

        loadingSpinner.setVisibility(View.GONE);

        //save button onclick listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make sure input fields have data
                if(validateForm()){

                    //create a WorkOrder object based on user input
                    workOrder = createWorkOrder();

                    loadingSpinner.setVisibility(View.VISIBLE);

                    sendPhotos();

                }else{
                    //the description was empty
                    Toast.makeText(getBaseContext(), "There must be a description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //cancel button onclick listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //attach photo button onclick listener
        attachPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for max photo limit
                if(attachedPhotosPaths[MAXPHOTOS - 1] == null) {

                    PackageManager pm = getBaseContext().getPackageManager();

                    //next check to ensure a camera is available
                    //If there is no camera then this onClick will do nothing
                    if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                        //activate the camera and retrieve the photo
                        startCamera();

                    } else {
                        //There is no camera app available
                        Toast.makeText(getBaseContext(),"There is a problem with accessing the camera", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //the photo max has been reached
                    Toast.makeText(getBaseContext(), "The maximum photos for this work order has been reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//end of onCreate





    //The only required field is the description.
    //This ensures description is not empty
    private boolean validateForm(){

        if(descriptionInput.getText().toString().isEmpty()){
            return false;
        }

        return true;
    }





    //This method creates a new work order object
    private WorkOrder createWorkOrder(){

        String description = descriptionInput.getText().toString();
        Date rightNow = new Date();

        return new WorkOrder(user, description, rightNow, photoUrlList);
    }





    //This sends the work order data to the web service
    private void sendData(){

        //update url to access web service
        Database.changeBaseURL("https://gtcyxyfpt5.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create JsonObject to send data to web service
        JsonObject json = new JsonObject();

        json.addProperty("description", workOrder.getDescription());
        json.addProperty("creator", workOrder.getCreator().getUserEmail());
        json.addProperty("admin", workOrder.getEditor().getUserEmail());
        json.addProperty("submissionDate", workOrder.getSubmissionDate());
        json.addProperty("lastActivityDate", workOrder.getLastActivityDate());
        json.addProperty("currentStatus", workOrder.getCurrentStatus());

        JsonArray commentList = new JsonArray();
        commentList.add("No Comments");

        json.add("comments", commentList);

        //there are photos to send to web service
        JsonArray photoURLs = new JsonArray();

        if(photoUrlList.length == 0){
            photoURLs.add("empty");
        }else {

            for (int i = 0; i < photoUrlList.length; i++) {

                if (photoUrlList[i] != null) {

                    photoURLs.add(photoUrlList[i]);

                }
            }
        }

        json.add("attachedPhotos", photoURLs);


        //create Call object to receive response from web service
        Call<JsonArray> call = retrofit.newWorkOrder(json);

        //background thread
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                cancelButton.setEnabled(true);
                saveButton.setEnabled(true);
                attachPhotoButton.setEnabled(true);

                loadingSpinner.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    //retrieve response body
                    JsonArray jsonArray = response.body();

                    //convert JsonArray into JsonObject
                    JsonObject jsonObject = (JsonObject) jsonArray.get(0);

                    //Extract data from JsonObject
                    String message = jsonObject.get("message").toString();
                    message = message.replace("\"", "");

                    String status = jsonObject.get("status").toString();
                    status = status.replace("\"", "");

                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();


                    counter = 0;

                    //return to the users main activity
                    Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                    intent.putExtra(HomeOwnerMainActivity.userCode, user);

                    startActivity(intent);


                }else{
                    //the response was not successful

                    Toast.makeText(getBaseContext(),"The response failed", Toast.LENGTH_SHORT).show();

                    System.out.println("Response failed sendData************************");
                    System.out.println(response.code());
                    System.out.println(response.message());

                    counter = 0;

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                cancelButton.setEnabled(true);
                saveButton.setEnabled(true);
                attachPhotoButton.setEnabled(true);

                loadingSpinner.setVisibility(View.GONE);

                System.out.println("Failure Create New Work Order sendData*************************");
                System.out.println(t.getMessage());

                counter = 0;
            }
        });
    }





    //this method sends the photos to web service and builds a list of urls for
    //retrieving the photos
    private void sendPhotos(){

        if(attachedPhotos[0] != null) {

            //there are photos to send
            for(int i = 0; i < attachedPhotos.length; i++) {

                if(attachedPhotos[i] == null){
                    break;
                }

                //test to see if this is the final image
                boolean isLast = (i == attachedPhotos.length - 1 || attachedPhotos[i + 1] == null);

                sendPhoto = new SendPhoto(attachedPhotos[i], isLast);
                sendPhoto.execute();

            }

        } else {

            //there are no photos to send
            sendData();
        }

    }//end of sendPhoto


    private void addPhotoURL(String photoURL){

        for(int i = 0; i < photoUrlList.length; i++){
            if(photoUrlList[i] == null || photoUrlList[i].equals("empty")){
                photoUrlList[i] = photoURL;
                break;
            }
        }
    }




    //This method is used to interact with the camera and retrieve a photo
    private void startCamera(){

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePhotoIntent.resolveActivity(getPackageManager()) != null){

            //create file where the photo should go
            File photoFile = null;

            try{
                photoFile = createImageFile();
            }catch(IOException error){
                System.out.println("Error occurred while creating image file");
                System.out.println(error.getMessage());
            }

            //continue only if the file was successfully created
            if(photoFile != null){

                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        "com.CS480.hoa.fileprovider",
                        photoFile);

                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePhotoIntent,requestImageCapture);
            }
        }

    }




    //This method handles what the camera returns to this application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == requestImageCapture && resultCode == RESULT_OK){

            Bitmap imageBitmap;

            try {
                final Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(imageStream);



                //ensure the image has proper orientation before storage
                ExifInterface ei = new ExifInterface(imageUri.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);


                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        imageBitmap = rotateImage(imageBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        imageBitmap = rotateImage(imageBitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        imageBitmap = rotateImage(imageBitmap, 270);
                }



                //cycle through attachedPhotos to find the first blank spot
                for(int index = 0; index < MAXPHOTOS; index++){
                    if(attachedPhotos[index] == null) {
                        attachedPhotos[index] = imageBitmap;
                        break;
                    }
                }


                imageStream.close();


            } catch (FileNotFoundException e) {
                System.out.println("Could not find the photo file*****************************");
                e.printStackTrace();
            } catch (IOException error){
                System.out.println(error.getMessage());
            }




        }
    }




    //this method rotates an image
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }





    //this method creates and image file
    private File createImageFile() throws IOException{
        //create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();

        for(int i = 0; i < attachedPhotosPaths.length; i++){
            if (attachedPhotosPaths[i] == null) {

                attachedPhotosPaths[i] = imageFile.getAbsolutePath();
                break;

            }
        }
        return imageFile;
    }




    //background class for converting image into string and sending data to webservice
    class SendPhoto extends AsyncTask<Void, Void, String>{

        private Bitmap image;
        private boolean isLast;

        public SendPhoto(Bitmap image, boolean isLast){
            this.image = image;
            this.isLast = isLast;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            cancelButton.setEnabled(false);
            saveButton.setEnabled(false);
            attachPhotoButton.setEnabled(false);

            loadingSpinner.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String imageAsString) {
            super.onPostExecute(imageAsString);

            //update url to access web service
            Database.changeBaseURL("https://4bx3iimbre.execute-api.us-east-1.amazonaws.com/");

            //create retrofit object
            RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

            String fileName = "Image" + counter + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            counter++;
            fileName = fileName.replace(" ", "");

            //create JsonObject to send data to web service
            JsonObject json = new JsonObject();

            //send photo to web service
            json.addProperty("content", imageAsString);


            json.addProperty("fname", fileName + ".jpg");


            //create Call object to receive response from web service
            Call<JsonObject> call = retrofit.uploadImages(json);

            //background thread
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful()) {

                        //convert JsonArray into JsonObject
                        JsonObject jsonObject = response.body();

                        //Extract data from JsonObject
                        String statusCode = jsonObject.get("statusCode").toString();
                        statusCode = statusCode.replace("\"", "");

                        JsonObject url = jsonObject.get("body").getAsJsonObject();

                        String photoURL = url.get("file_path").toString();
                        photoURL = photoURL.replace("\"", "");


                        addPhotoURL(photoURL);

                        //after the last photo is sent then send the work order data
                        if(isLast){
                            sendData();
                        }


                    } else {
                        //the response was not successful

                        Toast.makeText(getBaseContext(), "Error in uploading image", Toast.LENGTH_SHORT).show();

                        System.out.println("Response failed************************");
                        System.out.println(response.code());
                        System.out.println(response.message());


                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("Failure Create New Work Order sendPhoto*************************");
                    System.out.println(t.getMessage());

                }
            });

        }//end of onPostExecute

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... voids) {

            return ConvertImage.convertImageToString(image);
        }
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case android.R.id.home:

                onBackPressed();
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onBackPressed(){

        Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

        intent.putExtra(HomeOwnerMainActivity.userCode, user);

        startActivity(intent);
    }
}
