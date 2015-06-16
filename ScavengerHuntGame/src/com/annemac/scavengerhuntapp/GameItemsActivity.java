package com.annemac.scavengerhuntapp;

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


public class GameItemsActivity extends Activity {  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_items);
    listCurrentItems();
    setupButtonCallbacks();
  }

  private void listCurrentItems(){
   
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
				   setupItemListView(itemsList);
				   }
			   }
		   else {
			   showToast(context);
			   startActivity(getIntent());
			   finish();
			   }
		   }
	   });
   }
  
  private void showToast(Context context) {
	    CharSequence text = "Excepiton occured.";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
  }
  
  private void setupItemListView(List<String> itemsList) {
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, itemsList);
      final ListView listView = (ListView) findViewById(R.id.listView1);
      listView.setAdapter(adapter);      
  }
  
  private String getUserInput(int id) {
      EditText input = (EditText) findViewById(id);
      return input.getText().toString();
  }

  /*
   * complete adding game items then launches GamePlayersActivty
   */
  private void setupButtonCallbacks() {
    final Button addItemButton = (Button) findViewById(R.id.gameItemsButton_addItem);
    addItemButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        final Intent intent = getIntent();    
        query.getInBackground(intent.getStringExtra("GameId"), new GetCallback<ParseObject>() {
          @Override
          final public void done(ParseObject game, ParseException e) {
            if (e == null) {
              final JSONArray items = game.getJSONArray("itemsList");	
              if (items != null) {
            	  addItemsToArray(items, game);
              }  
              else { 
                final JSONArray new_items = new JSONArray();
                addItemsToArray(new_items, game);
             }
            }    
            else{
              Context context = getApplicationContext();
              showToast(context);
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              startActivity(getIntent());
              finish();
            }
          }  
        });    
      } 
    }); 
    final Button doneButton = (Button) findViewById(R.id.gameItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        
    	final Intent in = getIntent();
        final String GameId = in.getStringExtra("GameId");
        
        
        Intent intent = new Intent(GameItemsActivity.this, GamePlayersActivity.class);
        intent.putExtra("GameId", GameId);
        GameItemsActivity.this.startActivity(intent);
        finish();
      } 
    });
    final Button cancelButton = (Button) findViewById(R.id.gameItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        GameItemsActivity.this.startActivity(new Intent(GameItemsActivity.this, MainMenuActivity.class));
        finish();
      }
    });
  }    

  private void addItemsToArray(JSONArray items, ParseObject game){
      final String new_item = getUserInput(R.id.enterText); 
	  items.put(new_item); 
      game.put("itemsList", items == null ? null : items);   
      game.saveInBackground();
      startActivity(getIntent());
      finish();
  }
  
}