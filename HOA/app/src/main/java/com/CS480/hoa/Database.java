package com.CS480.hoa;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Database {

    //NEED TO REPLACE WITH ACTUAL URL
    private static String baseURL = "";

    //create the Retrofit object
    private static Retrofit retrofit;


    public static <T> T createService(Class<T> serviceClass){

        return retrofit.create(serviceClass);
    }


    public static void changeBaseURL(String newURL){

        baseURL = newURL;

        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}//end of class
