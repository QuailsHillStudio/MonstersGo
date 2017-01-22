package com.sar2016.panczuk.monstersgo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by olivier on 16/01/17.
 */

public class MonsterAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Monster> mDataSource;

    public MonsterAdapter(Context context, List<Monster> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.monster, parent, false);

        TextView tweetTextView =
                (TextView) rowView.findViewById(R.id.monster_name);
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.monster_image);
        //TextView userTextView =
          //      (TextView) rowView.findViewById(R.id.tweet_user);


        Monster tweet = (Monster) getItem(position);

        thumbnailImageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),mContext.getResources().getIdentifier(tweet.getImageName(), "drawable", mContext.getPackageName())));
        tweetTextView.setText(tweet.getName());
        //userTextView.setText(tweet.getImageName());

        return rowView;
    }
}
