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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;

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


public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private ResultsDB db;
    private FloatingActionButton _micButton;
    private AutoCompleteTextView searchBox;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    // list of autosuggestions
    String[] list = {"java", "programming", "proactive", "disease", "adventure", "strategy",
            "android", "and", "soldier", "war", "vandalism", "surface", "surprise", "arm",
            "damage", "children", "explosion", "health", "weapon", "study", "syndrome",
            "medicine", "science", "country", "countries", "car", "airplane", "government",
            "population", "death", "city", "nature", "sports", "ship", "tourism", "economy",
            "sport", "technology", "fight", "suicide", "traffic", "travel", "fitness",
            "champion", "winner", "attack", "defense", "safety", "oil", "resources", "water",
            "politics", "love", "peace", "candidate", "school", "university", "development",
            "vote", "election", "organization", "president", "animals", "plants", "cable",
            "mission", "corruption", "gang", "protest", "music", "songs", "class", "violation",
            "shock", "international", "national", "regional", "volcano", "space", "air", "rocks",
            "computer", "invention", "transportation", "phone", "house", "summer", "winter",
            "tablet", "key", "chip", "food", "drinks", "temple", "snow", "rain", "storm", "wave",
            "temperature", "disaster", "weather", "maps", "sister", "brother", "skills", "society",
            "school", "kids", "teenager", "adolescent", "mother", "socialism", "democracy", "cook",
            "father", "family", "ocean", "America", "table", "athlete", "fruits", "vegetables",
            "doctor", "strength", "training", "structure", "religion", "hotels", "language", "town",
            "difficulty", "beginner", "birth", "task", "objective", "tax", "law", "attorney",
            "company", "enterprise", "fast", "film", "movie", "event", "theory", "threat", "lunch",
            "dinner", "restaurant", "sleep", "boys", "girls", "men", "women", "boy", "man", "woman",
            "code", "difference", "incredible", "academy", "academia", "teach", "learn", "courses",
            "time", "hour", "shops", "building", "apartment", "complex", "mountain", "hiking",
            "places", "letter", "message", "continent", "ocean", "sea", "calamity", "competition",
            "architecture", "art", "museum", "street", "walk", "creation", "email", "taste", "touch",
            "jeb", "bush", "bacon", "bernie", "sanders", "clinton", "zika"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("404 Search");

        // Populates firebase DB with pages
        //String[] toBeLoaded = populateLinks();
        //new RetrieveWiki().execute(toBeLoaded);

        setUpAutoSuggestion();
        setUpSpeech();
        
    }

    protected void setUpAutoSuggestion() {
        searchBox = (AutoCompleteTextView) findViewById(R.id.search_input);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        searchBox.setThreshold(1);
        searchBox.setDropDownWidth(500);
        searchBox.setAdapter(adapter);
        searchBox.setOnEditorActionListener(this);
    }

    protected void setUpSpeech() {
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
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchBox.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void onClick(View v) throws IOException {
        // This gets the search box and allows us to check if it is empty
        // EditText searchBox = (EditText) findViewById(R.id.search_input);
        performSearch();
    }

    private void performSearch (){
        boolean searchEmpty = TextUtils.isEmpty(searchBox.getText().toString());
        if (!searchEmpty) {
            // If the search box is not empty we retrieve the string
            String searchWord = searchBox.getText().toString().trim();

            String[] allWords = searchWord.split("\\s+");

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("searched", searchWord);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // TODO figure out how to add more links without hardcoding page URL
    private String[] populateLinks() {
        String[] pages = new String[13];
        pages[0] = "https://en.wikipedia.org/wiki/Awareness";
        pages[1] = "https://en.wikipedia.org/wiki/Computer_science";
        pages[2] = "https://en.wikipedia.org/wiki/Concurrent_computing";
        pages[3] = "https://en.wikipedia.org/wiki/Consciousness";
        pages[4] = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        pages[5] = "https://en.wikipedia.org/wiki/Knowledge";
        pages[6] = "https://en.wikipedia.org/wiki/Mathematics";
        pages[7] = "https://en.wikipedia.org/wiki/Modern_philosophy";
        pages[8] = "https://en.wikipedia.org/wiki/Philosophy";
        pages[9] = "https://en.wikipedia.org/wiki/Programming_language";
        pages[10] = "https://en.wikipedia.org/wiki/Property_(philosophy)";
        pages[11] = "https://en.wikipedia.org/wiki/Quality_(philosophy)";
        pages[12] = "https://en.wikipedia.org/wiki/Science";
        return pages;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            handled = true;
        }
        return handled;
    }
}
