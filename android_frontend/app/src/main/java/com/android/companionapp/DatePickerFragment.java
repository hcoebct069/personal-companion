package com.android.companionapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by Nishan on 8/16/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    Button setDate;

    public DatePickerFragment(Button setDate){
        this.setDate=setDate;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        //setDate = (Button) setDate.findViewById(R.id.date_btn);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        // setDate.setText(year+"-"+month+"-"+day);
        // Button setDate = (Button) view.findViewById(R.id.date_btn);

        setDate.setText(day+" "+getMonthName(month)+" "+year);
        setDate.setBackgroundResource(R.color.transparent);
        setDate.setTextColor(Color.parseColor("#222222"));
        setDate.setPadding(0,0,0,0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 0);
        setDate.setLayoutParams(params);
    }
    public String getMonthName(int month){
        month+=1;
        String mName = null;
        switch (month){
            case 1:
                mName="January";
                break;
            case 2:
                mName="Febraury";
                break;
            case 3:
                mName="March";
                break;
            case 4:
                mName="April";
                break;
            case 5:
                mName="May";
                break;
            case 6:
                mName="June";
                break;
            case 7:
                mName="July";
                break;
            case 8:
                mName="August";
                break;
            case 9:
                mName="September";
                break;
            case 10:
                mName="October";
                break;
            case 11:
                mName="November";
                break;
            case 12:
                mName="December";
                break;
        }
        return mName;
    }
}
