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
    private ArrayList<Map<String, SearchResult>> results = new ArrayList<>();
    private readListener listener;

    public ResultsDB(FirebaseDatabase resultsDB) {
        this.resultsDB = resultsDB;
        this.mDatabase = resultsDB.getReference();
        this.listener = null;
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
//    private void writeTerm(String title, String image, TermCounter tc) {
//        String url = tc.getLabel();
//        String urlKey = generateURLPath(url);
//
//        for (String term: tc.keySet()) {
//            String FBKey = generateFBKey(term);
//            double rel = tc.getTF(term);
//            SearchResult current = new SearchResult(title, url, image, rel);
//
//            if (checkExsistence(term)) {
//                Map<String, Object> currentVals = current.toMap();
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("terms/" + FBKey + urlKey, currentVals);
//                mDatabase.updateChildren(childUpdates);
//            } else {
//                mDatabase.child("terms").child(FBKey).child(urlKey).setValue(current);
//            }
//        }
//    }

    private void getTermLinks(Query query) {
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
                        double rel = (double) link.get("rel");
                        String image = (String) link.get("image");
                        String des = (String) link.get("description");
                        SearchResult res = new SearchResult(title, url, image, des, rel);
                        output.put(url, res);
                    }
                    results.add(output);
                    if (listener != null) {
                        listener.onDataLoaded(output);
                    } else {
                        Log.d("Listener null", "null");
                    }
                } else {
                    if (listener != null) {
                        listener.noResults();
                    }
                }
                //results.add(output);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

    }

    public void readResults(String[] terms) throws InterruptedException {
        for (String term : terms) {
            term = term.toLowerCase();
            String key = generateFBKey(term);
            Query query = mDatabase.child("terms").child(term);
            getTermLinks(query);
        }
    }

    public ArrayList<SearchResult> mergeResults(ArrayList<Map<String, SearchResult>> results) {
        Map<String, SearchResult> fin;
        if (results.size() == 1) {
            fin = results.get(0);
        } else {
            WikiSearch temp1 = new WikiSearch(results.get(0));
            WikiSearch temp2 = new WikiSearch(results.get(1));
            WikiSearch wSearch = temp1.and(temp2);
            if (wSearch.getMap().isEmpty()) {
                if (listener != null) {
                    listener.noResults();
                }
            }
            for (int x = 2; x < results.size(); x++) {
                WikiSearch curr = new WikiSearch(results.get(x));
                wSearch = wSearch.and(curr);
            }
            fin = wSearch.getMap();
        }
        return sortResults(fin);
    }

    private ArrayList<SearchResult> sortResults(Map<String, SearchResult> list) {
        ArrayList<SearchResult> output = new ArrayList<>();
        WikiSearch temp = new WikiSearch(list);
        List<Map.Entry<String, SearchResult>> entries = temp.sort();
        for (Map.Entry<String, SearchResult> entry: entries) {
			output.add(entry.getValue());
		}
        return output;
    }

    /**
     * call to termcounter to do all the counting
     * @param title
     * @param url
     * @param para
     */
//    public void indexPage(String title, String url, String image, Elements para) {
//        TermCounter tc = new TermCounter(url);
//        tc.processElements(para);
//        tc.createTFMap();
//        writeTerm(title, image, tc);
//    }

    /**
     * Queues up each url to be indexed and added to the data base
     * @param urls
     * @throws IOException
     */
//    public void loadIntoDB(String[] urls) throws IOException {
//        WikiFetcher wf = new WikiFetcher();
//
//        for (String url: urls) {
//            ArrayList<Object> wfReturn = wf.fetchWikipedia(url);
//            String title = (String) wfReturn.get(0);
//            String image = (String) wfReturn.get(3);
//            Elements paragraphs = (Elements) wfReturn.get(1);
//            indexPage(title, url, image, paragraphs);
//        }
//    }

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

    public void setCustomObjectListener(readListener listener) {
        this.listener = listener;
    }

    public interface readListener {

        public void onDataLoaded(Map<String, SearchResult> data);

        public void noResults();
    }
}
