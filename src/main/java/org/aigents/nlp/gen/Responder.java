/*
 * MIT License
 * 
 * Copyright (c) 2020-Present by Vignav Ramesh and Anton Kolonin, Aigents®
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package main.java.org.aigents.nlp.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import main.java.org.aigents.nlp.lg.Dictionary;
import main.java.org.aigents.nlp.lg.Loader;

public class Responder {
	public static Dictionary dict, hyphenated;
	public static HashMap<String, Integer> corpusLexicon, contextLexicon;
	public static TreeMap<Double, HashSet<String>> results;
	public static List<String> dw;
	public static long startTime;
	public static boolean stop = false;
	public static long limit = 0;

	public static void main(String[] args) throws IOException {	
		stop = false;
		try {
			if (args.length > 2) {
				if (args[0].contains("/4.0.dict")) {
					Dictionary[] dicts = Loader.buildLGDict(args[0]);
					dict = dicts[0];
					hyphenated = dicts[1];
				} else {
					dict = Loader.grammarBuildLinks(args[0], true);
				}
				
				startTime = System.currentTimeMillis();
				
				corpusLexicon = Loader.getCorpusLexicon(args[0]);
				contextLexicon = Loader.getContextLexicon(args[1]);
												
				String[] words = new String[args.length - 2];
				for (int i = 2; i < args.length; i++) {
					words[i - 2] = args[i];
				}
				
				results = new TreeMap<>(Collections.reverseOrder());

				Set<String> dws = contextLexicon.keySet();
				dw = new ArrayList<>(dws);
				Collections.sort(dw, (o1, o2) -> {
					if (zipfian(o2)>zipfian(o1)) return 1;
					else if (zipfian(o2)<zipfian(o1)) return -1;
					else return 0;
				});
								
				int numWords = 1;
				while (results.isEmpty()) {
					String[] param = new String[words.length + numWords];
					for (int i = 0; i < words.length; i++) {
						param[i] = words[i];
					}
					int idx = words.length;
					recurseFirst(args[0], args[1], idx, param, words);
					if (results.isEmpty()) {
						recurse(args[0], args[1], idx, param, words);
					}
					numWords++;
				}
			} else {
				System.out.println("Incorrect number of command line parameters given.");
			}
		} catch (Exception e) {
			System.err.println("Error building dictionary. Please try again with a different filename.");
		}
	}
	
	public static HashSet<String> generateSentence(String args, String args1, String[] words) {
		if (args.contains("/4.0.dict")) {
			return Generator.generateSentence(dict, hyphenated, args1, words);
		}
		else {
			return SmallGrammarGen.generateSentence(dict, words);
		}
	}
	
	public static HashSet<String> isValid(String args, String args1, String[] words) {
		if (args.contains("/4.0.dict")) {
			return Generator.isValid(dict, hyphenated, args1, words);
		}
		else {
			return SmallGrammarGen.generateSentence(dict, words);
		}
	}
	
	private static void recurseFirst(String args, String args1,  int idx, String[] param, String[] words) {
		if (stop && (System.currentTimeMillis() -startTime)>limit) return;
		if (idx >= param.length) {
			double rank = 0;
			for (int i = words.length; i < param.length; i++) {
				rank += zipfian(param[i]);
			}
			String[] clone = new String[param.length];
			for (int i = 0; i < param.length; i++) {
				clone[i] = param[i];
			}
			HashSet<String> pAns = isValid(args, args1, clone);
			if (!pAns.isEmpty() && !contains(pAns)) {
				results.put(rank, pAns);
				long runtime = System.currentTimeMillis() - startTime;
				String t = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(runtime),
						TimeUnit.MILLISECONDS.toSeconds(runtime)
								- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runtime)));
				
				System.out.println(results.get(results.lastKey()));
				System.out.println("Runtime: " + t);
				stop = true;
			}
			return;
		}
		String[] n = new String[param.length];
		for (int i = 0; i < param.length; i++) {
			n[i] = param[i];
		}
		for (String w : dw) {
			if (!contains(n, w)) {
				n[idx] = w;
				recurseFirst(args, args1, idx+1, n, words);
			}
		}
	}
	
	private static void recurse(String args, String args1,  int idx, String[] param, String[] words) {
		if (stop && (System.currentTimeMillis() -startTime)>limit) return;
		if (idx >= param.length) {
			double rank = 0;
			for (int i = words.length; i < param.length; i++) {
				rank += zipfian(param[i]);
			}
			String[] clone = new String[param.length];
			for (int i = 0; i < param.length; i++) {
				clone[i] = param[i];
			}
			HashSet<String> pAns = generateSentence(args, args1, clone);
			if (!pAns.isEmpty() && !contains(pAns)) {
				results.put(rank, pAns);
				long runtime = System.currentTimeMillis() - startTime;
				String t = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(runtime),
						TimeUnit.MILLISECONDS.toSeconds(runtime)
								- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runtime)));
				
				System.out.println(results.get(results.lastKey()));
				System.out.println("Runtime: " + t);
				stop = true;
			}
			return;
		}
		String[] n = new String[param.length];
		for (int i = 0; i < param.length; i++) {
			n[i] = param[i];
		}
		for (String w : dw) {
			if (!contains(n, w)) {
				n[idx] = w;
				recurse(args, args1, idx+1, n, words);
			}
		}
	}
	
	private static boolean contains(HashSet<String> pAns) {
		for (HashSet<String> val : results.values()) {
			if (val.equals(pAns)) return true;
		}
		return false;
	}
	
	private static double zipfian(String w) {
		int wContext = contextLexicon.get(w) == null? 0 : contextLexicon.get(w);
		int wCorpus = corpusLexicon.get(w) == null? 0 : corpusLexicon.get(w);
		return Math.log(1+wContext)/Math.log(1+wCorpus);
	}
		
	private static boolean contains(String[] input, String str) {
		for (String s : input) {
			if (s != null && s.equals(str))
				return true;
		}
		return false;
	}
}