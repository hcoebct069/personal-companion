package com.android.companionapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;

public class Feed {
	private String feed_name;
	private String feed_desc;
	private String feed_img_url;
	private String feed_url;
	private Bitmap feed_image;
	private String feed_type;
	private FeedAdapter adapter;

	public Feed (String feed_name,String feed_desc,String feed_img_url,String feed_url,String feed_type){
		this.feed_name = feed_name;
		this.feed_desc = feed_desc;
		this.feed_img_url = feed_img_url;
		this.feed_image = null;
		this.feed_url = feed_url;
		this.feed_type = feed_type;
	}
	public String getName(){return feed_name;}
	public void setName(String feed_name){this.feed_name = feed_name;}
	public String getDesc(){return feed_desc;}
	public void setDesc(String feed_desc){this.feed_desc = feed_desc;}
	public String getUrl(){return feed_img_url;}
	public void setUrl(String feed_img_url){this.feed_img_url = feed_img_url;}
	public String getFeedUrl(){return feed_url;}
	public void setFeedUrl(String feed_url){this.feed_url = feed_url;}
	public String getType(){return feed_type;}
	public void setType(String feed_type){this.feed_type = feed_type;}
	public Bitmap getImage(){return feed_image;}
	//public void setName(String feed_name){this.feed_name = feed_name;}

	public FeedAdapter getAdapter(){return adapter;}
	public void setAdapter(FeedAdapter adapter){this.adapter = adapter;}

	public void loadImage(FeedAdapter adapter){
		this.adapter = adapter;
		if(feed_img_url!=null && !feed_img_url.equals("")){
			new ImageLoadTask().execute(feed_img_url);
			
		}
	}
	private class ImageLoadTask extends AsyncTask<String,String,Bitmap> {
		@Override 
		protected void onPreExecute(){
			//Before Async Task
		}
		protected Bitmap doInBackground(String... param){
			try{
				//Bitmap b = ImageService.getBitmapFromURLWithScale(param[0]);
				Bitmap b = BitmapFactory.decodeStream((InputStream) new URL(param[0]).getContent());
				return b;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
		protected void onProgressUpdate(String... progress){

		}
		protected void onPostExecute(Bitmap ret){
			if(ret!=null){
				//Image Load Successful
				feed_image = ret;
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
			}else{
				//Image Load Fail
			}
		}
	}
}