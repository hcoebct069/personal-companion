package com.android.companionapp;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List items = new ArrayList();
	public FeedAdapter(Context context,List items){
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}
	public int getCount(){
		return items.size();
	}
	public Feed getItem(int position){
		return (Feed) items.get(position);
	}
	public long getItemId(int position){
		return position;
	}
	public View getView(int position,View convertView,ViewGroup parent){
		ViewHolder holder;
		Feed f = (Feed) items.get(position);
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.feed_item,null);
			holder = new ViewHolder();
			holder.feed_name = (TextView) convertView.findViewById(R.id.feed_item_name);
			holder.feed_desc = (TextView) convertView.findViewById(R.id.feed_item_desc);
			holder.feed_image = (ImageView) convertView.findViewById(R.id.feed_image);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.feed_name.setText(f.getName());
		holder.feed_desc.setText(f.getDesc());
		if(f.getImage()!=null){
			holder.feed_image.setImageBitmap(f.getImage());
			int width = parent.getWidth();
			int height = width * f.getImage().getHeight() / f.getImage().getWidth();
			LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
			holder.feed_image.setLayoutParams(parms);
		}else{
			Drawable d=null;
			//Load Default Image Placeholder
			switch (f.getType()){
				case "news":
					holder.feed_image.setImageResource(R.drawable.news);
					d = convertView.getResources().getDrawable(R.drawable.news);
					break;
				case "media":
					holder.feed_image.setImageResource(R.drawable.yt);
					d = convertView.getResources().getDrawable(R.drawable.yt);
					break;
				case "tweet":
					holder.feed_image.setImageResource(R.drawable.tw);
					d = convertView.getResources().getDrawable(R.drawable.tw);
					break;
				default:
					holder.feed_image.setImageResource(R.mipmap.ic_launcher);
					d = convertView.getResources().getDrawable(R.mipmap.ic_launcher);
			}
			int width = parent.getWidth();
			int height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
			LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
			holder.feed_image.setLayoutParams(parms);
		}
		return convertView;
	}

	public static class ViewHolder{
		TextView feed_name;
		TextView feed_desc;
		ImageView feed_image;
	}
}