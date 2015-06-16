package com.annemac.scavengerhuntapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class NoInvitedDialogFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.noInvitedGames)
               .setPositiveButton("Return to Main Menu", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                       startActivity(intent);
                   }
               });
        return builder.create();
    }
    
}