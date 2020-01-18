package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeOwnerMainActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.homeOwnerMain";

    private RecyclerView recyclerView;
    private User user;
    private Button newWorkOrderButton;
    private WorkOrder[] workOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner_main);

        //retrieve the user from the previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        newWorkOrderButton = findViewById(R.id.createNewWorkOrderButton);

        //new work order button onclick listener
        newWorkOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to create new work order activity
                Intent intent = new Intent(getBaseContext(), CreateNewWorkOrderActivity.class);

                //pass the user data to new activity
                intent.putExtra(CreateNewWorkOrderActivity.userCode, user);

                startActivity(intent);
            }
        });




        //get the list of workorders from web service

        //update url to access web service
        Database.changeBaseURL("");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add user email to JsonObject
        //This will be used to look up all workorders created by this user
        json.addProperty("email", user.getUserEmail());

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getWorkOrders(json);

        //background thread to communicate with the web service
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                //JsonArray object to store the response from web service
                JsonArray jsonArray = response.body();

                //parse the JsonArray into WorkOrder objects
                workOrders = new Gson().fromJson(jsonArray, WorkOrder[].class);





                //because the Recyclerview requires the workOrders to be implemented correctly
                //the Recyclerview initialization will be in the onResponse method

                //recycler view initialization and implementation
                recyclerView = findViewById(R.id.recyclerViewHomeOwnerMain);

                //add dividers between each address
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);

                //linear layout for vertical scrolling
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                //send list of workorder to the setAdapter
                WorkOrderAdapter adapter = new WorkOrderAdapter(workOrders);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });






    }

    private class WorkOrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //the current work order object
        private WorkOrder workOrder;

        //TextViews from the list item for display
        private TextView workOrderId;
        private TextView workOrderDate;
        private TextView workOrderStatus;

        //constructor
        public WorkOrderHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.work_order_list_item, parent, false));
            itemView.setOnClickListener(this);
            workOrderId = itemView.findViewById(R.id.workOrderListItemId);
        }

        //binding the data from the work order object to the clickable item
        public void bind(WorkOrder wo){
            workOrder = wo;

            workOrderId.setText(workOrder.getId());
            workOrderDate.setText(workOrder.getSubmissionDate().toString());
            workOrderStatus.setText(workOrder.getCurrentStatus().toString());

        }

        //onclick Method
        @Override
        public void onClick(View v) {

            //when a work order is selected display its information
            Intent intent = new Intent(getBaseContext(), WorkOrderViewActivity.class);

            //send the current work order to the display activity
            intent.putExtra(WorkOrderViewActivity.workOrderCode, workOrder);

            startActivity(intent);
        }
    }

    private class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderHolder>{

        private WorkOrder[] workOrderList;

        //constructor
        public WorkOrderAdapter(WorkOrder[] workOrderList){this.workOrderList = workOrderList;}

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
            WorkOrder workOrder = workOrderList[position];
            holder.bind(workOrder);
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            return workOrderList.length;
        }
    }


}
