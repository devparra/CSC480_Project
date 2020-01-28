package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMainActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.adminMain";

    //request code used for child activities returning to this activity
    //private final int adminMainRequestCode = 2;

    private RecyclerView recyclerView;
    private TextView blankListTextView;
    private User user;
    private WorkOrder[] workOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        //retrieve the user from the previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        blankListTextView = findViewById(R.id.adminMainBlankListTextView);


        //get the list of workorders from web service
        getData();

    }


    //This method is used to create each line item in the recycler view
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
            workOrderDate = itemView.findViewById(R.id.workOrderListItemDate);
            workOrderStatus = itemView.findViewById(R.id.workOrderListItemStatus);
        }

        //binding the data from the work order object to the clickable item
        public void bind(WorkOrder wo){
            workOrder = wo;

            workOrderId.setText(workOrder.getId());
            workOrderDate.setText(workOrder.getSubmissionDate());
            workOrderStatus.setText(workOrder.getCurrentStatus());

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
    private class WorkOrderAdapter extends RecyclerView.Adapter<AdminMainActivity.WorkOrderHolder>{

        private WorkOrder[] workOrderList;

        //constructor
        public WorkOrderAdapter(WorkOrder[] workOrderList){

            this.workOrderList = workOrderList;
        }

        //create a holder for the workOrder
        @NonNull
        @Override
        public AdminMainActivity.WorkOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
            return new AdminMainActivity.WorkOrderHolder(layoutInflater, parent);
        }

        //bind the work order information to the holder item
        @Override
        public void onBindViewHolder(@NonNull AdminMainActivity.WorkOrderHolder holder, int position) {
            WorkOrder workOrder = workOrderList[position];
            holder.bind(workOrder);
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            return workOrderList.length;
        }
    }


    //This method is used to retrieve work order data from
    //web service and update the recycler view
    private void getData(){
        //update url to access web service
        Database.changeBaseURL("https://g2sp4z1w94.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add user email to JsonObject
        //This will be used to look up all workorders created by this user
        json.addProperty("status", "pending");

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getAllWorkOrders(json);

        //background thread to communicate with the web service
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                //JsonArray object to store the response from web service
                JsonArray jsonArray = response.body();

                boolean hasWorkOrders = getWorkOrders(jsonArray);

                //if the array is empty then don't populate the recycler view
                if(!hasWorkOrders){
                    //There are no work orders to display
                    blankListTextView.setVisibility(View.VISIBLE);
                }else {
                    //There are work orders to display

                    //because the Recyclerview requires the workOrders to be implemented correctly
                    //the Recyclerview initialization will be in the onResponse method

                    //recycler view initialization and implementation
                    recyclerView = findViewById(R.id.recyclerViewAdminMain);

                    //add dividers between each address
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(dividerItemDecoration);

                    //linear layout for vertical scrolling
                    recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                    //send list of workorder to the setAdapter
                    AdminMainActivity.WorkOrderAdapter adapter = new AdminMainActivity.WorkOrderAdapter(workOrders);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure**********************");
                System.out.println(t.getMessage());
            }
        });

    }//end of getData



    //this method handles converting the JsonArray into a list of workOrders
    private boolean getWorkOrders(JsonArray jsonArray){

        workOrders = new WorkOrder[jsonArray.size()];
        JsonObject[] jsonObjects = new JsonObject[jsonArray.size()];

        for(int i = 0; i < jsonArray.size(); i++){
            jsonObjects[i] = (JsonObject) jsonArray.get(i);
        }

        if(jsonObjects[0].has("status")){

            if(jsonObjects[0].get("status").toString().equals("\"0\"")){

                //if status is 0 then there are no work orders to display
                return false;
            }
        }



        for(int i = 0; i < jsonObjects.length; i++){

            String id = jsonObjects[i].get("id").toString();
            id = id.replace("\"", "");

            String creator = jsonObjects[i].get("creator").toString();
            creator = creator.replace("\"", "");

            String admin = jsonObjects[i].get("admin").toString();
            admin = admin.replace("\"", "");

            String description = jsonObjects[i].get("description").toString();
            description = description.replace("\"", "");

            String submissionDate = jsonObjects[i].get("submission_date").toString();
            submissionDate = submissionDate.replace("\"", "");

            String lastActivityDate = jsonObjects[i].get("last_activity_date").toString();
            lastActivityDate = lastActivityDate.replace("\"", "");

            String currentStatus = jsonObjects[i].get("current_status").toString();
            currentStatus = currentStatus.replace("\"", "");

            //*****************************************************
            //Still need to retrieve photos



            workOrders[i] = new WorkOrder(id, creator, admin, description,
                    submissionDate, lastActivityDate, currentStatus);
        }

        return true;


    }



    //This is if the back button is pressed
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }




    //this method creates the options menu for the admin user
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.admin_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //this method handles the menu item that are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent intent;

        switch(item.getItemId()){
            case R.id.adminMenuRules:

                //go to rules and policies activity

                //************************************
                //this will be replaced when rules and policies is complete
                intent = new Intent(getBaseContext(), BlankActivity.class);
                //************************************


                //commented out until complete
                //intent = new Intent(getBaseContext(), ViewRulesAndPoliciesActivity.class);

                //send user data to rules and policies activity
                intent.putExtra(ViewRulesAndPoliciesActivity.userCode, user);

                startActivity(intent);


                return true;

            case R.id.adminMenuEditHomeOwner:

                //go to edit home owners activity

                //*******************************************
                //this will be replaced when edit home owners activity is complete
                intent = new Intent(getBaseContext(), BlankActivity.class);
                //*******************************************

                //commented out until complete
                //intent = new Intent(getBaseContext(), EditHomeOwnerActivity.class);

                //send user data to edit home owner activity
                intent.putExtra(EditHomeOwnerActivity.userCode, user);

                startActivity(intent);

                return true;

            case R.id.parent:
                //The back navigation button is pressed
                onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

