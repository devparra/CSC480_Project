package com.CS480.hoa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {

    @POST("Login")
    Call<JsonArray> userLogin(@Body JsonObject object);


    @POST("getUser")
    Call<JsonArray> getUser(@Body JsonObject object);

    @GET("getAllUsers")
    Call<JsonArray> getAllUsers();

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

    @POST("uplaodImage")
    Call<JsonObject> uploadImages(@Body JsonObject object);

    @POST("pdfUpload")
    Call<JsonArray> uploadPdf(@Body JsonObject object);

}
