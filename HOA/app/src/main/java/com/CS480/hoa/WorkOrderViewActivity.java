package com.CS480.hoa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class WorkOrderViewActivity extends AppCompatActivity {

    public static final String workOrderCode = "com.CS480.hoa.workOrderView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_view);
    }
}
