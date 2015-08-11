package com.example.aa.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;




public class MainActivity extends ActionBarActivity {


int flag=0,num=1;

    Button val1;
    Button val2;
    Button info;
    Button skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView newtext = (TextView) findViewById(R.id.textView2);
        newtext.setText("This is question number" + num);
        val1 = (Button) findViewById(R.id.val1);
        val2 = (Button) findViewById(R.id.val2);
        info = (Button) findViewById(R.id.info);
        skip = (Button) findViewById(R.id.skip);



        val1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                flag = 1;
                num++;
                newtext.setText("This is question number"+num);
            }


        });



        val2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                flag=2;
                num++;
                newtext.setText("This is question number"+num);

            }
        });



        info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                Intent myIntent = new Intent(MainActivity.this,MainActivity2.class);


                MainActivity.this.startActivity(myIntent);


            }
        });



        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               flag=0;
                num++;
                newtext.setText("This is question number"+num);
            }
        });





    }



    }
