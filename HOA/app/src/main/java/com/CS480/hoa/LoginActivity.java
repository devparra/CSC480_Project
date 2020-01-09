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

public class LoginActivity extends AppCompatActivity implements AccessAdminDialog.AccessAdminSelectedListener {

    private EditText userName;
    private EditText password;
    private Button loginButton;
    private Button createNewUser;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //assign objects from xml form to variables
        userName = findViewById(R.id.userNameLogin);
        password = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        createNewUser = findViewById(R.id.createNewUserButton);

        //add onClickListener to login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //test to see if input is empty
                if(userName.getText().toString().isEmpty() ||
                   password.getText().toString().isEmpty()){

                    //one of the inputs is empty. display message to user
                    Toast.makeText(getBaseContext(), "You must enter a user name and password", Toast.LENGTH_SHORT).show();

                }else{

                    //the input is not empty

                    //check the database for login
                    user = Database.db.login(userName.getText().toString(), password.getText().toString());

                    if(user != null){

                        //login is successful

                        //check to see if user has admin access
                        if(user.isAdmin()){

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

                    }else{

                        //login failed. display message to user
                        Toast.makeText(getBaseContext(),"The user name or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
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

            //the user wants to access admin features
            Intent intent = new Intent(this, AdminMainActivity.class);

            //pass user data to new activity
            intent.putExtra(HomeOwnerMainActivity.userCode, user);

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
}
