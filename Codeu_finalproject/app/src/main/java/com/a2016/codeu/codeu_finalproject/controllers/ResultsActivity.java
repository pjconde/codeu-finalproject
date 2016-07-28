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
import android.widget.ListView;
import android.widget.TextView;

import com.a2016.codeu.codeu_finalproject.R;
import com.a2016.codeu.codeu_finalproject.models.ResultsDB;
import com.a2016.codeu.codeu_finalproject.models.SearchResult;
import com.a2016.codeu.codeu_finalproject.models.SearchResultArrayAdapator;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ResultsActivity extends ListActivity {

    ResultsDB db;
    String searched;
    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.mDatabase = FirebaseDatabase.getInstance();
        db = new ResultsDB(mDatabase);
        this.searched = (String) intent.getSerializableExtra("searched");
        //this.results = WikiSearch.search(searched, db);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Results for %s", searched));

        populate();
    }

    // Populates the list view with the results
    private void populate() {

        ListView view = (ListView) findViewById(android.R.id.list);

//        ArrayList<SearchResult> resultList = db.readResult(searched);
//        Log.v("Populate list size", Integer.toString(resultList.size()));

        DatabaseReference ref = mDatabase.getReference();
        Query results = ref.child("terms").child(db.generateFBKey(searched)).child("wiki");
        Log.v("Query", results.toString());

        FirebaseListAdapter<SearchResult> adapter =
                new FirebaseListAdapter<SearchResult>(this, SearchResult.class,
                        R.layout.result_item, results) {
                    @Override
                    protected void populateView(View v, SearchResult model, int position) {
                        TextView result = (TextView) v.findViewById(R.id.result_name);
                        TextView url = (TextView) v.findViewById(R.id.url);
                        TextView snip = (TextView) v.findViewById(R.id.snip);

                        Log.v("Result URL", model.getUrl());
                        Log.v("Result snip", String.format("%s", model.getRel()));

                        result.setText("Result: " + "Pending");
                        url.setText("Url: " + model.getUrl());
                        snip.setText("Snip: " + model.getRel());
                    }
                };
        view.setAdapter(adapter);
    }


}
