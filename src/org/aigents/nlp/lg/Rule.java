package org.aigents.nlp.lg;

import java.util.ArrayList;

public class Rule {
	private ArrayList<String> words;
	private ArrayList<Disjunct> disjuncts;
	
	public Rule() {
		words = new ArrayList<>();
		disjuncts = new ArrayList<>();
	}
	
	public Rule(ArrayList<String> words, ArrayList<Disjunct> disjuncts) {
		this.words = words;
		this.disjuncts = disjuncts;
	}
	
	public void addWord(String word) {
		words.add(word);
		Disjunct d = new Disjunct();
		for (String connector : word.split(" & ")) {
			d.addConnector(connector);
		}
		addDisjunct(d);
	}
	
	public void addDisjunct(Disjunct disjunct) {
		disjuncts.add(disjunct);
	}
	
	public void updateWords(ArrayList<String> words) {
		this.words = words;
	}
	
	public void updateDisjuncts(ArrayList<Disjunct> disjuncts) {
		this.disjuncts = disjuncts;
	}
	
	public ArrayList<String> getWords() {	return words;	}
	
	public ArrayList<Disjunct> getDisjuncts() {	return disjuncts;	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < words.size() - 1; i++) {
			s.append("(" + words.get(i) + ") or ");
		}
		s.append("(" + words.get(words.size() - 1) + ")");
		return s.toString();
	}
}