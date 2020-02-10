package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteUserDialog extends DialogFragment {

    //create the interface to be used
    public interface DeleteUserSelectedListener{
        void deleteUserClick(int which);
    }

    private DeleteUserDialog.DeleteUserSelectedListener mListener;

    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.deleteUser);
        builder.setMessage(R.string.deleteUserMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.deleteUserClick(which);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.deleteUserClick(which);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (DeleteUserDialog.DeleteUserSelectedListener)context;
    }
}
