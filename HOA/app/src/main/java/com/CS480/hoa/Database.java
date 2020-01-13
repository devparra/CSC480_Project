package com.CS480.hoa;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Database {

    //NEED TO REPLACE WITH ACTUAL URL
    private final String baseURL = "URL";

    private User user;
    private ArrayList<User> users;
    private ArrayList<WorkOrder> workOrders;
    private boolean success;

    //Create a static instance to be used by all other activities
    public static Database db;

    //create the Retrofit object
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    //create an instance of the interface
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);


    //Access and manipulate User information


    //Login method
    public User login(String userEmail, String userPassword){

        Call<User> call = retrofitAPI.userLogin(userEmail,userPassword);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(!response.isSuccessful()){
                    //ERROR MESSAGE

                    user = null;

                    return;
                }

                user = response.body();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //ERROR MESSAGE
                user = null;
            }
        });

        return user;
    }


    //This method sends user data to server
    public boolean createUser(User user){

        success = false;

        Call<User> call = retrofitAPI.newUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(response.isSuccessful()){
                    success = true;
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        return success;
    }

    //This method retrieves all of the users from the database
    public ArrayList<User> getUsers(){

        users = null;

        Call<ArrayList<User>> call = retrofitAPI.getAllUsers();

        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {

                if(response.isSuccessful()) {
                    users = (ArrayList<User>) response.body();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {

            }
        });

        return users;
    }


    //This method replaces the user in the database with the object sent
    public boolean updateUser(User user){

        success = false;

        Call<User> call = retrofitAPI.updateUser(user.getId(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    success = true;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        return success;
    }


    //This method will remove a user from the server
    public boolean deleteUser(User user){

        success = false;

        Call<Void> call = retrofitAPI.deleteUser(user.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    success = true;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

        return success;

    }


    //Access and manipulate work order information


    //This method send a work order to the server
    public boolean createWorkOrder(WorkOrder workOrder){

        success = false;

        Call<WorkOrder> call = retrofitAPI.newWorkOrder(workOrder);

        call.enqueue(new Callback<WorkOrder>() {
            @Override
            public void onResponse(Call<WorkOrder> call, Response<WorkOrder> response) {

                if(response.isSuccessful()){
                    success = true;
                }
            }

            @Override
            public void onFailure(Call<WorkOrder> call, Throwable t) {

            }
        });

        return success;
    }


    //This method retrieves a list of work orders from the server
    public ArrayList<WorkOrder> getWorkOrder(User user){

        workOrders = null;

        Call<ArrayList<WorkOrder>> call = retrofitAPI.getWorkOrders(user.getId());

        call.enqueue(new Callback<ArrayList<WorkOrder>>() {
            @Override
            public void onResponse(Call<ArrayList<WorkOrder>> call, Response<ArrayList<WorkOrder>> response) {

                if(response.isSuccessful()){
                    workOrders = response.body();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WorkOrder>> call, Throwable t) {

            }
        });

        return workOrders;
    }


    //This method gets all work orders from the server
    public ArrayList<WorkOrder> getAllWorkOrders(){

        workOrders = null;

        Call<ArrayList<WorkOrder>> call = retrofitAPI.getAllWorkOrders();

        call.enqueue(new Callback<ArrayList<WorkOrder>>() {
            @Override
            public void onResponse(Call<ArrayList<WorkOrder>> call, Response<ArrayList<WorkOrder>> response) {

                if(response.isSuccessful()){
                    workOrders = response.body();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<WorkOrder>> call, Throwable t) {

            }
        });

        return workOrders;
    }

    public boolean updateWorkOrder(WorkOrder workOrder){

        success = false;

        Call<WorkOrder> call = retrofitAPI.updateWorkOrder(workOrder.getId(), workOrder);

        call.enqueue(new Callback<WorkOrder>() {
            @Override
            public void onResponse(Call<WorkOrder> call, Response<WorkOrder> response) {

                if(response.isSuccessful()){
                    success = true;
                }
            }

            @Override
            public void onFailure(Call<WorkOrder> call, Throwable t) {

            }
        });

        return success;
    }

    public boolean deleteWorkOrder(WorkOrder workOrder){

        success = false;

        Call<Void> call = retrofitAPI.deleteWorkOrder(workOrder.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    success = true;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

        return success;
    }
}
