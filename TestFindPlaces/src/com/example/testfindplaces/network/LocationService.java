package com.example.testfindplaces.network;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	LocationClient mLocationClient;
	Context con;
	Address address;
	LocationManager locationManager;
	 private LocationRequest mLocationRequest;
	public interface LocationServiceListener{
		public abstract void onServiceConnected();
		public abstract void onLocationChanged(Location location);
	}
	public LocationServiceListener serviceListener;
	public LocationService(Context context) {
		
	mLocationClient=new LocationClient(context, this, this);
	con=context;
	locationManager=(LocationManager)con.getSystemService(Context.LOCATION_SERVICE);
	mLocationRequest=LocationRequest.create();
	mLocationRequest.setInterval(3000);
	mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	mLocationRequest.setFastestInterval(1000);
	
	}
	public void setLocationServiceListener(LocationServiceListener listener)
	{
		serviceListener=listener;
	}
	public void connect()
	{
		mLocationClient.connect();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				
				
			}
		});
	}
	public void disconnect()
	{
		mLocationClient.disconnect();
	}
	 public Location displayCurrentLocation() {
	     // Get the current location's latitude & longitude
		 try{
	      Location currentLocation = mLocationClient.getLastLocation();
	      if(currentLocation==null)
	      {
	    	  GpsWorker worker=new GpsWorker(con);
	    	  currentLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    	  
	    	  if((currentLocation==null)&&(worker.isGpsPresent())) 
	    	  {
	    		 if(worker.isGpsEnabled()) 
	    	  currentLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    		 else
	    			 worker.buildAlertMessageNoGps();
	    	  }
	    			
	       }
	      
	      return currentLocation;
		 }
		 catch(Exception exception)
		 { 
			  exception.printStackTrace();
		 }
		return null;
	   }
	 public boolean isconnected()
	 {
		 return mLocationClient.isConnected();
	 }
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		
		Toast.makeText(con, "Connection Failure : " + connectionResult.getErrorCode(),    Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		if(serviceListener!=null)
 		serviceListener.onServiceConnected(); 
		mLocationClient.requestLocationUpdates(mLocationRequest, locationListener); 
		// Toast.makeText(con, "Connected", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		
		Toast.makeText(con, "Disconnected. Please re-connect.",
			      Toast.LENGTH_SHORT).show();
	}
	LocationListener locationListener=new LocationListener() {
		
		@Override
		public void onLocationChanged(Location arg0) {
			serviceListener.onLocationChanged(arg0);
			
		}
	};
	
}
