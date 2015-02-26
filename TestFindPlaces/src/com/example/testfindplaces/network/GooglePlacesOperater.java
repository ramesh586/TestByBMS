package com.example.testfindplaces.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testfindplaces.R;
import com.example.testfindplaces.R.string;
import com.example.testfindplaces.custom.Converters;
import com.example.testfindplaces.custom.CustomSSLSocketFactory;
import com.example.testfindplaces.custom.CustomX509TrustManager;
import com.example.testfindplaces.pojos.FavBean;
import com.example.testfindplaces.pojos.PlaceBean;

import android.content.Context;
import android.location.Location;
import android.provider.Settings.Global;
import android.util.Log;

@SuppressWarnings("unused")
public class GooglePlacesOperater {
	
	Context con;
	 public interface volleyListener
	    {
	    	abstract public void requestsCompleted(List<FavBean> list);
	    }
	    
	 private static final String PLACES_DETAILS="https://maps.googleapis.com/maps/api/place/details/json?reference=";
     
     private static final String PLACES_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=280&photoreference=";
	    private static String PLACES_RADIUS="&radius=300";
	    private static String PLACES_LOCATION="&sensor=false&location=";
	    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	  
	     private static String PLACES_TEXT_SEARCH_QUERY="&query=";
	    private static  String API_KEY ;
	    private boolean has_nexttoken=false;
	    private String pageToken="&pagetoken=";
	    private List<FavBean> list;  
	    
	    
	    
	public GooglePlacesOperater(Context con) {
	this.con=con;	
	API_KEY=con.getResources().getString(R.string.MAP_KEY);
	}
	public void setupQuery(String query,Location location)
	{	
		PLACES_LOCATION="&sensor=false&location="+location.getLatitude()+","+location.getLongitude()+"&key="+API_KEY;
		PLACES_TEXT_SEARCH_QUERY="&query="+query;
	}
	
	public List<FavBean> searchPlaces()
	{
		list=new ArrayList<FavBean>();
    	doTheSearch();
    	return list;		
	}
	public void doTheSearch()
	{
		String url=PLACES_TEXT_SEARCH_URL+PLACES_TEXT_SEARCH_QUERY+PLACES_RADIUS+PLACES_LOCATION;
    	
    	if(has_nexttoken) 
		{
    		getCommon(url+pageToken);
		}
    	else
    	{
    		getCommon(url);
    	}
	}
	public  void getCommon(String url)
	{
		Log.i("url", url);
		HttpGet get=new HttpGet(url); 
		HttpResponse response;
		
		try {
			 			 
			HttpClient client=secureLoadData();
			response=client.execute(get);
			
			 Json(new JSONObject( Converters.responseTOString(response)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	private void Json(JSONObject object)
    {
    	JSONObject mainObject=object;
    	
    	
    	if(mainObject!=null)
    	{
    		FavBean bean=new FavBean();
    		try {
				JSONArray resultsArray=mainObject.getJSONArray("results");
				pageToken="&pagetoken=";
				for(int i=0;i<resultsArray.length();i++) 
				{
					bean=new FavBean();
					JSONObject resultObject=resultsArray.getJSONObject(i);
					bean.name=resultObject.getString("name");
					bean.reference=resultObject.getString("reference");
					bean.address=resultObject.getString("formatted_address");
					if(!resultObject.isNull("icon"))
					bean.icon=resultObject.getString("icon");
					
					JSONObject geometryObject=resultObject.getJSONObject("geometry");
					bean.lat=geometryObject.getJSONObject("location").getDouble("lat");
					bean.lng=geometryObject.getJSONObject("location").getDouble("lng");
					list.add(bean);
				}
				Log.i("array length", list.size()+"");
				
				if(!mainObject.isNull("next_page_token"))
				{
					pageToken=pageToken+mainObject.getString("next_page_token");
					has_nexttoken=true;
					/*try {
	    				Thread.sleep(2000);
	    			} catch (InterruptedException e) {
	    				
	    				e.printStackTrace();
	    			}	*/
					doTheSearch();
				}
				//if(list.size()>0)
					//listener.requestsCompleted(list); 
			} catch (JSONException e) {
								e.printStackTrace();
			}
    	}
    }
	public static PlaceBean parseJson(JSONObject json)
	{
	PlaceBean	bean=new PlaceBean();
		bean.reviList=new ArrayList<String>();
		if(json!=null)
		{
				
			try {
				if(json.getString("status").equals("OK"))
				{
				JSONObject result=json.getJSONObject("result");
				if(result!=null)
				{
					if(!result.isNull("opening_hours"))
					{	
					JSONObject open_hours=result.getJSONObject("opening_hours");
					if(open_hours!=null)
					{
						JSONObject open_close=open_hours.getJSONArray("periods").getJSONObject(0);
						bean.time=open_close.getJSONObject("open").getString("time")+" to "+open_close.getJSONObject("close").getString("time");
					}
					}
					if(!result.isNull("photos"))
					{
					bean.photo_reference=result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
					}
					bean.name=result.getString("name");
					if(!result.isNull("rating"))
					bean.ratings=result.getString("rating"); 
					if(!result.isNull("reviews"))
					{
					JSONArray reviews=result.getJSONArray("reviews");
					for(int i=0;i<reviews.length();i++) 
						bean.reviList.add(i, reviews.getJSONObject(i).getString("text"));
					}
					bean.address=result.getString("formatted_address");
					if(!result.isNull("formatted_phone_number"))
					{
					bean.phone=result.getString("formatted_phone_number").replaceAll("\\s", ""); 
					}
					else
					{
						if(!result.isNull("international_phone_number"))
						{
						bean.phone=result.getString("international_phone_number").replaceAll("\\s", "");
						 
						}
						else 
							bean.phone="Number not available";
					}
					JSONObject geometryObject=result.getJSONObject("geometry");
					bean.lat=geometryObject.getJSONObject("location").getDouble("lat");
					bean.lng=geometryObject.getJSONObject("location").getDouble("lng");
					
					//findDirection();
				}
				}
				 
			} catch (JSONException e) {
				
				e.printStackTrace();
				
			}
			
		}
		return bean; 
	}
	 public static DefaultHttpClient secureLoadData() throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException,
     URISyntaxException, KeyStoreException, UnrecoverableKeyException {

 SSLContext ctx = SSLContext.getInstance("TLS");
 ctx.init(null, new TrustManager[] { new CustomX509TrustManager() },
         new SecureRandom());

 HttpClient client = new DefaultHttpClient();

 SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
 ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
 ClientConnectionManager ccm = client.getConnectionManager();
 SchemeRegistry sr = ccm.getSchemeRegistry();
 sr.register(new Scheme("https", ssf, 443)); 
 DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
         client.getParams());

// HttpGet get = new HttpGet(new URI(url));
// HttpResponse response = sslClient.execute(get);

 return sslClient;
}
}
