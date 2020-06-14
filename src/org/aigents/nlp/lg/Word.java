package org.aigents.nlp.lg;

import java.util.*;
import java.io.*;

public class Word {
	String word, classname, subscript, rule;
	
	public Word(String word, String classname, String subscript, String rule) {
		this.word = word;
		this.classname = classname;
		this.subscript = subscript;
		this.rule = rule;
	}
	
	public void updateRule(String rule) {
		this.rule = rule;
	}
}
