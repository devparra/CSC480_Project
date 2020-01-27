package com.CS480.hoa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements AccessAdminDialog.AccessAdminSelectedListener {

    private EditText userEmail;
    private EditText userPassword;
    private Button loginButton;
    private Button createNewUser;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //assign objects from xml form to variables
        userEmail = findViewById(R.id.userNameLogin);
        userPassword = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        createNewUser = findViewById(R.id.createNewUserButton);

        //add onClickListener to login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //test to see if input is empty
                if(userEmail.getText().toString().isEmpty() ||
                   userPassword.getText().toString().isEmpty()){

                    //one of the inputs is empty. display message to user
                    displayMessage("You must enter a user email and user password");

                }else{

                    //the input is not empty

                   sendData();

                }

            }// end of onClick


        }); //end of onclick listener for login button

        //onclick listener for create new user button
        createNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //go straight to the create new home owner activity
                Intent intent = new Intent(getBaseContext(), CreateNewHomeOwnerActivity.class);
                startActivity(intent);
            }
        });

    }

    //The following method handles the selection from the AlertDialog
    //that asked if the user wants to access admin features
    @Override
    public void accessAdminClick(int which){

        if(which == AlertDialog.BUTTON_POSITIVE){

            //*********************************
            //use until admin main activity is ready
            //Intent intent = new Intent(this, BlankActivity.class);
            //*********************************


            //the user wants to access admin features
            Intent intent = new Intent(this, AdminMainActivity.class);

            //pass user data to new activity
            intent.putExtra(AdminMainActivity.userCode, user);

            //start the next activity
            startActivity(intent);

        }else{

            //**************************************
            //use this until home owner main activity is ready
            //Intent intent = new Intent(this, BlankActivity.class);
            //****************************************




            //the user wants to access regular user features
            Intent intent = new Intent(this, HomeOwnerMainActivity.class);

            //pass user data to new activity
            intent.putExtra(HomeOwnerMainActivity.userCode, user);

            //start the next activity
            startActivity(intent);

        }
    }

    //This method displays a message for the user to see
    public void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //This method communicates with the webservice and handles the response
    private void sendData(){

        //update url to access the web service
        Database.changeBaseURL("https://ewoyiqpfh8.execute-api.us-east-1.amazonaws.com/");

        RetrofitAPI retrofitAPI = Database.createService(RetrofitAPI.class);

        //create JsonObject to send data to web service
        JsonObject json = new JsonObject();
        json.addProperty("userEmail", userEmail.getText().toString());
        json.addProperty("userPassword", userPassword.getText().toString());

        //create Call object to receive results from web service
        Call<JsonArray> call = retrofitAPI.userLogin(json);

        //background thread
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.isSuccessful()) {

                    JsonArray jsonArray = response.body();

                    user = createUser(jsonArray);

                    if (user.getStatus().equals("1")) {

                        //login is successful

                        //check to see if user has admin access
                        if (user.isAdmin()) {

                            //use dialog box to ask if user wants to access the admin features
                            FragmentManager manager = getSupportFragmentManager();
                            AccessAdminDialog dialog = new AccessAdminDialog();
                            dialog.show(manager, "Access Admin");

                        }else {


                            //******************************
                            //use until home owner main activity is ready
                            //Intent intent = new Intent(getBaseContext(), BlankActivity.class);
                            //*******************************


                            //the user does not have admin access
                            Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                            //pass user data to new activity
                            intent.putExtra(HomeOwnerMainActivity.userCode, user);

                            //start the next activity
                            startActivity(intent);
                        }

                    } else {

                        //login failed. display message to user
                        displayMessage("Incorrect email or password");
                    }

                }else{
                    //the response was not successful
                    displayMessage("Response from web service was unsuccessful");
                }

            }//end of onResponse

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure in Login****************************************");
                System.out.println(t.getMessage());
            }
        });

    }//end of sendData



    //this method takes a JsonArray, retrieves the data and uses it to create a User object
    private User createUser(JsonArray jsonArray){

        JsonObject object = (JsonObject) jsonArray.get(0);

        String status = object.get("status").toString();
        status = status.replace("\"", "");

        String message = object.get("message").toString();
        message = message.replace("\"", "");


        if(object.size() > 2) {

            String name = object.get("userName").toString();
            name = name.replace("\"", "");

            String address = object.get("userAddress").toString();
            address = address.replace("\"", "");

            String phone = object.get("userPhone").toString();
            phone = phone.replace("\"", "");

            String email = object.get("userEmail").toString();
            email = email.replace("\"", "");


            User newUser = new User(name, address, email, phone, status, message);


            String isAdmin = object.get("isAdmin").toString();
            isAdmin = isAdmin.replace("\"", "");

            if (isAdmin.equals("1")) {
                newUser.setAdmin(true);
            } else {
                newUser.setAdmin(false);
            }

            return newUser;
        } else{
            return new User(status, message);
        }

    }//end of getUser
}
