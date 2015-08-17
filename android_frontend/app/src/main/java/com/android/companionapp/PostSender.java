package com.android.companionapp;

/**
 * Created by Nishan on 8/8/2015.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostSender extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://10.0.2.2/android/receive.php?id="+params[0]+"&ans="+params[1];
            Log.e("THESENDER",postReceiverUrl);
            //Log.v(TAG, "postURL: " + postReceiverUrl);

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            // add your data
            //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("qid", params[0]));
            //nameValuePairs.add(new BasicNameValuePair("sendans", params[1]));

            //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            //HttpEntity resEntity = response.getEntity();
            /*
            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                //Log.v(TAG, "Response: " +  responseStr);

                // you can add an if statement here and do other actions based on the response
            }*/

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}