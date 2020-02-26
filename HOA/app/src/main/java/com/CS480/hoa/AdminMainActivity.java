package com.CS480.hoa;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMainActivity extends AppCompatActivity implements
        StatusChangeDialog.StatusChangeSelectedListener {

    public static final String userCode = "com.CS480.hoa.adminMain";
    private final int PDF = 666;

    private RecyclerView recyclerView;
    private TextView blankListTextView;
    private Button changeStatusButton;
    private String listStatus;
    private User user;
    private WorkOrder[] workOrders;

    private String filePath;

    private AsyncTask<Void, Void, String> sendPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        //retrieve the user from the previous activity
        user = (User) getIntent().getSerializableExtra(userCode);

        blankListTextView = findViewById(R.id.adminMainBlankListTextView);
        changeStatusButton = findViewById(R.id.adminMainStatusButton);
        recyclerView = findViewById(R.id.recyclerViewAdminMain);

        //set the initial list view to pending work orders
        listStatus = "pending";


        //add onclick listener for the status change button
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Dialog to choose which status to set
                FragmentManager manager = getSupportFragmentManager();
                StatusChangeDialog dialog = new StatusChangeDialog();
                dialog.show(manager, "Status Change");
            }
        });



        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }


        //get the list of workorders from web service
        getData(listStatus);

    }





    //This class is used to create each line item in the recycler view
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


            try {
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

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //This class takes the list of work orders and generates a holder for each one
    private class WorkOrderAdapter extends RecyclerView.Adapter<AdminMainActivity.WorkOrderHolder>{

        private WorkOrder[] workOrderList;

        //constructor
        public WorkOrderAdapter(WorkOrder[] workOrderList){

            //clear the recycler adapter
            if(this.workOrderList != null) {
                clear();
            }

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

            if (workOrder != null) {
                holder.bind(workOrder);
            }
        }

        //return the total number of addresses
        @Override
        public int getItemCount() {
            return workOrderList.length;
        }



        public void clear(){
            notifyItemRangeRemoved(0, workOrderList.length);
        }

    }






    //This method handles the choice from the dialog box to select which type of
    //work orders to display
    @Override
    public void statusChangeChoice(int which) {

        if(which == 0){

            //if pending is already displayed do nothing
            if(!listStatus.equals("pending")){
                listStatus = "pending";
                getData(listStatus);
            }

        }else if(which == 1){

            //if approved is already displayed do nothing
            if(!listStatus.equals("approved")){
                listStatus = "approved";
                getData(listStatus);
            }

        }else{

            //if denied is already displayed do nothing
            if(!listStatus.equals("denied")){
                listStatus = "denied";
                getData(listStatus);
            }

        }
    }//end statusChangeChoice





    //This method is used to retrieve work order data from
    //web service and update the recycler view
    private void getData(String listStatus){
        //update url to access web service
        Database.changeBaseURL("https://g2sp4z1w94.execute-api.us-east-1.amazonaws.com/");

        //create retrofit object
        RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

        //create a JsonObject to store data to send to web service
        JsonObject json = new JsonObject();

        //add user email to JsonObject
        //This will be used to look up all workorders created by this user
        json.addProperty("status", listStatus);

        //create a Call object to receive web service response
        Call<JsonArray> call = retrofit.getAllWorkOrders(json);

        //background thread to communicate with the web service
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                //JsonArray object to store the response from web service
                JsonArray jsonArray = response.body();

                //translate the Json Array into a list of work orders
                boolean hasWorkOrders = getWorkOrders(jsonArray);


                //add dividers between each work order
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);



                //if the array is empty then don't populate the recycler view
                if(!hasWorkOrders){

                    //There are no work orders to display
                    blankListTextView.setVisibility(View.VISIBLE);

                    //if there are no work order to display hide the recycler view
                    recyclerView.setVisibility(View.INVISIBLE);

                }else {

                    //There are work orders to display
                    blankListTextView.setVisibility((View.INVISIBLE));

                    //show the recycler view
                    recyclerView.setVisibility(View.VISIBLE);

                }

                //because the Recyclerview requires the workOrders to be implemented correctly
                //the Recyclerview initialization will be in the onResponse method

                //recycler view initialization and implementation


                //linear layout for vertical scrolling
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                //send list of workorder to the setAdapter
                AdminMainActivity.WorkOrderAdapter adapter = new AdminMainActivity.WorkOrderAdapter(workOrders);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                System.out.println("Failure in Admin Main**********************");
                System.out.println(t.getMessage());
            }
        });

    }//end of getData





    //this method handles converting the JsonArray into a list of workOrders
    private boolean getWorkOrders(JsonArray jsonArray){

        //clear the workOrders variable
        workOrders = null;

        //create new workOrders of the right size
        workOrders = new WorkOrder[jsonArray.size()];
        JsonObject[] jsonObjects = new JsonObject[jsonArray.size()];

        for(int i = 0; i < jsonArray.size(); i++){
            jsonObjects[i] = jsonArray.get(i).getAsJsonObject();
        }


        //check to see if there are any work orders to display
        if(jsonObjects[0].has("status")){

            if(jsonObjects[0].get("status").toString().equals("\"0\"")){

                //if status is 0 then there are no work orders to display
                return false;
            }
        }


        //This will only execute if there are work orders to display
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


            workOrders[i] = new WorkOrder(id, creator, admin, description,
                    submissionDate, lastActivityDate, currentStatus);


            String[] attachedPhotos = getImages(jsonObjects[i].get("attached_photos").toString());

            workOrders[i].setAttachedPhotos(attachedPhotos);

            //add comments
            String[] comments = getComments(jsonObjects[i].get("comments").toString());

            workOrders[i].setComments(comments);
        }

        return true;


    }




    //This method splits the input string into a list of comments
    private String[] getComments(String input){

        //clean up the string
        input = input.replace("\"", "");
        input = input.replace("[", "");
        input = input.replace("]", "");

        //if there are more then one comment they will be separated by a comma
        return input.split(",");
    }




    //This method splits the input string into a list of image urls
    //and then retrieves the image using each url
    private String[] getImages(String input){

        //clean up the string
        input = input.replace("\"", "");
        input = input.replace("[", "");
        input = input.replace("]", "");

        //if there are more then one image their urls will be separated by a comma
        return input.split(",");
    }





    //This is if the back button is pressed
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);

        startActivity(intent);
    }




    //this method creates the options menu for the admin user
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.admin_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //this method handles the menu item that are selected
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent intent;

        switch(item.getItemId()){
            case R.id.adminMenuRules:

                //upload file rules and policies activity

                new MaterialFilePicker()
                        .withActivity(AdminMainActivity.this)
                        .withPath(Environment.DIRECTORY_DOWNLOADS)
                        .withRequestCode(PDF)
                        .withHiddenFiles(true)
                        .start();

                return true;

            case R.id.adminMenuEditHomeOwner:

                //go to edit home owners activity

                intent = new Intent(getBaseContext(), EditHomeOwnerMainActivity.class);

                startActivity(intent);

                return true;

            case android.R.id.home:
                //The back navigation button is pressed
                onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }//end onOptionItemSelected





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PDF && resultCode == RESULT_OK){

            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            sendPdf = new SendPdf();
            sendPdf.execute();
        }
    }







    class SendPdf extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {

            return ConvertImage.convertFileToString(new File(filePath));
        }

        @Override
        protected void onPostExecute(String fileAsString){

            //update url to access web service
            Database.changeBaseURL("https://rvbrfawc2a.execute-api.us-east-1.amazonaws.com/");

            //create retrofit object
            RetrofitAPI retrofit = Database.createService(RetrofitAPI.class);

            //create a JsonObject to store data to send to web service
            JsonObject json = new JsonObject();

            //add user email to JsonObject
            //This will be used to look up all workorders created by this user
            json.addProperty("content", fileAsString);
            json.addProperty("fname",  "rulesandpolicies.pdf");

            //create a Call object to receive web service response
            Call<JsonArray> call = retrofit.uploadPdf(json);

            //background thread to communicate with the web service
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                    if(response.isSuccessful()) {

                        JsonArray jsonArray = response.body();


                        //convert JsonArray into JsonObject
                        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();




                        System.out.println(jsonObject);



                        //Extract data from JsonObject
                        String statusCode = jsonObject.get("status").toString();
                        statusCode = statusCode.replace("\"", "");

                        if(statusCode.equals("200")){
                            Toast.makeText(getBaseContext(), "The upload was successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "The upload failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        System.out.println("Failed response in Admin Main SendPdf************************");
                        Toast.makeText(getBaseContext(), "The web service response failed", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    System.out.println("Failure in Admin Main SendPdf**********************");
                    System.out.println(t.getMessage());
                }
            });


        }
    }
}

