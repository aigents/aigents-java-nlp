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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import main.java.org.aigents.nlp.lg.Dictionary;
import main.java.org.aigents.nlp.lg.Loader;
import main.java.org.aigents.nlp.lg.Rule;

public class Segment {
	public static Dictionary dict, hyphenated;

	public static void main(String[] args) throws IOException {
		args = new String[] {"en/4.0.dict", "I", "am", "glad"};
		if (args.length > 1) {
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
				if (i != words.length - 2 && (i+1>=words.length? true : check(words[i+1])) && check(arr) && isValid(arr)) {
					ret.add(makeSentence(arr));
					idx = i+1;
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
	
	private static boolean check(String[] input) {
		if (input.length <= 1) return false;
		String first = input[0].toLowerCase().trim();
		String last = input[input.length - 1].toLowerCase().trim();
		if (contains(input, "in") && last.equals("wise")) return false;
		if (last.equals("of") || first.equals("of"))
			return false;
		if (last.equals("on") || first.equals("on"))
			return false;
		if (last.equals("with") || first.equals("with"))
			return false;
		if (first.equals("board"))
			return false;
		if (first.equals("in") || last.equals("in")) return false;
		if (first.equals("writes") || first.equals("wants") || first.equals("has") || first.equals("sees")
				|| first.equals("sawed") || first.equals("knocked") || first.startsWith("like") || first.equals("is")
				|| first.equals("was")) {
			return false;
		}
		return true;
	}
	
	private static String display(String[] words) {
		String ret = "";
		for (String word : words) {
			ret += word + " ";
		}
		ret = ret.substring(0, ret.length() - 1);
		return ret;
	}

	private static boolean check(String first) {
		if (first.equals("of")) return false;
		if (first.equals("in")) return false;
		if (first.equals("on")) return false;
		if (first.equals("with")) return false;
		if (first.equals("board")) return false;
		if (first.equals("writes") || first.equals("wants") || first.equals("has") || first.equals("sees")
				|| first.equals("sawed") || first.equals("knocked") || first.startsWith("like") || first.equals("is")
				|| first.equals("was")) {
			return false;
		}
		return true;
	}

	private static boolean isValid(String[] input) {
		if (idx(input, "A") != -1 && idx(input, "A") != 0)
			return false;
		if ((dict.getSubscript(input[0]).contains("v") || dict.getSubscript(input[0]).contains("v-d"))
				&& !(dict.getSubscript(input[1]).contains("a") || dict.getSubscript(input[0]).contains("n") || dict.getSubscript(input[0]).contains("n-u")
						|| dict.getSubscript(input[0]).contains("p")))
			return false;
		for (int i = 0; i < input.length; i++) {
			if (!input[i].equals("A")) {
				input[i] = input[i].toLowerCase().trim();
			}
		}
		String last = input[input.length - 1].toLowerCase().trim();
		if (last.equals("a") || (dict.getSubscript(last).size() > 0 && (!dict.getSubscript(last).contains("n")
				&& !dict.getSubscript(last).contains("r") 
				&& !dict.getSubscript(last).contains("a") 
				&& !dict.getSubscript(last).contains("n-u"))))
			return false;
		outer: for (int i = 0; i < input.length - 1; i++) {
			String left = input[i];
			String right = input[i + 1];
			if (left.toLowerCase().trim().equals(right.toLowerCase().trim()))
				return false;
			if ((dict.getRule(left.toLowerCase()).get(0).toString().equals(dict.getRule("sawed").get(0).toString())
					|| (dict.getRule(left.toLowerCase()).get(0).toString()
							.equals(dict.getRule("writes").get(0).toString()) && contains(input, "to"))
					|| left.equals("saw")) && (idx(input, "with") == i + 2 || idx(input, "with") == i + 3)) {
				int idx = idx(input, "with");
				if (idx == i + 2) {
					i = idx - 1;
					if (connectsLeft(left, right, input[i + 1]))
						continue outer;
				} else {
					i = idx - 1;
					if (right.equals("to")) {
						if (connectsLeft(left, right, input[i + 1]) && connects(right, input[i]))
							continue outer;
					} else if (right.equals("the")) {
						if (connects(left, input[i]) && connects(right, input[i]) && connects(left, input[i + 1]))
							continue outer;
					}
				}
			} else if (left.equals("in")) {
				if (connects(left, input[input.length - 1])) continue outer;
			} else if (left.equals("on") && right.equals("the") && i + 2 < input.length) {
				i++;
				if (connects(left, input[i + 1]) && connects(right, input[i + 1]))
					continue outer;
			} else if ((right.equals("a") || right.equals("the")) && !left.equals("has") && i + 2 < input.length) {
				i++;
				if (connects(left, right, input[i + 1])) {
					continue outer;
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
		if (!checkLR(left, right))
			return false;
		ArrayList<Rule> leftList = dict.getRule(left), rightList = dict.getRule(right);
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
					Lops.add(lr.substring(start, end));
					lr = lr.substring(0, start) + lr.substring(end);
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
					Rops.add(rr.substring(start, end));
					rr = rr.substring(0, start) + rr.substring(end);
				}
				rr = fixString(rr);

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
							if (fr.endsWith(" & "))
								fr = fr.substring(0, fr.length() - 3);
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
							if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
								return true;
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
		if (left.toLowerCase().equals("a") && right.equals("is"))
			return false;
		if (left.toLowerCase().equals("directors") && right.toLowerCase().equals("on"))
			return false;
		if (left.toLowerCase().trim().equals("of") && right.toLowerCase().trim().equals("is"))
			return false;
		if (left.toLowerCase().trim().equals("is") && right.toLowerCase().trim().equals("of"))
			return false;
		if (left.toLowerCase().equals("a") || right.toLowerCase().equals("a"))
			return true;
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
		if (!checkLR(left, right))
			return new Object[] { false, 0 };
		ArrayList<Rule> leftList = dict.getRule(left), rightList = dict.getRule(right);
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
					Lops.add(lr.substring(start, end));
					lr = lr.substring(0, start) + lr.substring(end);
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
					Rops.add(rr.substring(start, end));
					rr = rr.substring(0, start) + rr.substring(end);
				}
				rr = fixString(rr);

				int li = 0, ri = 0;
				for (String l : lr.split(" or ")) {
					ri = 0;
					li++;
					for (String r : rr.split(" or ")) {
						ri++;
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
							return new Object[] { true, isLeft ? li : ri };
						}
					}
				}
				li = 0;
				ri = 0;
				for (String lb : Lops) {
					ri = 0;
					li++;
					for (String rb : Rops) {
						ri++;
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
								if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim()))
									return new Object[] { true, isLeft ? li : ri };
							}
						}
					}
				}
				li = 0;
				ri = 0;
				for (String lb : Lops) {
					ri = 0;
					li++;
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
							ri++;
							r = format(r);
							String fr = "";
							for (String p : r.split(" & ")) {
								if (p.contains("-")) {
									fr += p + " & ";
								}
							}
							if (fr.endsWith(" & "))
								fr = fr.substring(0, fr.length() - 3);
							if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim()))
								return new Object[] { true, isLeft ? li : ri };
						}
					}
				}
				li = 0;
				ri = 0;
				for (String rb : Rops) {
					li = 0;
					ri++;
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
							li++;
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
							if (!fl.isEmpty() && !fr.isEmpty() && equals(fl.trim(), fr.trim())) {
								return new Object[] { true, isLeft ? li : ri };
							}
						}
					}
				}
			}
		}
		return new Object[] { false, 0 };
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
		if (mid.toLowerCase().equals("a") && right.equals("is"))
			return false;
		Object[] leftRight = connectsIdx(left, right, false);
		Object[] midRight = connectsIdx(mid, right, false);
		return (boolean) leftRight[0] && (boolean) midRight[0] && (int) leftRight[1] < (int) midRight[1];
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
		}
		return false;
	}

	public static List<String[]> processSentences(String path) throws IOException {
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
			Path p;
			if (path.contains("/4.0.dict")) {
				if (System.getProperty("user.dir").endsWith("src")) {
					p = Paths.get(Paths.get("../data/" + path).toAbsolutePath().toString());
				} else {
					p = Paths.get(Paths.get("data/" + path).toAbsolutePath().toString());
				}
			} else {
				if (System.getProperty("user.dir").endsWith("src")) {
					p = Paths.get(Paths.get("test/java/org/aigents/nlp/gen/" + path).toAbsolutePath().toString());
				} else {
					p = Paths.get(Paths.get("src/test/java/org/aigents/nlp/gen/" + path).toAbsolutePath().toString());
				}
			}
			File f = p.toFile();
			List<String> sentences = Files.readAllLines(f.toPath());
			Iterator<String> it = sentences.iterator();
			while (it.hasNext()) {
				if (it.next().isEmpty())
					it.remove();
			}
			return sentences;
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

	private static String makeSentence(String[] arr) {
		String ret = "";
		for (int i = 0; i < arr.length - 1; i++) {
			if (arr[i].equals(",")) {
				ret = ret.substring(0, ret.length() - 1);
			}
			ret += arr[i] + " ";
		}
		ret += arr[arr.length - 1] + ".";
		ret = ret.toLowerCase();
		ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
		return ret;
	}
}
