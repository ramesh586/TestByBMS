package com.example.testfindplaces.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

import android.util.Log;

public class Converters {

	public static String responseTOString(HttpResponse response)
	{
		InputStream inputStream;
		try {
			inputStream = response.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			StringBuilder stringBuilder = new StringBuilder();

			String bufferedStrChunk = null;

			while((bufferedStrChunk = bufferedReader.readLine()) != null){
				stringBuilder.append(bufferedStrChunk);
			}

	    Log.i("response", stringBuilder.toString());
	        return stringBuilder.toString();
		} catch (IllegalStateException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return ""; 

	}
}
