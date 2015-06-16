package com.annemac.scavengerhuntapp;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainMenuActivity extends Activity {
  private Button newGameButton;
  private Button joinGameButton;
  private Button myGamesButton;
  private  final ParseUser parseUser = ParseUser.getCurrentUser();


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);
   //  welcomeEditText.setText("Welcome" + ParseUser.getCurrentUser().getUsername());
	
    ParseAnalytics.trackAppOpened(getIntent());
    setupButtonCallbacks();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void setupButtonCallbacks() {

    newGameButton = (Button) findViewById(R.id.newGameButton_name);
    newGameButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
          startActivity(new Intent(MainMenuActivity.this, NewGameActivity.class));
      }
    });

    joinGameButton = (Button) findViewById(R.id.mainMenuButton_joinGame);
    joinGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, InvitedGamesActivity.class));
            }
    });

    myGamesButton = (Button) findViewById(R.id.mainMenuButton_myGames);
    myGamesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MainMenuActivity.this, MyGamesList.class));
            }
    });
    
  }
 @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.mainmenu, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menuitem_prefs:
      // Intent i = new Intent(mThisActivity, PrefsActivity.class);
      // mThisActivity.startActivity(i);
      return true;
    case R.id.menuitem_logout:
      ParseUser.logOut();
      finish();
      return true;
    default:
      break;
    }
    return false;
  }

}
