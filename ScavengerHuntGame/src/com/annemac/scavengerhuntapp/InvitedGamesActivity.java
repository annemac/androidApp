package com.annemac.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.parse.*;

public class InvitedGamesActivity extends Activity {

     List<ParseObject> currentGames = new ArrayList<ParseObject>();
    final Context context = this;
    final  ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_games_list);
        setCurrentGamesListView();
        setupButtonCallbacks();
    }

    private void setCurrentGamesListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final ListView currentGamesListview = (ListView) findViewById(R.id.listview_CurrentGames);
        currentGamesListview.setAdapter(adapter);
        findCurrentInvitedGames();
        currentGamesListview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            final View view, int position, long id) {
                        final ParseObject Game = currentGames.get(position);
                        launchGameView(Game.getObjectId());
                    }
                });
    }

    private void launchGameView(String GameId) {
        Intent intent = new Intent(InvitedGamesActivity.this, GamePlayingActivity.class);
        intent.putExtra("GameId", GameId);
        Log.d("GameId", "Game id is " + GameId);
        startActivity(intent);
    }

    private void findCurrentInvitedGames() {
      
      final ParseQuery<ParseObject> fquery = ParseQuery.getQuery("Players");
      fquery.whereEqualTo("user", currentUser);
      fquery.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> gameIdList, ParseException e) {
               	 ParseQuery<ParseObject> squery = ParseQuery.getQuery("Game");
				      squery.whereMatchesKeyInQuery("objectId", "game", fquery);        
				      squery.findInBackground(new FindCallback<ParseObject>() {
				          public void done(List<ParseObject> games, ParseException e) {
				              if (e == null) {
				            	  if (games.isEmpty()) {
				                      Toast.makeText(context, "You have not been invited to any games yet."
				                    		  , Toast.LENGTH_LONG).show();
				                      InvitedGamesActivity.this.startActivity(new Intent(InvitedGamesActivity.this, MainMenuActivity.class));
				                      finish();
				                  }
				                  for (final ParseObject game : games) {
		                            Log.d("ScavengerHuntApp", "Game record retrieved: " + game.getString("name"));
		                            checkGameStatus(game); 
				                  }		            	  
//		                  for (ParseObject game : games) {
//		                	  if (games != null){
//		                      Log.d("Parse GameName",
//		                              "Retrieved Game Named: " + game.getString("name"));
//		                    	  currentGames.add(game);
//		                    	  addToListView(game, getCurrentGamesAdapter());
//		                	  }
//		                	  if (games == null){
//		                		  Log.d("Parse GameName",
//			                              "Has been invited to no games");
//		                		  launchNoInvitedGamesDialogFragment();
//		                	
//		                	  }
//		                  }
				                  
				              } else {
				                  Log.w("Parse Error", "game name retreival failure");
				              }
				        }
				   }); 
              }           
      });
    }
    
    private void checkGameStatus(ParseObject game) {
    	final SimpleDateFormat sdf_datetime = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
        final String start_datetime = sdf_datetime.format(game.getDate("start_datetime"));
        final Date today = new Date();
        final String errortext  = "Sorry, there was a problem";
        
         try {   	 
           switch (game.getDate("end_datetime").compareTo(today)){
             case -1: //game ended already (before today)
            	 String message1 = " (Expired) ";
            	 addToListView(game, getCurrentGamesAdapter(), message1);
             break;
             case 0: //game ends today
            	 String message2 = " (Current) " ;
            	 addToListView(game, getCurrentGamesAdapter(), message2);
             break;
             case 1: //game ends after today
               switch (game.getDate("start_datetime").compareTo(today)) {
                 case -1: case 0: //game start date was before today or equal to today
                	 String message3 = " (Current)";
                	 addToListView(game, getCurrentGamesAdapter(), message3);
                 break;
                 case 1: //game starts after today
                	 String message4 = " (Pending)- \n opens " + start_datetime;
                	 addToListView(game, getCurrentGamesAdapter(), message4);
                 break;
                 default:        
                  Toast.makeText(context, 
                		   errortext, Toast.LENGTH_SHORT).show();
                   finish();
                   startActivity(getIntent());
               }
             break;
             default:
               Toast.makeText(context, 
            		   errortext, Toast.LENGTH_SHORT).show();
               finish();
               startActivity(getIntent());
           }
         }
         
         catch (final NullPointerException null_exc) {
           Toast.makeText(context, errortext, Toast.LENGTH_SHORT).show();
           finish();
           startActivity(getIntent());
         }
    }
    
    private void addToListView(ParseObject game, ArrayAdapter<String> adapter, String message) {
        adapter.add(game.getString("name") + message);
        adapter.notifyDataSetChanged();
        currentGames.add(game);
    }
    
    private ArrayAdapter<String> getCurrentGamesAdapter() {
        final ListView currentGamesListView = (ListView) findViewById(R.id.listview_CurrentGames);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) currentGamesListView
                .getAdapter();
        return adapter;
    }
           
    private void setupButtonCallbacks() {
      final Button menuButton = (Button) findViewById(R.id.CurrentgameslistButton_menu);
      menuButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          InvitedGamesActivity.this.startActivity(new Intent(InvitedGamesActivity.this, MainMenuActivity.class));
          finish();
        }
      });
    }
}