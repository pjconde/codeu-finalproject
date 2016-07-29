package com.a2016.codeu.codeu_finalproject.models;

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

    public ResultsDB(FirebaseDatabase resultsDB) {
        this.resultsDB = resultsDB;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

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

    public String generateURLKey(String url) {
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

    //TODO need to find a way to do snip of line where term is found
    // TODO rather than use generated keys by me, use .push() to use Firebase keys. This avoids unwanted characters
    private void writeTerm(TermCounter tc) {
        String url = tc.getLabel();
        String urlKey = generateURLKey(url);

        for (String term: tc.keySet()) {
            String FBKey = generateFBKey(term);
            int rel = tc.get(term);
            SearchResult current = new SearchResult(url, rel);

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


    public ArrayList<SearchResult> readResult(String term) {
        Query results = mDatabase.child(generateFBKey(term));  //.equalTo(generateFBKey(term));
        DatabaseReference ref = results.getRef();
        System.out.println("Query null check " + results.toString());
        final ArrayList<SearchResult> resultList = new ArrayList<>();

        results.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Map<String, Object> current = (Map<String, Object>) dataSnapshot.getValue();
                        for (String key: current.keySet()) {
                            System.out.println("Keys: " + key);
                        }
                        Long temp = (Long) current.get("rel");
                        int rel = temp.intValue();
                        resultList.add(new SearchResult((String) current.get("url"), rel));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );

        return resultList;
    }

    public void indexPage(String url, Elements para) {
        TermCounter tc = new TermCounter(url);
        tc.processElements(para);

        writeTerm(tc);
    }

    public void loadIntoDB(String[] urls) throws IOException {
        WikiFetcher wf = new WikiFetcher();

        for (String url: urls) {
            Elements paragraphs = wf.fetchWikipedia(url);
            indexPage(url, paragraphs);
        }
    }

    public boolean checkExsistence(String term) {
        String key = generateFBKey(term);
        Query results = mDatabase.child(key);
        final boolean[] exsists = {false};
        results.addListenerForSingleValueEvent(
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
}
