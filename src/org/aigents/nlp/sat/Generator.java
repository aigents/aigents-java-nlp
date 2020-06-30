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

package org.aigents.nlp.sat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.aigents.nlp.lg.Dictionary;
import org.aigents.nlp.lg.Disjunct;
import org.aigents.nlp.lg.Loader;
import org.aigents.nlp.lg.Rule;

public class Generator {
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			Dictionary dict = Loader.grammarBuildLinks(args[0], true);
			List<String[]> words = processSentences(args[1]);
			if (words == null) {
				System.err.println("Error loading and tokenizing sentences.");
				return;
			}
			for (String[] w : words) {
				System.out.println(Arrays.toString(w) + ": " + generateSentence(w, dict));
			}
		} else {
			System.out.println("No command line parameters given.");
		}
		Dictionary dict = Loader.grammarBuildLinks("dict_30C_2018-12-31_0006.4.0.dict", true);
		List<String[]> words = processSentences("poc_english.txt");
		if (words == null) {
			System.err.println("Error loading and tokenizing sentences.");
			return;
		}
		for (String[] w : words) {
			System.out.println(Arrays.toString(w) + ": " + generateSentence(w, dict));
		}
	}
	
	public static HashSet<String> generateSentence(String[] elements, Dictionary dict) {
		boolean not = false;
		HashSet<String> ret = new HashSet<>();
		int n = elements.length;
		int[] indexes = new int[n];
		for (int i = 0; i < n; i++) {
		    indexes[i] = 0;
		} 
		ArrayList<String> w = new ArrayList<>(Arrays.asList(elements));
		if (w.contains("not") && w.contains("a")) {
			not = true;
			w.remove("not");
			int i = 0;
			String[] input = new String[w.size()];
			for (String word : w) {
				input[i] = word;
				i++;
			}
			if (isValid(input, dict)) {
				String str = makeSentence(input);
				str = str.replace(" a", " not a");
				ret.add(str);
			}
		} else {
			if (isValid(elements, dict)) {
				ret.add(makeSentence(elements));
			}
		}
		int i = 0;
		while (i < n) {
		    if (indexes[i] < i) {
		        swap(elements, i % 2 == 0 ?  0: indexes[i], i);
				if (not) {
					int idx = 0;
					String[] input = new String[w.size()];
					for (String word : w) {
						input[idx] = word;
						idx++;
					}
					if (isValid(input, dict)) {
						String str = makeSentence(input);
						str = str.replace(" a", " not a");
						ret.add(str);
					}
				} else {
					if (isValid(elements, dict)) {
						ret.add(makeSentence(elements));
					}
				}
		        indexes[i]++;
		        i = 0;
		    }
		    else {
		        indexes[i] = 0;
		        i++;
		    }
		}
		return ret;
	}
	
	private static boolean connects(String left, String right, Dictionary dict) {
		Rule leftRule = dict.getRule(left.toLowerCase());
    	Rule rightRule = dict.getRule(right.toLowerCase());
    	for (Disjunct dl : leftRule.getDisjuncts()) {
    		for (Disjunct dr : rightRule.getDisjuncts()) {
    			String wl = "";
    			String wr = "";
    			if (dl.getConnectors().size() > 1) {
    				for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
	    				String c = dl.getConnectors().get(ci);
	    				if (!c.contains("-")) {
	    					wl += c + " & ";
	    				}
	    			}
    				String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
    				if (!c.contains("-")) {
    					wl += c;
    				} else {
    					if (wl.contains("&")) wl = wl.substring(0, wl.length() - 3);
    				}
    			} else {
    				wl = dl.getConnectors().get(0);
    			}
    			if (dr.getConnectors().size() > 1) {
    				for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
	    				String c = dr.getConnectors().get(ci);
	    				if (c.contains("-")) {
	    					wr += c + " & ";
	    				}
	    			}
    				String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
    				if (c.contains("-")) {
    					wr += c;
    				} else {
    					if (wr.contains("&")) wr = wr.substring(0, wr.length() - 3);
    				}
    			} else {
    				wr = dr.getConnectors().get(0);
    			}    			
    			String wlu = wl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
    			if (wlu.equals(wr)) {
    				return true;
    			}
    		}
    	}
    	return false;
	}
	
	private static boolean connects(String left, String mid, String right, Dictionary dict) {
		Rule leftRule = dict.getRule(left.toLowerCase());
		Rule midRule = dict.getRule(left.toLowerCase());
    	Rule rightRule = dict.getRule(right.toLowerCase());
    	boolean leftTrue = false;
    	boolean midTrue = false;
    	for (Disjunct dr : rightRule.getDisjuncts()) {
    		String wr = "";
    		if (dr.getConnectors().size() > 1) {
				for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
    				String c = dr.getConnectors().get(ci);
    				if (c.contains("-")) {
    					wr += c + " & ";
    				}
    			}
				String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
				if (c.contains("-")) {
					wr += c;
				} else {
					if (wr.contains("&")) wr = wr.substring(0, wr.length() - 3);
				}
			} else {
				wr = dr.getConnectors().get(0);
			}
    		for (Disjunct dl : leftRule.getDisjuncts()) {
    			for (Disjunct dm : midRule.getDisjuncts()) {
    				if (!wr.contains("&")) return false;
    				String[] parts = wr.split(" & ");
    				for (String part : parts) {
    					String wl = "";
    	    			if (dl.getConnectors().size() > 1) {
    	    				for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
    		    				String c = dl.getConnectors().get(ci);
    		    				if (!c.contains("-")) {
    		    					wl += c + " & ";
    		    				}
    		    			}
    	    				String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
    	    				if (!c.contains("-")) {
    	    					wl += c;
    	    				} else {
    	    					if (wl.contains("&")) wl = wl.substring(0, wl.length() - 3);
    	    				}
    	    			} else {
    	    				wl = dl.getConnectors().get(0);
    	    			}
    	    			String wm = "";
    	    			if (dm.getConnectors().size() > 1) {
    	    				for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
    		    				String c = dm.getConnectors().get(ci);
    		    				if (!c.contains("-")) {
    		    					wm += c + " & ";
    		    				}
    		    			}
    	    				String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
    	    				if (!c.contains("-")) {
    	    					wm += c;
    	    				} else {
    	    					if (wm.contains("&")) wm = wm.substring(0, wm.length() - 3);
    	    				}
    	    			} else {
    	    				wm = dm.getConnectors().get(0);
    	    			}
    	    			String wlu = wl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
    	    			String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
    	    			if (wlu.equals(part)) leftTrue = true;
    	    			if (wmu.equals(part)) midTrue = true;
    				}
    			}
    		}
    	}
    	return leftTrue && midTrue;
	}
	
	private static boolean contains(String[] input, String str) {
		for (String s : input) {
			if (s.equals(str)) return true;
		}
		return false;
	}

	private static boolean isValid(String[] input, Dictionary dict) {
	    outer: for (int i = 0; i < input.length - 1; i++) {
	    	String left = input[i];
	    	String right = input[i+1];
	    	if ((right.equals("a") || right.equals("the"))  && i+2 < input.length) {
	    		i++;
	    		if ((connects(left, input[i+1], dict) && connects(right, input[i+1], dict)) 
	    				|| connects(left, right, input[i+1], dict)) continue outer;
	    	} else if (right.equals("before") && !contains(input, "a") && i >= 1) {
	    		if (connects(input[i-1], right, dict)) continue outer;
	    	} else {
		    	if (connects(left, right, dict)) continue outer;
	    	}
	    	return false;
	    }
	    return true;
	}

	private static void swap(String[] input, int a, int b) {
	    String tmp = input[a];
	    input[a] = input[b];
	    input[b] = tmp;
	}
	
	private static String makeSentence(String[] arr) {
		String ret = "";
		for (int i = 0; i < arr.length - 1; i++) {
			ret += arr[i] + " ";
		}
		ret += arr[arr.length - 1] + ".";
		return ret;
	}
	
	public static List<String[]> processSentences(String path) throws IOException {
		URL url = new Generator().getClass().getResource(path);
		File f = new File(url.getPath());
		if (!f.exists()) return null;
		List<String> sentences = Files.readAllLines(f.toPath());
		Iterator<String> it = sentences.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty()) it.remove();
		}
		List<String[]> words = new ArrayList<>();
		for (String sentence : sentences) {
			String[] w = sentence.split(" ");
			w[w.length - 1] = w[w.length - 1].substring(0, w[w.length - 1].length() - 1);
			words.add(w);
		}
		return words;
	}
}
