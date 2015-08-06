package com.android.companionapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private LinearLayout actionBar;
    private DrawerLayout mDrawerLayout;
    private ImageButton navToggle;
    private ListView navDrawer;
    NavDrawerAdapter navAdapter;
    FragmentManager frgManager = getFragmentManager();
    Fragment fragment = null;
    ImageButton navBack;

    List<NavDrawerItem> navList;

    FeedAdapter feedAdapter;
    List<Feed> feedList;
    ListView feedMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            Intent myIntent = new Intent(MainActivity.this, StartScreen.class);
            MainActivity.this.startActivity(myIntent);


        navList = new ArrayList<NavDrawerItem>();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer = (ListView) findViewById(R.id.nav_drawer);
        navToggle = (ImageButton) findViewById(R.id.navToggle);

        navToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(navDrawer);
            }
        });

        navList.add(new NavDrawerItem("Home", R.drawable.ic_home_white));
        navList.add(new NavDrawerItem("News", R.drawable.ic_reorder_white));
        navList.add(new NavDrawerItem("Music", R.drawable.ic_music_note_white));
        navList.add(new NavDrawerItem("Tweets",R.drawable.ic_people_white));
        navList.add(new NavDrawerItem("Questionnaire",R.drawable.ic_question_answer_white));
        navList.add(new NavDrawerItem("Settings", R.drawable.ic_settings_white));

        navAdapter = new NavDrawerAdapter(this, R.layout.nav_drawer_item,
                navList);

        navDrawer.setAdapter(navAdapter);

        navDrawer.setOnItemClickListener(new DrawerItemClickListener());



        feedList = new ArrayList<Feed>();
        feedMain = (ListView) findViewById(R.id.main_feeds);
        //new LoadImage().execute("http://www.learn2crack.com/wp-content/uploads/2014/04/node-cover-720x340.png");
        //Drawable draw= new BitmapDrawable(getResources(), bitmap);
        feedList.add(new Feed("Title1", "Description", "http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg"));
        feedList.add(new Feed("Title2", "Description", "http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2014/4/11/1397210130748/Spring-Lamb.-Image-shot-2-011.jpg"));
        //feedList.add(new FeedItem(bitmap, "Title2", "Description"));
        //feedList.add(new Feed("Title1","Description","http://localhost/android/2.jpg"));
        //feedList.add(new Feed("Title2","Description","http://localhost/android/5.jpg"));

        feedAdapter = new FeedAdapter(this,feedList);
        feedMain.setAdapter(feedAdapter);

        for(Feed f:feedList){
            f.loadImage(feedAdapter);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            frgManager.beginTransaction().replace(R.id.content_frame, new FragmentSettings()).addToBackStack("Settings")
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SelectItem(int position) {
        String tag = null;
        switch(position){
            case 0:
                tag=null;
                frgManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case 1:
                tag="News";
                fragment = new FragmentNews();
                break;
            case 2:
                tag="Music";
                fragment = new FragmentMusic();
                break;
            case 3:
                tag="Tweet";
                fragment = new FragmentTweet();
                break;
            case 4:
                tag="Question";
                fragment = new FragmentQuestion();
                break;
            case 5:
                tag="Settings";
                fragment = new FragmentSettings();
                break;
            default:
                tag=null;
                break;
        }
        if(tag!=null) {
            frgManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(tag)
                    .commit();
        }
        navDrawer.setItemChecked(position, true);
        //setTitle(navList.get(position).getItemName());
        mDrawerLayout.closeDrawer(navDrawer);

    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SelectItem(position);

        }
    }

    @Override
    public void onBackPressed(){
        if(getFragmentManager().getBackStackEntryCount()>0) {
            getFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }
}
