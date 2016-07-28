package com.a2016.codeu.codeu_finalproject.models;

import android.os.AsyncTask;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Created by pj on 7/26/16.
 */

public class RetrieveWiki extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        ResultsDB db = new ResultsDB(FirebaseDatabase.getInstance());
        try {
            db.loadIntoDB((String[]) params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
