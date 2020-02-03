package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteWorkOrderDialog extends DialogFragment {

    //create the interface to be used
    public interface DeleteWorkOrderSelectedListener{
        void deleteWorkOrderClick(int which);
    }

    private DeleteWorkOrderDialog.DeleteWorkOrderSelectedListener mListener;

    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.deleteWO);
        builder.setMessage(R.string.deleteWOMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.deleteWorkOrderClick(which);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.deleteWorkOrderClick(which);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (DeleteWorkOrderDialog.DeleteWorkOrderSelectedListener)context;
    }
}
