package com.annemac.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	// Declare Variables
	Context context;
	LayoutInflater inflater;
	ImageLoader imageLoader;
	private List<GameItem> gameItems = null;
	private ArrayList<GameItem> arraylist;

	public ListViewAdapter(Context context,
			List<GameItem> gameItems) {
		this.context = context;
		this.gameItems = gameItems;
		inflater = LayoutInflater.from(context);
		this.arraylist = new ArrayList<GameItem>();
		this.arraylist.addAll(gameItems);
		imageLoader = new ImageLoader(context);
	}

	public class ViewHolder {
		TextView itemName;
		ImageView imageView;
	}

	@Override
	public int getCount() {
		return gameItems.size();
	}

	@Override
	public Object getItem(int position) {
		return gameItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.listview_item, null);
			// Locate the TextViews in listview_item.xml
			holder.itemName = (TextView) view.findViewById(R.id.itemName);
			//holder.country = (TextView) view.findViewById(R.id.country);
			//holder.population = (TextView) view.findViewById(R.id.population);
			// Locate the ImageView in listview_item.xml
			holder.imageView = (ImageView) view.findViewById(R.id.image);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
		holder.itemName.setText(gameItems.get(position).getGameItem());
		// Set the results into ImageView
		imageLoader.DisplayImage(gameItems.get(position).getPhoto(),holder.imageView);
		// Listen for ListView Item Click
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Send single item click data to SingleItemView Class
				Intent intent = new Intent(context, SingleItemView.class);
				
				// Pass game item
				intent.putExtra("gameItem",	(gameItems.get(position).getGameItem()));
				intent.putExtra("image",	(gameItems.get(position).getPhoto()));
				
				// Start SingleItemView Class
				context.startActivity(intent);
			}
		});
		return view;
	}

}
