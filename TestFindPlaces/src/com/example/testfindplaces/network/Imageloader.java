package com.example.testfindplaces.network;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

public class Imageloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    
    LruCache<String, Bitmap> cache;
    public Imageloader(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    public Imageloader(ImageView bmImage,LruCache<String, Bitmap> lruCache) {
        this.bmImage = bmImage;
        this.cache=lruCache;
    }
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        
        Bitmap mIcon11 = cache!=null?getBitmapFromMemCache(urldisplay):null;
        if(mIcon11==null)
        {
        	try {
        		InputStream in = new java.net.URL(urldisplay).openStream();
        		mIcon11 = BitmapFactory.decodeStream(in);
        		if(cache!=null)
        		addBitmapToMemoryCache(urldisplay, mIcon11);
        	} catch (Exception e) {
        		Log.e("Error", e.getMessage());
        		e.printStackTrace();
        	}
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        cache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return cache.get(key);
	}
}
