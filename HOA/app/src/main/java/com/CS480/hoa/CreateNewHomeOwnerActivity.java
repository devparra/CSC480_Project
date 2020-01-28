package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewHomeOwnerActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText zip;
    private EditText phone;
    private EditText email;
    private EditText password;

    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_home_owner);

        //assign objects to each variable
        firstName = findViewById(R.id.editTextFirstNameInput);
        lastName = findViewById(R.id.editTextLastNameInput);
        street = findViewById(R.id.editTextStreetInput);
        city = findViewById(R.id.editTextCityInput);
        state = findViewById(R.id.editTextStateInput);
        zip = findViewById(R.id.editTextZipInput);
        phone = findViewById(R.id.editTextPhoneInput);
        email = findViewById(R.id.editTextEmailInput);
        password = findViewById(R.id.editTextPasswordInput);
        saveButton = findViewById(R.id.createSaveButton);
        cancelButton = findViewById(R.id.createCancelButton);

        //assign onclick listener to save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateForm()){
                    //all fields in the form were filled out

                    //create a user object with user input
                    User user = createUser();

                    //send user object to database for storage
                    sendData(user);

                    //return to login activity
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);

                }else{
                    //not all fields are filled in display message for the user
                    Toast.makeText(getBaseContext(),"You must fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //assign onclick listener to cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //when cancel is pressed return to the login activity
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

    }//end of onCreate


    //this is for if the back button is pressed
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }



    //This method will return true only if every input field has data
    private boolean validateForm(){

        //ensure entire form is filled out
        if(firstName.getText().toString().isEmpty() ||
           lastName.getText().toString().isEmpty() ||
           street.getText().toString().isEmpty() ||
           city.getText().toString().isEmpty() ||
           state.getText().toString().isEmpty() ||
           zip.getText().toString().isEmpty() ||
           phone.getText().toString().isEmpty() ||
           email.getText().toString().isEmpty() ||
           password.getText().toString().isEmpty()){

            return false;
        }

        return true;
    }



    //This method extracts the information from the user input and
    //uses it to create a User object
    private User createUser(){

        String userName = firstName.getText().toString() + " " + lastName.getText().toString();
        String userAddress = street.getText().toString() + "\n" +
                         city.getText().toString() + ", " +
                         state.getText().toString() + " " +
                         zip.getText().toString();
        String userEmail = email.getText().toString();
        String userPhone = phone.getText().toString();
        String userPassword = password.getText().toString();

        return new User(userName, userAddress, userEmail, userPhone, userPassword);
    }



    //This method is used to send the user data to the web service
    //and handle the web service response
    private void sendData(User user){

        //update url to access web service
        Database.changeBaseURL("https://1fmwtml80g.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create JsonObject to send data to web service
        JsonObject json = new JsonObject();
        json.addProperty("userName", user.getUserName());
        json.addProperty("userAddress", user.getUserAddress());
        json.addProperty("userPhone", user.getUserPhone());
        json.addProperty("userEmail", user.getUserEmail());
        json.addProperty("userPassword", user.getUserPassword());

        if(user.isAdmin()){
            json.addProperty("isAdmin", "1");
        }else {
            json.addProperty("isAdmin", "0");
        }


        System.out.println(json);



        //create Call object to receive response from web service
        Call<JsonArray> call = retrofit.newUser(json);

        //background thread
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if(response.isSuccessful()) {

                    JsonArray jsonArray = response.body();

                    JsonObject jsonObject = (JsonObject) jsonArray.get(0);

                    String message = jsonObject.get("message").toString();
                    message = message.replace("\"", "");

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
    }//end of sendData


    //This is used if the back navigation button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.parent:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
