package com.a2016.codeu.codeu_finalproject.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a2016.codeu.codeu_finalproject.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResultArrayAdapator extends ArrayAdapter<SearchResult> {

    /**
     * Creates a SearchResultArrayAdapator object.
     * @param context the current context of the application
     * @param results array list of the results
     */
    public SearchResultArrayAdapator(Context context, ArrayList<SearchResult> results) {
        super(context, 0, results);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get Rating object at this position
        SearchResult current = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_item, parent, false);
        }
        // Look up text views for data population
        TextView title = (TextView) convertView.findViewById(R.id.result_name);
        TextView url = (TextView) convertView.findViewById(R.id.url);
        TextView snip = (TextView) convertView.findViewById(R.id.snip);
        // Populate data using SearchResult object
        title.setText(current.getTitle());
        url.setText(current.getUrl());
        snip.setText("Rel: " + current.getRel());
        // Return completed view
        return convertView;
    }
}

