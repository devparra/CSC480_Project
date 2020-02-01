package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;


public class ViewUserDialog extends DialogFragment {

    private TextView userName;
    private TextView userAddress;
    private TextView userPhone;
    private TextView userEmail;


    //constructor
    public static ViewUserDialog newInstance(User user){
        ViewUserDialog dialog = new ViewUserDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        dialog.setArguments(bundle);

        return dialog;
    }


    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.displayPersonalInformation);

        final View customLayout = layoutInflater.inflate(R.layout.display_personal_information, null);

        //getLayoutInflater().inflate(R.layout.display_personal_information, );
        builder.setView(customLayout);

        User user = (User) getArguments().getSerializable("user");

        userName = customLayout.findViewById(R.id.viewPersonalInformationName);
        userAddress = customLayout.findViewById(R.id.viewPersonalInformationAddress);
        userPhone = customLayout.findViewById(R.id.viewPersonalInformationPhone);
        userEmail = customLayout.findViewById(R.id.viewPersonalInformationEmail);

        userName.setText(user.getUserName());
        userAddress.setText(user.getUserAddress());
        userPhone.setText(user.getUserPhone());
        userEmail.setText(user.getUserEmail());


        builder.setPositiveButton("OK", null);

        return builder.create();
    }

}
