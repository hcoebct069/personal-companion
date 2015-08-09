package com.android.companionapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Nishan on 8/9/2015.
 */
public class ConnectionDetector {
    private Context context;
    public ConnectionDetector(Context context){
        this.context = context;
    }
    public boolean connected(){
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(con!=null){
            NetworkInfo[] info = con.getAllNetworkInfo();
            if(info!=null) {
                for (int i = 0; i <info.length;i++){
                    if(info[i].getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
