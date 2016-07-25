package com.a2016.codeu.codeu_finalproject.controllers;

import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.a2016.codeu.codeu_finalproject.R;
import com.a2016.codeu.codeu_finalproject.models.ResultsDB;
import com.a2016.codeu.codeu_finalproject.models.SearchResult;
import com.a2016.codeu.codeu_finalproject.models.SearchResultArrayAdapator;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ResultsActivity extends ListActivity {

    ResultsDB db;
    String searched;
    Query results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        db = new ResultsDB(FirebaseDatabase.getInstance());
        this.searched = (String) intent.getSerializableExtra("searched");
        this.results = WikiSearch.search(searched, db);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Results for %s", searched));

        populate();
    }

    // Populates the list view with the results
    private void populate() {
        final ArrayList<SearchResult> resultList = new ArrayList<>();

        ValueEventListener populateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 SearchResult result = dataSnapshot.getValue(SearchResult.class);
                resultList.add(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }

        };

        results.addValueEventListener(populateListener);

//        Map<String, Integer> map = results.getMap();
//        for (String key: map.keySet()) {
//            SearchResult current = new SearchResult(key, 1, map.get(key).toString());
//            resultList.add(current);
//        }
        SearchResultArrayAdapator adapter = new SearchResultArrayAdapator(getApplicationContext(),
                resultList);
        setListAdapter(adapter);
    }


}
