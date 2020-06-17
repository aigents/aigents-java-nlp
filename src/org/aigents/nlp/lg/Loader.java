/*******************************************************************
 * Loader.java                                                     *
 * Loads the Link Grammar dictionary specified by the given path   *
 * into a new Dictionary object.                                   *
 *                                                                 *
 * Written by Vignav Ramesh                                        *
 *******************************************************************/

package org.aigents.nlp.lg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Loader {
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			Dictionary dict = grammarBuildLinks(args[0]);
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
	
	public static Dictionary grammarBuildLinks(String path) throws IOException {
		URL url = new Loader().getClass().getResource(path);
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
					rules[i] = rules[i].substring(0, rules[i].length() - 1).substring(1);
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
		return dict;
	}
	
	private static boolean empty(String str) {
		return str == null || str.isEmpty();
	}
}