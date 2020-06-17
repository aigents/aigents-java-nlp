/*******************************************************************
 * Dictionary.java                                                 *
 * Stores the Link Grammar dictionary for later use.               *
 *                                                                 *
 * Written by Vignav Ramesh                                        *
 *******************************************************************/

package org.aigents.nlp.lg;

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