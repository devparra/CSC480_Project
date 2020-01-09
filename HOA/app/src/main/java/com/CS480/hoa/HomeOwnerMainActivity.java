package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class HomeOwnerMainActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.homeOwnerMain";

    private RecyclerView recyclerView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner_main);

        user = (User) getIntent().getSerializableExtra(userCode);

        recyclerView = findViewById(R.id.recyclerViewHomeOwnerMain);

        //add dividers between each address
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //linear layout for vertical scrolling
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get addresses from the database and setAdapter
        WorkOrderAdapter adapter = new WorkOrderAdapter(Database.db.getWorkOrder(user));
        recyclerView.setAdapter(adapter);

    }

    private class WorkOrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //the current work order object
        private WorkOrder workOrder;

        //TextView to place the name of contact in for display
        private TextView workOrderName;

        //constructor
        public WorkOrderHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.work_order_list_item, parent, false));
            itemView.setOnClickListener(this);
            workOrderName = itemView.findViewById(R.id.workOrderListItem);
        }

        //binding the data from the work order object to the clickable item
        public void bind(WorkOrder wo){
            workOrder = wo;

            workOrderName.setText(workOrder.getId());

        }

        //onclick Method
        @Override
        public void onClick(View v) {

            //when a work order is selecred display its information
            Intent intent = new Intent(getBaseContext(), WorkOrderViewActivity.class);

            //send the current work order to the display activity
            intent.putExtra(WorkOrderViewActivity.workOrderCode, workOrder);

            startActivity(intent);
        }
    }

    private class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderHolder>{

        private List<WorkOrder> workOrderList;

        //constructor
        public WorkOrderAdapter(List<WorkOrder> workOrderList){this.workOrderList = workOrderList;}

        //create a holder for the workOrder
        @NonNull
        @Override
        public WorkOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            return new WorkOrderHolder(layoutInflater, parent);
        }

        //bind the work order information to the holder item
        @Override
        public void onBindViewHolder(@NonNull WorkOrderHolder holder, int position) {
            WorkOrder workOrder = workOrderList.get(position);
            holder.bind(workOrder);
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            return workOrderList.size();
        }
    }


}
