package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeOwnerMainActivity extends AppCompatActivity {

    public static final String userCode = "com.CS480.hoa.homeOwnerMain";

    //request code used for child activities returning to this activity
    //private final int homeOwnerMainRequestCode = 3;

    private RecyclerView recyclerView;
    private TextView blankListTextView;
    private User user;
    private Button newWorkOrderButton;
    private WorkOrder[] workOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_owner_main);

        //retrieve the user from the previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        blankListTextView = findViewById(R.id.homeOwnerMainBlankListTextView);
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
        getData();

    }//end of onCreate method


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

                Intent intent = new Intent(getBaseContext(), WorkOrderViewActivity.class);

                Bundle bundle = new Bundle();

                //add work order
                bundle.putSerializable(WorkOrderViewActivity.workOrderCode, workOrder);

                //add current user
                bundle.putSerializable(WorkOrderViewActivity.userCode, user);

                //let the next activity know where to return to
                bundle.putString(WorkOrderViewActivity.callingActivityCode, userCode);


                //add bundle to intent
                intent.putExtras(bundle);

                //Pop intent
                startActivity(intent);
        }
    }


    //This method takes the list of work orders and generates a holder for each one
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



    //This method is used to retrieve a list of work orders from
    //the web service and populate the recycler view
    private void getData(){
        //update url to access web service
        Database.changeBaseURL("https://4tddwwxia9.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add user email to JsonObject
        //This will be used to look up all workorders created by this user
        json.addProperty("userEmail", user.getUserEmail());

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getWorkOrders(json);

        //background thread to communicate with the web service
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                //JsonArray object to store the response from web service
                JsonArray jsonArray = response.body();

                boolean hasWorkOrder = getWorkOrders(jsonArray);


                if(!hasWorkOrder){
                    //there are no work orders to display
                    blankListTextView.setVisibility(View.VISIBLE);
                }else {
                    //There are work orders to display

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
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure in HomeOwnerMain***************************");
                System.out.println(t.getMessage());
            }
        });
    }


    //this method handles converting the JsonArray into a list of workOrders
    private boolean getWorkOrders(JsonArray jsonArray){

        workOrders = new WorkOrder[jsonArray.size()];
        JsonObject[] jsonObjects = new JsonObject[jsonArray.size()];

        for(int i = 0; i < jsonArray.size(); i++){
            jsonObjects[i] =  jsonArray.get(i).getAsJsonObject();
        }

        for(int i = 0; i < jsonObjects.length; i++){


            if(jsonObjects[i].has("status")){
                if(jsonObjects[i].get("status").toString().equals("\"0\"")){

                    //If the status is zero then there are no work orders to display
                    return false;
                }
            }



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

            String commentString = jsonObjects[i].get("comments").toString();
            commentString = commentString.replace("[","");
            commentString = commentString.replace("]","");
            commentString = commentString.replace("\"", "");

            String[] comments = commentString.split(",");


            String attachedPhoto = jsonObjects[i].get("attached_photos").toString();
            attachedPhoto = attachedPhoto.replace("[", "");
            attachedPhoto = attachedPhoto.replace("]", "");
            attachedPhoto = attachedPhoto.replace("\"", "");

            String[] attachedPhotos = attachedPhoto.split(",");

            workOrders[i] = new WorkOrder(id, creator, admin, description,
                    submissionDate, lastActivityDate, currentStatus);

            workOrders[i].setAttachedPhotos(attachedPhotos);
            workOrders[i].setComments(comments);

        }

        return true;

    }





    //This is if the back button is pressed
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);

        startActivity(intent);
    }



    //this creates the menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.homeowner_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //this method responds to a selection of a menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.homeOwnerMenuRules:

                //go to view rules and policies activity

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cscproject480b.s3.amazonaws.com/images/rulesandpolicies.pdf"));


                try {
                    startActivity(intent);
                }catch(Exception error){
                    System.out.println("Failed to open web image*****************************************");
                    System.out.println(error.getMessage());
                    Toast.makeText(getBaseContext(), "There was a problem accessing file", Toast.LENGTH_SHORT).show();
                }

                return true;

            case android.R.id.home:

                //This is called if the back navigation button is pressed
                onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
