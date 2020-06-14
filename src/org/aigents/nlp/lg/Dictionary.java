package org.aigents.nlp.lg;

import java.util.*;
import java.io.*;

public class Dictionary {
	ArrayList<Word> words;
	ArrayList<Disjunct> disjuncts;
	int wordIdx, classIdx;
	public static final String versionNumber = "V5v8v0+";
	public static final String locale = "EN4us+";
	
	public Dictionary() {
		words = new ArrayList<>();
		disjuncts = new ArrayList<>();
	}
	
	public Dictionary(ArrayList<Word> words, ArrayList<Disjunct> disjuncts) {
		this.words = words;
		this.disjuncts = disjuncts;
		wordIdx = classIdx = 0;
	}
	
	public void addWord(Word word) {
		words.add(word);
	}
	
	public void addDisjunct(Disjunct disjunct) {
		disjuncts.add(disjunct);
	}
	
	public void updateWords(ArrayList<Word> words) {
		this.words = words;
	}
	
	public void updateDisjuncts(ArrayList<Disjunct> disjuncts) {
		this.disjuncts = disjuncts;
	}
	
	public void setWordIdx(int wordIdx) {
		this.wordIdx = wordIdx;
	}
	
	public void setClassIdx(int classIdx) {
		this.classIdx = classIdx;
	}
	
	public String getVersionNumber() {	return versionNumber;	}
	
	public String getLocale() {	return locale;	}
	
	public int getWordIdx() {	return wordIdx;	}
	
	public int getClassIdx() {	return classIdx;	}
	
	public ArrayList<Word> getWords() {	return words;	}
	
	public ArrayList<Disjunct> getDisjuncts() {	return disjuncts;	}
}