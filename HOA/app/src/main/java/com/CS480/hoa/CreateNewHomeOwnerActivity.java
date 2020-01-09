package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

                    //send user object to database for storage

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
}
