package com.example.testfindplaces.custom;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import com.example.testfindplaces.PlaceDescription;
import com.example.testfindplaces.R;
import com.example.testfindplaces.db.DbHelper;
import com.example.testfindplaces.network.Imageloader;
import com.example.testfindplaces.pojos.FavBean;

public class MyRecyclerAdapter extends RecyclerView.Adapter<FeedListRowHolder> implements View.OnClickListener{

    private List<FavBean> feedItemList;
    private Activity activity;
    private DbHelper helper;
    public MyRecyclerAdapter(Activity context, List<FavBean> feedItemList) {
        this.feedItemList = feedItemList;
        activity=context;
        helper=new DbHelper(activity);
		helper.opendb();
    }

    @Override
    public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        FeedListRowHolder mh = new FeedListRowHolder(v);
       
      //  v.setOnClickListener(this); 
        return mh;
    }

    @Override
    public void onBindViewHolder(FeedListRowHolder feedListRowHolder, int i) {
        FavBean feedItem = feedItemList.get(i); 
        	 if(feedItem.icon!=null)
        new Imageloader(feedListRowHolder.thumbnail).execute(feedItem.icon);
        feedListRowHolder.title.setText(feedItem.name);
        if(helper.isFavorite(feedItem.lng, feedItem.lat)>0) 
        	feedListRowHolder.fav.setImageResource(R.drawable.fav_selected); 
        feedListRowHolder.main.setOnClickListener(this);
        feedListRowHolder.main.setTag(i);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

	
	@Override
	public void onClick(View v) {
		
		int index=(Integer) v.getTag();
		FavBean bean=feedItemList.get(index);
		Intent in=new Intent(activity, PlaceDescription.class);
		in.putExtra("reference", bean.reference); 
		in.putExtra("lat", bean.lat);
		in.putExtra("lng", bean.lng); 
		activity.startActivity(in);
		Toast.makeText(activity, bean.name, Toast.LENGTH_SHORT).show();
	}
}