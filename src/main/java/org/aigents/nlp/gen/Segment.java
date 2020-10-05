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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.org.aigents.nlp.lg.Dictionary;
import main.java.org.aigents.nlp.lg.Loader;
import main.java.org.aigents.nlp.lg.Rule;

public class Segment {
	public static Dictionary dict, hyphenated;

	public static void main(String[] args) throws IOException {
		if (args.length > 2) {
			try {
				if (args[0].contains("/4.0.dict")) {
					Dictionary[] dicts = Loader.buildLGDict(args[0]);
					dict = dicts[0];
					hyphenated = dicts[1];
				} else {
					dict = Loader.grammarBuildLinks(args[0], false);
				}
				String[] words = new String[args.length - 1];
				for (int i = 1; i < args.length; i++) {
					words[i - 1] = args[i];
				}
				System.out.println(display(words) + ": " + segment(words));
			} catch (Exception e) {
				System.err.println("Error building dictionary. Please try again with a different filename.");
			}
		} else if (args.length == 2) {
			try {
				if (args[0].contains("/4.0.dict")) {
					Dictionary[] dicts = Loader.buildLGDict(args[0]);
					dict = dicts[0];
					hyphenated = dicts[1];
				} else {
					dict = Loader.grammarBuildLinks(args[0], false);
				}
				List<String> w = new ArrayList<>();
				try {
					w = processSentences("gutenberg544.txt");
				} catch (Exception e) {
					System.err.println("Invalid filename: unable to locate and retrieve text to segment.");
				}
				String[] words = new String[w.size()];
				for (int i = 0; i < w.size(); i++) {
					words[i] = w.get(i);
				}
				System.out.println(display(words) + ": " + segment(words)); 
			} catch (Exception e) {
				System.err.println("Error building dictionary. Please try again with a different filename.");
			}
		} else {
			System.out.println("Invalid or nonexistent command line parameters. Include [path/to/dict] followed by the sentence.");
		}
	}
	
	private static ArrayList<String> segment(String[] words) {
		ArrayList<String> ret = new ArrayList<>();
		int idx = 0;
		while (idx < words.length) {
			boolean valid = false;
			for (int i = idx; i < words.length; i++) {
				String[] arr = new String[i-idx+1];
				for (int a = idx; a <= i; a++) {
					arr[a-idx] = words[a];
				}
				if (i != words.length - 2 && (i+1>=words.length? true : check(words[i+1], 
						i+2<words.length? words[i+2] : words[i+1])) && check(arr) && isValid(arr)) {
					String[] finalArr = arr;
					int finalI = i+1;
					int threshold = (arr.length >= 13)? 0 : 2;
					for (int j = Math.min(i+threshold, words.length-1); j > i; j--) {
						String[] ar = new String[arr.length + j - i];
						for (int id = 0; id < arr.length; id++) {
							ar[id] = arr[id];
						}
						for (int id = arr.length; id < ar.length; id++) {
							ar[id] = words[i + id - arr.length + 1];
						}
						if (j != words.length - 2 && (j+1>=words.length? true : check(words[j+1], 
								j+2<words.length? words[j+2] : words[j+1])) && check(ar) && isValid(ar)) {
							finalArr = ar;
							finalI = j+1;
							break;
						}
					}
					ret.add(sentence(finalArr));
					idx = finalI;
					valid = true;
					break;
				}
			}
			if (!valid) {
				ret.clear();
				ret.add("No valid sentences.");
				break;
			}
		}
		return ret;
	}
	
	private static boolean containsVerb(String[] input) {
		boolean containsVerb = false;
		for (String word : input) {
			ArrayList<String> subs;
			try {
				subs = dict.getSubscript(word);
			} catch (Exception e) {
				subs = new ArrayList<>();
			}
			if (!subs.contains("a") && (subs.contains("v") || subs.contains("v-d"))) {
				containsVerb = true;
			}
		}
		return containsVerb;
	}
	
	private static boolean check(String[] input) {
		if (contains(input, ",") && input.length < 6) return false;
		if (input.length <= 1) return false;
		int comma = idx(input, ",");
		if (comma == -1) comma = idx(input, "how");
		if (dict.getSubscript(input[input.length - 1]).contains("r")) return false;
		if ((dict.getSubscript(input[0]).contains("v") || dict.getSubscript(input[0]).contains("v-d"))
				&& !(dict.getSubscript(input[1]).contains("a") || dict.getSubscript(input[0]).contains("n") || dict.getSubscript(input[0]).contains("n-u")
						|| dict.getSubscript(input[0]).contains("p")))
			return false;
		String last = input[input.length - 1].toLowerCase().trim();
		if (last.equals("a") || (dict.getSubscript(last).size() > 0 && (!dict.getSubscript(last).contains("n")
				&& !dict.getSubscript(last).contains("r") 
				&& !dict.getSubscript(last).contains("a") 
				&& !dict.getSubscript(last).contains("n-u"))))
			return false;
		if (comma != -1) {
			String[] in = new String[input.length - comma];
			for (int i = comma + 1; i < input.length;  i++) {
				in[i - comma - 1] = input[i];
			}
			if (!containsVerb(in)) return false;
		}
		return containsVerb(input);
	}
	
	private static String sentence(String[] words) {
		String ret = "";
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].toLowerCase();
			String word = words[i];
			word = word.toLowerCase();
			ArrayList<String> subs;
			subs = dict.getSubscript(word);
			if (subs.isEmpty()) {
				subs = dict.getSubscript(word.substring(0,1).toUpperCase() + word.substring(1));
			}
			if (subs.size() == 1 && (subs.contains("m") || subs.contains("l") || subs.contains("f"))) {
				word = word.substring(0,1).toUpperCase() + word.substring(1);
			}
			if (word.equals(",")) ret = ret.substring(0, ret.length() - 1);
			ret += word + " ";
		}
		ret = ret.substring(0,1).toUpperCase() + ret.substring(1, ret.length() - 1) + ".";
		return ret;
	}
	
	private static String display(String[] words) {
		String ret = "";
		for (String word : words) {
			ret += word + " ";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}

	private static boolean check(String first, String second) {
		first = first.toLowerCase();
		if (first.equals("of") || second.equals(",")) return false;
		second = second.toLowerCase();
		List<String> subs;
		try {
			subs = dict.getSubscript(first);
		} catch (Exception e) {
			subs = dict.getSubscript(first.substring(0,1).toUpperCase() + first.substring(1));
		}
		if (subs.size() == 1 && subs.contains("a")) return false;
		if (subs.contains("r") && !(subs.size() == 2 && subs.contains("#their"))) return false;
		if ((subs.contains("v") || subs.contains("v-d")) && !subs.contains("n") && !subs.contains("n-u")) {
			return false;
		}
		return true;
	}

	private static boolean isValid(String[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = input[i].toLowerCase().trim();
		}
		outer: for (int i = 0; i < input.length - 1; i++) {
			String left = input[i];
			String right = input[i + 1];
			if (left.toLowerCase().trim().equals(right.toLowerCase().trim()))
				return false;
			left = left.toLowerCase();
			right = right.toLowerCase();
			if (dict.getRule(left).isEmpty()) left = left.substring(0,1).toUpperCase() + left.substring(1);
			if (dict.getRule(right).isEmpty()) right = right.substring(0,1).toUpperCase() + right.substring(1);
			if ((left.equals("in") || left.equals("by") || left.equals("of")) 
					&& (input[input.length - 1].equals("the") || dict.getSubscript(input[input.length - 1]).contains("a"))) return false;
			if (left.equals("in") || left.equals("by")) {
				if (connects(left, input[input.length - 1])) continue outer;
			} else if (left.equals("on") && right.equals("the") && i + 2 < input.length) {
				i++;
				if (connects(left, input[i + 1]) && connects(right, input[i + 1]))
					continue outer;
			} else if (left.equals("the")) {
				int fin = 0;
				for (int idx = i+1; idx < input.length; idx++) {
					if (dict.getSubscript(input[idx]).contains("n") || dict.getSubscript(input[idx]).contains("n-u")) {
						fin = idx;
						break;
					}
				}
				if (fin == 0) {
					if (connects(left, right)) continue outer;
				} else {
					boolean v = true;
					for (int idx = i; idx < fin; idx++) {
						if (!connects(input[idx], input[fin])) {
							v = false;
						}
					}
					i = fin;
					if (v) continue outer;
				}
			} else if (right.equals("a") || right.equals("the") && !left.equals("has") && i + 2 < input.length) {
				if (dict.getSubscript(input[i+2]).contains("a") && i+3>=input.length) return false;
				i++;
				if (input[i+1].toLowerCase().equals("cordelia") || input[i+1].toLowerCase().equals("smile")) {
					if (connects(left, input[i+1]) && connects(right, input[i+1])) continue outer;
				} else {
					if (connects(left, right, input[i + 1])) {
						continue outer;
					}
				}
			} else if (right.equals("before") && i - 2 >= 0) {
				if (connects(input[i - 2], right))
					continue outer;
			} else {
				if (connects(left, right)) {
					continue outer;
				}
				if (right.equals("with")) {
					int idx;
					for (idx = 0; idx < input.length; idx++) {
						if (input[idx].equals("on")) {
							break;
						}
					}
					if (idx > i) {
						if (connectsLeft(left, right, "on"))
							continue outer;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	private static boolean connects(String left, String right) {
		if (!hyphenated.getRule("each_other").isEmpty()) return true;
		if (!checkLR(left, right)) return false;
		ArrayList<Rule> leftList = dict.getRule(left), rightList = dict.getRule(right);
		if (leftList.size() == 0) {
			leftList = dict.getRule(left.substring(0,1).toUpperCase() + left.substring(1));
		}
		if (rightList.size() == 0) {
			rightList = dict.getRule(right.substring(0,1).toUpperCase() + right.substring(1));
		}
		if (leftList.size() == 0) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		if (rightList.size() == 0) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
		for (Rule leftRule : leftList) {
			for (Rule rightRule : rightList) {
				String lr = leftRule.toString();
				String rr = rightRule.toString();
				lr = beforeNull(lr);
				rr = beforeNull(rr);
				lr = replaceNull(lr);
				rr = replaceNull(rr);
				
				ArrayList<String> Lops = new ArrayList<>(), Rops = new ArrayList<>(), Lcosts = new ArrayList<>(),
						Rcosts = new ArrayList<>();
				while (lr.contains("{")) {
					int start = lr.indexOf("{");
					int end = 0;
					int numC = 1, num = 0;
					for (int i = start + 1; i < lr.length(); i++) {
						if (lr.charAt(i) == '{')
							numC++;
						else if (lr.charAt(i) == '}')
							num++;
						if (numC == num) {
							end = i + 1;
							break;
						}
					}
					try {
						Lops.add(lr.substring(start, end));
						lr = lr.substring(0, start) + lr.substring(end);
					} catch (Exception e) {
						Lops.add(lr.substring(start+1, lr.length()));
						lr = lr.substring(0, start);
					}
				}
				lr = fixString(lr);

				while (rr.contains("{")) {
					int start = rr.indexOf("{");
					int end = 0;
					int numC = 1, num = 0;
					for (int i = start + 1; i < rr.length(); i++) {
						if (rr.charAt(i) == '{')
							numC++;
						else if (rr.charAt(i) == '}')
							num++;
						if (numC == num) {
							end = i + 1;
							break;
						}
					}
					try {
						Rops.add(rr.substring(start, end));
						rr = rr.substring(0, start) + rr.substring(end);
					} catch (Exception e) {
						Rops.add(rr.substring(start+1, rr.length()));
						rr = rr.substring(0, start);
					}
				}
				rr = fixString(rr);
				
				ArrayList<String> toAddLops = new ArrayList<>();
				ArrayList<String> toAddRops = new ArrayList<>();
				
				int id = 0;
				for (String str : Rops) {
					str = str.substring(1, str.length() - 1);
					while (str.contains("{")) {
						int start = str.indexOf("{");
						int end = 0;
						int numC = 1, num = 0;
						for (int i = start + 1; i < str.length(); i++) {
							if (str.charAt(i) == '{')
								numC++;
							else if (str.charAt(i) == '}')
								num++;
							if (numC == num) {
								end = i + 1;
								break;
							}
						}
						try {
							toAddRops.add(str.substring(start, end));
							str = str.substring(0, start) + str.substring(end);
						} catch (Exception e) {
							toAddRops.add(str.substring(start+1, str.length()));
							str = str.substring(0, start);
						}
					}
					str = fixString(str);
					Rops.set(id, str);
					id++;
				}
				
				id = 0;
				for (String str : Lops) {
					str = str.substring(1, str.length() - 1);
					while (str.contains("{")) {
						int start = str.indexOf("{");
						int end = 0;
						int numC = 1, num = 0;
						for (int i = start + 1; i < str.length(); i++) {
							if (str.charAt(i) == '{')
								numC++;
							else if (str.charAt(i) == '}')
								num++;
							if (numC == num) {
								end = i + 1;
								break;
							}
						}
						try {
							toAddLops.add(str.substring(start, end));
							str = str.substring(0, start) + str.substring(end);
						} catch (Exception e) {
							toAddLops.add(str.substring(start+1, str.length()));
							str = str.substring(0, start);
						}
					}
					str = fixString(str);
					Lops.set(id, str);
					id++;
				}
				
				Lops.addAll(toAddLops);
				Rops.addAll(toAddRops);
				
				for (String l : lr.split(" or ")) {
					int ri = -1;
					rloop: for (String r : rr.split(" or ")) {
						ri++;
						int numC = 0, num = 0;
						for (int q = ri; q < rr.split(" or ").length; q++) {
							boolean a = false;
							for (char c : rr.split(" or ")[q].toCharArray()) {
								if (c == '(')
									numC++;
								else if (c == ')')
									num++;
								else if (c == '&')
									a = true;
							}
							if (a)
								continue rloop;
							if (num > numC)
								break;
						}
						l = format(l);
						r = format(r);
						String fl = "";
						for (String p : l.split(" & ")) {
							if (!p.contains("-")) {
								fl += p + " & ";
							}
						}
						if (fl.endsWith(" & "))
							fl = fl.substring(0, fl.length() - 3);
						String fr = "";
						for (String p : r.split(" & ")) {
							if (p.contains("-")) {
								fr += p + " & ";
							}
						}
						if (fr.endsWith(" & "))
							fr = fr.substring(0, fr.length() - 3);
						fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
						if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
							return true;
						}
					}
				}
				for (String lb : Lops) {
					for (String rb : Rops) {
						for (String l : lb.split(" or ")) {
							for (String r : rb.split(" or ")) {
								l = l.split("& \\{")[0];
								r = r.split("& \\{")[0];
								l = format(l);
								r = format(r);
								String fl = "";
								for (String p : l.split(" & ")) {
									if (!p.contains("-")) {
										fl += p + " & ";
									}
								}
								if (fl.endsWith(" & "))
									fl = fl.substring(0, fl.length() - 3);
								String fr = "";
								for (String p : r.split(" & ")) {
									if (r.contains("-")) {
										fr += p + " & ";
									}
								}
								if (fr.endsWith(" & "))
									fr = fr.substring(0, fr.length() - 3);
								fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
								if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
									return true;
								}
							}
						}
					}
				}

				for (String lb : Lops) {
					for (String l : lb.split(" or ")) {
						l = l.split("& \\{")[0];
						while (l.contains("[")) {
							int start = l.indexOf("[");
							int end = 0;
							int numC = 1, num = 0;
							for (int i = start + 1; i < l.length(); i++) {
								if (l.charAt(i) == '[')
									numC++;
								else if (l.charAt(i) == ']')
									num++;
								if (numC == num) {
									end = i + 1;
									break;
								}
							}
							Lcosts.add(l.substring(start, end == 0 ? l.length() : end));
							l = l.substring(0, start) + l.substring(end == 0 ? l.length() : end);
						}
						l = format(l);
						String fl = "";
						for (String p : l.split(" & ")) {
							if (!p.contains("-")) {
								fl += p + " & ";
							}
						}
						if (fl.endsWith(" & "))
							fl = fl.substring(0, fl.length() - 3);
						fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
						for (String r : rr.split(" or ")) {
							r = format(r);
							String fr = "";
							for (String p : r.split(" & ")) {
								if (p.contains("-")) {
									fr += p + " & ";
								}
							}
							if (fr.endsWith(" & ")) fr = fr.substring(0, fr.length() - 3);
							if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
								return true;
							}
						}
					}
				}

				for (String rb : Rops) {
					for (String r : rb.split(" or ")) {
						r = r.split("& \\{")[0];
						while (r.contains("[")) {
							int start = r.indexOf("[");
							int end = 0;
							int numC = 1, num = 0;
							for (int i = start + 1; i < r.length(); i++) {
								if (r.charAt(i) == '[')
									numC++;
								else if (r.charAt(i) == ']')
									num++;
								if (numC == num) {
									end = i + 1;
									break;
								}
							}
							Rcosts.add(r.substring(start, end == 0 ? r.length() : end));
							r = r.substring(0, start) + r.substring(end == 0 ? r.length() : end);
						}
						r = format(r);
						String fr = "";
						for (String p : r.split(" & ")) {
							if (p.contains("-")) {
								fr += p + " & ";
							}
						}
						if (fr.endsWith(" & "))
							fr = fr.substring(0, fr.length() - 3);
						for (String l : lr.split(" or ")) {
							l = format(l);
							String fl = "";
							for (String p : l.split(" & ")) {
								if (!p.contains("-")) {
									fl += p + " & ";
								}
							}
							if (fl.endsWith(" & "))
								fl = fl.substring(0, fl.length() - 3);
							fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
							for (String pfr : fr.split(" & ")) {
								for (String pfl : fl.split(" & ")) {
									if (!pfl.isEmpty() && !pfr.isEmpty() && equals(pfl.trim(), pfr.trim())) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private static boolean checkLR(String left, String right) {
		if (left.toLowerCase().trim().equals(right.toLowerCase().trim()))
			return false;
		if (dict.getSubscript(left.toLowerCase().trim()).contains("n-u")
				&& dict.getSubscript(right.toLowerCase().trim()).contains("m"))
			return false;
		if (dict.getSubscript(right.toLowerCase().trim()).contains("n-u")
				&& dict.getSubscript(left.toLowerCase().trim()).contains("m"))
			return false;
		if (dict.getSubscript(left.toLowerCase().trim()).contains("n-u")
				&& dict.getSubscript(right.toLowerCase().trim()).contains("f"))
			return false;
		if (dict.getSubscript(right.toLowerCase().trim()).contains("n-u")
				&& dict.getSubscript(left.toLowerCase().trim()).contains("f"))
			return false;
		if (left.toLowerCase().equals("a") && dict.getSubscript(right).size() == 1
				&& dict.getSubscript(right).get(0).equals("v"))
			return false;
		return true;
	}
	
	private static Object[] connectsIdx(String left, String right, boolean isLeft) {
		if (!hyphenated.getRule("each_other").isEmpty()) return new Object[] { true, 0 };
		if (!checkLR(left, right)) return new Object[] { false, 0 };
		ArrayList<Rule> leftList = dict.getRule(left), rightList = dict.getRule(right);
		if (leftList.size() == 0) {
			leftList = dict.getRule(left.substring(0,1).toUpperCase() + left.substring(1));
		}
		if (rightList.size() == 0) {
			rightList = dict.getRule(right.substring(0,1).toUpperCase() + right.substring(1));
		}
		if (leftList.size() == 0) {
			System.err.println("Word '" + left + "' not found in dictionary.");
			System.exit(0);
		}
		if (rightList.size() == 0) {
			System.err.println("Word '" + right + "' not found in dictionary.");
			System.exit(0);
		}
		for (Rule leftRule : leftList) {
			for (Rule rightRule : rightList) {
				String lr = leftRule.toString();
				String rr = rightRule.toString();
				lr = beforeNull(lr);
				rr = beforeNull(rr);
				lr = replaceNull(lr);
				rr = replaceNull(rr);
				
				ArrayList<String> Lops = new ArrayList<>(), Rops = new ArrayList<>(), Lcosts = new ArrayList<>(),
						Rcosts = new ArrayList<>();
				while (lr.contains("{")) {
					int start = lr.indexOf("{");
					int end = 0;
					int numC = 1, num = 0;
					for (int i = start + 1; i < lr.length(); i++) {
						if (lr.charAt(i) == '{')
							numC++;
						else if (lr.charAt(i) == '}')
							num++;
						if (numC == num) {
							end = i + 1;
							break;
						}
					}
					try {
						Lops.add(lr.substring(start, end));
						lr = lr.substring(0, start) + lr.substring(end);
					} catch (Exception e) {
						Lops.add(lr.substring(start+1, lr.length()));
						lr = lr.substring(0, start);
					}
				}
				lr = fixString(lr);

				while (rr.contains("{")) {
					int start = rr.indexOf("{");
					int end = 0;
					int numC = 1, num = 0;
					for (int i = start + 1; i < rr.length(); i++) {
						if (rr.charAt(i) == '{')
							numC++;
						else if (rr.charAt(i) == '}')
							num++;
						if (numC == num) {
							end = i + 1;
							break;
						}
					}
					try {
						Rops.add(rr.substring(start, end));
						rr = rr.substring(0, start) + rr.substring(end);
					} catch (Exception e) {
						Rops.add(rr.substring(start+1, rr.length()));
						rr = rr.substring(0, start);
					}
				}
				rr = fixString(rr);
				
				ArrayList<String> toAddLops = new ArrayList<>();
				ArrayList<String> toAddRops = new ArrayList<>();
				
				int id = 0;
				for (String str : Rops) {
					str = str.substring(1, str.length() - 1);
					while (str.contains("{")) {
						int start = str.indexOf("{");
						int end = 0;
						int numC = 1, num = 0;
						for (int i = start + 1; i < str.length(); i++) {
							if (str.charAt(i) == '{')
								numC++;
							else if (str.charAt(i) == '}')
								num++;
							if (numC == num) {
								end = i + 1;
								break;
							}
						}
						try {
							toAddRops.add(str.substring(start, end));
							str = str.substring(0, start) + str.substring(end);
						} catch (Exception e) {
							toAddRops.add(str.substring(start+1, str.length()));
							str = str.substring(0, start);
						}
					}
					str = fixString(str);
					Rops.set(id, str);
					id++;
				}
				
				id = 0;
				for (String str : Lops) {
					str = str.substring(1, str.length() - 1);
					while (str.contains("{")) {
						int start = str.indexOf("{");
						int end = 0;
						int numC = 1, num = 0;
						for (int i = start + 1; i < str.length(); i++) {
							if (str.charAt(i) == '{')
								numC++;
							else if (str.charAt(i) == '}')
								num++;
							if (numC == num) {
								end = i + 1;
								break;
							}
						}
						try {
							toAddLops.add(str.substring(start, end));
							str = str.substring(0, start) + str.substring(end);
						} catch (Exception e) {
							toAddLops.add(str.substring(start+1, str.length()));
							str = str.substring(0, start);
						}
					}
					str = fixString(str);
					Lops.set(id, str);
					id++;
				}
				
				Lops.addAll(toAddLops);
				Rops.addAll(toAddRops);
				int lk = 0, rk = 0;
				for (String l : lr.split(" or ")) {
					rk = 0;
					lk++;
					int ri = -1;
					rloop: for (String r : rr.split(" or ")) {
						ri++;
						rk++;
						int numC = 0, num = 0;
						for (int q = ri; q < rr.split(" or ").length; q++) {
							boolean a = false;
							for (char c : rr.split(" or ")[q].toCharArray()) {
								if (c == '(')
									numC++;
								else if (c == ')')
									num++;
								else if (c == '&')
									a = true;
							}
							if (a)
								continue rloop;
							if (num > numC)
								break;
						}
						l = format(l);
						r = format(r);
						String fl = "";
						for (String p : l.split(" & ")) {
							if (!p.contains("-")) {
								fl += p + " & ";
							}
						}
						if (fl.endsWith(" & "))
							fl = fl.substring(0, fl.length() - 3);
						String fr = "";
						for (String p : r.split(" & ")) {
							if (p.contains("-")) {
								fr += p + " & ";
							}
						}
						if (fr.endsWith(" & "))
							fr = fr.substring(0, fr.length() - 3);
						fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
						if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
							return new Object[] { true, isLeft ? lk : rk };
						}
					}
				}
				lk = 0;
				rk = 0;
				for (String lb : Lops) {
					rk = 0;
					lk++;
					for (String rb : Rops) {
						rk++;
						for (String l : lb.split(" or ")) {
							for (String r : rb.split(" or ")) {
								l = l.split("& \\{")[0];
								r = r.split("& \\{")[0];
								l = format(l);
								r = format(r);
								String fl = "";
								for (String p : l.split(" & ")) {
									if (!p.contains("-")) {
										fl += p + " & ";
									}
								}
								if (fl.endsWith(" & "))
									fl = fl.substring(0, fl.length() - 3);
								String fr = "";
								for (String p : r.split(" & ")) {
									if (r.contains("-")) {
										fr += p + " & ";
									}
								}
								if (fr.endsWith(" & "))
									fr = fr.substring(0, fr.length() - 3);
								fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
								if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
									return new Object[] { true, isLeft ? lk : rk };
								}
							}
						}
					}
				}
				
				lk = 0;
				rk = 0;
				for (String lb : Lops) {
					rk = 0;
					lk++;
					for (String l : lb.split(" or ")) {
						l = l.split("& \\{")[0];
						while (l.contains("[")) {
							int start = l.indexOf("[");
							int end = 0;
							int numC = 1, num = 0;
							for (int i = start + 1; i < l.length(); i++) {
								if (l.charAt(i) == '[')
									numC++;
								else if (l.charAt(i) == ']')
									num++;
								if (numC == num) {
									end = i + 1;
									break;
								}
							}
							Lcosts.add(l.substring(start, end == 0 ? l.length() : end));
							l = l.substring(0, start) + l.substring(end == 0 ? l.length() : end);
						}
						l = format(l);
						String fl = "";
						for (String p : l.split(" & ")) {
							if (!p.contains("-")) {
								fl += p + " & ";
							}
						}
						if (fl.endsWith(" & "))
							fl = fl.substring(0, fl.length() - 3);
						fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
						for (String r : rr.split(" or ")) {
							rk++;
							r = format(r);
							String fr = "";
							for (String p : r.split(" & ")) {
								if (p.contains("-")) {
									fr += p + " & ";
								}
							}
							if (fr.endsWith(" & ")) fr = fr.substring(0, fr.length() - 3);
							if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
								return new Object[] { true, isLeft ? lk : rk };
							}
						}
					}
				}

				lk = 0;
				rk = 0;
				for (String rb : Rops) {
					lk = 0;
					rk++;
					for (String r : rb.split(" or ")) {
						r = r.split("& \\{")[0];
						while (r.contains("[")) {
							int start = r.indexOf("[");
							int end = 0;
							int numC = 1, num = 0;
							for (int i = start + 1; i < r.length(); i++) {
								if (r.charAt(i) == '[')
									numC++;
								else if (r.charAt(i) == ']')
									num++;
								if (numC == num) {
									end = i + 1;
									break;
								}
							}
							Rcosts.add(r.substring(start, end == 0 ? r.length() : end));
							r = r.substring(0, start) + r.substring(end == 0 ? r.length() : end);
						}
						r = format(r);
						String fr = "";
						for (String p : r.split(" & ")) {
							if (p.contains("-")) {
								fr += p + " & ";
							}
						}
						if (fr.endsWith(" & "))
							fr = fr.substring(0, fr.length() - 3);
						for (String l : lr.split(" or ")) {
							lk++;
							l = format(l);
							String fl = "";
							for (String p : l.split(" & ")) {
								if (!p.contains("-")) {
									fl += p + " & ";
								}
							}
							if (fl.endsWith(" & "))
								fl = fl.substring(0, fl.length() - 3);
							fl = fl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
							for (String pfr : fr.split(" & ")) {
								for (String pfl : fl.split(" & ")) {
									if (!pfl.isEmpty() && !pfr.isEmpty() && equals(pfl.trim(), pfr.trim())) {
										return new Object[] { true, isLeft ? lk : rk };
									}
								}
							}
						}
					}
				}
			}
		}
		return new Object[] {false, 0};
	}

	private static String replaceNull(String lr) {
		while (lr.contains("()")) {
			int idx = lr.indexOf("()");
			int end = lr.indexOf(" ", idx) != -1 ? lr.indexOf(" ", idx) : lr.length() - 1;
			if (lr.charAt(end - 1) == '}')
				return lr;
			if (lr.charAt(end - 1) == ')') {
				for (int i = end - 2; i >= 0; i--) {
					if (lr.charAt(i) != ')') {
						end = i + 2;
						break;
					}
				}
				int num = 1;
				int numC = 0;
				int finish = 0;
				for (int i = end - 2; i >= 0; i--) {
					if (lr.charAt(i) == ')')
						num++;
					else if (lr.charAt(i) == '(')
						numC++;
					if (numC == num) {
						finish = i;
						break;
					}
				}
				String total = lr.substring(finish, end);
				lr = lr.replace(total, "{" + total.substring(1, total.lastIndexOf("or")) + "}");
			} else {
				int space1 = 0;
				for (int i = end - 1; i >= 0; i--) {
					if (lr.charAt(i) == ' ') {
						space1 = i;
						break;
					}
				}
				if (lr.charAt(space1 - 4) == ')' || lr.charAt(space1 - 4) == ']') {
					char c = lr.charAt(space1 - 4);
					char oc;
					if (c == ')')
						oc = '(';
					else
						oc = '[';
					int finish = 0;
					int num = 1;
					int numC = 0;
					for (int i = space1 - 5; i >= 0; i--) {
						if (lr.charAt(i) == c)
							num++;
						else if (lr.charAt(i) == oc)
							numC++;
						if (numC == num) {
							finish = i;
							break;
						}
					}
					lr = lr.replace(lr.substring(finish, end), "{" + lr.substring(finish + 1, space1 - 4) + "}");
				} else if (lr.charAt(space1 - 5) == ')' || lr.charAt(space1 - 5) == ']') {
					char c = lr.charAt(space1 - 5);
					char oc;
					if (c == ')')
						oc = '(';
					else
						oc = '[';
					int finish = 0;
					int num = 1;
					int numC = 0;
					for (int i = space1 - 6; i >= 0; i--) {
						if (lr.charAt(i) == c)
							num++;
						else if (lr.charAt(i) == oc)
							numC++;
						if (numC == num) {
							finish = i;
							break;
						}
					}
					lr = lr.replace(lr.substring(finish, end), "{" + lr.substring(finish + 1, space1 - 4) + "}");
				} else {
					int space2 = 0;
					for (int i = space1 - 5; i >= 0; i--) {
						if (lr.charAt(i) == ' ') {
							space2 = i;
							break;
						}
					}
					if (lr.charAt(space2 + 1) == '(')
						space2++;
					int fin = lr.indexOf(' ', space2 + 1);
					lr = lr.replace(lr.substring(space2 + 1, end), "{" + lr.substring(space2 + 1, fin) + "}");
				}
			}
		}
		return lr;
	}

	private static String beforeNull(String lr) {
		lr = fix(lr, "([()] & ", "(");
		lr = fix(lr, "  or ", " or ");
		lr = fix(lr, "or ()", "or [()]");
		return lr;
	}

	private static String fixString(String lr) {
		if (lr.startsWith(" &")) lr = lr.substring(2).trim();
		if (lr.startsWith(" or")) lr = lr.substring(3).trim();
		lr = fix(lr, "[ & ]", "");
		lr = fix(lr, "( & )", "");
		lr = fix(lr, "[ or ]", "");
		lr = fix(lr, "( or )", "");
		lr = fix(lr, "( or ", "(");
		lr = fix(lr, "( & ", "(");
		lr = fix(lr, " or )", ")");
		lr = fix(lr, " & )", ")");
		lr = fix(lr, "[ or ", "[");
		lr = fix(lr, "[ & ", "[");
		lr = fix(lr, " or ]", "]");
		lr = fix(lr, " & ]", "]");
		lr = fix(lr, "&  &", "&");
		lr = fix(lr, "or  or", "or");
		lr = fix(lr, "or  &", "or");
		return lr;
	}

	private static String fix(String lr, String reg, String rep) {
		while (lr.contains(reg))
			lr = lr.replace(reg, rep);
		return lr;
	}

	private static String format(String l) {
		return l.replace("(", "").replace(")", "").replace("[", "").replace("]", "").replace("}", "").replace("{", "")
				.trim();
	}

	private static boolean connects(String left, String mid, String right) {
		if (left.toLowerCase().equals(mid.toLowerCase()) || left.toLowerCase().equals(right.toLowerCase())
				|| mid.toLowerCase().equals(right.toLowerCase()))
			return false;
		Object[] leftRight = connectsIdx(left, right, false);
		Object[] midRight = connectsIdx(mid, right, false);
		List<String> subs = dict.getSubscript(right);
		if (subs.isEmpty()) {
			subs = dict.getSubscript(right.substring(0,1).toUpperCase() + right.substring(1));
		}
		return (boolean) leftRight[0] && (boolean) midRight[0] 
				&& (subs.contains("p")? true : (int) leftRight[1] <= (int) midRight[1]);
	}

	private static boolean connectsLeft(String left, String mid, String right) {
		if (left.toLowerCase().equals(mid.toLowerCase()) || left.toLowerCase().equals(right.toLowerCase())
				|| mid.toLowerCase().equals(right.toLowerCase()))
			return false;
		Object[] leftMid = connectsIdx(left, mid, true);
		Object[] leftRight = connectsIdx(left, right, true);
		return (boolean) leftMid[0] && (boolean) leftRight[0] && (int) leftMid[1] < (int) leftRight[1];
	}

	private static boolean equals(String wlu, String wr) {
		wlu = wlu.replace("@", "");
		wr = wr.replace("@", "");
		if (wlu.equals(wr)) {
			return true;
		}
		if (wlu.contains("*")) {
			int idx = wlu.indexOf("*");
			int lid = wlu.lastIndexOf("*");
			if (wr.length() == wlu.length()) {
				if (wlu.substring(0, idx).equals(wr.substring(0, idx))
						&& wlu.substring(lid + 1).equals(wr.substring(lid + 1))) {
					return true;
				}
			} else {
				if (idx + 1 > wr.length() && wlu.substring(0, idx).equals(wr.substring(0, Math.min(wr.length(), idx))))
					return true;
			}
		}
		if (wr.contains("*")) {
			int idx = wr.indexOf("*");
			int lid = wr.lastIndexOf("*");
			if (wr.length() == wlu.length()) {
				if (wlu.substring(0, idx).equals(wr.substring(0, idx))
						&& wlu.substring(lid + 1).equals(wr.substring(lid + 1))) {
					return true;
				}
			} else {
				if (idx + 1 > wlu.length()
						&& wlu.substring(0, Math.min(wlu.length(), idx)).equals(wr.substring(0, idx))) {
					return true;
				}
			}
		}
		wr = wr.replace("-", "");
		wlu = wlu.replace("-", "");
		if (wlu.contains("&") || wr.contains("&"))
			return false;
		if (wlu.length() < wr.length()) {
			if (wlu.equals(wr.substring(0, wlu.length())))
				return true;
		}
		if (wr.length() < wlu.length()) {
			if (wr.equals(wlu.substring(0, wr.length())))
				return true;
			try {
				if (wr.equals(wlu.substring(1, wr.length()+1)))
					return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static List<String> processSentences(String path) throws IOException {
		try {
			Path p;
			if (System.getProperty("user.dir").endsWith("src")) {
				p = Paths.get(Paths.get("test/java/org/aigents/nlp/gen/" + path).toAbsolutePath().toString());
			} else {
				p = Paths.get(Paths.get("src/test/java/org/aigents/nlp/gen/" + path).toAbsolutePath().toString());
			}
			File f = p.toFile();
			List<String> sentences = Files.readAllLines(f.toPath());
			Iterator<String> it = sentences.iterator();
			while (it.hasNext()) {
				String str = it.next();
				if (str.isEmpty() || str.contains("[") || str.contains("]") || str.indexOf(".") != str.length() - 1 
						|| str.contains("(") || str.contains(")") || str.contains("{") || str.contains("}")
						|| str.contains("\"") || str.contains("'"))
					it.remove();
			}
			List<String> words = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				String sentence = sentences.get(i);
				String[] w = sentence.split(" ");
				w[w.length - 1] = w[w.length - 1].substring(0, w[w.length - 1].length() - 1);
				for (String word: w) {
					words.add(word);
				}
			}
			return words;
		} catch (Exception e) {
			return null;
		}
	}

	private static boolean contains(String[] input, String str) {
		for (String s : input) {
			if (s.equals(str))
				return true;
		}
		return false;
	}

	private static int idx(String[] input, String str) {
		int i = -1;
		for (String s : input) {
			i++;
			if (s.equals(str))
				return i;
		}
		return -1;
	}
}
