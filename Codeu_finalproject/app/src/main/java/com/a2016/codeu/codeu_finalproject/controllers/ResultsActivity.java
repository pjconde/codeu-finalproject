package com.a2016.codeu.codeu_finalproject.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
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
import com.a2016.codeu.codeu_finalproject.models.TermCounter;
import com.a2016.codeu.codeu_finalproject.models.WikiSearch;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class ResultsActivity extends ListActivity {

    private ResultsDB db;
    private String[] searched;
    private FirebaseDatabase mDatabase;
    private DatabaseReference ref;
    private int runs;
    private ArrayList<Map<String, SearchResult>> results = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.mDatabase = FirebaseDatabase.getInstance();
        db = new ResultsDB(mDatabase);
        db.setCustomObjectListener(new ResultsDB.readListener() {

            @Override
            public void onDataLoaded(Map<String, SearchResult> data) {
                results.add(data);
                runs--;
                if (runs == 0) {
                    // populate stuff
                    ArrayList<SearchResult> pop = db.mergeResults(results);
                    populate(pop);
                }
            }

            @Override
            public void noResults() {
                Snackbar noResults = Snackbar.make(findViewById(android.R.id.list),
                        "No results found", Snackbar.LENGTH_INDEFINITE);
                View.OnClickListener sOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                };

                noResults.setAction("Back", sOnClickListener).show();
            }
        });
        String input = (String) intent.getSerializableExtra("searched");
        this.searched = input.split("\\s+");
        this.searched = filter(this.searched);
        this.runs = this.searched.length;
        this.ref = mDatabase.getReference();
        //this.results = WikiSearch.search(searched, db);

        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Results for %s", input));

        try {
            db.readResults(searched);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String[] filter(String[] results) {
        ArrayList<String> temp = new ArrayList<String>();
        for (String term: results) {
            if (!TermCounter.stopWords.contains(term)) {
                temp.add(term);
            }
        }
        String[] output = new String[temp.size()];
        for (int x = 0; x < output.length; x++) {
            output[x] = temp.get(x);
        }
        return output;
    }

    /**
     * This method populates the list view of ResultsActivity using FireBaseUI's list adapter
     * It also does the query to firebase
     */
    private void populate(ArrayList<SearchResult> list) {
        SearchResultArrayAdapator adapter = new SearchResultArrayAdapator(getApplicationContext(), list);
        setListAdapter(adapter);
        // TODO figure out how to sort by rel score descending. Currently orders smallest first
//        Query results = ref.child("terms").child(db.generateFBKey(searched[0])).child("wiki")
//                .orderByChild("rel");
//        final Map<String, SearchResult> map = new HashMap<>();
//        // TODO due to async nature, calculations should be done in event listeners!!!
//        FirebaseListAdapter<SearchResult> adapter =
//                new FirebaseListAdapter<SearchResult>(this, SearchResult.class,
//                        R.layout.result_item, results) {
//                    @Override
//                    protected void populateView(View v, SearchResult model, int position) {
//                        TextView title = (TextView) v.findViewById(R.id.result_name);
//                        TextView url = (TextView) v.findViewById(R.id.url);
//                        TextView snip = (TextView) v.findViewById(R.id.snip);
//
//                        String rurl = model.getUrl();
//                        String rtitle = model.getTitle();
//                        int rel = model.getRel();
//                        // TODO get the snip in some way
//                        title.setText(rtitle);
//                        url.setText(rurl);
//                        snip.setText("Rel: " + rel);
//                        map.put(rurl, model);
//                    }
//                };
//        view.setAdapter(adapter);
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        SearchResult currentResult = (SearchResult) l.getItemAtPosition(position);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentResult.getUrl()));
        startActivity(browserIntent);
    }

}
