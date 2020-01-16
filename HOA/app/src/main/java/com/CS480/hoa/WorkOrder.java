package com.CS480.hoa;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class WorkOrder implements Serializable {

    public enum status{PENDING, DENIED, APPROVED}

    private int id;
    private User creator; //the initial user that creates the work order
    private User admin;  //the last admin user to make any changes to the work order
    private String description; //users description of work to be done
    private Date submissionDate; //the date the work order was submitted
    private Date lastActivityDate; //the date the last change was made to the work order
    private status currentStatus; //the current status of the work order
    private ArrayList<Image> attachedPhotos; //a list of all attached photos

    //several different constructors

    //CREATING A NEW WORK ORDER
    //the id will be determined by the database and so we can give it
    //a default value of -99
    //the status will initially be PENDING
    //the submission Date and last activity date are the same here
    //there is no admin editor at this time
    public WorkOrder(User creator, String description, Date submissionDate,
                     ArrayList<Image> attachedPhotos){
        this.id = -99;
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
    public WorkOrder(int id, User creator, User admin, String description,
                     Date submissionDate, Date lastActivityDate, status currentStatus,
                     ArrayList<Image> attachedPhotos){
        this.id = id;
        this.creator = creator;
        this.admin = admin;
        this.description = description;
        this.submissionDate = submissionDate;
        this.lastActivityDate = lastActivityDate;
        this.currentStatus = currentStatus;
        this.attachedPhotos = attachedPhotos;
    }

    //getters
    public int getId(){return id;}
    public User getCreator(){return creator;}
    public User getEditor(){return admin;}
    public String getDescription(){return description;}
    public Date getSubmissionDate(){return submissionDate;}
    public Date getLastActivityDate(){return lastActivityDate;}
    public status getCurrentStatus(){return currentStatus;}
    public ArrayList<Image> getAttachedPhotos(){return attachedPhotos;}

    //setters
    public void setId(int id){this.id = id;}
    public void setCreator(User creator){this.creator = creator;}
    public void setEditor(User admin){this.admin = admin;}
    public void setDescription(String description){this.description = description;}
    public void setSubmissionDate(Date submissionDate){this.submissionDate = submissionDate;}
    public void setLastActivityDate(Date lastActivityDate){this.lastActivityDate = lastActivityDate;}
    public void setCurrentStatus(status currentStatus){this.currentStatus = currentStatus;}
    public void setAttachedPhotos(ArrayList<Image> attachedPhotos){this.attachedPhotos = attachedPhotos;}
}
