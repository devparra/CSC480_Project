package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkOrderViewActivity extends AppCompatActivity implements StatusChangeDialog.StatusChangeSelectedListener {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView.workOrder";
    public static final String userCode = "com.CS480.hoa.workOrderView.user";
    public static final String callingActivityCode = "com.CS480.hoa.workOrderView.callingActivity";

    private String callingActivity;

    private User user; //The current user that is viewing the workOrder
    private WorkOrder workOrder;

    private Button addPhototButton;
    private Button currentStatusButton;
    private Button creatorInfoButton;
    private Button editorInfoButton;
    private Button returnButton;
    private TextView idTextView;
    private EditText descriptionEditText;
    private TextView creationDateTextView;
    private TextView lastActivityTextView;
    private TextView attachedPhotoTextView;

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

        //assign all objects to variables
        addPhototButton = findViewById(R.id.workOrderViewAttachPhotoButton);
        currentStatusButton = findViewById(R.id.workOrderViewStatusButton);
        creatorInfoButton = findViewById(R.id.workOrderViewCreatorInfoButton);
        editorInfoButton = findViewById(R.id.workOrderViewEditorInfoButton);
        returnButton = findViewById(R.id.workOrderViewReturnButton);
        idTextView = findViewById(R.id.workOrderIDView);
        descriptionEditText = findViewById(R.id.workOrderDescriptionView);
        creationDateTextView = findViewById(R.id.workOrderCreatedDateView);
        lastActivityTextView = findViewById(R.id.workOrderEditedDateView);
        attachedPhotoTextView = findViewById(R.id.workOrderAttachedPhotoView);

        //populate the fields with the information from the WorkOrder object
        currentStatusButton.setText(workOrder.getCurrentStatus());

        creatorInfoButton.setText(workOrder.getCreator().getUserName());

        editorInfoButton.setText(workOrder.getEditor().getUserName());

        idTextView.setText(workOrder.getId());

        descriptionEditText.setText(workOrder.getDescription());

        creationDateTextView.setText(workOrder.getSubmissionDate());

        lastActivityTextView.setText(workOrder.getLastActivityDate());



        //***********************************************
        //This needs to be replaced with actual images
        //***********************************************
        attachedPhotoTextView.setText("Test data");


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
        addPhototButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activate camera to add a photo
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

                descriptionEditText.setEnabled(true);
                addPhototButton.setVisibility(View.VISIBLE);
                addPhototButton.setEnabled(true);

                menuEdit.setVisible(false);
                menuSave.setVisible(true);
                menuCancel.setVisible(true);

                return true;

            case R.id.editWorkOrderSaveMenu:
                //save the changes that where made to the database

                sendData(workOrder);

                descriptionEditText.setEnabled(false);
                addPhototButton.setVisibility(View.INVISIBLE);
                addPhototButton.setEnabled(false);
                currentStatusButton.setEnabled(false);


                //disable the appropriate fields
                menuEdit.setVisible(true);
                menuSave.setVisible(false);
                menuCancel.setVisible(false);

                return true;

            case R.id.editWorkOrderCancelMenu:


                descriptionEditText.setEnabled(false);
                addPhototButton.setVisibility(View.INVISIBLE);
                addPhototButton.setEnabled(false);
                currentStatusButton.setEnabled(false);


                //disable the appropriate fields
                menuEdit.setVisible(true);
                menuSave.setVisible(false);
                menuCancel.setVisible(false);

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

}
