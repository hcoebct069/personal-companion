package com.android.companionapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nishan on 8/3/2015.
 */
public class FragmentReminder extends Fragment{
    ImageButton navBack;
    Button setDate;
    Button setTime;
    Button saveRem;
    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    public FragmentReminder() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminder, container,
                false);
        navBack = (ImageButton) view.findViewById(R.id.navBack);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        setDate = (Button) view.findViewById(R.id.date_btn);
        setDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new DatePickerFragment(setDate);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        setTime = (Button) view.findViewById(R.id.time_btn);
        setTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogFragment newFragment = new TimePickerFragment(setTime);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        final EditText msgArea = (EditText) view.findViewById(R.id.reminder_msg);
        saveRem = (Button) view.findViewById(R.id.rem_btn);
        saveRem.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                String msg = msgArea.getText().toString();
                    //Toast.makeText(v.getContext(), "Reminder message set", Toast.LENGTH_LONG).show();
                if(TextUtils.isEmpty(msg)){
                    //e.printStackTrace();
                    Toast.makeText(v.getContext(), "You didn't set any reminder message", Toast.LENGTH_LONG).show();
                }else {

                    Long seconds = Long.valueOf(0);
                    Date dt = null;

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date currentDate = new Date();
                    dateFormat.format(currentDate);
                    int day = 0, yr = 0, mth = 0, hr = 0, mn = 0;
                    String dt_set = setDate.getText().toString();
                    String tm_set = setTime.getText().toString();
                    if (dt_set.toLowerCase() != "set date") {
                        String[] dt_arr = dt_set.split(" ");
                        day = Integer.parseInt(dt_arr[0]);
                        yr = Integer.parseInt(dt_arr[2]);
                        mth = getNumFromMonth(dt_arr[1]);
                    }
                    if (tm_set.toLowerCase() != "set time") {
                        String[] tm_arr = tm_set.split(":");
                        hr = Integer.parseInt(tm_arr[0]);
                        mn = Integer.parseInt(tm_arr[1]);
                    }
                    Date newDate = null;
                    try {
                        newDate = dateFormat.parse(yr + "/" + mth + "/" + day + " " + hr + ":" + mn);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Date newDate = new Date(yr,mth,day,hr,mn);
                    dateFormat.format(newDate);
                    seconds = Long.valueOf(newDate.getTime() - currentDate.getTime())/1000;
                    if (seconds < 0) {
                        Toast.makeText(v.getContext(), "Please set a proper date. And please set one for the future!", Toast.LENGTH_LONG).show();
                    }else{
                        Reminder r = new Reminder(msg,seconds,newDate.toString());
                        r.scheduleAlarm(v);
                    }
                }
            }
        });
        return view;
    }

    public int getNumFromMonth(String month){
        int num=0;
        switch(month.toLowerCase()){
            case "january":
                num=0;
                break;
            case "february":
                num=1;
                break;
            case "march":
                num=2;
                break;
            case "april":
                num=3;
                break;
            case "may":
                num=4;
                break;
            case "june":
                num=5;
                break;
            case "july":
                num=6;
                break;
            case "august":
                num=7;
                break;
            case "september":
                num=8;
                break;
            case "october":
                num=9;
                break;
            case "november":
                num=10;
                break;
            case "december":
                num=11;
                break;

        }
        return num+1;
    }
}
