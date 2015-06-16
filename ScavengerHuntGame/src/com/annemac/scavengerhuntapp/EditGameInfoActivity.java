package com.annemac.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import com.parse.*;

public class EditGameInfoActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_game_info);
        setupButtonCallbacks();
        getGame();
    }
    
    private void getGame() {
        Bundle extras = getIntent().getExtras();
        String gameId = extras.getString("GameId");
        Log.d("Game Info", "Game View ID is " + gameId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("name"));
                    
                    EditText gameName = (EditText) findViewById(R.id.edit_gameName);
                    
                    gameName.setText(game.getString("name"));
                    populateDateTimeFields(game);            
                } else {
                    Log.w("error", "game retreival failure");
                }
            }
        });
    }

    private void setupButtonCallbacks() {
        final Button updateGameButton = (Button) findViewById(R.id.button_update);
        updateGameButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doUpdateGame();
            }
        });
        final Button editplayersButton = (Button) findViewById(R.id.button_editplayers); 
        editplayersButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            final Intent i = getIntent();
            final String GameId = i.getStringExtra("GameId");
            Intent b = new Intent(EditGameInfoActivity.this, EditPlayersActivity.class);
            b.putExtra("GameId", GameId);
            EditGameInfoActivity.this.startActivity(b);
            finish();
          } 
        });   
        final Button edititemsButton = (Button) findViewById(R.id.button_edititems); 
        edititemsButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            final Intent i = getIntent();
            final String GameId = i.getStringExtra("GameId");
            Intent b = new Intent(EditGameInfoActivity.this, EditGameItemsActivity.class);
            b.putExtra("GameId", GameId);
            EditGameInfoActivity.this.startActivity(b);
            finish();
          } 
        });
        final Button backButton = (Button) findViewById(R.id.newGameButton_cancel); 
        backButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            final Intent i = getIntent();
            final String GameId = i.getStringExtra("GameId");
            Intent b = new Intent(EditGameInfoActivity.this, MainMenuActivity.class);
            b.putExtra("GameId", GameId);
            EditGameInfoActivity.this.startActivity(b);
            finish();
          } 
        });
    }

    private void doUpdateGame() {
        Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("GameId");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(final ParseObject game, ParseException e) {
                if (e == null) {
                    game.put("name", getGameName());
                    game.put("start_datetime", getStartDateTime());
                    game.put("end_datetime", getEndDateTime());
                    game.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("Game Update", "Game Updated!");
                                 showToast("Game Updated!");
                            } else {
                                Log.d("Game Creation", "Error creating game: "
                                        + e);
                            }
                        }
                    });
                }
            }
        });
    }

    private void showToast(String message) {
        ScavengerHuntApp.getInstance()
                .showToast(EditGameInfoActivity.this, message);
    }

    private String getGameName() {
        return getUserInput(R.id.edit_gameName);
    }

    private Date getStartDateTime() {
        return convertToDateTime(getUserInput(R.id.editStartDate) + " "
                + getUserInput(R.id.editStartTime));
    }

    private Date getEndDateTime() {
        return convertToDateTime(getUserInput(R.id.editEndDate) + " "
                + getUserInput(R.id.editEndTime));
    }

    private String getUserInput(int id) {
        EditText input = (EditText) findViewById(id);
        return input.getText().toString();
    }

    private static Date convertToDateTime(String dateString) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"MMM d, yyyy 'at' h:mm a", Locale.US);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
    
//    This is something that can be refactored much better. 
//    There's one general task: parse and return the date in some user input. 
//    Replace these two methods with just one getDateTime() method 
//    that takes a view ID parameter. 
//    Fix the calling code appropriately.
    
    public void showStartDatePickerDialog(View v) {
        final DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "startDatePicker");
    }

    public void showStartTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "startTimePicker");
    }

    public void showEndDatePickerDialog(View v) {
        final DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "endDatePicker");
    }

    public void showEndTimePickerDialog(View v) {
        final DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "endTimePicker");
    }

    
//    Seems like you need just two methods not four here: 
//    	one for showing date pickers, one for showing time pickers, 
//    	with a parameter for the name of the picker.
//    You pass a parameter in these methods but you don't use it.
    
    private void populateDateTimeFields(ParseObject game) {
        EditText startDateView = (EditText) findViewById(R.id.editStartDate);
        EditText startTimeView = (EditText) findViewById(R.id.editStartTime);
        EditText endDateView = (EditText) findViewById(R.id.editEndDate);
        EditText endTimeView = (EditText) findViewById(R.id.editEndTime);

        startDateView.setText(getGameDate(game.getDate("start_datetime")));
        endDateView.setText(getGameDate(game.getDate("end_datetime")));
        startTimeView.setText(getGameTime(game.getDate("start_datetime")));
        endTimeView.setText(getGameTime(game.getDate("end_datetime")));
    }

    private String getGameDate(Date date) {
        return convertDatetimeToDate(date);

    }

    private String getGameTime(Date date) {
        return convertDatetimeToTime(date);
    }

    private String convertDatetimeToTime(Date dateTime) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a",
                Locale.US);
        String convertedTime = new String();
        convertedTime = dateFormat.format(dateTime);
        return convertedTime;
    }

    private String convertDatetimeToDate(Date dateTime) {

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy",
                Locale.US);

        String convertedDate = new String();
        convertedDate = dateFormat.format(dateTime);
        return convertedDate;
    }

}