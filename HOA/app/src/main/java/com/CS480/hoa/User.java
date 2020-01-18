package com.CS480.hoa;

import java.io.Serializable;

public class User implements Serializable {

    private String message = "";
    private String status = "-1";
    private String userName;
    private String userAddress;
    private String userEmail;
    private String userPhone;
    private String userPassword;
    private boolean isAdmin;

    //constructors

    //This constructor is used for creating a new user and includes
    //the password that the user inputs
    public User(String name, String address, String email, String phone, String password){
        userName = name;
        userAddress = address;
        userEmail = email;
        userPhone = phone;
        userPassword = password;
        this.isAdmin = false;
    }


    //This constructor is user for creating a user during login and
    //does not include the password or isAdmin
    public User(String name, String address, String email, String phone, String status, String message){
        userName = name;
        userAddress = address;
        userEmail = email;
        userPhone = phone;
        userPassword = "";
        this.status = status;
        this.message = message;
    }


    //This constructor is used for creating a user for the purpose of
    //capturing a status and message response from the web service
    public User(String status, String message){
        this.status = status;
        this.message = message;
    }

    //getters
    public String getUserName(){return userName;}
    public String getUserAddress(){return userAddress;}
    public String getUserEmail(){return userEmail;}
    public String getUserPhone(){return userPhone;}
    public String getUserPassword(){return userPassword;}
    public boolean isAdmin(){return isAdmin;}
    public String getMessage(){return message;}
    public String getStatus(){return status;}

    //setters
    public void setName(String name){userName = name;}
    public void setAddress(String address){userAddress = address;}
    public void setEmail(String email){userEmail = email;}
    public void setPhone(String phone){userPhone = phone;}
    public void setPassword(String password){userPassword = password;}
    public void setAdmin(boolean isAdmin){this.isAdmin = isAdmin;}

    @Override
    public String toString(){
        String string = "";

        string += "NAME: " + getUserName() + "\n";
        string += "ADDRESS: " + getUserAddress() + "\n";
        string += "PHONE: " + getUserPhone() + "\n";
        string += "EMAIL: " + getUserEmail() + "\n";
        string += "STATUS: " + getStatus() + "\n";
        string += "MESSAGE: " + getMessage() + "\n";
        string += "IS ADMIN: " + isAdmin() + "\n";

        return string;
    }
}
