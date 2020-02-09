package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewWorkOrderActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.createNewWorkOrder";
    public static final int requestImageCapture = 1;
    public static final int MAXPHOTOS = 5;


    private WorkOrder workOrder;
    private User user;
    private Bitmap[] attachedPhotos;

    private EditText descriptionInput;
    private Button saveButton;
    private Button cancelButton;
    private Button attachPhotoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_work_order);

        //maximum of 5 photos for each work order
        attachedPhotos = new Bitmap[MAXPHOTOS];

        //retrieve the user from previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        //assign objects to variables
        descriptionInput = findViewById(R.id.workOrderCreateDescriptionInput);
        saveButton = findViewById(R.id.workOrderCreateSaveButton);
        cancelButton = findViewById(R.id.workOrderCreateCancelButton);
        attachPhotoButton = findViewById(R.id.workOrderCreateAttachPhotoButton);

        //save button onclick listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make sure input fields have data
                if(validateForm()){

                    //create a WorkOrder object based on user input
                    workOrder = createWorkOrder();

                    //send the WorkOrder to the web service
                    sendData();

                    //return to the users main activity
                    Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                    intent.putExtra(HomeOwnerMainActivity.userCode, user);

                    startActivity(intent);

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
                //return to home owner main activity
                Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                //send user data to new activity
                intent.putExtra(HomeOwnerMainActivity.userCode, user);

                startActivity(intent);
            }
        });

        //attach photo button onclick listener
        attachPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for max photo limit
                if(attachedPhotos[MAXPHOTOS - 1] == null) {

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

        return new WorkOrder(user, description, rightNow, attachedPhotos);
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


        if(attachedPhotos[0] != null) {


            System.out.println(attachedPhotos[0]);



            //there are photos to send to web service
            JsonArray photoList = new JsonArray();

            for (int i = 0; i < attachedPhotos.length; i++) {

                if (attachedPhotos[i] != null) {
                    //separate class for converting to Base64 string
                    photoList.add(ConvertImage.convertImageToString(attachedPhotos[i]));

                }
            }

            json.add("attachedPhotos", photoList);

        }else{
            //There are no photos to send to web service
            JsonArray photoList = new JsonArray();
            photoList.add("empty");
            json.add("attachedPhotos", photoList);
        }

        //create Call object to receive response from web service
        Call<JsonArray> call = retrofit.newWorkOrder(json);

        //background thread
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

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


                }else{
                    //the response was not successful

                    Toast.makeText(getBaseContext(),"The response failed", Toast.LENGTH_SHORT).show();

                    System.out.println("Response failed************************");
                    System.out.println(response.code());
                    System.out.println(response.message());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure Create New Work Order*************************");
                System.out.println(t.getMessage());
            }
        });
    }




    //This method is used to interact with the camera and retrieve a photo
    private void startCamera(){

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePhotoIntent,requestImageCapture);
        }

    }




    //This method handles what the camera returns to this application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == requestImageCapture && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            //cycle through attachedPhotos to find the first blank spot
            int index;

            for(index = 0; index < MAXPHOTOS - 1; index++){
                if(attachedPhotos[index] == null) break;
            }

                attachedPhotos[index] = imageBitmap;

        }
    }
}
