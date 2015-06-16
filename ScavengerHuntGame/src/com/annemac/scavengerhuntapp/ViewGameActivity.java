package com.annemac.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.parse.*;

public class ViewGameActivity extends Activity {
  final List<ParseUser> playerList = new ArrayList<ParseUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getExtra from GamePlayersActivity
        Bundle extras = getIntent().getExtras();
        final String GameId = extras.getString("GameId");
        getCreatedGame();
        setContentView(R.layout.view_selected_game);
        
        setupButtonCallbacks(GameId);
    }

    private void setupButtonCallbacks(final String GameId) {
        final Button editGameButton = (Button) findViewById(R.id.button_editGame);
        editGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(ViewGameActivity.this, EditGameInfoActivity.class);
              intent.putExtra("GameId", GameId);
              Log.d("GameId", "Game id is " + GameId);
              startActivity(intent);
            }
        });

        final Button menuButton = (Button) findViewById(R.id.button_Back);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            finish();
            Intent i = new Intent(ViewGameActivity.this, MainMenuActivity.class);
            ViewGameActivity.this.startActivity(i);
          }
        });
               
    }

    private void setItemList(ParseObject game) {
      final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
      final Intent intent = getIntent();
      final Context context = this;
      query.getInBackground(intent.getStringExtra("GameId"), new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject game, ParseException e) {
          if (e == null) {
            JSONArray items = game.getJSONArray("itemsList");
            if (items != null) {
              final List<String> itemsList = new ArrayList<String>();
              for(int i = 0; i < items.length(); i++){
                try{
                  itemsList.add(items.getString(i));
                }
                catch (Exception exc) {
                  Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
                }
              }
              ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
              ListView listView = (ListView) findViewById(R.id.listview_gameItems);
              listView.setAdapter(adapter);
            }
          }
          else {
            CharSequence text = "Exception occured.";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, text, duration).show();
            finish();
            startActivity(getIntent());
          }
        }
      });
    }
    
    private void getCreatedGame() {
        Bundle extras = getIntent().getExtras();
        String GameId = extras.getString("GameId");
        Log.d("Game Info", "Game ID is " + GameId);
        ParseQuery<ParseObject> queryForGame = ParseQuery.getQuery("Game");
        queryForGame.getInBackground(GameId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject game, ParseException e) {
                if (e == null) {
                    Log.d("Game Info", "Game name is " + game.getString("name"));

                    setupUsernameListView();
                    
                    TextView gameName = (TextView) findViewById(R.id.text_gameName);
                    TextView startDatetime = (TextView) findViewById(R.id.text_startDatetime);
                    TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);

                    gameName.setText(game.getString("name"));
                    final SimpleDateFormat sdf_datetime = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
                    endDatetime.setText(sdf_datetime.format(game.getDate("end_datetime")));
                    startDatetime.setText(sdf_datetime.format(game.getDate("start_datetime")));                    
                    setItemList(game);                    
                    gameName.setText(game.getString("name"));
                    gameName.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                              Bundle extras = getIntent().getExtras();
                              String GameId = extras.getString("GameId");
                              launchEditGame(GameId);
                            }
                    });                
                    
                    startDatetime.setText(game.getDate("start_datetime").toString());
                    startDatetime.setOnClickListener(new OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        Bundle extras = getIntent().getExtras();
                        String GameId = extras.getString("GameId");
                        launchEditGame(GameId);
                      }
                    });
     
                    endDatetime.setText(game.getDate("end_datetime").toString());
                    endDatetime.setOnClickListener(new OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        Bundle extras = getIntent().getExtras();
                        String GameId = extras.getString("GameId");
                        launchEditGame(GameId);
                      }
                    });
                  
                    setItemList(game);
                    setPlayerList();
                    
                } else {
                    Log.w("error", "failed to retrieve game");
                }
            }
        });
    }

    private void launchEditGame(String GameId) {
      Intent intent = new Intent(ViewGameActivity.this, EditGameInfoActivity.class);
      intent.putExtra("GameId", GameId);
      Log.d("GameId", "Game id is " + GameId);
      startActivity(intent);
   }

      
    
  private void setPlayerList() {      
	  ParseQuery<ParseObject> queryPlayers = ParseQuery.getQuery("Players");
      Bundle extras = getIntent().getExtras();
      final String GameId = extras.getString("GameId");
      queryPlayers.whereEqualTo("game", GameId);      
      queryPlayers.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> playerList, ParseException e) {
              if (e == null) {
                  Log.d("User List", "Retrieved " + playerList.size() + " player(s)");
                  getUsernameList(playerList);
              } else {
                  Log.w("Parse Error", "game retreival failure");
              }
          }
      });
    }
    
  private void getUsernameList(List<ParseObject> playerList) {
      for (int i = 0; i < playerList.size(); i++) {
        playerList.get(i).getParseObject("user").fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                      @Override
                      public void done(ParseUser User, ParseException e) {
                          if (e == null) {
                              Log.d("Username", "Retrieved User "
                                      + User.getString("username"));
                              addToListView(User.getString("username"),
                                      getPlayerAdapter());
                          } else {
                              Log.w("Parse Error", "game retreival failure");

                          }
                      }
                  });
      }
  }

  private void setupUsernameListView() {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1);
      final ListView playerListView = (ListView) findViewById(R.id.listview_currentPlayers);
      playerListView.setAdapter(adapter);
  }

  private void addToListView(String item, ArrayAdapter<String> adapter) {
      adapter.add(item);
      adapter.notifyDataSetChanged();
  }

  private ArrayAdapter<String> getPlayerAdapter() {
      final ListView playerListView = (ListView) findViewById(R.id.listview_currentPlayers);
      final ArrayAdapter<String> adapter = (ArrayAdapter<String>) playerListView
              .getAdapter();
      return adapter;
  }
  
}