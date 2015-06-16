package com.annemac.scavengerhuntapp;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.parse.*;

public class MyGamesList extends Activity {

    ParseUser currentUser = ParseUser.getCurrentUser();
    List<ParseObject> myGamesList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_games_list);
        setMyGamesListView();
        setupCallbacksButton();
    }
    private void setMyGamesListView() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final ListView myGamesListview = (ListView) findViewById(R.id.listview_myGames);
        myGamesListview.setAdapter(adapter);
        findMyCreatedGames();
        myGamesListview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                            final View view, int position, long id) {
                        final ParseObject Game = myGamesList.get(position);
                        launchAGame(Game.getObjectId());
                    }
                });
    }

  
    /*
     *  Add game to Array adapter
     */
    private void addToListView(ParseObject game, ArrayAdapter<String> adapter) {
        adapter.add(game.getString("name"));
        adapter.notifyDataSetChanged();
    }
    
    private void launchAGame(String GameId) {
        Intent intent = new Intent(MyGamesList.this, ViewGameActivity.class);
        intent.putExtra("GameId", GameId);
        Log.d("GameId", "Game id = " + GameId);
        startActivity(intent);
    }

   /*
    * Query all games that were created by owner
    */
    private void findMyCreatedGames() {
    	
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("creator", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> games, ParseException e) {
                if (e == null) {
                    for (final ParseObject game : games) {
                        Log.d("Game Info","Game name :" + game.getString("name"));
                        addToListView(game, getMyGamesAdapter());
                    }
                    myGamesList = games;
                } else {
                    Log.w("error", "game retreival failure");
                }
            }
        });
    }
   
    /*
     * call back button to mainMenu Activity
     */
    private void setupCallbacksButton() {
      final Button menuButton = (Button) findViewById(R.id.mygameslistButton_menu); 
      menuButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
          Intent i = new Intent(MyGamesList.this, MainMenuActivity.class);
          MyGamesList.this.startActivity(i);
        }
      });
    
    }
    /*
     * Load all games into list View
     */
    private ArrayAdapter<String> getMyGamesAdapter() {
        final ListView myGamesListView = (ListView) findViewById(R.id.listview_myGames);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) myGamesListView
                .getAdapter();
        return adapter;
    }
}