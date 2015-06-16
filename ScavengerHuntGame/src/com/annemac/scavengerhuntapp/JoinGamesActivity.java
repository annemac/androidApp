package com.annemac.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.parse.*;

public class JoinGamesActivity extends Activity {
	ParseUser currentUser = ParseUser.getCurrentUser();
	final List<ParseObject> gameList = new ArrayList<ParseObject>();   
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.current_games_list);
        setupButtonCallbacks();
    }

    private void setupButtonCallbacks() {
        final Button menuButton = (Button) findViewById(R.id.CurrentgameslistButton_menu);
        menuButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            JoinGamesActivity.this.startActivity(new Intent(JoinGamesActivity.this, MainMenuActivity.class));
            finish();
          }
        });
      }
        public void onResume() {
        super.onResume();
    }

}