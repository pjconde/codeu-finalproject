package com.a2016.codeu.codeu_finalproject.models;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResult {

    private String url;
    private String snip;

    public SearchResult(String url, String snip) {
        this.url = url;
        this.snip = snip;
    }

    public SearchResult(String url) {
        this(url, "No snip");
    }

    public String getSnip() {
        return snip;
    }

    public void setSnip(String snip) {
        this.snip = snip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
