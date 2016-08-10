package com.a2016.codeu.codeu_finalproject.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a2016.codeu.codeu_finalproject.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResultArrayAdapator extends ArrayAdapter<SearchResult> {

    private InputStream stream;
    private SearchResult current;
    private int posititon;
    private View convertView;
    private ViewGroup parent;

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
        this.current = current;
        this.posititon = position;
        this.convertView = convertView;
        this.parent = parent;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_item, parent, false);
        }
        // Look up text views for data population
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView title = (TextView) convertView.findViewById(R.id.result_name);
        TextView url = (TextView) convertView.findViewById(R.id.url);
        // Populate data using SearchResult object
        new DownloadImageTask(image).execute(current.getImage());
        image.setImageBitmap(BitmapFactory.decodeStream(stream));
        title.setText(current.getTitle());
        url.setText(current.getDescription());
        // Return completed view
        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            if (urldisplay == null || urldisplay.isEmpty()) {
                urldisplay = "https://static01.nyt.com/images/icons/t_logo_291_black.png";
            }
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

