package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WorkOrderViewActivity extends AppCompatActivity {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView";
    public static final String userCode = "com.CS480.hoa.workOrderView";

    private User user;
    private WorkOrder workOrder;

    private Button editWorkOrderButton;
    private Button addPhototButton;
    private Button cancelButton;
    private Button currentStatusButton;
    private TextView idTextView;
    private EditText descriptionEditText;
    private TextView creationDateTextView;
    private TextView lastActivityTextView;
    private TextView attachedPhotoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_view);
    }
}
