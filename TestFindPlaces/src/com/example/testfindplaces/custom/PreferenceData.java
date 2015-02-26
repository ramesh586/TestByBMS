package com.example.testfindplaces.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

public class PreferenceData {

	public static final String SHARED_PREF="credentials";
	private static SharedPreferences preferences;
	private static Editor editor;
	private static final String KEY_LAT="lat";
	private static final String KEY_LNG="lng";
	private static final String KEY_TYPE="type";
	
	public static void putLocation(Context con,Location token,String type)
	{
		
			if(preferences==null)
				preferences=con.getSharedPreferences(SHARED_PREF, 0);
			editor=preferences.edit();
			editor.putFloat(KEY_LAT,  (float) token.getLatitude());  
			editor.putFloat(KEY_LNG, (float) token.getLongitude());
			editor.putString(KEY_TYPE, type);
			editor.commit();
	}
	public static Location getLocation(Context con) 
	{
		if(preferences==null)
			preferences=con.getSharedPreferences(SHARED_PREF, 0);
		Location loc=new Location("Test");
		loc.setLatitude(preferences.getFloat(KEY_LAT, 0));  
		loc.setLongitude(preferences.getFloat(KEY_LNG, 0));
		return loc;
	}
 
	public static String getType(Context con)
	{
		if(preferences==null)
			preferences=con.getSharedPreferences(SHARED_PREF, 0);
		
		return preferences.getString(KEY_TYPE, null); 
	}
}
