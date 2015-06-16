package com.annemac.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.parse.*;

public class GamePlayingActivity extends Activity {
	int score;
	ParseObject game;
    List<ParseObject> itemsFoundByUsers;
   final  ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        final String gameId = extras.getString("GameId");
        setContentView(R.layout.play_game);
        getGame(gameId);

        setupCallbackButton(gameId);
    }

    private void setupCallbackButton(final String GameId) {
        final Button menuButton = (Button) findViewById(R.id.button_back);
       // final Button photo =(Button)findViewById(R.id.captureFront);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            GamePlayingActivity.this.startActivity(new Intent(GamePlayingActivity.this, MainMenuActivity.class));
            finish();
          }
        });


        final Button viewGamePictures = (Button) findViewById(R.id.button_picture);

        viewGamePictures.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1. create an intent pass class name or it action name
                Intent intent = new Intent(GamePlayingActivity.this, DisplayPicturesActivity.class);

                // 3. or you can add data to a bundle
                Bundle extras = new Bundle();
                extras.putString("gameId", game.getObjectId());

                // 4. add bundle to intent
                intent.putExtras(extras);

                // 5. start the activity
                startActivity(intent);
                                           }
        });

      /*  
        PhotoIntentActivity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              GamePlayingActivity.this.startActivity(new Intent(GamePlayingActivity.this, PhotoIntentActivity.class));
              finish();
            }
          });
        
        */
        
    }    
    
    private void getGame(final String gameId) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<ParseObject>() {
            public void done(ParseObject currentGame, ParseException e) {
                if (e == null) {
                    game = currentGame;
                    //set game info: name data and then load game items

                    final TextView gameName = (TextView) findViewById(R.id.title_gameName);
                    final TextView endDatetime = (TextView) findViewById(R.id.text_endDatetime);
                    gameName.setText(game.getString("name"));
                    final SimpleDateFormat sdf_datetime = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
                    endDatetime.setText(sdf_datetime.format(game.getDate("end_datetime")));
                    
                    //load game items 
                    loadGameItemsListView();

                    setItemList(game);

              } else {
                    Log.w("error", "while retrieving game. ");
                }
            }
        });
    }
    
    private void loadGameItemsListView() {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1);
      final ListView itemListView = (ListView) findViewById(R.id.listview_remainingItems);
      itemListView.setAdapter(adapter);
      
      final Date endDatetime = game.getDate("end_datetime");
      if (new Date().before(endDatetime)) {   
	      itemListView
	       .setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent,
		                final View view, int position, long id) {
		            Log.d("Dialog Values",        "Position is " + String.valueOf(position)
		                            + ". View is " + view.toString()
		                            + ". ID is " + String.valueOf(id));
		            final String itemName = (String) parent
		                    .getItemAtPosition(position);


                    Bundle item = new Bundle();
		            item.putString("name", itemName);
		            
		            //when the item is found, prompt users to take picture to claim points
		            final DialogFragment itemFoundDialogFragment = new ItemFoundDialogFragment();
		            
		            itemFoundDialogFragment.setArguments(item);
		            itemFoundDialogFragment.show(getFragmentManager(), "itemFound");
		            
		            
		        }
	        });
      }
	  if (new Date().after(endDatetime)) { //game over of time
		  determineWinnerNames();          
		  itemListView
	       .setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> parent,
		                final View view, int position, long id) {
		            final DialogFragment gameOverDialogFragment = new GameOverDialogFragment();
		            gameOverDialogFragment.show(getFragmentManager(), "Game Over!");
		        }
	      });	 
	   } 
    }
    
   private void determineWinnerNames() {
           final ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItemFound");
           query.whereEqualTo("game", game);
           query.include("user");
           query.findInBackground(new FindCallback<ParseObject>() {
               public void done(final List<ParseObject> foundItems, ParseException e) {
                   if (e == null) {
                	   //set up empty table that loop will fill
                	   final Map<String, Set<String>> finds = new HashMap<String, Set<String>>();
                	   
                	   for (final ParseObject foundItem : foundItems) {
                         final String username = foundItem.getParseObject("user").getString("username");
                         final String item = foundItem.getString("item");
                         addUser(finds, username);
                         addItem(finds, username, item);                                      
                         Log.d("Found Players", "players:" + username);
                        
                       }
                	   final int highscore = findHighScore(finds);
                	   final List<String> winners = findWinners(finds, highscore);
                	   saveWinnersInfo(winners);
                   } else {
                       Log.w("Parse Error", "player username retrieval failure");
                   }     
               }          
           });
   }


   private void saveWinnersInfo(List<String> winners) {
	   final List<ParseObject> newWinnersList = new ArrayList<ParseObject>();
	   final ParseObject gamewinner = new ParseObject("GameWinner");
	   for (int i=0;i < winners.size();i++) {
		 gamewinner.put("winner", winners.get(i)); 
		 gamewinner.put("game", game);
		 newWinnersList.add(gamewinner);
	   }
	   
	   ParseObject.saveAllInBackground(newWinnersList, new SaveCallback() {
           public void done(ParseException e) {
               if (e == null) {
                   Log.d("Play Game",
                           "Winner Saved to game named "
                                   + game.getString("name") + "!");
               } else {
                   Log.d("Play Game", "Error in saving winner: " + e);
               }
           }
       });   
	 }
   
   
  private int findHighScore(final Map<String, Set<String>> finds) {
	 int highscore = 0 ;
	 for(Map.Entry<String, Set<String>> entry : finds.entrySet()) {
	    if (entry.getValue().size() > highscore ) {
	    	highscore = entry.getValue().size();
	    }
	 }
	 return highscore ;
   }

   
   
   private List<String> findWinners(final Map<String, Set<String>> finds, final int highscore) {
	 final List<String> winners = new ArrayList<String>() ; 
     if (highscore != 0){
     	 for(Map.Entry<String, Set<String>> entry : finds.entrySet()) {
   		    if (entry.getValue().size() == highscore ) {
   		    	 winners.add(entry.getKey()); //put this key (the username) into the winners list
   		    }	 
	     }
	 } 
	 return winners;
   }
   
    private void addUser(final Map<String, Set<String>> finds, final String name) {
        if (finds.get(name) == null) { 
    	  finds.put(name, new HashSet<String>());
        }
    }
   
    private void addItem(final Map<String, Set<String>> finds, final String username, final String item) {
    	finds.get(username).add(item);
    }

    //update database when items is marked "found"
    private void markFound() {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItemFound");
        query.whereEqualTo("game", game);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                	itemsFoundByUsers = foundItems;
                    for (final String listItem : getItemListViewItems()) {
                        for (final ParseObject foundItem : itemsFoundByUsers) {
                            if (listItem.equals(foundItem.getString("item"))) {
                            	markFoundItem(listItem);
                            }
                        }
                    }
                } else {
                    Log.w("Parse Error", "player username retrieval failure");
                }
            }
        });
    }

    private ArrayList<String> getItemListViewItems() {
        final ArrayList<String> itemList = new ArrayList<String>();
        final ArrayAdapter<String> adapter = getItemAdapter();
        for (int i = 0; i < (adapter.getCount()); i++) {
            itemList.add(adapter.getItem(i));
        }
        return itemList;
    }

    private void setItemList(ParseObject game) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        final Intent i = getIntent();
        final Context context = this;
        query.getInBackground(i.getStringExtra("GameId"), new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject game, ParseException e) {
            if (e == null) {
              final Date startDatetime = game.getDate("start_datetime");
              if (new Date().after(startDatetime)) {
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
	                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
	                final ListView listView = (ListView) findViewById(R.id.listview_remainingItems);
	                listView.setAdapter(adapter);
	                
	                setScore(game.getJSONArray("itemsList"));
	                markFound();
	             }  
              }
              
              //validate the game time if it is a future, (game set to pending)
              //add "game has not started yet"
              if (new Date().before(startDatetime)) {
	              JSONArray items = game.getJSONArray("itemsList");
	              if (items != null) {
	                final List<String> itemsList = new ArrayList<String>();
	                itemsList.add("Game has not started yet.");	                
	                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList);
	                final ListView listView = (ListView) findViewById(R.id.listview_remainingItems);
	                listView.setAdapter(adapter);	                
	             }  
              }
            }
            else {
              CharSequence text = "There was a problem. Please hold.";
              int duration = Toast.LENGTH_SHORT;
              Toast.makeText(context, text, duration).show();
              finish();
              startActivity(getIntent());
            }
          }
        });
      }                
                
  
    
    public void onFoundItemDialog(final String name) {

    	//take picture then send found item to Parse

    	// 1. create an intent pass class name or it action name
        Intent intent = new Intent(getApplicationContext(), PhotoIntentActivity.class);

        // 3. or you can add data to a bundle
        Bundle extras = new Bundle();
        extras.putString("gameId", game.getObjectId());
        extras.putString("gameItem", name);

        // 4. add bundle to intent
        intent.putExtras(extras);

        // 5. start the activity
        startActivity(intent);


        sendFoundItemToParse(name);

            final ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItemFound");
            query.whereEqualTo("game", game);
            query.whereEqualTo("user", currentUser);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> foundItems, ParseException e) {
                    if (e == null) {
                        markFoundItem(name);
                        score++;
                        final TextView scoreView = (TextView) findViewById(R.id.text_score);
                        scoreView.setText(String.valueOf(score));
                        checkIfWinner();
                    } else {
                        Log.w("Parse Error", "foundItem not retrieved");
                    }
                }
            });


    }
                
    
    private void checkIfWinner() {
    	final JSONArray totalItems = game.getJSONArray("itemsList");
    	final ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItemFound");
    	query.whereEqualTo("game", game);
    	query.include("user");
    	query.findInBackground(new FindCallback<ParseObject>() {
    		public void done(final List<ParseObject> foundItems, ParseException e) {
    			if (e == null) {
    				final Map<String, Set<String>> finds = new HashMap<String, Set<String>>();
    				for (final ParseObject foundItem : foundItems) {
    					final String username = foundItem.getParseObject("user").getString("username");
    					final String item = foundItem.getString("item");
    					addUser(finds, username);
    					addItem(finds, username, item);
    					Log.d("Found Players", "players:" + username);
    					}
    				final int highscore = findHighScore(finds);
    				if (score == totalItems.length()) {  //currentScore is for currentUser
    					launchWinnerDialogFragment();
    					setWinnerInfo();
    					}
    				if (highscore == totalItems.length()){ //should announce if someone else has won...too slow so is not working here
    					launchGameAlreadyWonDialogFragment();
    					}
    			} else {
    				Log.w("Parse Error", "player username retrieval failure");
    				}
    			}
    		});
    	}
    
    private void launchWinnerDialogFragment() {
    	final DialogFragment winnerDialogFragment = new WinnerDialogFragment();     		            
         winnerDialogFragment.show(getFragmentManager(), "Winner");
    }
    
    private void launchGameAlreadyWonDialogFragment() {
    	final DialogFragment gameAlreadyWonDialogFragment = new WinnerDialogFragment();     		            
         gameAlreadyWonDialogFragment.show(getFragmentManager(), "Game Already Won");
    }
    
    private void sendFoundItemToParse(final String name) {
        final ParseObject foundItem = new ParseObject("GameItemFound");
        foundItem.put("game", game);
        foundItem.put("user", currentUser);
        foundItem.put("item", name);
        foundItem.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("PlayGame", "Item Found!");
                } else {
                    Log.d("PlayGame", "Error creating found item: " + e);
                }
            }
        });


    }

    protected void markFoundItem(final String item) {
    	final ArrayAdapter<String> adapter = getItemAdapter();
        adapter.remove(item);
    	adapter.add((item + " \u2713"));
        adapter.notifyDataSetChanged();
    }
    
    private void setWinnerInfo() {
    	 final ParseObject gamewinner = new ParseObject("GameWinner");
    	  gamewinner.put("game", game);
        gamewinner.put("winner", currentUser.getString("username"));
          gamewinner.saveInBackground(new SaveCallback() {
             public void done(ParseException e) {
                if (e == null) {
                    Log.d("Play Game",
                            "Winner Saved : "
                                    + currentUser.getString("username") + "!");
                } else {
                    Log.d("Play Game", "Error in saving winner: " + e);
                }
            }
        });
    }
        
    private void setScore(final JSONArray totalItems) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("GameItemFound");
        query.whereEqualTo("game", game);
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                	itemsFoundByUsers = foundItems;                    
                    score = itemsFoundByUsers.size();
                    final TextView scoreView = (TextView) findViewById(R.id.text_score);
                    final TextView totalPointsView = (TextView) findViewById(R.id.text_totalPoints);
                    scoreView.setText(String.valueOf(score));
                    totalPointsView.setText(" out of " + totalItems.length());
                } else {
                    Log.w("Parse Error", "player username retrieval failure");
                }
            }
        });
    }
    
    private ArrayAdapter<String> getItemAdapter() {
        final ListView itemListView = (ListView) findViewById(R.id.listview_remainingItems);
        final ArrayAdapter<String> adapter = (ArrayAdapter<String>) itemListView
                .getAdapter();
        return adapter;
    }
}
