package com.a2016.codeu.codeu_finalproject.models;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResult {

    private String url;
    private String title;
    private String image;
    private String description;
    private double rel;
    private imageListener listener;

    public SearchResult() {

    }

    public SearchResult(String title, String url, String image, String description, double rel) {
        this.title = title;
        this.url = url;
        this.image = image;
        this.description = description;
        this.rel = rel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getRel() {
        return rel;
    }

    public void setRel(double rel) {
        this.rel = rel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("url", url);
        result.put("rel", rel);
        result.put("image", image);
        result.put("description", description);
        return result;
    }

    public InputStream loadImage(String url) {
        if (url.equals("") || url == null) {
            return null;
        }
        try {
            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream stream = connection.getInputStream();
//            view.setImageBitmap(BitmapFactory.decodeStream(stream));

            return stream;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s with rel of %s", url, rel);
    }

    public void setCustomObjectListener(SearchResult.imageListener listener) {
        this.listener = listener;
    }

    public interface imageListener {

        public void onImageLoad(InputStream stream);

    }

}
