/*******************************************************************
 * Word.java                                                       *
 * Stores the name and rule of a given word in the Link Grammar    *
 * dictionary.                                                     *
 *                                                                 *
 * Written by Vignav Ramesh                                        *
 *******************************************************************/

package org.aigents.nlp.lg;

public class Word {
	private String word;
	private Rule rule;
	
	public Word(String word, String classname, Rule rule) {
		this.word = word;
		this.rule = rule;
	}
	
	public Word(String word) {
		this.word = word;
		rule = new Rule();
	}
	
	public void addRule(String rule) {
		this.rule.addWord(rule);
	}
	
	public boolean containsRule(String rule) {
		return this.rule.getWords().contains(rule);
	}
	
	public void updateRule(Rule rule) {
		this.rule = rule;
	}
	
	public String getWord() {	return word;	}
	
	public Rule getRule() {	return rule;	}
	
	@Override
	public String toString() {
		return word;
	}
}