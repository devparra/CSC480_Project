package com.CS480.hoa;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import retrofit2.http.Body;
import retrofit2.http.Field;

public class User implements Serializable {

    private String message = "";
    private int status = -1;
    private int userID;
    private String userName;
    private String userAddress;

    @SerializedName("email")
    private String userEmail;
    private int userPhone;

    @SerializedName("pword")
    private String userPassword;
    private boolean isAdmin;

    //constructors

    //This constructor is used for creating a new user and includes
    //the password that the user inputs
    public User(int id, String name, String address, String email, int phone, String password, boolean isAdmin){
        userID = id;
        userName = name;
        userAddress = address;
        userEmail = email;
        userPhone = phone;
        userPassword = password;
        this.isAdmin = isAdmin;
    }


    //This constructor is user for creating a user during login and
    //does not include the password
    public User(int id, String name, String address, String email, int phone, boolean isAdmin){
        userID = id;
        userName = name;
        userAddress = address;
        userEmail = email;
        userPhone = phone;
        userPassword = "";
        this.isAdmin = isAdmin;
    }

    //getters
    public int getUserID(){return userID;}
    public String getUserName(){return userName;}
    public String getUserAddress(){return userAddress;}
    public String getUserEmail(){return userEmail;}
    public int getUserPhone(){return userPhone;}
    public String getUserPassword(){return userPassword;}
    public boolean isAdmin(){return isAdmin;}
    public String getMessage(){return message;}
    public int getStatus(){return status;}

    //setters
    public void setId(int id){userID = id;}
    public void setName(String name){userName = name;}
    public void setAddress(String address){userAddress = address;}
    public void setEmail(String email){userEmail = email;}
    public void setPhone(int phone){userPhone = phone;}
    public void setPassword(String password){userPassword = password;}
    public void setAdmin(boolean isAdmin){this.isAdmin = isAdmin;}

    @Override
    public String toString(){
        String string = "";

        string += "ID: " + getUserID() + "/n";
        string += "NAME: " + getUserName() + "/n";
        string += "ADDRESS: " + getUserAddress() + "/n";
        string += "PHONE: " + getUserPhone() + "/n";
        string += "EMAIL: " + getUserEmail() + "/n";
        string += "STATUS: " + getStatus() + "/n";
        string += "MESSAGE: " + getMessage() + "/n";

        return string;
    }
}
