package com.annemac.scavengerhuntapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleItemView extends Activity {
	// Declare Variables
	String gameItem;
	String photo;
	
	ImageLoader imageLoader = new ImageLoader(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from singleitemview.xml
		setContentView(R.layout.singleitemview);

		Intent i = getIntent();
		// Get the result of rank
		gameItem = i.getStringExtra("gameItem");
		// Get the result of country
		photo = i.getStringExtra("image");

		// Locate the TextViews in singleitemview.xml
		TextView txt = (TextView) findViewById(R.id.itemName);
		
		// Locate the ImageView in singleitemview.xml
		ImageView imgflag = (ImageView) findViewById(R.id.image);

		// Set results to the TextViews
		txt.setText(gameItem);
	
		// Capture position and set results to the ImageView
		// Passes flag images URL into ImageLoader.class
		imageLoader.DisplayImage(photo, imgflag);
	}
}