package com.example.testfindplaces;

import java.util.List;
import java.util.Locale;

import com.example.testfindplaces.custom.MyRecyclerAdapter;
import com.example.testfindplaces.custom.PreferenceData;
import com.example.testfindplaces.network.GooglePlacesOperater;
import com.example.testfindplaces.pojos.FavBean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

public class SelectedPlaceListActivity extends Activity implements RecyclerView.RecyclerListener,OnItemTouchListener{

	private GooglePlacesOperater placesOperater;
	private ProgressDialog dialog;
	private static final String TAG ="SelectedPlaceListActivity";
	private RecyclerView mRecyclerView;
	 private MyRecyclerAdapter adapter;
	private Tast mTast;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState); 
			setContentView(R.layout.activity_selected_place_list);
			dialog=new ProgressDialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setMessage("Please wait searching...");
			dialog.setCancelable(true);
			mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
	        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
	        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); 
			placesOperater=new GooglePlacesOperater(this);
			getActionBar().setDisplayHomeAsUpEnabled(true);;
			getActionBar().setTitle(PreferenceData.getType(this).toUpperCase(Locale.getDefault())); 
			setQuery(PreferenceData.getType(this), PreferenceData.getLocation(this)); 
	}
	@Override
	protected void onResume() {
		
		super.onResume();
	}
	private void setQuery(String park,Location loc)
	{
		
		dialog.show();
		if(loc!=null)
		{
			Log.i(loc.getLatitude()+"", loc.getLongitude()+"");
		placesOperater.setupQuery(park, loc);
		
		mTast=new Tast();
		mTast.execute();
		}
		else
		{
			dialog.dismiss();
			Toast.makeText(this, "please click MyLocation button", Toast.LENGTH_SHORT).show();
		}
	}
	class Tast extends AsyncTask<Void, Void, List<FavBean>>
	{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute(); 
		}

		@Override
		protected List<FavBean> doInBackground(Void... params) { 
			return placesOperater.searchPlaces();
			 
		}

		@Override
		protected void onPostExecute(List<FavBean> list) {
			
			
			if(list!=null)
			if(list.size()>0)
			{
				Log.i(TAG, list.size()+"");
				adapter=new MyRecyclerAdapter(SelectedPlaceListActivity.this, list);
				mRecyclerView.setAdapter(adapter); 
			}
			dialog.dismiss();
		} 
		
		 
	}
	@Override
	public void onViewRecycled(ViewHolder arg0) {
		
		
	}
	@Override
	public boolean onInterceptTouchEvent(RecyclerView arg0, MotionEvent arg1) {
		
		return false;
	}
	@Override
	public void onTouchEvent(RecyclerView arg0, MotionEvent arg1) {
		
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}
	
}
