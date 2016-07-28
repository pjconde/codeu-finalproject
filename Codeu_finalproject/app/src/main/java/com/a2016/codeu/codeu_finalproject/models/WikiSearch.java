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
	private Map<String, Integer> map;
    private Query q;


	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

    /**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
        // FILL THIS IN!
        Map<String, Integer> outputMap = new HashMap<String, Integer>(map);
        for (String key : that.map.keySet()) {
        	int rel = totalRelevance(this.getRelevance(key), that.getRelevance(key));
        	outputMap.put(key, rel);
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
        Map<String, Integer> outputMap = new HashMap<String, Integer>();
        for(String key : this.map.keySet()) {
        	if (that.map.containsKey(key) && key != null) {
	        	int rel = totalRelevance(this.getRelevance(key), that.getRelevance(key));
	        	outputMap.put(key, rel);	
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
        Map<String, Integer> outputMap = new HashMap<String, Integer>(map);
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
	public List<Entry<String, Integer>> sort() {
        // FILL THIS IN!
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare( Map.Entry<String, Integer> v1, Map.Entry<String, Integer> v2)
            {
                return (v1.getValue()).compareTo(v2.getValue());
            }
        });
		return list;
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @return
	 */
//	public static Query search(String term, ResultsDB db) {
//        System.out.println("JedisIndex Term: " + term);
//
//        //Map<String, Integer> map = index.getCounts(term);
//
//		return temp;
//	}

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

}
