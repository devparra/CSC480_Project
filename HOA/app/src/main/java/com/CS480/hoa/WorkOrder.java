package com.CS480.hoa;

import android.graphics.Bitmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrder implements Serializable {

    public enum status{PENDING, DENIED, APPROVED}

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private String id;
    private User creator; //the initial user that creates the work order
    private User editor;  //the last admin user to make any changes to the work order
    private String description; //users description of work to be done
    private Date submissionDate; //the date the work order was submitted
    private Date lastActivityDate; //the date the last change was made to the work order
    private status currentStatus; //the current status of the work order
    private String[] comments;
    private String[] attachedPhotos; //a list of all attached photos

    //several different constructors

    //CREATING A NEW WORK ORDER
    //the id will be determined by the database and so we can give it
    //a default value of -99
    //the status will initially be PENDING
    //the submission Date and last activity date are the same here
    //there is no admin editor at this time
    public WorkOrder(User creator, String description, Date submissionDate,
                     String[] attachedPhotos){
        this.id = "-99";
        this.creator = creator;
        this.editor = creator;
        this.description = description;
        this.submissionDate = submissionDate;
        this.lastActivityDate = submissionDate;
        this.currentStatus = status.PENDING;
        this.attachedPhotos = attachedPhotos;

        String[] comment = new String[1];
        comment[0] = "No comments";

        this.comments = comment;
    }

    //RETRIEVING WORK ORDER FROM DATABASE
    //whatever values are stored in the database will be directly placed into each variable
    public WorkOrder(String id, String creator, String editor, String description,
                     String submissionDate, String lastActivityDate, String currentStatus,
                     String[] comments, String[] attachedPhotos){
        this.id = id;
        setCreator(creator);
        setEditor(editor);
        this.description = description;
        setSubmissionDate(submissionDate);
        setLastActivityDate(lastActivityDate);
        setCurrentStatus(currentStatus);
        setComments(comments);
        setAttachedPhotos(attachedPhotos);
    }

    public WorkOrder(String id, String creator, String admin, String description,
              String submissionDate, String lastActivityDate, String currentStatus){
        this.id = id;
        setCreator(creator);
        setEditor(admin);
        this.description = description;
        setSubmissionDate(submissionDate);
        setLastActivityDate(lastActivityDate);
        setCurrentStatus(currentStatus);
    }

    //getters
    public String getId(){return id;}
    public User getCreator(){return creator;}
    public User getEditor(){return editor;}
    public String getDescription(){return description;}
    public String getSubmissionDate(){

        String date = dateFormat.format(submissionDate);

        return date;
    }
    public String getLastActivityDate() {

        String date = dateFormat.format(lastActivityDate);

        return date;
    }
    public String getCurrentStatus(){
        if(currentStatus == status.APPROVED)
            return "Approved";
        else if(currentStatus == status.DENIED)
            return "Denied";
        else
            return "Pending";

    }
    public String[] getComments(){return comments;}
    public String[] getAttachedPhotos(){return attachedPhotos;}




    //setters
    public void setId(String id){this.id = id;}
    public void setCreator(String creator){

        getUser(creator, "creator");
    }

    public void setEditor(String editor){

        getUser(editor, "editor");
    }

    public void setEditor(User user){
        this.editor = user;
    }

    public void setDescription(String description){this.description = description;}

    public void setSubmissionDate(String submissionDate){
        try {
            this.submissionDate = dateFormat.parse(submissionDate);
        }catch(ParseException error){
            try {
                this.submissionDate = dateFormat.parse("00-00-0000");
            }catch(ParseException error2){}
            System.out.println(error.getMessage());
        }
    }

    public void setLastActivityDate(String lastActivityDate){
        try {
            this.lastActivityDate = dateFormat.parse(lastActivityDate);
        }catch(ParseException error){
            try {
                this.lastActivityDate = dateFormat.parse("00-00-0000");
            }catch(ParseException error2){}
            System.out.println(error.getMessage());
        }
    }

    public void setLastActivityDate(Date date){
        this.lastActivityDate = date;
    }

    public void setCurrentStatus(String currentStatus){
        if(currentStatus.equals("Approved")){
            this.currentStatus = status.APPROVED;
        }else if(currentStatus.equals("Denied")){
            this.currentStatus = status.DENIED;
        }else {
            this.currentStatus = status.PENDING;
        }
    }

    public void setComments(String[] comments){this.comments = comments;}
    public void setAttachedPhotos(String[] attachedPhotos){this.attachedPhotos = attachedPhotos;}


    private void getUser(String email, String whichUser){
        //Implementation to retrieve use data from web service

        //update url to access web service
        Database.changeBaseURL("https://7d0xmalo45.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add user email to JsonObject
        //This will be used to look up all workorders created by this user
        json.addProperty("userEmail", email);

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getUser(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                JsonArray jsonArray = response.body();

                //create a user object to assign to this attribute
                if(whichUser.equals("creator")) {
                    creator = createUser(jsonArray);
                }else{
                    editor = createUser(jsonArray);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure in WorkOrder class getUser method*********************");
                System.out.println(t.getMessage());
            }
        });
    }



    //this method takes a JsonArray, retrieves the data and uses it to create a User object
    private User createUser(JsonArray jsonArray){

        JsonObject object = jsonArray.get(0).getAsJsonObject();

        if(object.size() > 2) {

            String name = object.get("userName").toString();
            name = name.replace("\"", "");

            String address = object.get("userAddress").toString();
            address = address.replace("\"", "");

            String phone = object.get("userPhone").toString();
            phone = phone.replace("\"", "");

            String email = object.get("userEmail").toString();
            email = email.replace("\"", "");


            User newUser = new User(name, address, email, phone);


            return newUser;

        } else{


            System.out.println("Returning Null from WorkOrder createUser ******************************");
            return null;
        }

    }//end of getUser


    @Override
    public String toString(){

        String string = "";

        string += "ID: " + getId() + "\n";
        string += "CREATOR: " + getCreator() + "\n";
        string += "LAST EDITOR: " + getEditor() + "\n";
        string += "DESCRIPTION: " + getDescription() + "\n";
        string += "SUBMIT DATE: " + getSubmissionDate() + "\n";
        string += "LAST ACTIVITY DATE: " + getLastActivityDate() + "\n";
        string += "STATUS: " + getCurrentStatus() + "\n";
        string += "Comments: \n";
        for(String comment : comments){
            string += comment + "\n";
        }
        string += "Photos: " + getAttachedPhotos();

        return string;
    }
}
