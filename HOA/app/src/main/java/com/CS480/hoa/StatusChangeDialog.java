package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class StatusChangeDialog extends DialogFragment {

    //create the interface to be used by Activity
    public interface StatusChangeSelectedListener{
        void statusChangeChoice(int which);
    }

    private StatusChangeSelectedListener mListener;

    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.statusSelect);

        //get the choice options from the array in strings.xml
        builder.setItems(R.array.statusOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //send the selected choice to the activity
                mListener.statusChangeChoice(which);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (StatusChangeSelectedListener)context;
    }

}
