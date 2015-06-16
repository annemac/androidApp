package com.annemac.scavengerhuntapp;
import java.net.URI;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class ItemFoundDialogFragment extends DialogFragment {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private URI fileUri;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String itemName = getArguments().getString("name");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Item " + "\""+ itemName + " found \"" + "?")
                .setPositiveButton(R.string.dialog_found,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            	     
                            	//	(PhotoIntentActivity)getActivity()).
                          ((GamePlayingActivity) getActivity()).onFoundItemDialog(itemName);
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ItemFoundDialogFragment.this.getDialog()
                                        .cancel();
                            }
                        });
        return builder.create();
    }

}