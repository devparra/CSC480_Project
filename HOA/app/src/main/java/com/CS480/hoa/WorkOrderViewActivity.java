package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.FileInputStream;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CS480.hoa.CreateNewWorkOrderActivity.MAXPHOTOS;

public class WorkOrderViewActivity extends AppCompatActivity
        implements StatusChangeDialog.StatusChangeSelectedListener,
        DeleteWorkOrderDialog.DeleteWorkOrderSelectedListener,
        AddCommentDialog.AddCommentSelectedListener {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView.workOrder";
    public static final String userCode = "com.CS480.hoa.workOrderView.user";
    public static final String callingActivityCode = "com.CS480.hoa.workOrderView.callingActivity";
    //private final int requestImageCapture = 1;

    private String callingActivity;

    private User user; //The current user that is viewing the workOrder
    private WorkOrder workOrder;

    private String[] newImages;

   // private Button addPhotoButton;
    private Button currentStatusButton;
    private Button creatorInfoButton;
    private Button editorInfoButton;
    private Button returnButton;
    private Button viewPhotosButton;
    private Button addCommentButton;
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
        //MAXPHOTOS is in the create new work order activity
        newImages = new String[MAXPHOTOS];

        //assign all objects to variables
        //addPhotoButton = findViewById(R.id.workOrderViewAttachPhotoButton);
        viewPhotosButton = findViewById(R.id.workOrderViewPhotoButton);
        currentStatusButton = findViewById(R.id.workOrderViewStatusButton);
        creatorInfoButton = findViewById(R.id.workOrderViewCreatorInfoButton);
        editorInfoButton = findViewById(R.id.workOrderViewEditorInfoButton);
        returnButton = findViewById(R.id.workOrderViewReturnButton);
        addCommentButton = findViewById(R.id.workOrderViewAddCommentsButton);
        idTextView = findViewById(R.id.workOrderIDView);
        descriptionTextView = findViewById(R.id.workOrderDescriptionView);
        creationDateTextView = findViewById(R.id.workOrderCreatedDateView);
        lastActivityTextView = findViewById(R.id.workOrderEditedDateView);

        //populate the fields with the information from the WorkOrder object
        currentStatusButton.setText(workOrder.getCurrentStatus());

        if(workOrder.getCurrentStatus().equals("Pending")){
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.orange));
        } else if(workOrder.getCurrentStatus().equals("Approved")){
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.red));
        }

        //The status change feature is only accessed by admin
        if(callingActivity.equals(AdminMainActivity.userCode)){
            currentStatusButton.setEnabled(true);
        }

//        //if the work order has the maximum photos, remove option to add more
//        //MAXPHOTOS is in the CreateNewWorkOrderActivity
//        if(workOrder.getAttachedPhotos().length == MAXPHOTOS){
//            addPhotoButton.setEnabled(false);
//            addPhotoButton.setVisibility(View.INVISIBLE);
//        }

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
        if(workOrder.getAttachedPhotos() == null){
            viewPhotosButton.setEnabled(false);
            viewPhotosButton.setVisibility(View.INVISIBLE);
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
        WorkOrderViewActivity.CommentAdapter adapter = new WorkOrderViewActivity.CommentAdapter(workOrder.getComments());
        commentRecyclerView.setAdapter(adapter);



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
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                AddCommentDialog dialog = new AddCommentDialog();
                dialog.show(manager, "Add Comment");
            }
        });


//        //onClick for adding additional photo
//        addPhotoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //activate camera to add a photo
//
//                //MAXPHOTOS is in the CreateNewWorkOrderActivity
//                if(workOrder.getAttachedPhotos().length == MAXPHOTOS){
//                    //the work order already has the maximum number of photos
//                    Toast.makeText(getBaseContext(),"This work order has the maximum photos", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    //There is room for more photos
//                    startCamera();
//
//                }
//
//            }
//        });


        //onClick for viewing attached photos
        viewPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //display the photos
                Intent intent = new Intent(getBaseContext(), ViewPhotoActivity.class);

                intent.putExtra(ViewPhotoActivity.photoCode, workOrder.getAttachedPhotos());

                startActivity(intent);
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

                workOrder.setCurrentStatus("Pending");

                updateData();
            }

        }else if(which == 1){

            if(!currentStatusButton.getText().toString().equals("Approved")) {
                currentStatusButton.setText(getResources().getString(R.string.approved));
                currentStatusButton.setBackgroundColor(getResources().getColor(R.color.green));

                workOrder.setCurrentStatus("Approved");

                updateData();
            }

        }else{

            if(!currentStatusButton.getText().toString().equals("Denied")) {
                currentStatusButton.setText(getResources().getString(R.string.denied));
                currentStatusButton.setBackgroundColor(getResources().getColor(R.color.red));

                workOrder.setCurrentStatus("Denied");

                updateData();
            }
        }
    }//end statusChangeChoice




    //This method handles adding an new comment to the work order
    public void addCommentClick(int which, String newComment){

        //test to ensure comment is not blank
        if(!newComment.isEmpty()) {

            //if cancel is selected this method does nothing

            if (which == AlertDialog.BUTTON_POSITIVE) {
                //there is a new message to add
                String[] comments;

                if (workOrder.getComments()[0].equals("No Comments")) {

                    //This is the first comment to be added
                    comments = new String[1];

                    comments[0] = newComment;

                } else {
                    //There are already comments for this work order

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

        } else {

            //the comment was empty
            Toast.makeText(getBaseContext(), "Cannot submit a blank comment", Toast.LENGTH_SHORT).show();
        }

    }//end of addCommentClick








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
        getMenuInflater().inflate(R.menu.delete_menu, menu);

//        //only allow users to delete their work orders
//        if(callingActivity.equals(AdminMainActivity.userCode)){
//
//            menu.findItem(R.id.deleteWorkOrderMenuItem).setVisible(false);
//            menu.findItem(R.id.deleteWorkOrderMenuItem).setEnabled(false);
//        }

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






    //this handles the response from the delete work order dialog
    public void deleteWorkOrderClick(int which){

        if(which == AlertDialog.BUTTON_POSITIVE){
            deleteData();
        }

    }






    //This method will send the updated data to the web service
    private void updateData(){

        //update the last activity date
        workOrder.setLastActivityDate(new Date());
        lastActivityTextView.setText(workOrder.getLastActivityDate());

        //update the last person to make changes to the work order
        workOrder.setEditor(user);
        editorInfoButton.setText(workOrder.getEditor().getUserName());

        //update the comments list
        WorkOrderViewActivity.CommentAdapter adapter = new WorkOrderViewActivity.CommentAdapter(workOrder.getComments());
        commentRecyclerView.setAdapter(adapter);

        //send update for workorder to the web service
        //update url to access web service
        Database.changeBaseURL("https://c9tas4uave.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add current workorder data to JsonObject
        json.addProperty("wo_id", workOrder.getId());
        json.addProperty("wo_creator", workOrder.getCreator().getUserEmail());
        json.addProperty("wo_admin", workOrder.getEditor().getUserEmail());
        json.addProperty("wo_description", workOrder.getDescription());
        json.addProperty("wo_submissionDate", workOrder.getSubmissionDate());
        json.addProperty("wo_lastActivityDate", workOrder.getLastActivityDate());
        json.addProperty("wo_currentStatus", workOrder.getCurrentStatus());

        JsonArray comments = new JsonArray();
        for(String comment : workOrder.getComments()){
            comments.add(comment);
        }
        json.add("wo_comments", comments);


        if(workOrder.getAttachedPhotos() != null && workOrder.getAttachedPhotos().length > 0){

            JsonArray photoList = new JsonArray();

            for (int i = 0; i < workOrder.getAttachedPhotos().length; i++) {



                System.out.println(workOrder.getAttachedPhotos()[i]);



                photoList.add(workOrder.getAttachedPhotos()[i]);
            }

            json.add("wo_attachedPhotos", photoList);

        }else{
            JsonArray photoList = new JsonArray();
            photoList.add("empty");
            json.add("wo_attachedPhotos", photoList);
        }


            //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.updateWorkOrder(json);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                JsonArray jsonArray = response.body();

                JsonObject object = jsonArray.get(0).getAsJsonObject();

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

                System.out.println("Failure in WorkOrderView Delete*********************");
                System.out.println(t.getMessage());
            }
        });


    }//end deleteData






    //This class is used to create each line item in the recycler view
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

            commentTitle.setText(comment);

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


    //This class takes the list of work orders and generates a holder for each one
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
