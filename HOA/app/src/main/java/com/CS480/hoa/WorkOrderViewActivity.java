package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WorkOrderViewActivity extends AppCompatActivity {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView.workOrder";
    public static final String userCode = "com.CS480.hoa.workOrderView.user";

    private User user; //The current user that is viewing the workOrder
    private WorkOrder workOrder;

    private Button addPhototButton;
    private Button currentStatusButton;
    private Button creatorInfoButton;
    private Button editorInfoButton;
    private TextView idTextView;
    private EditText descriptionEditText;
    private TextView creationDateTextView;
    private TextView lastActivityTextView;
    private TextView attachedPhotoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_view);

        //retrieve the work order data from the previous activity
        workOrder = (WorkOrder) getIntent().getSerializableExtra(workOrderCode);

        //retrieve the current user that will be viewing the work order
        user = (User) getIntent().getSerializableExtra(userCode);

        //assign all objects to variables
        addPhototButton = findViewById(R.id.workOrderViewAttachPhotoButton);
        currentStatusButton = findViewById(R.id.workOrderViewStatusButton);
        creatorInfoButton = findViewById(R.id.workOrderViewCreatorInfoButton);
        editorInfoButton = findViewById(R.id.workOrderViewEditorInfoButton);
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

        attachedPhotoTextView.setText("Test data");


    }


    //this method creates the options menu for the view work order
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_work_order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //this method handles the menu item that are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.editWorkOrderButton:
                //enable the appropriate fields for editing

                return true;

            case R.id.editWorkOrderSaveButton:
                //save the changes that where made to the database

                return true;

            case R.id.editWorkOrderCancelButton:
                //disable the appropriate fields

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
