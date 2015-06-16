package com.annemac.scavengerhuntapp;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import com.parse.ParseAnalytics;

public class StartActivity extends Activity {
  
  @SuppressWarnings("rawtypes")
  class InitializeParseTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object... params) {
      // Login to parse
      ScavengerHuntApp.getInstance().initializeParse();
      return null;
    }
    
    @Override
    protected void onPostExecute(Object result) {
      // Actually start/show LoginActivity
      startActivity(new Intent(StartActivity.this, LoginActivity.class));
      // Finish this SplashScreen activity
      finish();
    }
  } 
  
  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Set Splash screen layout
    setContentView(R.layout.splash);
    // Create new thread to initialize Parse
    new InitializeParseTask().execute();
  }
}
  