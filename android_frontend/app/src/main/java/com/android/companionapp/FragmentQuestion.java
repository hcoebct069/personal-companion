package com.android.companionapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Nishan on 8/3/2015.
 */
public class FragmentQuestion extends Fragment{
    public TextView question;
    public Button ans1;
    public Button ans2;
    ImageButton navBack;
    public JSONArray jsonArray;
    public PostFetcher fetcher;
    public FragmentQuestion() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_questions, container,
                false);
        navBack = (ImageButton) view.findViewById(R.id.navBack);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        question = (TextView) view.findViewById(R.id.q);
        ans1 = (Button) view.findViewById(R.id.ansf);
        ans2 = (Button) view.findViewById(R.id.anss);
        fetcher = new PostFetcher();

        runQuestions();
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetcher = new PostFetcher();
                runQuestions();
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetcher = new PostFetcher();
                runQuestions();
            }
        });
        return view;
    }
    public void displayQuestions(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jObject = jsonArray.getJSONObject(i);
            question.setText(jObject.getString("question"));
            ans1.setText(jObject.getString("answer1"));
            ans2.setText(jObject.getString("answer2"));
        }
    }
    public void getQuestion() throws JSONException, ExecutionException, InterruptedException {
        jsonArray = fetcher.execute("http://10.0.2.2/android/questionnaire.php").get();
        Log.e("THEURL", "MAINACTIVITY : " + jsonArray.getString(0));

        displayQuestions(jsonArray);
    }
    public void runQuestions(){
                try {
                    getQuestion();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    }
}
