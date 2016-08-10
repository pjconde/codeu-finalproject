package com.a2016.codeu.codeu_finalproject.models;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;


/**
 * Encapsulates a map from search term to frequency (count).
 * 
 * @author downey
 *
 */
public class TermCounter {

	private Map<String, Double> tfMap;
	private Map<String, Integer> map;
	private String label;
	public static Set<String> stopWords = new HashSet<String>(Arrays.asList("a", "about", "above",
			"above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone",
			"along", "already","also","although","always","am","among", "amongst", "amoungst",
			"amount",  "an", "and","another", "any","anyhow","anyone","anything","anyway",
			"anywhere", "are", "around","as",  "at", "back","be","became", "because","become",
			"becomes", "becoming", "been","before", "beforehand", "behind", "being", "below",
			"beside", "besides", "between","beyond", "bill", "both", "bottom","but", "by", "call",
			"can", "cannot", "cant", "co","con", "could", "couldnt", "cry", "de", "describe",
			"detail", "do", "done", "down","due", "during", "each", "eg", "eight", "either",
			"eleven","else", "elsewhere", "empty","enough", "etc", "even", "ever", "every",
			"everyone", "everything", "everywhere","except", "few", "fifteen", "fify", "fill",
			"find", "fire", "first", "five", "for","former", "formerly", "forty", "found", "four",
			"from", "front", "full", "further","get", "give", "go", "had", "has", "hasnt", "have",
			"he", "hence", "her", "here","hereafter", "hereby", "herein", "hereupon", "hers",
			"herself", "him", "himself","his", "how", "however", "hundred", "ie", "if", "in", "inc",
			"indeed", "interest","into","is", "it", "its", "itself", "keep", "last", "latter",
			"latterly", "least", "less", "ltd","made", "many", "may", "me", "meanwhile", "might",
			"mill", "mine", "more", "moreover","most", "mostly", "move", "much", "must", "my",
			"myself", "name", "namely", "neither","never", "nevertheless", "next", "nine", "no",
			"nobody", "none", "noone", "nor", "not","nothing", "now", "nowhere", "of", "off", "often",
			"on", "once", "one", "only", "onto","or", "other", "others", "otherwise", "our", "ours",
			"ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather",
			"re", "same", "see", "seem","seemed", "seeming", "seems", "serious", "several", "she",
			"should", "show", "side","since", "sincere", "six", "sixty", "so", "some", "somehow",
			"someone", "something","sometime", "sometimes", "somewhere", "still", "such", "system",
			"take", "ten", "than","that", "the", "their", "them", "themselves", "then", "thence",
			"there", "thereafter","thereby", "therefore", "therein", "thereupon", "these", "they",
			"thickv", "thin","third", "this", "those", "though", "three", "through", "throughout",
			"thru", "thus","to", "together", "too", "top", "toward", "towards", "twelve", "twenty",
			"two", "un","under", "until", "up", "upon", "us", "very", "via", "was", "we", "well",
			"were", "what","whatever", "when", "whence", "whenever", "where", "whereafter","whereas",
			"whereby","wherein", "whereupon", "wherever", "whether", "which", "while", "whither",
			"who","whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without",
			"would","yet", "you", "your", "yours", "yourself", "yourselves", "the"));
	
	public TermCounter(String label) {
		this.label = label;
		this.map = new HashMap<>();
        this.tfMap = new HashMap<>();
	}
	
	public String getLabel() {
		return label;
	}
	
	/**
	 * Returns the total of all counts.
	 * 
	 * @return total counts
	 */
	public int size() {
		int total = 0;
		for (Integer value: map.values()) {
			total += value;
		}
		return total;
	}

    /**
     * Returns the term proportional frequency.
     *
     * @return total counts
     */
    public double getTermFrequency(String term) {
        double temp = get(term)/((double) size());
        Log.d("Frequency", String.format("%s", temp));
        return temp;
    }

    /**
     * Takes a collection of Elements and counts their words.
     *
     */
    public void createTFMap() {
        for (String key : keySet()) {
            tfMap.put(key, getTermFrequency(key));
        }
    }

    //	/**
//	 * Takes a collection of Elements and places them in
//     * the term frequency map.
//	 *
//	 * @param root
//	 */
//	public void makeTfTree(Elements paragraphs) {
//		// NOTE: we could use select to find the TextNodes, but since
//		// we already have a tree iterator, let's use it.
//        for (Node root : paragraphs) {
//            for (Node node : new WikiNodeIterable(root)) {
//                if (node instanceof TextNode) {
//                    String text = ((TextNode) node).text();
//
//                    String[] array = text.replaceAll("\\p{P}", " ").toLowerCase().split("\\s+");
//                    for (int i = 0; i < array.length; i++) {
//                        String term = array[i];
//                        tfMap.put(term, (getTermFrequency(term) + 1) / size());
//                    }
//                }
//            }
//        }
//	}

	/**
	 * Takes a collection of Elements and counts their words.
	 * 
	 * @param paragraphs
	 */
	public void processElements(Elements paragraphs) {
		for (Node node: paragraphs) {
			processTree(node);
		}
	}

    /**
     * Finds TextNodes in a DOM tree and counts their words.
     *
     * @param root
     */
    public void processTree(Node root) {
        for (Node node : new WikiNodeIterable(root)) {
            if (node instanceof TextNode) {
                processText(((TextNode) node).text());
            }
        }
    }

	/**
	 * Splits `text` into words and counts them.
	 * 
	 * @param text  The text to process.
	 */
	public void processText(String text) {
		// replace punctuation with spaces, convert to lower case, and split on whitespace
		String[] array = text.replaceAll("\\p{P}", " ").toLowerCase().split("\\s+");
		
		for (int i=0; i<array.length; i++) {
			String term = array[i];
			if (!stopWords.contains(term)) {
				incrementTermCount(term);
			}
		}
	}

	/**
	 * Increments the counter associated with `term`.
	 * 
	 * @param term
	 */
	public void incrementTermCount(String term) {
		// System.out.println(term);
		put(term, get(term) + 1);
	}



	/**
	 * Adds a term to the map with a given count.
	 * 
	 * @param term
	 * @param count
	 */
	public void put(String term, int count) {
		map.put(term, count);
	}

	/**
	 * Returns the count associated with this term, or 0 if it is unseen.
	 * 
	 * @param term
	 * @return the count associated with this term
	 */
	public int get(String term) {
		Integer count = map.get(term);
		return count == null ? 0 : count;
	}

	public double getTF(String term) {
        double count = tfMap.get(term);
        return count;
    }

    public Map<String, Double> getTfMap() {
        return tfMap;
    }

    /**
	 * Returns the set of terms that have been counted.
	 * 
	 * @return
	 */
	public Set<String> keySet() {
		return map.keySet();
	}
	
	/**
	 * Print the terms and their counts in arbitrary order.
	 */
//	public void printCounts() {
//		for (String key: keySet()) {
//			Integer count = get(key);
//			System.out.println(key + ", " + count);
//		}
//		System.out.println("Total of all counts = " + size());
//	}

	/**
	 * @param args
	 * @throws IOException 
	 */
//	public static void main(String[] args) throws IOException {
//		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
//
//		WikiFetcher wf = new WikiFetcher();
//		Elements paragraphs = wf.fetchWikipedia(url);
//
//		TermCounter counter = new TermCounter(url.toString());
//		counter.processElements(paragraphs);
//		counter.printCounts();
//	}
}
