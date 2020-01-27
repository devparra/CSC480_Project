package com.CS480.hoa;

import android.media.Image;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkOrder implements Serializable {

    public enum status{PENDING, DENIED, APPROVED}

    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");

    private String id;
    private User creator; //the initial user that creates the work order
    private User admin;  //the last admin user to make any changes to the work order
    private String description; //users description of work to be done
    private Date submissionDate; //the date the work order was submitted
    private Date lastActivityDate; //the date the last change was made to the work order
    private status currentStatus; //the current status of the work order
    private Image[] attachedPhotos; //a list of all attached photos

    //several different constructors

    //CREATING A NEW WORK ORDER
    //the id will be determined by the database and so we can give it
    //a default value of -99
    //the status will initially be PENDING
    //the submission Date and last activity date are the same here
    //there is no admin editor at this time
    public WorkOrder(User creator, String description, Date submissionDate,
                     Image[] attachedPhotos){
        this.id = "-99";
        this.creator = creator;
        this.admin = null;
        this.description = description;
        this.submissionDate = submissionDate;
        this.lastActivityDate = submissionDate;
        this.currentStatus = status.PENDING;
        this.attachedPhotos = attachedPhotos;
    }

    //RETRIEVING WORK ORDER FROM DATABASE
    //whatever values are stored in the database will be directly placed into each variable
    public WorkOrder(String id, User creator, User admin, String description,
                     Date submissionDate, Date lastActivityDate, status currentStatus,
                     Image[] attachedPhotos){
        this.id = id;
        this.creator = creator;
        this.admin = admin;
        this.description = description;
        this.submissionDate = submissionDate;
        this.lastActivityDate = lastActivityDate;
        this.currentStatus = currentStatus;
        this.attachedPhotos = attachedPhotos;
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
    public User getEditor(){return admin;}
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
    public Image[] getAttachedPhotos(){return attachedPhotos;}

    //setters
    public void setId(String id){this.id = id;}
    public void setCreator(String creator){

        //*********************************************************************
        //this needs to be replaces with pulling user data from the web service
        //*********************************************************************
        this.creator = new User();
    }
    public void setEditor(String admin){

        //*********************************************************************
        //this needs to be replaces with pulling user data from the web service
        //*********************************************************************
        this.admin = new User();
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

    public void setCurrentStatus(String currentStatus){
        if(currentStatus.equals("approved")){
            this.currentStatus = status.APPROVED;
        }else if(currentStatus.equals("denied")){
            this.currentStatus = status.DENIED;
        }else {
            this.currentStatus = status.PENDING;
        }
    }

    public void setAttachedPhotos(Image[] attachedPhotos){this.attachedPhotos = attachedPhotos;}


    //****************************************************************
    //future implementation to obtain actual user from webservice
//
//    private User getUser(String user){
//        //Implementation to retrieve use data from web service
//
//    }


    @Override
    public String toString(){

        String string = "";

        string += "ID: " + getId();
        string += "CREATOR: " + getCreator();
        string += "LAST EDITOR: " + getEditor();
        string += "DESCRIPTION: " + getDescription();
        string += "SUBMIT DATE: " + getSubmissionDate();
        string += "LAST ACTIVITY DATE: " + getLastActivityDate();
        string += "STATUS: " + getCurrentStatus();

        return string;
    }
}
