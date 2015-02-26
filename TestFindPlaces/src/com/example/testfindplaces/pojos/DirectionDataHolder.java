package com.example.testfindplaces.pojos;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class DirectionDataHolder {

	public ArrayList<LatLng>  polyData;
	public String distance,duration,startAddress,endAddress;
	
	public class polyData
	{
		public LatLng point;
		public String distance,duration,instruction;
	}
}
