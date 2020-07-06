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

package org.aigents.nlp.lg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aigents.nlp.gen.Generator;

public class Loader {
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			Dictionary dict = grammarBuildLinks(args[0], false);
			for (Word w : dict.getWords()) {
				if (w.getWord().equals(args[1])) {
					System.out.print(w.getWord() + ": ");
					Rule rule = w.getRule();
					assert rule != null && rule.getWords().size() > 0: " No valid rules";
					System.out.println(rule);
					ArrayList<Disjunct> disjuncts = rule.getDisjuncts();
					assert disjuncts != null && disjuncts.size() > 0 : "No valid disjunct";
					System.out.print("Disjuncts: ");
					for (int i = 0; i < disjuncts.size() - 1; i++) {
						System.out.print(disjuncts.get(i) + "; ");
					}
					System.out.println(disjuncts.get(disjuncts.size() - 1));
				}
			}
		} else {
			System.out.println("No command line parameters given.");
		}
	}
	
	public static Dictionary[] buildLGDict(String path) throws IOException {
		URL url = new Loader().getClass().getResource(path);
		File f = new File(url.getPath());
		if (!f.exists()) return null;
		List<String> list = Files.readAllLines(f.toPath());
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if ((str.contains("%") && !str.contains("\"%\"")) || str.contains("<dictionary-version-number>") 
					|| str.contains("<dictionary-locale>")) {
				it.remove();
			}
		}
		String[] lines = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			lines[i] = list.get(i).trim();
		}
		return makeLGDict(lines);
	}
	
	private static Dictionary[] makeLGDict(String[] lines) throws IOException {
		HashMap<String, String> macros = new HashMap<>();
		Dictionary dict = new Dictionary();
		Dictionary hyphenated = new Dictionary();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() == 0) continue;
			if (line.charAt(0) == '<') { // process macros
				String str = "";
				while (!lines[i].contains(";")) {
					str += lines[i] + " ";
					i++;
				}
				str += " " + lines[i].substring(0, lines[i].length()-1);
				if (str.trim().length() == 0) continue;
				str = str.replaceAll("\\d","");
				String[] parts = str.split(":");
				for (int k = 0; k < parts.length; k++) parts[k] = parts[k].trim();
				String rule = parts[1];
				while (rule.contains("<")) {
					String macro = rule.substring(rule.indexOf("<"), rule.indexOf(">")+1);
					rule = rule.replace(macro, macros.get(macro));
				}
				for (String m : parts[0].split(" ")) {
					macros.put(m, rule);
				}
			} else if (line.contains("/words/")) { // process files
				String str = "";
				while (!lines[i].contains(";")) {
					str += lines[i] + " ";
					i++;
				}
				str += " " + lines[i].substring(0, lines[i].length()-1);
				String[] parts = str.split(":");
				for (int k = 0; k < parts.length; k++) parts[k] = parts[k].trim();
				for (String path : parts[0].split(" ")) {
					URL url = new Loader().getClass().getResource(path.substring(4));
					File f = new File(url.getPath());
					if (!f.exists()) return null;
					List<String> list = Files.readAllLines(f.toPath());
					for (String l : list) {
						for (String word : l.split(" ")) {
							addRule(dict, hyphenated, macros, word, parts[1]);
						}
					}
				}
			} else { // process words or lists of words
				String str = "";
				while (!lines[i].contains(";")) {
					str += lines[i] + " ";
					i++;
				}
				str += " " + lines[i].substring(0, lines[i].length()-1);
				str = str.replaceAll("\\d","");
				if (str.contains("\"%\"")) str.replaceAll("\"%\"", "%");
				String[] parts = str.split(":");
				for (int k = 0; k < parts.length; k++) parts[k] = parts[k].trim();
				for (String word : parts[0].split(" ")) {
					addRule(dict, hyphenated, macros, word, parts[1]);
				}
			}
		}
		return new Dictionary[] {dict, hyphenated};
	}
	
	private static void addRule(Dictionary dict, Dictionary hyphenated, HashMap<String, String> macros, String word, String rule) {
		Word w;
		if (word.contains(".")) {
			String[] split = word.split("\\.");
			if (split.length == 1) w = new Word(split[0]);
			else w = new Word(split[0], split[1]);
		} else w = new Word(word);
		while (rule.length() > 0) {
			if (rule.charAt(0) == '<') {
				String macro = rule.substring(0, rule.indexOf(">") + 1);
				rule = (rule.indexOf(" or") == -1)? "" : rule.substring(rule.indexOf(" or") + 4);
				w.addRule(macros.get(macro));
			} else if (rule.charAt(0) == '(') {
				int numC = 1;
				int num = 0;
				int idx = 0;
				for (int i = 1; i < rule.length(); i++) {
					if (rule.charAt(i) == '(') numC++;
					else if (rule.charAt(i) == ')') num++;
					if (numC == num) {
						idx = i; break;
					}
				}
				String r = rule.substring(0, idx+1);
				while (r.contains("<")) {
					String macro = r.substring(r.indexOf("<"), r.indexOf(">")+1);
					r = r.replace(macro, macros.get(macro));
				}
				w.addRule(r);
				rule = idx + 5 >= rule.length()? "" : rule.substring(idx + 5);
			} else if (rule.charAt(0) == '{') {
				int numC = 1;
				int num = 0;
				int idx = 0;
				for (int i = 1; i < rule.length(); i++) {
					if (rule.charAt(i) == '{') numC++;
					else if (rule.charAt(i) == '}') num++;
					if (numC == num) {
						idx = i; break;
					}
				}
				String r = rule.substring(0, idx+1);
				while (r.contains("<")) {
					String macro = r.substring(r.indexOf("<"), r.indexOf(">")+1);
					r = r.replace(macro, macros.get(macro));
				}
				w.addRule(r);
				rule = idx + 5 >= rule.length()? "" : rule.substring(idx + 5);
			} else {
				w.addRule(rule.substring(0, (rule.indexOf(" or") == -1)? rule.length() : rule.indexOf(" or")));
				rule = (rule.indexOf(" or") == -1)? "" : rule.substring(rule.indexOf(" or") + 4);
			}		
		}
		if (word.contains("_")) hyphenated.addWord(w);
		else dict.addWord(w);
	}
	
	public static Dictionary grammarBuildLinks(String path, boolean isGenerator) throws IOException {
		URL url; 
		if (isGenerator) {
			url = new Generator().getClass().getResource(path);
		} else url = new Loader().getClass().getResource(path);
		File f = new File(url.getPath());
		if (!f.exists()) return null;
		List<String> list = Files.readAllLines(f.toPath());
		String[] lines = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			lines[i] = list.get(i);
		}
		return makeDict(lines);
	}
	
	private static Dictionary makeDict(String[] lines) {
		ArrayList<String[]> links = new ArrayList<>();
		if (lines == null || lines.length == 0) return null;
		for (int l = 0; l < lines.length; l++){
			String line = lines[l];
			if (empty(line)) continue;
			if (line.substring(0,2).equals("% ") && line.toUpperCase() == line) {
				String[] split = line.split(" ");
				if (split == null || split.length < 2) continue;
				String code = split[1];
				if (++l >= lines.length || empty(line = lines[l])) break;
				int colon = line.indexOf(":");
				if (colon == -1) break;
				line = line.substring(0, colon);
				String[] words = line.split(" ");
				if (words == null || words.length < 1) break;
				for (int i = 0; i < words.length; i++)
					words[i] = words[i].substring(0, words[i].length() - 1).substring(1);
				if (++l >= lines.length || empty(line = lines[l])) break;
				int semicolon = line.indexOf(";");
				if (semicolon == -1) break;
				line = line.substring(0, semicolon);
				String[] rules = line.split(" or ");
				for (int i = 0; i < rules.length; i++){
					rules[i] = rules[i].substring(1, rules[i].length() - 1);
					links.add(new String[] {code, rules[i], "cd"});
				}
				for (int i = 0; i < words.length; i++) {
					links.add(new String[] {words[i], code, "wc"});
				}
			}
		}
		Dictionary dict = new Dictionary();
		for (String[] arr : links) {
			if (arr[2].equals("wc")) {
				Word w = new Word(arr[0]);
				for (String[] a : links) {
					if (a[0].equals(arr[1]) && !w.containsRule(a[1])) {
						w.addRule(a[1]);
					}
				}
				dict.addWord(w);
			}
		}
		System.out.println("Dictionary built successfully.");
		return dict;
	}
	
	private static boolean empty(String str) {
		return str == null || str.isEmpty();
	}
}
