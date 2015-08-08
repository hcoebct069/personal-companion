package com.android.companionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class StartScreen extends AppCompatActivity {
    Button cBtn;
    TextToSpeech tt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        tt = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if(status!=TextToSpeech.ERROR){
                    tt.setLanguage(Locale.UK);
                    //tt.speak("Hi", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        //tts("Hi");
        cBtn = (Button) findViewById(R.id.cBtn);
        cBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"It seems your OS doesn't support Android TTS. Anyway, Hi!",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void onPause(){
        if(tt!=null){
            tt.stop();
            tt.shutdown();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void tts(String text){
    }
}
