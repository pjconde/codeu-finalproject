package com.a2016.codeu.codeu_finalproject.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

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

    //TODO need to find a way to do snip of line where term is found
    private void writeNewResult(String term, String url, int rel, String snip) {
        SearchResult result = new SearchResult(term, rel, url);

        mDatabase.child("results").child(term).setValue(result);
    }

    // TODO fill in with snip
    private void writeManyResults(ArrayList<SearchResult> results) {
        for (SearchResult result : results) {
            writeNewResult(result.getTerm(), result.getUrl(), result.getRel(), "");
        }
    }

    public Query readResult(String term) {
        Query results = mDatabase.child("results").child(term).equalTo(term);
        return results;
    }

    public void indexPage(String url, Elements para) {
        TermCounter tc = new TermCounter(url);
        tc.processElements(para);
        ArrayList<SearchResult> termList = new ArrayList<>();

        for (String term: tc.keySet()) {
            Integer count = tc.get(term);
            SearchResult temp = new SearchResult(term, count, url);
            termList.add(temp);
        }

        writeManyResults(termList);
    }

    public void loadIntoDB(String[] urls) throws IOException {
        WikiFetcher wf = new WikiFetcher();

        for (String url: urls) {
            Elements paragraphs = wf.readWikipedia(url);
            indexPage(url, paragraphs);
        }
    }
}
