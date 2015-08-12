package com.example.aa.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;




public class MainActivity2 extends ActionBarActivity {

    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infoscreen);
        back = (Button) findViewById(R.id.backfrominfo);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();

            }
        });
    }


}
