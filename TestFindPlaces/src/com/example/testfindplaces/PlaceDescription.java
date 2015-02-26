package com.example.testfindplaces;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testfindplaces.custom.Converters;
import com.example.testfindplaces.db.DbHelper;
import com.example.testfindplaces.network.GooglePlacesOperater;
import com.example.testfindplaces.network.Imageloader;
import com.example.testfindplaces.pojos.PlaceBean;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceDescription extends ActionBarActivity implements OnClickListener{

	  private static final String PLACES_DETAILS="https://maps.googleapis.com/maps/api/place/details/json?reference=";
	      private static final String API_KEY = "&key=";
	      private static final String PLACES_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=280&photoreference=";
	      private ImageView imageView;
	      private PlaceBean bean;
	      private String ref;
	      private ProgressDialog dialog;
	      private LinearLayout reviewLayout;
	      private TextView rating,timings,name,address,phone;
	      private ImageButton favorite;
	      private boolean isFav=false;
	      private DbHelper helper;
	      private RatingBar bar;
	      private Location location;
	      private LruCache<String, Bitmap> mMemoryCache;
	      
	      @Override
		protected void onPause() {
			
			super.onPause();
			helper.closeDB();
		}
		@Override
		protected void onResume() {
			super.onResume();
			helper=new DbHelper(this);
			helper.opendb();
			location=new Location("dummy");
			location.setLatitude(getIntent().getDoubleExtra("lat", 0));
			location.setLongitude(getIntent().getDoubleExtra("lng", 0)); 
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.placedescription);
		ref=getIntent().getStringExtra("reference");
		// int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 4;

		init();
		dialog=new ProgressDialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setMessage("Loading description");
		dialog.setCancelable(false);
		dialog.show();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);  
		mMemoryCache=new LruCache<String, Bitmap>(cacheSize)
				{
			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				return super.sizeOf(key, value);
			}
				};
		new descTask().execute();
		
	}
	
	private void init()
	{
		reviewLayout=(LinearLayout)findViewById(R.id.reviews_layout);
		rating=(TextView)findViewById(R.id.ratings);
		timings=(TextView)findViewById(R.id.timings);
		name=(TextView)findViewById(R.id.name);
		imageView=(ImageView)findViewById(R.id.imageView1);	
		favorite=(ImageButton)findViewById(R.id.imageButton1);
		address=(TextView)findViewById(R.id.address);
		phone=(TextView)findViewById(R.id.phone);
		bar=(RatingBar)findViewById(R.id.ratingBar1); 
		//bar.setFocusable(false);
		bar.setEnabled(false);  
		favorite.setOnClickListener(this);  
		phone.setAutoLinkMask(Linkify.PHONE_NUMBERS);
	}
	private void upDateUI()
	{
		if(bean!=null)
		{
			
			if(bean.ratings!=null)
			{	
			rating.setText(getResources().getString(R.string.Ratings)+"  "+ bean.ratings);
			bar.setRating(Float.parseFloat(bean.ratings));  
			}
			else
			{	
				rating.setVisibility(8);
				bar.setVisibility(8);
			}
			if(bean.time!=null)
			timings.setText(bean.time);
			else  
				timings.setText("Closed "); 
			phone.setText(getResources().getString(R.string.phone)+"  "+bean.phone);
			name.setText(bean.name);  
			getSupportActionBar().setTitle(bean.name);
			for(int i=0;i<bean.reviList.size();i++)
			{
				TextView tv=new TextView(this);
				tv.setText((i+1)+". "+bean.reviList.get(i));
				tv.setPadding(3, 3, 3, 3);
				tv.setTextSize(14); 
				reviewLayout.addView(tv); 
			}
			if(bean.reviList.size()<1)
			{
				TextView tv=new TextView(this);
				tv.setText("No reviews found");
				tv.setPadding(3, 3, 3, 3);
				tv.setTextSize(14); 
				reviewLayout.addView(tv); 
			}
			setImage(); 
			String addString=bean.address.replaceAll(",", "\n");
			address.setText(addString);
			if(helper.isFavorite(bean.lng, bean.lat)>0)
			{
				isFav=true; 
				favorite.setImageResource(R.drawable.favorite_selected);
				if(favoriteMenu!=null)
				favoriteMenu.setIcon(R.drawable.fav_selected);
			}
			dialog.dismiss();
		}
		else
			showNodataAlert();
		
	}
	private void setImage()
	{
		
		if(bean.photo_reference!=null)
			new Imageloader(imageView,mMemoryCache).execute(PLACES_PHOTO_URL+bean.photo_reference+API_KEY+getResources().getString(R.string.MAP_KEY));
		
		else 
			imageView.setImageResource(R.drawable.imagenotavailable);
	}
	
	private String getDecsription(String reference)
	{
		String url=PLACES_DETAILS+reference+API_KEY+getResources().getString(R.string.MAP_KEY);
		HttpGet get=new HttpGet(url); 
		HttpResponse response;
		
		try {
			 	 		 
			HttpClient client=new DefaultHttpClient();
			response=client.execute(get);
			
			 return Converters.responseTOString(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return url;
	}
	
	public class descTask extends AsyncTask<Void, Void, String>
	{
		
		@Override
		protected String doInBackground(Void... params) {
			return getDecsription(ref);  
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			super.onPostExecute(result);
			if(result!=null)
				try { 
					bean=GooglePlacesOperater.parseJson(new JSONObject(result)); 
					if(bean!=null)
					upDateUI();
					else
						showNodataAlert();
				} catch (JSONException e) {
					
					e.printStackTrace();
				} 
			else
				dialog.dismiss();
		}
	}
	private void showNodataAlert()
	{
		if(dialog.isShowing())
			dialog.dismiss();
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("No fata found");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("Unforunately data cannot be retrived please try after some time");
		builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			dialog.dismiss();
			finish();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	@Override
	public void onClick(View v) {
		if(isFav)
		{
			isFav=false;
			favorite.setImageResource(R.drawable.favorite_pressed);
			if(helper.removeFavorite(bean.lng, bean.lat)>0)
			Toast.makeText(this, "Favorite Removed", Toast.LENGTH_SHORT).show();
		}
		else
		{
			isFav=true;
			favorite.setImageResource(R.drawable.favorite_selected);
			if(helper.addFavorite(bean.name, bean.lng, bean.lat)>0)
				Toast.makeText(this, "Favorite added", Toast.LENGTH_SHORT).show();
		}
		
	}
	MenuItem favoriteMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.place_description, menu);
		favoriteMenu=(MenuItem)menu.findItem(R.id.fav);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.directions:
			Intent in=new Intent(this, DirectionsActivity.class);
			in.putExtra("lat", bean.lat);
			in.putExtra("lng", bean.lng);
			startActivity(in); 
			break;
		case android.R.id.home:
			finish();
			break;
		case R.id.fav:if(isFav)
		{
			isFav=false;
			item.setIcon(R.drawable.fav_normal);
			if(helper.removeFavorite(bean.lng, bean.lat)>0)
			Toast.makeText(this, "Favorite Removed", Toast.LENGTH_SHORT).show();
		}
		else
		{
			isFav=true;
			item.setIcon(R.drawable.fav_selected);
			if(helper.addFavorite(bean.name, bean.lng, bean.lat)>0)
				Toast.makeText(this, "Favorite added", Toast.LENGTH_SHORT).show();
		}
		break;
		default:
			break;
			}
		return true;
	}
	
	
}
