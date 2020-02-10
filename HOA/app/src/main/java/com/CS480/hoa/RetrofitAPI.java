package com.CS480.hoa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("Login")
    Call<JsonArray> userLogin(@Body JsonObject object);


    @POST("getUser")
    Call<JsonArray> getUser(@Body JsonObject object);

    @POST()
    Call<JsonArray> getAllUsers(@Body JsonObject object);

    @POST("retrieveUserWO")
    Call<JsonArray> getWorkOrders(@Body JsonObject object);

    @POST("retrieveAdminWO")
    Call<JsonArray> getAllWorkOrders(@Body JsonObject object);

    @POST("Register")
    Call<JsonArray> newUser(@Body JsonObject object);

    @POST("createWorkOrder")
    Call<JsonArray> newWorkOrder(@Body JsonObject object);

    @POST("editUser")
    Call<JsonArray> updateUser(@Body JsonObject object);

    @POST("editWO")
    Call<JsonArray> updateWorkOrder(@Body JsonObject object);

    @POST("deleteUser")
    Call<JsonArray> deleteUser(@Body JsonObject object);

    @POST("deleteWO")
    Call<JsonArray> deleteWorkOrder(@Body JsonObject object);


}
