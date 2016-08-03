package com.a2016.codeu.codeu_finalproject.models;

import com.google.firebase.database.Query;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch implements Serializable {
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, SearchResult> map;

	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, SearchResult> map) {
		this.map = map;
	}

    /**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url).getRel();
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
        // FILL THIS IN!
        Map<String, SearchResult> outputMap = new HashMap<>(map);
        for (String key : that.map.keySet()) {
            SearchResult temp = that.map.get(key);
//        	int rel = totalRelevance(this.getRelevance(key), that.getRelevance(key));
//            temp.setRel(rel);
        	outputMap.put(key, temp);
        }
        WikiSearch output = new WikiSearch(outputMap);
		return output;
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
        // FILL THIS IN!
        Map<String, SearchResult> outputMap = new HashMap<>();
        for(String key : this.map.keySet()) {
        	if (that.map.containsKey(key) && key != null) {
				SearchResult temp = this.map.get(key);
	        	int rel = totalRelevance(this.getRelevance(key), that.getRelevance(key));
                temp.setRel(rel);
	        	outputMap.put(key, temp);
        	}
        }
        WikiSearch output = new WikiSearch(outputMap);
		return output;
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
        // FILL THIS IN!
        Map<String, SearchResult> outputMap = new HashMap<>(map);
        for(String key : this.map.keySet()) {
	    	if (that.map.containsKey(key)) {
	        	outputMap.remove(key);	
	    	}
        }
        WikiSearch output = new WikiSearch(outputMap);
		return output;
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, SearchResult>> sort() {
        // FILL THIS IN!
        List<Map.Entry<String, SearchResult>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, SearchResult>>() {
            public int compare( Map.Entry<String, SearchResult> v1, Map.Entry<String, SearchResult> v2)
            {
                int val1 = v1.getValue().getRel();
                int val2 = v2.getValue().getRel();
                if (val1 > val2) {
                    return -1;
                } else if (val1 < val2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
		return list;
	}

    public Map<String, SearchResult> getMap() {
        return map;
    }

    public void setMap(Map<String, SearchResult> map) {
        this.map = map;
    }

}
