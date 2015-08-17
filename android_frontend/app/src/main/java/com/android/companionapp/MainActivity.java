package com.android.companionapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity{

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


            //Intent myIntent = new Intent(MainActivity.this, StartScreen.class);
            //MainActivity.this.startActivity(myIntent);


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
        navList.add(new NavDrawerItem("Reminders",R.drawable.ic_add_alert_white));
        navList.add(new NavDrawerItem("Settings", R.drawable.ic_settings_white));

        navAdapter = new NavDrawerAdapter(this, R.layout.nav_drawer_item,
                navList);

        navDrawer.setAdapter(navAdapter);

        navDrawer.setOnItemClickListener(new DrawerItemClickListener());



        feedList = new ArrayList<Feed>();
        feedMain = (ListView) findViewById(R.id.main_feeds);

        //feedList.add(new Feed("Title1", "Description", "http://www.keenthemes.com/preview/metronic/theme/assets/global/plugins/jcrop/demos/demo_files/image1.jpg"));
        //feedList.add(new Feed("Title2", "Description", "http://static.guim.co.uk/sys-images/Guardian/Pix/pictures/2014/4/11/1397210130748/Spring-Lamb.-Image-shot-2-011.jpg"));
        PostFetcher fetcher = new PostFetcher();
        JSONArray jsonArray;
        try {
            jsonArray = fetcher.execute("http://10.0.2.2/android/").get();
            //Log.e("THEURL", "MAINACTIVITY : " + jsonArray.getString(0));

            displayFeeds(jsonArray);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //scheduleAlarm("Alarm Set for 10 seconds",10);
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
                tag="Reminder";
                fragment = new FragmentReminder();
                break;
            case 6:
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

    private class FeedItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Feed feedItem = feedList.get(position);
            String url = feedItem.getFeedUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
            startActivity(intent);
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






    public void displayFeeds(JSONArray jsonArray) throws JSONException{
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jObject = jsonArray.getJSONObject(i);
            feedList.add(new Feed(jObject.getString("title"), jObject.getString("desc"),jObject.getString("img_url"),jObject.getString("feed_url"),jObject.getString("feed_type")));
        }
        feedAdapter = new FeedAdapter(this,feedList);
        feedMain.setAdapter(feedAdapter);
        feedMain.setOnItemClickListener(new FeedItemClickListener());
        for(Feed f:feedList){
            f.loadImage(feedAdapter);
        }
    }


}
