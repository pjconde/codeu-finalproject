package com.a2016.codeu.codeu_finalproject.controllers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populates firebase DB with pages
        //String[] toBeLoaded = populateLinks();
        //new RetrieveWiki().execute(toBeLoaded);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onClick(View v) throws IOException {
        // This gets the search box and allows us to check if it is empty
        EditText searchBox = (EditText) findViewById(R.id.search_input);
        boolean searchEmpty = TextUtils.isEmpty(searchBox.getText().toString());
        if (!searchEmpty) {
            // If the search box is not empty we retrieve the string
            String searchWord = searchBox.getText().toString();
//            Query search = WikiSearch.search(searchWord, db);
//            WikiSearch queryPass = new WikiSearch(search);

//            System.out.println(search.toString());

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

}
