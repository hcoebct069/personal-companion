package com.android.companionapp;

/**
 * Created by Nishan on 8/8/2015.
 */
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import android.os.AsyncTask;
import android.util.Log;

public class PostFetcher extends AsyncTask<String, Void, JSONArray> {
    InputStream is = null;
    JSONArray jsonArray;

    @Override
    protected JSONArray doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            //Log.e("THEURL", params[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //Log.e("THEURL", "HTTPREQUEST");

            //conn.setReadTimeout(10000 /* milliseconds */);
            //conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            //Log.e("THEURL", "GETMethod");
            //conn.setDoInput(true);
            is = conn.getInputStream();
            //Log.e("THEURL","GETINPUTSTREAM");

            conn.connect();
            //Log.e("THEURL", "Connect");

            //int response = conn.getResponseCode();
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            //byte[] bytes = IOUtils.toByteArray(is);
            //byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
            //int len = bytes.length;
            //Log.e("THESIZE", bytes.length +" sizes");
            //Log.e("THEURL", "Reader");
            //Log.e("THEURL", reader.toString());
            char[] buffer=new char[8000];
            reader.read(buffer);
            String output= new String(buffer);
            String[] op = output.split("<");
            output=op[0];
            Log.e("THEURL", "This HERE : "+output);
            Log.e("THEURL","Converting to ARRAY");
            jsonArray = new JSONArray(output);
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.e("THEURL", "JSONARRAY : " + jsonArray.getString(i));
            }



            //Log.e("THEURL",posts.toString());
            is.close();

        } catch(Exception ex) {
        }
        return jsonArray;
    }
}