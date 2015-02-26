package com.example.testfindplaces;


import com.example.testfindplaces.custom.PreferenceData;
import com.example.testfindplaces.network.GPSTracker;
import com.example.testfindplaces.network.LocationService;
import com.example.testfindplaces.network.LocationService.LocationServiceListener;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class ListItems extends ListActivity implements LocationServiceListener{

	private String[] places;
	
	    private LocationService service;
	    private Location location;
	    private static final String TAG="TEST";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			service=new LocationService(this);
			service.setLocationServiceListener(this);  
			service.connect();
			
			places=getResources().getStringArray(R.array.places);
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places));
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Log.i("Position", location.getLatitude()+" ds"+location.getLongitude()); 
			if(location!=null) 
			{
			PreferenceData.putLocation(this, location,places[position]);
			Intent in=new Intent(this, SelectedPlaceListActivity.class);
			//ActivityOptions options=ActivityOptions.makeScaleUpAnimation(v, 0, 100, v.getWidth(), v.getHeight());
			 
			startActivity(in);
			}
	}
	
			@Override
			public void onServiceConnected() { 
				Log.i(TAG, "Connected"); 
				if(service.isconnected())
				{	
					Log.i(TAG, "Connected2"); 
				location=service.displayCurrentLocation();
				new locationTask().execute();
				}
				
			}
class locationTask extends AsyncTask<Void, Void, Void>
{ 
	@Override
	protected void onPreExecute() {
		if(location==null)
		{	
		GPSTracker tracker=new GPSTracker(ListItems.this);
		location=tracker.getLocation();
		}
		//getLocationInfo(location.getLatitude(), location.getLongitude(), null, 0);
	}

				@Override
				protected Void doInBackground(Void... params) { 
					
					return null;
				}
				
			}
			@Override
			public void onLocationChanged(Location location) {
				
				
			}
			

}
