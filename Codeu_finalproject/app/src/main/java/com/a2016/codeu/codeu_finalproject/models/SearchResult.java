package com.a2016.codeu.codeu_finalproject.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResult {

    private String url;
    String title;
    private double rel;

    public SearchResult() {

    }

    public SearchResult(String title, String url, double rel) {
        this.title = title;
        this.url = url;
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

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("url", url);
        result.put("rel", rel);

        return result;
    }

    @Override
    public String toString() {
        return String.format("%s with rel of %s", url, rel);
    }

}
