package com.annemac.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.parse.*;

/*
 * this intent load all registered Parse users to the list view to allow game 
 * creator select from for creating a game. 
 * Once users were selected, notification will be sent.
 */
public class GamePlayersActivity extends Activity {
	
  private List<ParseUser> playerList = new ArrayList<ParseUser>();
  final ParseObject game = new ParseObject("Game");
  final ParseUser currentUser = ParseUser.getCurrentUser();
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_players);
    setCurrentUserList();
    Log.d("Game Creation", "Game Created!");
    setupCallbacksButton();
    /*
     * send notification when creating game
     * note: for steps to send/receive parse push notification, please use 
     * http://parse.com/tutorials/android-push-notifications
     * We need to enable nofication setting in Parse.com in order to send notification on the 
     * client side (phone)
     */
    sendParsePushInvitation(game, currentUser);
  }
 
  /*
   * get all users that have registered to Parse and set to PlayerList
   */
  private void setCurrentUserList() {
    final ParseQuery<ParseUser> parsequery = ParseUser.getQuery();
    parsequery.selectKeys(Arrays.asList("username"));
    parsequery.findInBackground(new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> userList, ParseException e) {
            if (e == null) {
                final String[] usernameArray = new String[userList.size()];
                Log.d("User List", "Retrieved " + userList.size());
                for (int i = 0; i < userList.size(); i++) {
                    Log.d("data", "Retrieved User: "  + userList.get(i).getString("username"));
                    usernameArray[i] = userList.get(i).getString("username");
                }
                playerList = userList;
                setUsernameListView(usernameArray);
            } else {
                Log.w("error", "game retreival failure");
            }
        }
    });
  }
 
  private void setUsernameListView(String[] usernameArray) {
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_multiple_choice, usernameArray);
    final ListView playerListView = (ListView) findViewById(R.id.listView_players);
    playerListView.setAdapter(adapter);
    playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
  }
 
   private void setupCallbacksButton() {
     final Button finishCreateGameButton = (Button) findViewById(R.id.playersButton_done);
     finishCreateGameButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {	 
           final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");          
           Intent b = new Intent();
           query.getInBackground(b.getStringExtra("Game"), new GetCallback<ParseObject>() {   
                    
             @Override
               public void done(ParseObject game, ParseException e) {
                 if (e == null) {
                   Log.d("Game Creation", "Game Created!");
                   saveGamePlayers(getSelectedPlayerList());
                   final Intent i = getIntent();
                   final String GameId = i.getStringExtra("GameId");
                   Intent intent = new Intent(GamePlayersActivity.this, ViewGameActivity.class);
                   intent.putExtra("GameId", GameId);
                   GamePlayersActivity.this.startActivity(intent);
                   finish();
                 } else {
                   Log.d("Game Creation", "Error creating game: " + e);
                 }
               }
           });
         }
     });
     final Button cancelButton = (Button) findViewById(R.id.playersButton_cancel);
     cancelButton.setOnClickListener(new OnClickListener() {
       @Override
       public void onClick(View v) {
         GamePlayersActivity.this.startActivity(new Intent(GamePlayersActivity.this, GameItemsActivity.class));
         finish();
       }
     });
   }

  private List<ParseUser> getSelectedPlayerList() {
     final List<ParseUser> chosenPlayers = new ArrayList<ParseUser>();
     final ListView playerListView = (ListView) findViewById(R.id.listView_players);
     SparseBooleanArray checkedItems = playerListView
             .getCheckedItemPositions();
     if (checkedItems != null) {
         for (int i = 0; i < checkedItems.size(); i++) {
             if (checkedItems.valueAt(i)) {
                 chosenPlayers.add(playerList.get(checkedItems.keyAt(i)));
             }
         }
     }
     return chosenPlayers;
   }

   /*
    * Persist GamePlayers
    */
   private void saveGamePlayers(List<ParseUser> selectedPlayerList) { 
     for (ParseUser selectedPlayer : selectedPlayerList) {
         Log.d("Players", selectedPlayer.toString());
         final Intent intent = getIntent();
         
        
         final ParseObject gamePlayer = new ParseObject("Players");
         gamePlayer.put("user", selectedPlayer);
         gamePlayer.put("game", intent.getStringExtra("GameId"));
         gamePlayer.saveInBackground(new SaveCallback() {
             @Override
             public void done(ParseException e) {
                 if (e == null) {
                     Log.d("Save", "gamePlayer data saved!");
                 } else {
                     Log.d("Save", "Error saving gamePlayer: " + e);
                 }
             }

         });
     }
  }

   private void sendParsePushInvitation(ParseObject game, ParseUser currentUser) {
	
	   for (ParseUser player : getSelectedPlayerList()) {	
		   //push.setChannel("myChannels");
	          //push channels do not work, 
			  // Push notification perfectly works through DashBoard 
			  // But not work if I use code from mobile:   
			  // push.setChann/el("myChannels");	    
	    	//use parsequery with owner instead
		   ParseQuery<ParseInstallation> pushQuery = ParseInstallation .getQuery();
		   pushQuery.whereEqualTo("username", player.getUsername());
		   ParsePush push = new ParsePush();
		   //push channels do not work, 
		  // Push notification perfectly works through DashBoard 
		  // But not work if I send note from mobile app deployed from phone:

		  // push.setChann/el("myChannels");
		   push.setQuery(pushQuery);
		   push.setMessage("You are invited to play the scanveger hunt game "
                       + game.getString("name") + " by "
                       + currentUser.getString("username") + ".");
		   push.sendInBackground();
		   Log.d("push player", player.getString("username"));
			 
	      }
	   }
   
}