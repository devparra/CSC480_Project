package com.CS480.hoa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AddCommentDialog extends DialogFragment {

    private EditText userInput;

    public interface AddCommentSelectedListener{
        void addCommentClick(int which, String comment);
    }

    private AddCommentSelectedListener mListener;

    //create the dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.addComment);

        final View customLayout = layoutInflater.inflate(R.layout.add_comment, null);

        builder.setView(customLayout);

        userInput = customLayout.findViewById(R.id.addCommentEditText);


        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.addCommentClick(which, userInput.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.addCommentClick(which, "");
            }
        });


        return builder.create();
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (AddCommentDialog.AddCommentSelectedListener)context;
    }
}
