package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewWorkOrderActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.createNewWorkOrder";

    private WorkOrder workOrder;
    private User user;
    private Drawable[] attachedPhotos;

    private EditText descriptionInput;
    private Button saveButton;
    private Button cancelButton;
    private Button attachPhotoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_work_order);

        //maximum of 5 photos for each work order
        attachedPhotos = new Drawable[5];

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

                //get Image from the camera

            }
        });
    }


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

        ArrayList<String> commentList = new ArrayList<>();
        commentList.add("empty");
        json.addProperty("comments", String.valueOf((new JSONArray(commentList))));

        if(attachedPhotos[0] != null) {
            ArrayList<String> photoList = new ArrayList<>();

            for (Drawable photo : attachedPhotos) {

                //separate class for converting to Base64 string
                photoList.add(ConvertImage.convertImageToString(photo));
            }

            json.addProperty("attachedPhotos", String.valueOf(new JSONArray(photoList)));

        }else{
            ArrayList<String> photoList = new ArrayList<>();
            photoList.add("empty");
            json.addProperty("attachedPhotos", String.valueOf(new JSONArray(photoList)));
        }



        System.out.println(json);


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
                System.out.println("Failure*************************");
                System.out.println(t.getMessage());
            }
        });
    }


}
