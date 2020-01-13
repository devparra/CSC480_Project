package com.CS480.hoa;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    //ALL GET POST PUT AND DELETE NEED TO BE UPDATED WITH SPECIFIC URL

    @GET()
    Call<User> userLogin(
            @Query("userEmail") String email,
            @Query("userPassword") String password
    );

    @GET()
    Call<ArrayList<User>> getAllUsers();

    @GET()
    Call<ArrayList<WorkOrder>> getWorkOrders(
            @Query("userID") int userID
    );

    @GET()
    Call<ArrayList<WorkOrder>> getAllWorkOrders();

    @POST()
    Call<User> newUser(@Body User user);

    @POST()
    Call<WorkOrder> newWorkOrder(@Body WorkOrder workOrder);

    @PUT()
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @PUT()
    Call<WorkOrder> updateWorkOrder(@Path("id") int id, @Body WorkOrder workOrder);

    @DELETE()
    Call<Void> deleteUser(@Path("id") int id);

    @DELETE()
    Call<Void> deleteWorkOrder(@Path("id") int id);


}
