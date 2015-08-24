package com.android.companionapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.GregorianCalendar;

/**
 * Created by Nishan on 8/16/2015.
 */
public class Reminder extends AppCompatActivity{
    String message;
    Long seconds;
    String extraMsg;

    public Reminder(String message,Long seconds,String extraMsg){
        this.message=message;
        this.seconds=seconds;
        this.extraMsg=extraMsg;
    }
    public void scheduleAlarm (View v)
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        Long time = new GregorianCalendar().getTimeInMillis() + seconds * 1000;

        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
        //Notification ntfy = new Notification(this.getApplicationContext());
        //ntfy.displayNotification("Reminder Set", "Reminder has been set for "+extraMsg+" and message : '"+message+"'", "Reminder", MainActivity.class);
        Intent intentAlarm = new Intent(v.getContext(),AlarmReceiver.class).putExtra("rem_title", "Reminder From Companion");
        //new Intent()
        intentAlarm.putExtra("rem_desc", message);
        // create the object
        AlarmManager alarmManager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(v.getContext(), 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        int num;
        if(message.length()<5){
            num=message.length()-1;
        }else{
            num=(message.length()-1)/2;
        }
        Toast.makeText(v.getContext(), "Reminder has been set for " + extraMsg + " and message : '" + message.substring(0,num) + "...'", Toast.LENGTH_LONG).show();

    }
}
