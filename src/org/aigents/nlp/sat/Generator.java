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
		if (args.length == 2) {
			int correctCases = 0;
			try {
				Dictionary dict = Loader.grammarBuildLinks(args[0], true);
				List<String> list = getList(args[1]);
				List<String[]> words = processSentences(args[1]);
				if (words == null) {
					System.err.println("Error loading and tokenizing sentences.");
					return;
				}
				int idx = -1;
				for (String[] w : words) {
					idx++;
					HashSet<String> sentence = generateSentence(w, dict);
					System.out.println(Arrays.toString(w) + ": " + sentence);
					String s = list.get(idx);
					if (sentence.size() > 1) {
						for (String sen2 : sentence) {
							if (sen2.equals(s)) continue;
							String sen = sen2.substring(0, sen2.length() - 1);
							String[] senParts = sen.split(" ");
							String[] sParts = s.substring(0, s.length() - 1).split(" ");
							ArrayList<String> mismatches = new ArrayList<>();
							for (int i = 0; i < sParts.length; i++) {
								if (!senParts[i].equals(sParts[i])) {
									mismatches.add(senParts[i]);
								}
							}
							System.out.println("      The words " + mismatches + " are in the wrong place.");
							System.out.println("      While the sentence \"" + sen2 + "\" is grammatically valid, it is contextually wrong.");
						}
					} else {
						for (String sen : sentence) {
							if (sen.equals(s)) correctCases++;
						}
					}
				}
				System.out.println("Correct cases: " + correctCases + "/" + words.size());
				System.out.println("Accuracy: " + ((double) correctCases)/words.size());
			} catch (Exception e) {
				System.err.println("Error building dictionary. Please try again with a different filename.");
			}
		} else if (args.length > 2) {
			try {
				Dictionary dict = Loader.grammarBuildLinks(args[0], true);
				String[] words = new String[args.length - 1];
				for (int i = 1; i < args.length; i++) {
					words[i-1] = args[i];
				}
				System.out.println(Arrays.toString(words) + ": " + generateSentence(words, dict));
			} catch (Exception e) {
				System.err.println("Error building dictionary. Please try again with a different filename.");
			}
		} else {
			System.out.println("No command line parameters given.");
		}
	}
	
	public static HashSet<String> generateSentence(String[] elements, Dictionary dict) {
		boolean not = false;
		boolean now = false;
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
		} 
		if (w.contains("now") && w.contains("a")) {
			now = true;
			w.remove("now");
		} 
		if (not || now) {
			int i = 0;
			String[] input = new String[w.size()];
			for (String word : w) {
				input[i] = word;
				i++;
			}
			if (now && !not) {
				if (isValid(input, dict)) {
					String str = makeSentence(input);
					int nowId = 0;
					String[] parts = str.split(" ");
					for (int m = 0; m < parts.length; m++) {
						if (parts[m].equals("a")) {
							nowId = str.indexOf(" a") + 2 + parts[m+1].length();
						}
					}
					String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
					ret.add(str3);
				}
			}
			if (not && !now) {
				if (isValid(input, dict)) {
					String str = makeSentence(input);
					str = str.replace(" a", " not a");
					ret.add(str);
				}
			}
			if (not && now) {
				if (isValid(input, dict)) {
					String str = makeSentence(input);
					int nowId = 0;
					String[] parts = str.split(" ");
					for (int m = 0; m < parts.length; m++) {
						if (parts[m].equals("a")) {
							nowId = str.indexOf(" a") + 2 + parts[m+1].length();
						}
					}
					String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
					str3 = str3.replace(" a", " not a");
					ret.add(str3);
				}
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
		        ArrayList<String> w2 = new ArrayList<>(Arrays.asList(elements));
		        if (w2.contains("not") && w2.contains("a")) {
					w2.remove("not");
				} 
				if (w2.contains("now") && w2.contains("a")) {
					w2.remove("now");
				} 
		        if (not || now) {
					int id = 0;
					String[] input = new String[w2.size()];
					for (String word : w2) {
						input[id] = word;
						id++;
					}
					if (now && !not) {
						if (isValid(input, dict)) {
							String str = makeSentence(input);
							int nowId = 0;
							String[] parts = str.split(" ");
							for (int m = 0; m < parts.length; m++) {
								if (parts[m].equals("a")) {
									nowId = str.indexOf(" a") + 2 + parts[m+1].length();
								}
							}
							String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
							ret.add(str3);
						}
					}
					if (not && !now) {
						if (isValid(input, dict)) {
							String str = makeSentence(input);
							str = str.replace(" a", " not a");
							ret.add(str);
						}
					}
					if (not && now) {
						if (isValid(input, dict)) {
							String str = makeSentence(input);
							int nowId = 0;
							String[] parts = str.split(" ");
							for (int m = 0; m < parts.length; m++) {
								if (parts[m].equals("a")) {
									nowId = str.indexOf(" a") + 2 + parts[m+1].length();
								}
							}
							String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
							str3 = str3.replace(" a", " not a");
							ret.add(str3);
						}
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
		Rule leftRule = new Rule(), rightRule = new Rule();
		try {
			leftRule = dict.getRule(left.toLowerCase());
			if (leftRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			rightRule = dict.getRule(right.toLowerCase());
			if (rightRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
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
		Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule();
		try {
			leftRule = dict.getRule(left.toLowerCase());
			if (leftRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			midRule = dict.getRule(mid.toLowerCase());
			if (midRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + mid + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			rightRule = dict.getRule(right.toLowerCase());
			if (rightRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
    	boolean leftTrue = false;
    	boolean midTrue = false;
    	int leftId = 0;
    	int midId = 0;
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
			if (!wr.contains("&")) continue;
    		for (Disjunct dl : leftRule.getDisjuncts()) {
    			for (Disjunct dm : midRule.getDisjuncts()) {
    				String[] parts = wr.split(" & ");
    				for (int idp = 0; idp < parts.length; idp++) {
    					String part = parts[idp];
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
    	    			if (wlu.equals(part)) {
    	    				leftTrue = true;
    	    				leftId = idp;
    	    			}
    	    			if (wmu.equals(part)) {
    	    				midTrue = true;
    	    				midId = idp;
    	    			}
    				}
    			}
    		}
    	}
    	return leftTrue && midTrue && midId < leftId;
	}
	
	private static boolean connectsLeft(String left, String mid, String right, Dictionary dict) {
		Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule();
		try {
			leftRule = dict.getRule(left.toLowerCase());
			if (leftRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			midRule = dict.getRule(mid.toLowerCase());
			if (midRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + mid + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			rightRule = dict.getRule(right.toLowerCase());
			if (rightRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
    	boolean midTrue = false;
    	boolean rightTrue = false;
    	int rightId = 0;
    	int midId = 0;
    	for (Disjunct dl : leftRule.getDisjuncts()) {
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
			if (!wl.contains("&")) continue;
    		for (Disjunct dr : rightRule.getDisjuncts()) {
    			for (Disjunct dm : midRule.getDisjuncts()) {
    				String[] parts = wl.split(" & ");
    				for (int idp = 0; idp < parts.length; idp++) {
    					String part = parts[idp];
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
    	    			String wm = "";
    	    			if (dm.getConnectors().size() > 1) {
    	    				for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
    		    				String c = dm.getConnectors().get(ci);
    		    				if (c.contains("-")) {
    		    					wm += c + " & ";
    		    				}
    		    			}
    	    				String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
    	    				if (c.contains("-")) {
    	    					wm += c;
    	    				} else {
    	    					if (wm.contains("&")) wm = wm.substring(0, wm.length() - 3);
    	    				}
    	    			} else {
    	    				wm = dm.getConnectors().get(0);
    	    			}
    	    			String wru = wr.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
    	    			String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
    	    			if (wru.equals(part)) {
    	    				rightTrue = true;
    	    				rightId = idp;
    	    			}
    	    			if (wmu.equals(part)) {
    	    				midTrue = true;
    	    				midId = idp;
    	    			}
    				}
    			}
    		}
    	}
    	return rightTrue && midTrue && midId < rightId;
	}
	
	private static boolean contains(String[] input, String str) {
		for (String s : input) {
			if (s.equals(str)) return true;
		}
		return false;
	}
	
	private static boolean connectsFour(String left, String mid, String right, String next, Dictionary dict) {
		Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule(), nextRule = new Rule();
		try {
			leftRule = dict.getRule(left.toLowerCase());
			if (leftRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			midRule = dict.getRule(mid.toLowerCase());
			if (midRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + mid + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			rightRule = dict.getRule(right.toLowerCase());
			if (rightRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
		try {
			nextRule = dict.getRule(next.toLowerCase());
			if (nextRule == null) throw new Exception();
		} catch (Exception e) {
			System.err.println("Word '" + next + "' not found in dictionary.");
			System.exit(0);
		}
    	boolean midTrue = false;
    	boolean rightTrue = false;
    	boolean nextTrue = false;
    	int rightId = 0;
    	int midId = 0;
    	int nextId = 0;
    	for (Disjunct dl : leftRule.getDisjuncts()) {
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
			if (!wl.contains("&")) continue;
    		for (Disjunct dr : rightRule.getDisjuncts()) {
    			for (Disjunct dm : midRule.getDisjuncts()) {
    				for (Disjunct dn : nextRule.getDisjuncts()) {
	    				String[] parts = wl.split(" & ");
	    				for (int idp = 0; idp < parts.length; idp++) {
	    					String part = parts[idp];
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
	    	    			String wm = "";
	    	    			if (dm.getConnectors().size() > 1) {
	    	    				for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
	    		    				String c = dm.getConnectors().get(ci);
	    		    				if (c.contains("-")) {
	    		    					wm += c + " & ";
	    		    				}
	    		    			}
	    	    				String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
	    	    				if (c.contains("-")) {
	    	    					wm += c;
	    	    				} else {
	    	    					if (wm.contains("&")) wm = wm.substring(0, wm.length() - 3);
	    	    				}
	    	    			} else {
	    	    				wm = dm.getConnectors().get(0);
	    	    			}
	    	    			String wn = "";
	    	    			if (dn.getConnectors().size() > 1) {
	    	    				for (int ci = 0; ci < dn.getConnectors().size() - 1; ci++) {
	    		    				String c = dn.getConnectors().get(ci);
	    		    				if (c.contains("-")) {
	    		    					wn += c + " & ";
	    		    				}
	    		    			}
	    	    				String c = dn.getConnectors().get(dn.getConnectors().size() - 1);
	    	    				if (c.contains("-")) {
	    	    					wn += c;
	    	    				} else {
	    	    					if (wn.contains("&")) wn = wn.substring(0, wn.length() - 3);
	    	    				}
	    	    			} else {
	    	    				wn = dn.getConnectors().get(0);
	    	    			}
	    	    			String wru = wr.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
	    	    			String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
	    	    			String wnu = wn.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
	    	    			if (wru.equals(part)) {
	    	    				rightTrue = true;
	    	    				rightId = idp;
	    	    			}
	    	    			if (wmu.equals(part)) {
	    	    				midTrue = true;
	    	    				midId = idp;
	    	    			}
	    	    			if (wnu.equals(part)) {
	    	    				nextTrue = true;
	    	    				nextId = idp;
	    	    			} else {
	    	    				ifc: if (wnu.contains("&")) {
	    	    					String[] p = wnu.split(" & ");
	    	    					for (String s : p) {
	    	    						if (s.equals(part)) {
	    		    	    				nextTrue = true;
	    		    	    				nextId = idp;
	    		    	    				break ifc;
	    		    	    			}
	    	    					}
	    	    				}
	    	    			}
	    				}
    				}
    			}
    		}
    	}
    	return rightTrue && midTrue && (midId < rightId) && (rightId < nextId);
	}

	private static boolean isValid(String[] input, Dictionary dict) {
		boolean debug = false;
		if (Arrays.equals(input, new String[] {"Dad", "wants", "Mom", "to", "be", "on", "the", "board", "of", "directors"})) debug = true;
		if (debug) System.out.println(Arrays.toString(input));
	    outer: for (int i = 0; i < input.length - 1; i++) {
	    	String left = input[i];
	    	String right = input[i+1];
	    	if ((right.equals("a") || right.equals("the")) && i+2 < input.length) {
	    		i++;
	    		if ((connects(left, input[i+1], dict) && connects(right, input[i+1], dict)) 
	    				|| connects(left, right, input[i+1], dict)) continue outer;
	    	} else if (i+2 < input.length && (input[i+2].equals("before") || input[i+2].equals("now")) && !contains(input, "a")) {
	    		i++;
	    		if ((connects(left, right, dict) && connects(left, input[i+1], dict)) 
	    				|| connectsLeft(left, right, input[i+1], dict)) continue outer;
	    	} else if (right.equals("on") && i >= 2 && (input[i-1].equals("a") || input[i-1].equals("with"))) {
	    		if (input[i-1].equals("a") && i >= 3) {
	    			if (connectsLeft(input[i-3], input[i-2], right, dict)) continue outer;
	    		} else {
	    			if (connectsLeft(input[i-2], input[i-1], right, dict)) continue outer;
	    		}
	    	} else if (left.equals("wants") && i+2 < input.length) {
	    		if (right.equals("to")) {
	    			i += 2;
	    			if (connectsLeft("wants", "to", input[i], dict)) continue outer;
	    		} else if (input[i+2].equals("to")) {
	    			if (i+3 < input.length) {
	    				i += 3;
	    				if (connectsFour(left, right, input[i-1], input[i], dict)) continue outer;
	    			}
	    		}
	    	} else {
		    	if (connects(left, right, dict)) continue outer;
		    	if (right.equals("with")) {
		    		int idx;
		    		for (idx = 0; idx < input.length; idx++) {
		    			if (input[idx].equals("on")) {
		    				break;
		    			}
		    		}
		    		if (idx > i) {
		    			if (connectsLeft(left, right, "on", dict)) continue outer;
		    		}
		    	}
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
		try {
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
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<String> getList(String path) throws IOException {
		try {
			URL url = new Generator().getClass().getResource(path);
			File f = new File(url.getPath());
			if (!f.exists()) return null;
			List<String> sentences = Files.readAllLines(f.toPath());
			Iterator<String> it = sentences.iterator();
			while (it.hasNext()) {
				if (it.next().isEmpty()) it.remove();
			}
			return sentences;
		} catch (Exception e) {
			return null;
		}
	}
}
