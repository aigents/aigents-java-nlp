package org.aigents.nlp.lg;

import java.util.ArrayList;
import java.util.HashSet;

public class Dictionary {
	private HashSet<Word> words;
	private static final String versionNumber = "V5v8v0+";
	private static final String locale = "EN4us+";
	
	public Dictionary() {
		words = new HashSet<>();
	}
	
	public Dictionary(HashSet<Word> words) {
		this.words = words;
	}
	
	public ArrayList<String> getRules(String word) {
		for (Word w : words) {
			if (w.getWord().equals(word)) return w.getRules();
		}
		return null;
	}
	
	public Disjunct getDisjunct(String rule) {
		Disjunct d = new Disjunct();
		String[] connectors = rule.split(" & ");
		for (String c : connectors) d.addConnector(c);
		return d;
	}
	
	public void addWord(Word word) {
		words.add(word);
	}
	
	public void updateWords(HashSet<Word> words) {
		this.words = words;
	}
	
	public String getVersionNumber() {	return versionNumber;	}
	
	public String getLocale() {	return locale;	}
	
	public HashSet<Word> getWords() {	return words;	}
}