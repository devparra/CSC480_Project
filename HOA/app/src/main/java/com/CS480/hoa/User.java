package com.CS480.hoa;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String name;
    private String address;
    private String email;
    private int phone;
    private boolean isAdmin;

    //constructor
    public User(int id, String name, String address, String email, int phone, boolean isAdmin){
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    //getters
    public int getId(){return id;}
    public String getName(){return name;}
    public String getAddress(){return address;}
    public String getEmail(){return email;}
    public int getPhone(){return phone;}
    public boolean isAdmin(){return isAdmin;}

    //setters
    public void setId(int id){this.id = id;}
    public void setName(String name){this.name = name;}
    public void setAddress(String address){this.address = address;}
    public void setEmail(String email){this.email = email;}
    public void setPhone(int phone){this.phone = phone;}
    public void setAdmin(boolean isAdmin){this.isAdmin = isAdmin;}
}
