package com.a2016.codeu.codeu_finalproject.models;

/**
 * Created by pj on 7/24/16.
 */

public class SearchResult {

    private String url;
    private String snip;
    private String term;
    private int rel;

    public SearchResult() {

    }

    public SearchResult(String term, String url, int rel, String snip) {
        this.term = term;
        this.url = url;
        this.rel = rel;
        this.snip = snip;
    }

    public SearchResult(String term, int rel, String url) {
        this(term, url, rel, "No snip");
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getRel() {
        return rel;
    }

    public void setRel(int rel) {
        this.rel = rel;
    }
}
