package com.example.testfindplaces.custom;
import com.example.testfindplaces.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail,fav;
    protected TextView title;
    protected LinearLayout main;

    public FeedListRowHolder(View view) {
        super(view);
        this.main=(LinearLayout)view.findViewById(R.id.mainlayout);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.fav=(ImageView)view.findViewById(R.id.fav);
    }

}