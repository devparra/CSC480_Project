package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrderViewActivity extends AppCompatActivity
        implements StatusChangeDialog.StatusChangeSelectedListener,
        DeleteWorkOrderDialog.DeleteWorkOrderSelectedListener,
        AddCommentDialog.AddCommentSelectedListener {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView.workOrder";
    public static final String userCode = "com.CS480.hoa.workOrderView.user";
    public static final String callingActivityCode = "com.CS480.hoa.workOrderView.callingActivity";

    private String callingActivity;

    private User user; //The current user that is viewing the workOrder
    private WorkOrder workOrder;

    private Drawable[] newImages;

    private Button addPhotoButton;
    private Button currentStatusButton;
    private Button creatorInfoButton;
    private Button editorInfoButton;
    private Button returnButton;
    private Button viewPhotos;
    private Button addComment;
    private TextView idTextView;
    private TextView descriptionTextView;
    private TextView creationDateTextView;
    private TextView lastActivityTextView;
    private RecyclerView commentRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_view);

        //retrieve the work order data from the previous activity
        workOrder = (WorkOrder) getIntent().getSerializableExtra(workOrderCode);

        //retrieve the current user that will be viewing the work order
        user = (User) getIntent().getSerializableExtra(userCode);

        //get the calling activity code
        callingActivity = getIntent().getStringExtra(callingActivityCode);

        //set up array to hold many additional photos if needed
        if(workOrder.getAttachedPhotos() == null){
            newImages = new Drawable[10];
        }else {
            newImages = new Drawable[workOrder.getAttachedPhotos().length + 10];
        }

        //assign all objects to variables
        addPhotoButton = findViewById(R.id.workOrderViewAttachPhotoButton);
        currentStatusButton = findViewById(R.id.workOrderViewStatusButton);
        creatorInfoButton = findViewById(R.id.workOrderViewCreatorInfoButton);
        editorInfoButton = findViewById(R.id.workOrderViewEditorInfoButton);
        returnButton = findViewById(R.id.workOrderViewReturnButton);
        addComment = findViewById(R.id.workOrderViewAddCommentsButton);
        idTextView = findViewById(R.id.workOrderIDView);
        descriptionTextView = findViewById(R.id.workOrderDescriptionView);
        creationDateTextView = findViewById(R.id.workOrderCreatedDateView);
        lastActivityTextView = findViewById(R.id.workOrderEditedDateView);
        viewPhotos = findViewById(R.id.workOrderViewPhotoButton);

        //populate the fields with the information from the WorkOrder object
        currentStatusButton.setText(workOrder.getCurrentStatus());

        //The status change feature is only accessed by admin
        if(callingActivity.equals(AdminMainActivity.userCode)){
            currentStatusButton.setEnabled(true);
        }

        //check to ensure that creator is not null
        if(workOrder.getCreator() == null){
            creatorInfoButton.setText("Unknown");
            creatorInfoButton.setEnabled(false);
        }else {
            creatorInfoButton.setText(workOrder.getCreator().getUserName());
        }

        //check to ensure that the editor is not null
        if(workOrder.getEditor() == null){
            editorInfoButton.setText("Unknown");
            editorInfoButton.setEnabled(false);
        }else {
            editorInfoButton.setText(workOrder.getEditor().getUserName());
        }

        idTextView.setText(workOrder.getId());

        descriptionTextView.setText(workOrder.getDescription());

        creationDateTextView.setText(workOrder.getSubmissionDate());

        lastActivityTextView.setText(workOrder.getLastActivityDate());

        //only make photo button visible if there are photos to view
        if(workOrder.getAttachedPhotos() == null || workOrder.getAttachedPhotos().length < 1){
            viewPhotos.setEnabled(false);
            viewPhotos.setVisibility(View.INVISIBLE);
        }

        //Set up recycler view for comments

        commentRecyclerView = findViewById(R.id.workOrderViewCommentRecyclerView);

        //add dividers between each comment
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(commentRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        commentRecyclerView.addItemDecoration(dividerItemDecoration);

        //linear layout for vertical scrolling
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        //send list of comments to the setAdapter

        //**************************************************************************
        //temporary until comments are working
        //****************************************************************************
            String[] testMessage = new String[1];
            testMessage[0] = "No Comments";
            WorkOrderViewActivity.CommentAdapter adapter = new WorkOrderViewActivity.CommentAdapter(testMessage);
            commentRecyclerView.setAdapter(adapter);

            //end of temporary code
            //********************************************************************8



//            //this is the code to keep once things are working
//            WorkOrderViewActivity.CommentAdapter adapter = new WorkOrderViewActivity.CommentAdapter(workOrder.getComments());
//            commentRecyclerView.setAdapter(adapter);



        //onClick listener for all Buttons

        //onClick listener for return button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //onClick listener for creatorInfo button
        creatorInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog with user data displayed
                //use dialog box to display user information
                FragmentManager manager = getSupportFragmentManager();
                ViewUserDialog dialog = new ViewUserDialog().newInstance(workOrder.getCreator());
                dialog.show(manager, "Personal Information");
            }
        });


        //onClick listener for editorInfo button
        editorInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog with user data displayed
                //use dialog box to display user information
                FragmentManager manager = getSupportFragmentManager();
                ViewUserDialog dialog = new ViewUserDialog().newInstance(workOrder.getEditor());
                dialog.show(manager, "Personal Information");
            }
        });


        //onClick listener for status button
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this button should only be enabled if user is admin

                //Dialog to choose which status to set
                FragmentManager manager = getSupportFragmentManager();
                StatusChangeDialog dialog = new StatusChangeDialog();
                dialog.show(manager, "Status Change");
            }
        });


        //onClick for the add Comments button
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                AddCommentDialog dialog = new AddCommentDialog();
                dialog.show(manager, "Add Comment");
            }
        });


        //onClick for adding additional photo
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activate camera to add a photo
            }
        });


        //onClick for viewing attached photos
        viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }//end onCreate





    //This method handles the choice received from the status change dialog
    //This does not save the status change, only changes the view

    @Override
    public void statusChangeChoice(int which) {

        if(which == 0){

            if(!currentStatusButton.getText().toString().equals("Pending")) {

                currentStatusButton.setText(getResources().getString(R.string.pending));
                currentStatusButton.setBackgroundColor(getResources().getColor(R.color.orange));

                workOrder.setCurrentStatus("pending");

                updateData();
            }

        }else if(which == 1){

            if(!currentStatusButton.getText().toString().equals("Approved")) {
                currentStatusButton.setText(getResources().getString(R.string.approved));
                currentStatusButton.setBackgroundColor(getResources().getColor(R.color.green));

                workOrder.setCurrentStatus("approved");

                updateData();
            }

        }else{

            if(currentStatusButton.getText().toString().equals("Denied")) {
                currentStatusButton.setText(getResources().getString(R.string.denied));
                currentStatusButton.setBackgroundColor(getResources().getColor(R.color.red));

                workOrder.setCurrentStatus("denied");

                updateData();
            }
        }
    }//end statusChangeChoice




    //This method handles adding an new comment to the work order
    public void addCommentClick(int which, String newComment){

        //if cancel is selected this method does nothing

        if(which == AlertDialog.BUTTON_POSITIVE) {
            //there is a new message to add
            String[] comments;

            if (workOrder.getComments()[0].equals("No Comments")) {

                //This is the first comment to be added
                comments = new String[1];

                comments[0] = newComment;

            } else {

                comments = new String[workOrder.getComments().length + 1];


                //move all of old comments into the new list
                int count = 0;

                for (String comment : workOrder.getComments()) {
                    comments[count] = comment;
                    count++;
                }

                //add the new comment to the list
                comments[count] = newComment;
            }

            //update the work order
            workOrder.setComments(comments);

            //update the database
            updateData();
        }
    }




    //this handles the response from the delete work order dialog
    public void deleteWorkOrderClick(int which){

        if(which == AlertDialog.BUTTON_POSITIVE){
            deleteData();
        }

    }



    //This takes care of the back button
    @Override
    public void onBackPressed(){

        Intent intent;

        if(callingActivity.equals(HomeOwnerMainActivity.userCode)){

            //Return to the HomeOwnerMain activity
            intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

        }else{

            //Return to the AdminMain activity
            intent = new Intent(getBaseContext(), AdminMainActivity.class);

        }

        //return the current user to the previous activity
        intent.putExtra(callingActivity, user);

        startActivity(intent);

    }





    //this method creates the options menu for the admin user
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_work_order_menu, menu);

        //only allow users to delete their work orders
        if(callingActivity.equals(HomeOwnerMainActivity.userCode)){

            menu.findItem(R.id.deleteWorkOrderMenuItem).setVisible(true);
            menu.findItem(R.id.deleteWorkOrderMenuItem).setEnabled(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //this method handles the menu item that are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.deleteWorkOrderMenuItem:

                FragmentManager manager = getSupportFragmentManager();
                DeleteWorkOrderDialog dialog = new DeleteWorkOrderDialog();
                dialog.show(manager, "Delete Work Order");

                return true;

            default:
               return super.onOptionsItemSelected(item);
        }
    }




    //This method will send the updated data to the web service
    private void updateData(){


        workOrder.setLastActivityDate(new Date());
        workOrder.setEditor(user);

        //send update for workorder to the web service
        //update url to access web service
        Database.changeBaseURL("https://c9tas4uave.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add current workorder data to JsonObject
        json.addProperty("wo_creator", workOrder.getCreator().getUserEmail());
        json.addProperty("wo_admin", workOrder.getEditor().getUserEmail());
        json.addProperty("wo_description", workOrder.getDescription());
        json.addProperty("wo_submissionDate", workOrder.getSubmissionDate());
        json.addProperty("wo_lastActivityDate", workOrder.getLastActivityDate());
        json.addProperty("wo_currentStatus", workOrder.getCurrentStatus());

        ArrayList<String> comments = new ArrayList<>();
        for(String comment : workOrder.getComments()){
            comments.add(comment);
        }
        json.addProperty("wo_comments", String.valueOf(new JSONArray(comments)));

        if(workOrder.getAttachedPhotos() != null && workOrder.getAttachedPhotos().length > 0){

            ArrayList<String> photoList = new ArrayList<>();

            for (Drawable photo : workOrder.getAttachedPhotos()) {

                //separate class for converting to Base64 string
                photoList.add(ConvertImage.convertImageToString(photo));
            }

            json.addProperty("attachedPhotos", String.valueOf(new JSONArray(photoList)));

        }else{
            ArrayList<String> photoList = new ArrayList<>();
            photoList.add("empty");
            json.addProperty("attachedPhotos", String.valueOf(new JSONArray(photoList)));
        }


            //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.updateWorkOrder(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                JsonArray jsonArray = response.body();

                JsonObject object = (JsonObject) jsonArray.get(0);

                String status = object.get("status").toString();
                status = status.replace("\"", "");

                if(status.equals("0")){
                    Toast.makeText(getBaseContext(), "There was a problem with saving to database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "The database was successfully updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure work order view update*******************");
                System.out.println(t.getMessage());
            }
        });

    }




    //this method removes the work order from the database
    private void deleteData(){
        //send update for workorder to the web service
        //update url to access web service
        Database.changeBaseURL("https://5dvcszdpoc.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add current workorder data to JsonObject
        json.addProperty("wo_id", workOrder.getId());

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.deleteWorkOrder(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JsonArray jsonArray = response.body();

                JsonObject object = (JsonObject) jsonArray.get(0);

                String status = object.get("status").toString();
                status = status.replace("\"", "");

                if(status.equals("0")){
                    Toast.makeText(getBaseContext(), "There was a problem with deleting the work order", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "The work order was successfully deleted", Toast.LENGTH_SHORT).show();
                }

                //return to the users main page
                onBackPressed();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


    }



    //This method is used to create each line item in the recycler view
    private class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //TextViews from the list item for display
        private TextView commentTitle;
        private String message;

        //constructor
        public CommentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.work_order_comment_list_item, parent, false));
            itemView.setOnClickListener(this);

            commentTitle = itemView.findViewById(R.id.commentListItem);
        }

        //binding the data from the work order object to the clickable item
        public void bind(String comment){

            message = comment;

            if(comment.length() > 26) {
                commentTitle.setText(comment.substring(0, 25));
            }else{
                commentTitle.setText(comment);
            }
        }

        //onclick Method
        @Override
        public void onClick(View v) {

            //display comment in a dialog
            FragmentManager manager = getSupportFragmentManager();
            CommentViewDialog dialog = new CommentViewDialog(message);
            dialog.show(manager, "show comment");
        }
    }


    //This method takes the list of work orders and generates a holder for each one
    private class CommentAdapter extends RecyclerView.Adapter<WorkOrderViewActivity.CommentHolder>{

        private String[] comments;

        //constructor
        public CommentAdapter(String[] stringList) {

            comments = stringList;
        }

        //create a holder for the workOrder
        @NonNull
        @Override
        public WorkOrderViewActivity.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            return new WorkOrderViewActivity.CommentHolder(layoutInflater, parent);
        }

        //bind the work order information to the holder item
        @Override
        public void onBindViewHolder(@NonNull WorkOrderViewActivity.CommentHolder holder, int position) {
            String comment = comments[position];

            holder.bind(comment);
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            
            return comments.length;
        }
    }
}
