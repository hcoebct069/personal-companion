package com.android.companionapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by Nishan on 8/8/2015.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub
        String rem_title = intent.getStringExtra("rem_title");
        String rem_desc = intent.getStringExtra("rem_desc");

        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        // Show the toast  like in above screen shot
        //Toast.makeText(context, "", Toast.LENGTH_LONG).show();
        Notification ntfy = new Notification(context);
        ntfy.displayNotification(rem_title, rem_desc, rem_title, MainActivity.class);
    }

}