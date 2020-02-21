package com.CS480.hoa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditHomeOwnerActivity extends AppCompatActivity implements
        DeleteUserDialog.DeleteUserSelectedListener {

    public static final String userCode = "com.CS480.hoa.editHomeOwner";

    private EditText firstName;
    private EditText lastName;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText zip;
    private EditText phone;
    private EditText email;

    private RadioButton isAdminYes;
    private RadioButton isAdminNo;

    private Button saveButton;
    private Button cancelButton;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_home_owner);

        //get the user object from previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        //assign objects to variables
        firstName = findViewById(R.id.editUserFirstNameInput);
        lastName = findViewById(R.id.editUserLastNameInput);
        street = findViewById(R.id.editUserStreetInput);
        city = findViewById(R.id.editUserCityInput);
        state = findViewById(R.id.editUserStateInput);
        zip = findViewById(R.id.editUserZipInput);
        phone = findViewById(R.id.editUserPhoneInput);
        email = findViewById(R.id.editUserEmailInput);

        isAdminNo = findViewById(R.id.editUserIsAdminNoRadioButton);
        isAdminYes = findViewById(R.id.editUserIsAdminYesRadioButton);

        saveButton = findViewById(R.id.editUserSaveButton);
        cancelButton = findViewById(R.id.editUserCancelButton);


        //onClick listener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //no updating just return to previous activity
               onBackPressed();

            }
        });



        //onClick listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check to see if anything has changed
                if(anythingChanged()){

                    updateData();

                }

            }
        });


        //put the current user information into the edit texts
        populateFields();
    }



    //this method is used to populate the edit texts with user information
    private void populateFields() {

        try {
            //set the name fields
            String[] name = user.getUserName().split(" ");
            firstName.setText(name[0]);
            lastName.setText(name[1]);
        } catch (IndexOutOfBoundsException error) {
            firstName.setText("Unknown");
            lastName.setText("unknown");
        }

        try {

            //set the address fields
            String[] address1 = user.getUserAddress().split("\\n");
            street.setText(address1[0]);

            String[] address2 = address1[1].split(",");
            city.setText(address2[0]);

            String[] address3 = address2[1].split(" ");

           //address3[0] is an empty string
            state.setText(address3[1]);
            zip.setText(address3[2]);

        } catch (IndexOutOfBoundsException error) {
            street.setText("unknown");
            city.setText("");
            state.setText("");
            zip.setText("");
        }

        try {
            //set phone
            phone.setText(user.getUserPhone());
        } catch (IndexOutOfBoundsException error) {
            phone.setText("unknown");
        }

        try {
            //set email
            email.setText(user.getUserEmail());
        }catch(IndexOutOfBoundsException error){
            email.setText("unknown");
        }

        //set the admin access
        if(user.isAdmin()){
            isAdminYes.toggle();
        } else {
            isAdminNo.toggle();
        }
    }




    //this method returns true if any data has changed
    private boolean anythingChanged(){

        String userName = firstName.getText().toString() + " " + lastName.getText().toString();
        String userAddress = street.getText().toString() + "\\n" +
                city.getText().toString() + ", " +
                state.getText().toString() + " " +
                zip.getText().toString();
        String userEmail = email.getText().toString();
        String userPhone = phone.getText().toString();
        boolean isAdmin = isAdminYes.isChecked();


        if(
                !userName.equals(user.getUserName()) ||
                !userAddress.equals(user.getUserAddress()) ||
                !userEmail.equals(user.getUserEmail()) ||
                !userPhone.equals(user.getUserPhone()) ||
                !isAdmin == user.isAdmin()
        ){
            //a change has been made
            return true;
        } else {
            //no changes were made
            return false;
        }
    }





    //this method is used to update the home owners data in the web service
    private void updateData(){

        User newUser = createUser();

        //send update for workorder to the web service
        //update url to access web service
        Database.changeBaseURL("https://yfxlozs7i8.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add current workorder data to JsonObject
        json.addProperty("userName", newUser.getUserName());
        json.addProperty("userAddress", newUser.getUserAddress());
        json.addProperty("userPhone", newUser.getUserPhone());
        json.addProperty("userEmail", user.getUserEmail());
        json.addProperty("userNewEmail", newUser.getUserEmail());
        json.addProperty("userIsAdmin", newUser.isAdmin() ? "1" : "0");

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.updateUser(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray jsonArray = response.body();

                JsonObject object = jsonArray.get(0).getAsJsonObject();

                String status = object.get("status").toString();
                status = status.replace("\"", "");

                if(status.equals("0")){
                    Toast.makeText(getBaseContext(), "There was a problem with updating the user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "The user was successfully updated", Toast.LENGTH_SHORT).show();
                }

                //return to the users main page
                Intent intent = new Intent(getBaseContext(), EditHomeOwnerMainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                System.out.println("Failure in EditHomeOwner updateData*********************");
                System.out.println(t.getMessage());
            }
        });
    }//end updateData






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
        Boolean isAdmin = isAdminYes.isChecked();

        User newUser =  new User(userName, userAddress, userEmail, userPhone);
        newUser.setAdmin(isAdmin);

        return newUser;
    }






    //this method creates the options menu to delete a user
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.delete_menu, menu);

        menu.findItem(R.id.deleteWorkOrderMenuItem).setVisible(true);
        menu.findItem(R.id.deleteWorkOrderMenuItem).setEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }






    //this method handles the menu item that are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.deleteWorkOrderMenuItem:

                FragmentManager manager = getSupportFragmentManager();
                DeleteUserDialog dialog = new DeleteUserDialog();
                dialog.show(manager, "Delete User");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //this handles the response from selecting yes to confirm delete
    public void deleteUserClick(int which){
        if(which == AlertDialog.BUTTON_POSITIVE){
            deleteUser();
        }
    }




    //this tells web service to delete this user
    private void deleteUser(){
        //send update for workorder to the web service
        //update url to access web service
        Database.changeBaseURL("https://pbyqvbm7mc.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add current workorder data to JsonObject
        json.addProperty("userEmail", user.getUserEmail());

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.deleteUser(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray jsonArray = response.body();

                JsonObject object = (JsonObject) jsonArray.get(0);

                String status = object.get("status").toString();
                status = status.replace("\"", "");

                if(status.equals("0")){
                    Toast.makeText(getBaseContext(), "There was a problem with deleting the user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "The user was successfully deleted", Toast.LENGTH_SHORT).show();
                }

                //return to the users main page
                Intent intent = new Intent(getBaseContext(), AdminMainActivity.class);

                intent.putExtra(AdminMainActivity.userCode, user);

                startActivity(intent);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                System.out.println("Failure in EditHomeOwner Delete*********************");
                System.out.println(t.getMessage());
            }
        });

    }//end deleteUser
}
