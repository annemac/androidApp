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

/* 
 * This activity gets and date and time for a game using Android DialogFragment datapickers which is 
 * native to device API
 * 
 */
public class NewGameActivity extends Activity {  

  @Override
  public void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
    setContentView(R.layout.new_game_create);
    setupCallbacksButton();
  }  
  
  private void setupCallbacksButton() {
	
	final Button createButton = (Button) findViewById(R.id.newGameButton_continue);
	final Button cancelButton = (Button) findViewById(R.id.newGameButton_cancel); 

	 /*
	  * create a new game
	  */
	createButton.setOnClickListener(new OnClickListener() {
        @Override
        final public void onClick(View v) {
          createAGame();
        } 
    } );
	
	/*
	 * canncel a game  
	 */
    
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      final public void onClick(View v) {
        NewGameActivity.this.startActivity(new Intent(NewGameActivity.this, MainMenuActivity.class));
        finish();
      }
    });
    
  }
  
  /*
   * NewGameItem persists a new game with date and time then , direct activity to the players activity
   * --> GameItemsActivity to add game items (things to hunt) 
   * GameItemsActivity will forward game creator to GamePlayersActivity for adding game players
   */
  private void createAGame() {
      
	  final ParseObject game = new ParseObject("Game");
      final ParseUser currentUser = ParseUser.getCurrentUser();
      game.put("creator", currentUser);
      
      game.put("name", getUserInput(R.id.editGameName));
      game.put("start_datetime", getDateTime(R.id.editStartDate, R.id.editStartTime));
      game.put("end_datetime", getDateTime(R.id.editEndDate, R.id.editEndTime));
       
      game.saveInBackground(new SaveCallback() {
    	  @Override
	  		final public void done(ParseException e) {
    		  if (e == null) {
            	Log.d("Game Creation", "Game Created!");
            	final String GameId = game.getObjectId();
            	final Intent intent = new Intent(NewGameActivity.this, GameItemsActivity.class);
            	intent.putExtra("GameId", GameId);
            	NewGameActivity.this.startActivity(intent);
            } else {
            	Log.d("Game Creation", "Error creating game: " + e);
            }
         };
       });
  }

  private Date getDateTime(int date, int time) {	
	  final SimpleDateFormat dateFormat = new SimpleDateFormat(
              "MM-dd-yyyy h:mm a", Locale.CANADA);
	  Date convertedDate = new Date();
	  try {
          convertedDate = dateFormat.parse(getUserInput(date) + " "
                  + getUserInput(time));
      } 
      catch (java.text.ParseException e) {
          e.printStackTrace();
      }
      return convertedDate;
  }
  
  private String getUserInput(int id) {
      EditText input = (EditText) findViewById(id);
      return input.getText().toString();
  }

  /*
   * Date and time fragment picker
   */
  public void showEndDatePickerDialog(View v) {
      final DialogFragment fragment = new DatePickerFragment();
      fragment.show(getFragmentManager(), "endDatePicker");
  }

  public void showEndTimePickerDialog(View v) {
      final DialogFragment fragment = new TimePickerFragment();
      fragment.show(getFragmentManager(), "endTimePicker");
  }
 
  public void showStartDatePickerDialog(View v) {
      final DialogFragment fragment = new DatePickerFragment();
      fragment.show(getFragmentManager(), "startDatePicker");
  }

  public void showStartTimePickerDialog(View v) {
      final DialogFragment fragment = new TimePickerFragment();
      fragment.show(getFragmentManager(), "startTimePicker");
  }
 
}