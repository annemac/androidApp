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

public class EditPlayersActivity extends Activity {

    private List<ParseObject> players = new ArrayList<ParseObject>();
    private List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_game_players);
        setupCallbacksButton();
        setGame();
    }

    public void setGameInfo(ParseObject game) {
    	getGamePlayers(game);   
        TextView gameName = (TextView) findViewById(R.id.text_gameName);
        gameName.setText(game.getString("name"));
    }

    private void setupCallbacksButton() {
        final Button updateGameBtn = (Button) findViewById(R.id.button_save);
        updateGameBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	final ParseUser currentUser = ParseUser.getCurrentUser();
            	Bundle extras = getIntent().getExtras();
            	//query game by GameId
                final String gameId = extras.getString("GameId");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
                query.getInBackground(gameId, new GetCallback<ParseObject>() {
                    public void done(final ParseObject game, ParseException e) {
                        if (e == null) {
                            game.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("Game Update", "Game Updated!");
                                        updatePlayers(getChosenPlayerList(), game);
                                        //send notification to the updated users
                                        sendPushInvitation(game, currentUser);
                                        
                                        ScavengerHuntApp.getInstance().showToast(EditPlayersActivity.this, "Game Updated!");
                                         
                                         Intent intent = new Intent(EditPlayersActivity.this, ViewGameActivity.class);
                                         intent.putExtra("GameId",game.getObjectId());
                                         Log.d("GameId", "gameId " + game.getObjectId());
                                         startActivity(intent);
                                         
                                         
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
        });
    }

       
    private void clearParseObjects(List<ParseObject> parseList) {
        for (ParseObject item : parseList) {
            item.deleteInBackground();
        }
    }

    private void updatePlayers(List<ParseUser> chosenPlayerList, ParseObject game) {
        clearParseObjects(players);
        for (int i = 0; i < chosenPlayerList.size(); i++) {
            ParseUser user = chosenPlayerList.get(i);
            Log.d("Player", user.toString());
            ParseObject gamePlayer = new ParseObject("Players");
            gamePlayer.put("user", user);
            gamePlayer.put("game", game.getObjectId());
            gamePlayer.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("Save", "New Player added.");
                    } else {
                        Log.d("Save", "Error saving players: " + e);
                    }
                }

            });
        }
    }

    private List<ParseUser> getChosenPlayerList() {
        final List<ParseUser> chosenPlayers = new ArrayList<ParseUser>();
        final ListView registeredPlayers = (ListView) findViewById(R.id.listview_players);
        final SparseBooleanArray checkedPlayers = registeredPlayers.getCheckedItemPositions();
        if (checkedPlayers != null) {
            for (int i = 0; i < checkedPlayers.size(); i++) {
                if (checkedPlayers.valueAt(i)) {
                    chosenPlayers.add(playerList.get(checkedPlayers.keyAt(i)));
                }
            }
        }
        return chosenPlayers;
    }

    private void getGamePlayers(ParseObject game) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GamePlayer");
        Bundle extras = getIntent().getExtras();
        String gameId = extras.getString("GameId");
        query.whereEqualTo("game", gameId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> playerList, ParseException e) {
                if (e == null) {
                    Log.d("User List", "Retrieved " + playerList.size()
                            + " player(s)");
                    getPlayers(playerList);
                    players = playerList;
                } else {
                    Log.w("Parse Error", "game retreival failure");
                }
            }
        });
    }

    /*
     * load game players to array adopter
     */
    private void getPlayers(List<ParseObject> playerList) {
        final ArrayList<ParseUser> players = new ArrayList<ParseUser>();
        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).getParseObject("user")
                    .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Log.d("Parse User",
                                        "Retrieved User "
                                                + user.getString("username"));
                                players.add(user);
                            } else {
                                Log.w("Parse Error", "game retreival failure");
                            }
                        }
                    });
        }
        setParseUserList(players);
    }

    private void setParseUserList(final ArrayList<ParseUser> gamePlayerList) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.selectKeys(Arrays.asList("username"));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    String[] usernameList = new String[userList.size()];
                    int[] gamePlayerPositions = new int[gamePlayerList.size()];
                    Log.d("User List", "Retrieved " + userList.size());
                    for (int i = 0; i < userList.size(); i++) {
                        Log.d("data", "Retrieved User: "
                                + userList.get(i).getString("username"));
                        usernameList[i] = userList.get(i).getString("username");
                    }
                    playerList = userList;

                    for (int a = 0; a < gamePlayerList.size(); a++) {
                        for (int i = 0; i < userList.size(); i++) {
                            String userId = userList.get(i).getObjectId();
                            String playerId = gamePlayerList.get(a)
                                    .getObjectId();
                            Log.d("user comparison", gamePlayerList.get(a)
                                    .getString("username")
                                    + " : "
                                    + userList.get(i).getString("username"));

                            if (userId.equals(playerId)) {
                                Log.d("user comparison", "match found");
                                gamePlayerPositions[a] = i;
                                continue;
                            }
                        }
                    }
                    setupUsernameListView(usernameList, gamePlayerPositions);
                } else {
                    Log.w("error", "game retreival failure");
                }
            }
        });

    }

    private void setupUsernameListView(String[] usernameList,
            int[] gamePlayerPositions) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, usernameList);
        final ListView playerListView = (ListView) findViewById(R.id.listview_players);
        playerListView.setAdapter(adapter);
        playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for (int position : gamePlayerPositions) {
            playerListView.setItemChecked(position, true);
        }
    }

    private void setGame() {
        Bundle extras = getIntent().getExtras();
        String gameId = extras.getString("GameId");
        Log.d("Game Info", "Game View ID is " + gameId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("name"));
                    setGameInfo(game);
                } else {
                    Log.w("error", "game retreival failure");
                    setGameInfo(game);
                }
            }
        });
    }

    private void sendPushInvitation(ParseObject game, ParseUser currentUser) {
    	
    	  //push.setChannel("myChannels");
          //push channels do not work, 
		  // Push notification perfectly works through DashBoard 
    	 // But not work if I send note from mobile app deployed from phone:
    	   // push.setChann/el("myChannels");	    
    	//use parsequery with owner instead
		
    	//this method is to invited only players in the selected list
    	//the invited players logging in the app, players should see their invitiation when clicking "Join a game"
       for (ParseUser player : getChosenPlayerList()) {
 	            ParseQuery<ParseInstallation> pushQuery = ParseInstallation
 	                    .getQuery();
  	            pushQuery.whereEqualTo("username", player.getUsername());
  	            Log.d("push player", player.getString("username"));
  	            ParsePush push = new ParsePush();
  	            //push.setChannel("myChannels");
  	          //push channels do not work,
  			  // Push notification perfectly works through DashBoard 
  	          // But not work if I send note from mobile app deployed from phone:  	          
  			  // push.setChannel("myChannels");	            
  			  	            
  	            push.setQuery(pushQuery);
  	            push.setMessage("You are invited to play the scanveger hunt game"
                      + game.getString("name") + " by "
                      + currentUser.getString("username") + ".");               
               push.sendInBackground();
 	        }
 	    }

}