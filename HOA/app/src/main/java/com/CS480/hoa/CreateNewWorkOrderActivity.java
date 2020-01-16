package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CreateNewWorkOrderActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.createNewWorkOrder";

    private WorkOrder workOrder;
    private User user;
    private ArrayList<Image> attachedPhotos;

    private EditText description;
    private Button saveButton;
    private Button cancelButton;
    private Button attachPhotoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_work_order);

        user = (User) getIntent().getSerializableExtra(userCode);
        description = findViewById(R.id.workOrderCreateDescriptionInput);
        saveButton = findViewById(R.id.workOrderCreateSaveButton);
        cancelButton = findViewById(R.id.workOrderCreateCancelButton);
        attachPhotoButton = findViewById(R.id.workOrderCreateAttachPhotoButton);

        //save button onclick listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //cancel button onclick listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //return to home owner main activity
                Intent intent = new Intent(getBaseContext(), HomeOwnerMainActivity.class);

                //send user data to new activity
                intent.putExtra(HomeOwnerMainActivity.userCode, user);

                startActivity(intent);
            }
        });

        //attach photo button onclick listener
        attachPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
