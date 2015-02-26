package com.example.testfindplaces;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testfindplaces.custom.Converters;
import com.example.testfindplaces.custom.PathJSONParser;
import com.example.testfindplaces.network.LocationService;
import com.example.testfindplaces.network.LocationService.LocationServiceListener;
import com.example.testfindplaces.pojos.DirectionDataHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class DirectionsActivity extends ActionBarActivity implements LocationServiceListener,TabListener,OnMyLocationChangeListener {

	 private static  String API_KEY = "&key=";
     private static final String DIRECTIONS="https://maps.googleapis.com/maps/api/directions/json?";
     private static final String ORIGIN="origin=";
     private static final String DESTINATION="&destination=";
     private static final String MODE="&mode=",MODE_DRIVING="driving",MODE_CYCLING="bicycling",MODE_WALKING="walking";
     private LocationService service;
     private Location location,destLocation;
     private GoogleMap map;
     DirectionDataHolder dataHolder;
     List<List<HashMap<String, String>>> routes;
     
     ActionBar actionBar; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_direction);
		API_KEY="&key="+getResources().getString(R.string.MAP_KEY);
		map=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		service=new LocationService(this);
		service.setLocationServiceListener(this);  
		service.connect();
		destLocation=new Location("destination");
		destLocation.setLatitude(getIntent().getDoubleExtra("lat", 0));
		destLocation.setLongitude(getIntent().getDoubleExtra("lng", 0)); 
		actionBar=getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.addTab(actionBar.newTab().setIcon(R.drawable.driving_bus).setText("Driving").setTag(MODE_DRIVING).setTabListener(this)); 
		actionBar.addTab(actionBar.newTab().setIcon(R.drawable.walking).setText("Walking").setTag(MODE_WALKING).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setIcon(R.drawable.bicycle).setText("Cycling").setTag(MODE_CYCLING).setTabListener(this));	 
		mapSetttings();
	}
	private String findDirection(String m)
	{ 
		String url=DIRECTIONS+ORIGIN+location.getLatitude()+","+location.getLongitude()+DESTINATION+destLocation.getLatitude()+","+destLocation.getLongitude()+MODE+m+API_KEY;
		Log.i("url", url);
		HttpGet get=new HttpGet(url); 
		HttpResponse response;
		
		try {
			 			 
			HttpClient client=new DefaultHttpClient();
			response=client.execute(get);
			
			return  Converters.responseTOString(response); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return url;
		
	}
	private void parseJson(JSONObject object)
	{
		PathJSONParser parser = new PathJSONParser();
        dataHolder = parser.parse2(object);

        PolylineOptions polyLineOptions = null;
        new ArrayList<LatLng>();
        polyLineOptions = new PolylineOptions();
        polyLineOptions.addAll(dataHolder.polyData);
      
        if(polyLineOptions.getPoints().size()>0)
        {
        	map.clear();
        	polyLineOptions.width(4);
            polyLineOptions.color(Color.BLUE);
            map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), 
            		location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation))
            		.title(dataHolder.startAddress.replaceAll(",", "\\n"))
            		.anchor(0.5f, 0.5f));
            map.addMarker(new MarkerOptions().position(new LatLng(destLocation.getLatitude(), 
            		destLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))
            		.title(dataHolder.endAddress.replaceAll(",", "\\n"))
            		.anchor(0.5f, 0.5f));
            map.addPolyline(polyLineOptions); 
            actionBar.setTitle("Distacne:"+dataHolder.distance+"  Time:"+dataHolder.duration);
        }
        else
        {
        Toast.makeText(this, "This Route not available \n Shifting to Driving route", Toast.LENGTH_SHORT).show(); 
        actionBar.selectTab(actionBar.getTabAt(0));
        }
          map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13.5f));
	}
	class directionTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params) { 
			 
			return findDirection(params[0]); 
			
		}
		@Override
		protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if(result!=null)
				{
					try {
						parseJson(new JSONObject(result));
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
				}
				
		}
	}
	private void callTask(String mode)
	{ 
		
		new directionTask().execute(mode);
	}
	@Override
	public void onServiceConnected() {
		if(service.isconnected())
		{	
		location=service.displayCurrentLocation();
		callTask(MODE_DRIVING);
		
		}
		
	}
	private void mapSetttings()
	{
		map.setMyLocationEnabled(true); 
		map.getUiSettings().setMyLocationButtonEnabled(false); 
		map.getUiSettings().setZoomControlsEnabled(false); 
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL); 
		map.setOnMyLocationChangeListener(this);
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
	}
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		
		
	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {  
		if(service.isconnected())
		callTask(tab.getTag().toString());
		
	/*	switch (Integer.parseInt(tab.getTag().toString())) {
		case DRIVING: 
			if(service.isconnected())
			callTask(MODE_DRIVING);
			break;
		case CYCLING:
			callTask(MODE_CYCLING);
			break;
		case WALKING: 
			callTask(MODE_WALKING);
			break;

		default:
			break;
		}*/		
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
		
	}
	@Override
	public void onMyLocationChange(Location loc) {
	if(location.distanceTo(loc)>5)
	{ 
		Toast.makeText(this, ""+location.distanceTo(loc), Toast.LENGTH_SHORT).show();
		location=loc;
		callTask(actionBar.getSelectedTab().getTag().toString());
	}
	}
	@Override
	public void onLocationChanged(Location loc) {
		
		if(location.distanceTo(loc)>1)
		{ 
			//Toast.makeText(this, ""+location.distanceTo(loc), Toast.LENGTH_SHORT).show();
			location=loc;
			callTask(actionBar.getSelectedTab().getTag().toString());
		}
	}
	
}
