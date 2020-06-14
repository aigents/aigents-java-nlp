package org.aigents.nlp.lg;

import java.util.*;
import java.io.*;

public class Word {
	String word, classname, subscript;
	ArrayList<String> rules;
	
	public Word(String word, String classname, String subscript, String rule, ArrayList<String> rules) {
		this.word = word;
		this.classname = classname;
		this.subscript = subscript;
		this.rules = rules;
	}
	
	public Word(String word, String classname, String subscript, String rule) {
		this.word = word;
		this.classname = classname;
		this.subscript = subscript;
		this.rules = new ArrayList<>();
	}
	
	public void addRule(String rule) {
		rules.add(rule);
	}
	
	public void updateRules(ArrayList<String> rules) {
		this.rules = rules;
	}
	
	public String getWord() {	return word;	}
	
	public String getClassname() {	return classname;	}
	
	public String getSubscript() {	return subscript;	}
	
	public ArrayList<String> getRules() {	return rules;	}
}
