package com.a2016.codeu.codeu_finalproject.controllers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.speech.RecognizerIntent;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;

import com.a2016.codeu.codeu_finalproject.R;
import com.a2016.codeu.codeu_finalproject.models.ResultsDB;
import com.a2016.codeu.codeu_finalproject.models.RetrieveWiki;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class MainActivity extends AppCompatActivity {

    private ResultsDB db;
    private FloatingActionButton _micButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Populates firebase DB with pages
//        String[] toBeLoaded = populateLinks();
//        new RetrieveWiki().execute(toBeLoaded);

        this._micButton = (FloatingActionButton) findViewById(R.id.microphone);

        this._micButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                micOnClick(v);
            }
        });
        
    }

    public void micOnClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText searchBox = (EditText) findViewById(R.id.search_input);
                    searchBox.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void onClick(View v) throws IOException {
        // This gets the search box and allows us to check if it is empty
        EditText searchBox = (EditText) findViewById(R.id.search_input);
        boolean searchEmpty = TextUtils.isEmpty(searchBox.getText().toString());
        if (!searchEmpty) {
            // If the search box is not empty we retrieve the string
            String searchWord = searchBox.getText().toString().trim();

            String[] allWords = searchWord.split("\\s+");

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("searched", allWords);
            startActivity(intent);
        }
        Log.d("Empty", "box is empty");
    }

    // TODO figure out how to add more links without hardcoding page URL
    private String[] populateLinks() {
        String[] pages = new String[4];
//        pages[0] = "https://en.wikipedia.org/wiki/Awareness";
        pages[0] = "https://en.wikipedia.org/wiki/Computer_science";
        pages[1] = "https://en.wikipedia.org/wiki/Concurrent_computing";
//        pages[3] = "https://en.wikipedia.org/wiki/Consciousness";
        pages[2] = "https://en.wikipedia.org/wiki/Java_(programming_language)";
//        pages[5] = "https://en.wikipedia.org/wiki/Knowledge";
//        pages[6] = "https://en.wikipedia.org/wiki/Mathematics";
//        pages[7] = "https://en.wikipedia.org/wiki/Modern_philosophy";
//        pages[8] = "https://en.wikipedia.org/wiki/Philosophy";
        pages[3] = "https://en.wikipedia.org/wiki/Programming_language";
//        pages[10] = "https://en.wikipedia.org/wiki/Property_(philosophy)";
//        pages[11] = "https://en.wikipedia.org/wiki/Quality_(philosophy)";
//        pages[12] = "https://en.wikipedia.org/wiki/Science";
        return pages;
    }

}
