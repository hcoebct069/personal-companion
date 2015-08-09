package com.android.companionapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Nishan on 8/8/2015.
 */
public class Notification {
    private Context mContext;
    public Notification(Context context){
        this.mContext=context;
    }
    protected void displayNotification(String title, String text, String ticker, Class cls) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.mipmap.ic_launcher;
        Context context = mContext.getApplicationContext();
        android.app.Notification notification = new android.app.Notification(icon, ticker, System.currentTimeMillis());
        Intent notificationIntent = new Intent(context, cls);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


        notification.setLatestEventInfo(context, title, text, contentIntent);

        notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, notification);
    }
}
