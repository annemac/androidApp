package com.annemac.scavengerhuntapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class DisplayPicturesActivity extends Activity {

    // Declare Variables
    ListView listview;
    List<ParseObject> parseOjb;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    String gameId;

    //String gameId;
    private List<GameItem> gameItems = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        gameId = extras.getString("gameId");

        // Get the view from listview_main.xml
        setContentView(R.layout.activity_display_pictures);
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(DisplayPicturesActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Parse.com Custom ListView Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            gameItems = new ArrayList<GameItem>();
            try {
                // Locate the class table named "GameList with images" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ImageUpload");

                		query.whereEqualTo("gameId", gameId);

                parseOjb = query.find();
                for (ParseObject result : parseOjb) {
                    // Locate images in imageFile column to pareFile
                    ParseFile image = (ParseFile) result.get("ImageFile");

                    GameItem anItem = new GameItem();
                    anItem.setGameItem((String) (result.get("gameItem")));
                    anItem.setPhoto((String)image.getUrl());

                    gameItems.add(anItem);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(DisplayPicturesActivity.this, gameItems);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}

