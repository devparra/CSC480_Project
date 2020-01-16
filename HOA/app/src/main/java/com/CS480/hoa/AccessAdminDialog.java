package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.DialogFragment;

public class AccessAdminDialog extends DialogFragment {

    //create the interface to be used by MainActivity
    public interface AccessAdminSelectedListener{
        void accessAdminClick(int which);
    }

    private AccessAdminSelectedListener mListener;

    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.adminAccess);
        builder.setMessage(R.string.dialogMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.accessAdminClick(which);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.accessAdminClick(which);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (AccessAdminSelectedListener)context;
    }

}
