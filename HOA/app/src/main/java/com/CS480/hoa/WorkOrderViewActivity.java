package com.CS480.hoa;

import androidx.annotation.NonNull;
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

import java.util.Date;

public class WorkOrderViewActivity extends AppCompatActivity implements StatusChangeDialog.StatusChangeSelectedListener {

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
    private TextView idTextView;
    private TextView descriptionTextView;
    private TextView creationDateTextView;
    private TextView lastActivityTextView;
    private RecyclerView commentRecyclerView;

    private MenuItem menuEdit;
    private MenuItem menuSave;
    private MenuItem menuCancel;

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
        newImages = new Drawable[workOrder.getAttachedPhotos().length + 10];

        //assign all objects to variables
        addPhotoButton = findViewById(R.id.workOrderViewAttachPhotoButton);
        currentStatusButton = findViewById(R.id.workOrderViewStatusButton);
        creatorInfoButton = findViewById(R.id.workOrderViewCreatorInfoButton);
        editorInfoButton = findViewById(R.id.workOrderViewEditorInfoButton);
        returnButton = findViewById(R.id.workOrderViewReturnButton);
        idTextView = findViewById(R.id.workOrderIDView);
        descriptionTextView = findViewById(R.id.workOrderDescriptionView);
        creationDateTextView = findViewById(R.id.workOrderCreatedDateView);
        lastActivityTextView = findViewById(R.id.workOrderEditedDateView);
        viewPhotos = findViewById(R.id.workOrderViewPhotoButton);

        //populate the fields with the information from the WorkOrder object
        currentStatusButton.setText(workOrder.getCurrentStatus());

        creatorInfoButton.setText(workOrder.getCreator().getUserName());

        editorInfoButton.setText(workOrder.getEditor().getUserName());

        idTextView.setText(workOrder.getId());

        descriptionTextView.setText(workOrder.getDescription());

        creationDateTextView.setText(workOrder.getSubmissionDate());

        lastActivityTextView.setText(workOrder.getLastActivityDate());

        //only make photo button visible if there are photos to view
        if(workOrder.getAttachedPhotos().length < 1){
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

            currentStatusButton.setText(getResources().getString(R.string.pending));
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.orange));

        }else if(which == 1){

            currentStatusButton.setText(getResources().getString(R.string.approved));
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.green));

        }else{

            currentStatusButton.setText(getResources().getString(R.string.denied));
            currentStatusButton.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }





    //This takes care of the back button
    @Override
    public void onBackPressed(){

        Intent intent;

        if(callingActivityCode.equals(HomeOwnerMainActivity.userCode)){

            //Return to the HomeOwnerMain activity
            intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

        }else{

            //Return to the AdminMain activity
            intent = new Intent(getBaseContext(), AdminMainActivity.class);

        }

        //return the current user to the previous activity
        intent.putExtra(callingActivityCode, user);

        startActivity(intent);

    }





    //this method creates the options menu for the view work order
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_work_order_menu, menu);

        menuEdit = menu.findItem(R.id.editWorkOrderMenu);
        menuSave = menu.findItem(R.id.editWorkOrderSaveMenu);
        menuCancel = menu.findItem(R.id.editWorkOrderCancelMenu);

        return super.onCreateOptionsMenu(menu);
    }





    //this method handles the menu item that are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.editWorkOrderMenu:
                //enable the appropriate fields for editing

                //only enable the status button if the previous activity was admin main
                if(callingActivity.equals(AdminMainActivity.userCode)){

                    currentStatusButton.setEnabled(true);
                }

                addPhotoButton.setVisibility(View.VISIBLE);
                addPhotoButton.setEnabled(true);

                menuEdit.setVisible(false);
                menuSave.setVisible(true);
                menuCancel.setVisible(true);

                return true;

            case R.id.editWorkOrderSaveMenu:
                //save the changes that where made to the database

                workOrder.setLastActivityDate(new Date());
                workOrder.setEditor(user);

                if(currentStatusButton.getText().toString().equals(getResources().getString(R.string.pending))){
                    workOrder.setCurrentStatus("pending");
                }else if(currentStatusButton.getText().toString().equals(getResources().getString(R.string.approved))){
                    workOrder.setCurrentStatus("approved");
                }else {
                    workOrder.setCurrentStatus("denied");
                }

                //if new photos have been added
                if(newImages.length > 0){
                    Drawable[] temp = workOrder.getAttachedPhotos();

                    for(int i = 0; i < newImages.length; i++){
                        temp[i + temp.length] = newImages[i];
                    }

                    workOrder.setAttachedPhotos(temp);
                }

                //send the saved changes to the web service
                sendData(workOrder);

                //disable input options
                addPhotoButton.setVisibility(View.INVISIBLE);
                addPhotoButton.setEnabled(false);
                currentStatusButton.setEnabled(false);


                //update menu choices
                menuEdit.setVisible(true);
                menuSave.setVisible(false);
                menuCancel.setVisible(false);

                return true;

            case R.id.editWorkOrderCancelMenu:


                //disable input options
                addPhotoButton.setVisibility(View.INVISIBLE);
                addPhotoButton.setEnabled(false);
                currentStatusButton.setEnabled(false);


                //update menu choices
                menuEdit.setVisible(true);
                menuSave.setVisible(false);
                menuCancel.setVisible(false);

                //return any changed values to the original values
                if(workOrder.getCurrentStatus().equals("pending")){
                    currentStatusButton.setText(getResources().getString(R.string.pending));
                    currentStatusButton.setBackgroundColor(getResources().getColor(R.color.orange));
                }else if(workOrder.getCurrentStatus().equals("approved")){
                    currentStatusButton.setText(getResources().getString(R.string.approved));
                    currentStatusButton.setBackgroundColor(getResources().getColor(R.color.green));
                }else{
                    currentStatusButton.setText(getResources().getString(R.string.denied));
                    currentStatusButton.setBackgroundColor(getResources().getColor(R.color.red));
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }





    //This method will send the updated data to the web service
    private void sendData(WorkOrder workOrder){

        //First check to see if any changes were made


        //There are changes, send data to web service


    }





    //This method is used to create each line item in the recycler view
    private class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //TextViews from the list item for display
        private TextView commentTitle;

        //constructor
        public CommentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.work_order_list_item, parent, false));
            itemView.setOnClickListener(this);

            commentTitle = findViewById(R.id.commentListItem);
        }

        //binding the data from the work order object to the clickable item
        public void bind(String comment){

            commentTitle.setText(comment.substring(0,20));

        }

        //onclick Method
        @Override
        public void onClick(View v) {

            //when a work order is selected display its information
            Intent intent = new Intent(getBaseContext(), WorkOrderViewActivity.class);

            Bundle bundle = new Bundle();

            //add necessary data to the bundle
            bundle.putSerializable(WorkOrderViewActivity.workOrderCode, workOrder);
            bundle.putSerializable(WorkOrderViewActivity.userCode, user);
            bundle.putString(WorkOrderViewActivity.callingActivityCode, AdminMainActivity.userCode);

            //append bundle to intent
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }


    //This method takes the list of work orders and generates a holder for each one
    private class CommentAdapter extends RecyclerView.Adapter<WorkOrderViewActivity.CommentHolder>{

        private String[] comments;

        //constructor
        public CommentAdapter(String[] comments){

            this.comments = comments;
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
