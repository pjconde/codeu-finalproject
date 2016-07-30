package com.a2016.codeu.codeu_finalproject.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResult {

    private String url;
    String title;
    private int rel;

    public SearchResult() {

    }

    public SearchResult(String title, String url, int rel) {
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

    public int getRel() {
        return rel;
    }

    public void setRel(int rel) {
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

}
