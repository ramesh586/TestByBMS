package com.example.testfindplaces.db;

import java.util.ArrayList;
import java.util.List;

import com.example.testfindplaces.pojos.FavBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	

	private static final String DB_NAME="parker.db";
	private static final String TABLE="favorites";
	private static final String COL_ID="_id";
	private static final String COL_NAME="name";
	private static final String COL_LAT="lat";
	private static final String COL_LNG="lng";
	public static final int VERSION=1;
	SQLiteDatabase sdb;
	Context con;
	
	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		con=context; 
		
	} 
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS 'favorites' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT,'name' TEXT,'lat' INTEGER,'lng' INTEGER)");
		}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	public void opendb()throws SQLException
	{
	sdb=this.getWritableDatabase();
	//sdb.execSQL("PRAGMA foreign_keys = ON ");
	}
	public void foreignKeyOn()
	{
		sdb.execSQL("PRAGMA foreign_keys = ON ");
	}
	public void foreignKeyOff()
	{
		sdb.execSQL("PRAGMA foreign_keys = ON ");
	}
	public void closeDB() {
		
		//sdb.delete(TABLE_NAME, null, null);
		if(sdb.isOpen())
		sdb.close();
		
	}
	public long addFavorite(String name,double lng,double lat)
	{
		ContentValues con=new ContentValues();
		con.put(COL_NAME, name);
		con.put(COL_LAT, lat);
		con.put(COL_LNG, lng);
		return sdb.insert(TABLE, null, con);
	}
	public int removeFavorite(double lng,double lat)
	{ 
		return sdb.delete(TABLE, COL_LAT+"='"+lat+"' and "+COL_LNG+"='"+lng+"'", null);
	}
	public int isFavorite(double lng,double lat)
	{
		Cursor cr=sdb.query(TABLE, null, COL_LAT+"='"+lat+"' and "+COL_LNG+"='"+lng+"'", null, null, null, null);
		return cr.getCount();
	} 
	public List<FavBean> retFavList()
	{ 
		List<FavBean> list=new ArrayList<FavBean>();
		FavBean  bean;
		Cursor cr=sdb.query(TABLE, new String[]{COL_ID,COL_NAME,COL_LAT,COL_LNG}, null, null, null, null, COL_NAME);
		if(cr.getCount()>0)
		{
			for(;cr.moveToNext();)
			{
				bean=new FavBean();
				bean.id=cr.getInt(0);
				bean.name=cr.getString(1);
				bean.lat=cr.getDouble(2);
				bean.lng=cr.getDouble(3);
				list.add(bean);
			}
		}
		cr.close(); 
		return list; 
	}
	
}
