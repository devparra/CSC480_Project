package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditHomeOwnerMainActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.EditHomeOwnerMain";

    private RecyclerView recyclerView;
    User[] userList;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_home_owner_main);

        recyclerView = findViewById(R.id.recyclerViewEditHomeOwner);

        currentUser = (User) getIntent().getSerializableExtra(userCode);

        getData();
    }




    //This method retrieves all user data from the web service
    private void getData(){

        //update url to access web service
        Database.changeBaseURL("https://3ygu9ce6ed.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getAllUsers();

        //background thread to communicate with the web service
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                //JsonArray object to store the response from web service
                JsonArray jsonArray = response.body();

                //translate the Json Array into a list of work orders
                boolean hasUsers = getUsers(jsonArray);

                //if the array is empty then don't populate the recycler view
                if(!hasUsers){
                    //There are no users to display, something went wrong
                    Toast.makeText(getBaseContext(),"Error in accessing home owners from web service", Toast.LENGTH_SHORT).show();
                }else {
                    //There are users to display

                    //because the Recyclerview requires the userList to be implemented correctly
                    //the Recyclerview initialization will be in the onResponse method

                    //recycler view initialization and implementation

                    //add dividers between each user
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(dividerItemDecoration);

                    //linear layout for vertical scrolling
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    //send list of users to the setAdapter
                    EditHomeOwnerMainActivity.UserAdapter adapter = new EditHomeOwnerMainActivity.UserAdapter(userList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure in EditHomeOwnerMain**********************");
                System.out.println(t.getMessage());
            }
        });

    }




    //this method extracts the data from the JsonArray and returns a list of users
    private boolean getUsers(JsonArray jsonArray){

        userList = new User[jsonArray.size()];
        JsonObject[] jsonObjects = new JsonObject[jsonArray.size()];

        for(int i = 0; i < jsonArray.size(); i++){
            jsonObjects[i] = jsonArray.get(i).getAsJsonObject();
        }

        //check to see if there are any users to display
        if(jsonObjects[0].has("status")){

            if(jsonObjects[0].get("status").toString().equals("\"0\"")){

                //if status is 0 then there are no work orders to display
                return false;
            }
        }


        //This will only execute if there are users to display
        for(int i = 0; i < jsonObjects.length; i++){

            String name = jsonObjects[i].get("userName").toString();
            name = name.replace("\"", "");

            String address = jsonObjects[i].get("userAddress").toString();
            address = address.replace("\"", "");

            String phone = jsonObjects[i].get("userPhone").toString();
            phone = phone.replace("\"", "");

            String email = jsonObjects[i].get("userEmail").toString();
            email = email.replace("\"", "");


            userList[i] = new User(name, address, email, phone);

            //is admin is false by default only change if necessary
            if(jsonObjects[i].get("userIsAdmin").toString().equals("\"1\"")){
                userList[i].setAdmin(true);
            }

        }

        return true;
    }




    //This class is used to create each line item in the recycler view
    private class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //the current User object
        private User user;

        //TextViews from the list item for display
        private TextView userNameTextView;

        //constructor
        public UserHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.home_owner_list_item, parent, false));
            itemView.setOnClickListener(this);

            userNameTextView = itemView.findViewById(R.id.homeOwnerListTextView);
        }

        //binding the data from the work order object to the clickable item
        public void bind(User userItem){
            
            user = userItem;

            userNameTextView.setText(user.getUserName());

        }

        //onclick Method
        @Override
        public void onClick(View v) {

            //when a name is selected then display the user information
            Intent intent = new Intent(getBaseContext(), EditHomeOwnerActivity.class);

            intent.putExtra(EditHomeOwnerActivity.userCode, user);

            startActivity(intent);

        }
    }


    //This class takes the list of work orders and generates a holder for each one
    private class UserAdapter extends RecyclerView.Adapter<EditHomeOwnerMainActivity.UserHolder>{

        private User[] users;

        //constructor
        public UserAdapter(User[] userList){

            users = userList;
        }

        //create a holder for the workOrder
        @NonNull
        @Override
        public EditHomeOwnerMainActivity.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            return new EditHomeOwnerMainActivity.UserHolder(layoutInflater, parent);
        }

        //bind the work order information to the holder item
        @Override
        public void onBindViewHolder(@NonNull EditHomeOwnerMainActivity.UserHolder holder, int position) {
            User user = users[position];
            holder.bind(user);
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            return users.length;
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
        Intent intent = new Intent(getBaseContext(), AdminMainActivity.class);

        intent.putExtra(AdminMainActivity.userCode, currentUser);

        startActivity(intent);
    }

}
