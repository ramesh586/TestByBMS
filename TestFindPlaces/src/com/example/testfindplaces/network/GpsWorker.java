package com.example.testfindplaces.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

public class GpsWorker {

	Context con;
	LocationManager manager;
	
	public GpsWorker(Context c) {
	
		con=c;
		manager=(LocationManager)con.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean isGpsPresent()
	{
		PackageManager pm = con.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
	}
	public boolean isGpsEnabled()
	{
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	public void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(con);
	    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                   con.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(final DialogInterface dialog, final int id) {
	                    dialog.cancel();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
}
