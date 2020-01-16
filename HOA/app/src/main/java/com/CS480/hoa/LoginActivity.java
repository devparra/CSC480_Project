package com.CS480.hoa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                    Toast.makeText(getBaseContext(), "You must enter a user email and userPassword", Toast.LENGTH_SHORT).show();

                }else{

                    //the input is not empty


                    Database.changeBaseURL("https://hcn6q38o2l.execute-api.us-west-2.amazonaws.com");

                    RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

                    //check the database for login
                    //user = Database.db.login(userEmail.getText().toString(), userPassword.getText().toString());

                    Call<JsonObject> call = retrofit.userLogin(userEmail.getText().toString(), userPassword.getText().toString());

                    //background thread
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            //user = response.body();

                            System.out.println("**********************************");
                            System.out.println(response.code());
                            System.out.println("********************************");

                            System.out.println(user);


                            if (response.isSuccessful()) {
                                if (user.getStatus() == 1) {

                                    //login is successful

                                    //check to see if user has admin access
                                    if (user.isAdmin()) {

                                        //use dialog box to ask if user wants to access the admin features
                                        FragmentManager manager = getSupportFragmentManager();
                                        AccessAdminDialog dialog = new AccessAdminDialog();
                                        dialog.show(manager, "Access Admin");

                                    }

                                    //the user does not have admin access
                                    Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                                    //pass user data to new activity
                                    intent.putExtra(HomeOwnerMainActivity.userCode, user);

                                    //start the next activity
                                    startActivity(intent);

                                } else {

                                    //login failed. display message to user
                                    Toast.makeText(getBaseContext(), "The user name or userPassword is incorrect", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                //the response was not successful
                                Toast.makeText(getBaseContext(), "There is an error", Toast.LENGTH_SHORT).show();
                            }

                        }//end of onResponse

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });

                }// end of input is not empty

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

            //the user wants to access admin features
            Intent intent = new Intent(this, AdminMainActivity.class);

            //pass user data to new activity
            intent.putExtra(AdminMainActivity.userCode, user);

            //start the next activity
            startActivity(intent);

        }else{

            //the user wants to access regular user features
            Intent intent = new Intent(this, HomeOwnerMainActivity.class);

            //pass user data to new activity
            intent.putExtra(HomeOwnerMainActivity.userCode, user);

            //start the next activity
            startActivity(intent);

        }
    }

    public void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
