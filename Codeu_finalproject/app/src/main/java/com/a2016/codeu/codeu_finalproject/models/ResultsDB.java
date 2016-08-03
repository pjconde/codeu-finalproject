package com.a2016.codeu.codeu_finalproject.models;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by pj on 7/25/16.
 */

public class ResultsDB implements Serializable {

    private FirebaseDatabase resultsDB;
    private DatabaseReference mDatabase;
    Map<String, SearchResult> map = new HashMap<>();

    public ResultsDB(FirebaseDatabase resultsDB) {
        this.resultsDB = resultsDB;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Generates the path for the term in firebase
     * @param term
     * @return
     */
    public String generateFBKey(String term) {
        String FBKey = "FB:" + term;
        if (FBKey.contains(".")) {
            FBKey = FBKey.replace(".", "dot");
        } if (FBKey.contains("#")) {
            FBKey = FBKey.replace("#", "num");
        } if (FBKey.contains("$")) {
            FBKey = FBKey.replace("$", "money");;
        } if (FBKey.contains("[")) {
            FBKey = FBKey.replace("[", "left_square");
        } if (FBKey.contains("]")) {
            FBKey = FBKey.replace("]", "right_square");
        }
        return FBKey;
    }

    /**
     * Generates the path for the url for firebase
     * @param url
     * @return
     */
    public String generateURLPath(String url) {
        try {
            URL realURL = new URL(url);
            String key = realURL.getPath();
            if (key.contains(".")) {
                key = key.replace(".", "dot");
            } if (key.contains("#")) {
                key = key.replace("#", "num");
            } if (key.contains("$")) {
                key = key.replace("$", "money");;
            } if (key.contains("[")) {
                key = key.replace("[", "left_square");
            } if (key.contains("]")) {
                key = key.replace("]", "right_square");
            }
            return key;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "notUrl";
    }

    /**
     * Writes the urls in the termcounter as SearchResults
     * @param tc
     */
    //TODO need to find a way to do snip of line where term is found
    private void writeTerm(String title, TermCounter tc) {
        String url = tc.getLabel();
        String urlKey = generateURLPath(url);

        for (String term: tc.keySet()) {
            String FBKey = generateFBKey(term);
            int rel = tc.get(term);
            SearchResult current = new SearchResult(title, url, rel);

            if (checkExsistence(term)) {
                Map<String, Object> currentVals = current.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("terms/" + FBKey + urlKey, currentVals);
                mDatabase.updateChildren(childUpdates);
            } else {
                mDatabase.child("terms").child(FBKey).child(urlKey).setValue(current);
            }
        }
    }

    private Map<String, SearchResult> getTermLinks(Query query) {
        final Map<String, SearchResult> output = new HashMap<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> ds = (Map<String, Object>) dataSnapshot.getValue();
                    for (String key : ds.keySet()) {
                        Map<String, Object> link = (Map<String, Object>) ds.get(key);
                        String url = (String) link.get("url");
                        String title = (String) link.get("title");
                        int rel = ((Long) link.get("rel")).intValue();
                        SearchResult res = new SearchResult(title, url, rel);
                        output.put(url, res);
                        map.put(url, res);
                    }
                }
                Log.d("Link map", output.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

//        while(output.isEmpty() || map.isEmpty()) {
//            Log.d("Empty", "Map is empty");
//        }
        return output;
    }

    public void readResults(String[] terms) throws InterruptedException {
        for (String term : terms) {
            String key = generateFBKey(term);
            Query query = mDatabase.child("terms").child(key).child("wiki");
            Map<String, SearchResult> map1 = getTermLinks(query);
            Log.d("Map", map.toString());
        }
    }

    /**
     * call to termcounter to do all the counting
     * @param title
     * @param url
     * @param para
     */
    public void indexPage(String title, String url, Elements para) {
        TermCounter tc = new TermCounter(url);
        tc.processElements(para);

        writeTerm(title, tc);
    }

    /**
     * Queues up each url to be indexed and added to the data base
     * @param urls
     * @throws IOException
     */
    public void loadIntoDB(String[] urls) throws IOException {
        mDatabase.removeValue();
        WikiFetcher wf = new WikiFetcher();

        for (String url: urls) {
            ArrayList<Object> wfReturn = wf.fetchWikipedia(url);
            String title = (String) wfReturn.get(0);
            Elements paragraphs = (Elements) wfReturn.get(1);
            indexPage(title, url, paragraphs);
        }
    }

    /**
     * Checks if a term is already in the data base
     * @param term
     * @return
     */
    public boolean checkExsistence(String term) {
        String key = generateFBKey(term);
        Query results = mDatabase.child("term").child(key);
        final boolean[] exsists = {false};
        results.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        exsists[0] = dataSnapshot.exists();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );
        return exsists[0];
    }

    public Map<String, SearchResult> getMap() {
        return map;
    }

    public void setMap(Map<String, SearchResult> map) {
        this.map = map;
    }

    private void sleepforMap() {
        for (int x = 0; x < 5; x++) {
            Log.d("Wait", "Waiting");
        }
    }
}
