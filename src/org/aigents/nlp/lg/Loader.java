package org.aigents.nlp.lg;

import java.util.*;
import java.io.*;

public class Loader {
	public static void main(String[] args) {
		String data = "<dictionary-version-number>: V0v0v4+;\n" + 
				"<dictionary-locale>: EN4us+;\n" + 
				"\n" + 
				"% C01\n" + 
				"\"directors\" \"has\" \"with\":\n" + 
				"(C02C01-) or (C03C01- & C01C02+) or (C03C01- & C01C05+) or (C04C01- & C01C05+) or (C05C01- & C01C02+) or (C05C01- & C01C04+) or (C05C01- & C01C05+);\n" + 
				"\n" + 
				"% C02\n" + 
				"\"are\" \"binoculars\" \"board\" \"of\" \"sees\" \"to\" \"wants\" \"wood\":\n" + 
				"(C01C02-) or (C02C02+) or (C02C02- & C02C01+) or (C02C02- & C02C03+) or (C02C02- & C02C04+) or (C03C02- & C02C02+ & C02C03+) or (C03C02- & C02C03+ & C02C02+) or (C03C02- & C02C03+ & C02C03+) or (C05C02- & C02C03+) or (C05C02- & C04C02-) or (C05C02- & C05C02-) or (C05C02- & C05C02- & C02C02+);\n" + 
				"\n" + 
				"% C03\n" + 
				"\"a\" \"before\" \"cake\" \"child\" \"dad\" \"daughter\" \"food\" \"her\" \"his\" \"human\" \"mom\" \"not\" \"now\" \"parent\" \"sausage\" \"son\" \"tool\":\n" + 
				"(C02C03-) or (C02C03- & C03C03-) or (C03C01+) or (C03C02+) or (C03C03+) or (C03C03- & C02C03-) or (C03C03- & C03C04+) or (C03C03- & C03C05+) or (C03C03- & C05C03-) or (C03C04+) or (C03C05+) or (C04C03-) or (C05C03-) or (C05C03- & C03C01+);\n" + 
				"\n" + 
				"% C04\n" + 
				"\"be\" \"chalk\" \"knocked\" \"likes\" \"sawed\":\n" + 
				"(C01C04-) or (C02C04- & C04C05+) or (C03C04- & C04C02+ & C04C01+) or (C03C04- & C04C03+) or (C03C04- & C04C03+ & C04C03+);\n" + 
				"\n" + 
				"% C05\n" + 
				"\"hammer\" \"is\" \"liked\" \"on\" \"saw\" \"telescope\" \"the\" \"was\" \"writes\":\n" + 
				"(C01C05- & C03C05-) or (C03C05- & C01C05-) or (C03C05- & C05C01+ & C05C05+ & C05C02+) or (C03C05- & C05C03+) or (C03C05- & C05C03+ & C05C01+) or (C03C05- & C05C03+ & C05C03+) or (C03C05- & C05C03+ & C05C03+ & C05C03+) or (C03C05- & C05C05+) or (C03C05- & C05C05+ & C05C01+) or (C03C05- & C05C05+ & C05C02+ & C05C01+) or (C04C05- & C05C02+) or (C05C02+) or (C05C05+) or (C05C05- & C05C02+) or (C05C05- & C05C03+);\n" + 
				"\n" + 
				"UNKNOWN-WORD: XXX+;\n" + 
				"\n" + 
				"% 5 word clusters, 5 Link Grammar rules.\n" + 
				"% Link Grammar file saved to: /home/oleg/language-learning/output/POC-English-Amb-2018-06-06/POC-English-Amb/MST_fixed_manually/disjuncts-DRK-disjuncts/no-LEFT-WALL_no-period/generalized_rules/poc-english_5C_2018-06-06_0004.4.0.dict";
		ArrayList<String[]> links = grammarBuildLinks(data);
		for (String[] arr : links) {
			System.out.println(Arrays.toString(arr));
		}
	}
	
	private static boolean empty(String str) {
		return str == null || str.isEmpty();
	}
	
	public static ArrayList<String[]> grammarBuildLinks(String data) {
		String[] lines = data.split("\n");
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
					String[] connectors = rules[i].split(" & ");
					if (connectors == null || connectors.length < 1) break;
					for (int j = 0; j < connectors.length; j++) {
						String connector = connectors[j];
						String sign = connector.substring(connector.length() - 1);
						String word = connector.substring(0, connector.length() - 1);
						links.add(new String[] {rules[i], word, sign});
					}
				}
				for (int i = 0; i < words.length; i++) {
					links.add(new String[] {words[i], code, "wc"});
				}
			}
		}
		return links;
	}
}
