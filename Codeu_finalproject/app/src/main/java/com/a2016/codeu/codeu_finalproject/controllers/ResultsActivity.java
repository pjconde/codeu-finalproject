package com.a2016.codeu.codeu_finalproject.controllers;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.a2016.codeu.codeu_finalproject.R;
import com.a2016.codeu.codeu_finalproject.models.SearchResult;
import com.a2016.codeu.codeu_finalproject.models.SearchResultArrayAdapator;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;

import java.util.ArrayList;
import java.util.Map;

public class ResultsActivity extends ListActivity {

    WikiSearch results;
    String searched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.results = (WikiSearch) intent.getSerializableExtra("WS_results");
        this.searched = (String) intent.getSerializableExtra("searched");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Results for %s", searched));

        populate();
    }

    // Populates the list view with the results
    private void populate() {
        ArrayList<SearchResult> resultList = new ArrayList<>();
        Map<String, Integer> map = results.getMap();
        for (String key: map.keySet()) {
            SearchResult current = new SearchResult(key, map.get(key).toString());
            resultList.add(current);
        }
        SearchResultArrayAdapator adapter = new SearchResultArrayAdapator(getApplicationContext(),
                resultList);
        setListAdapter(adapter);
    }
}
