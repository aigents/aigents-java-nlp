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
import java.util.HashMap;
import java.util.List;

public class SATInstance {
	private ArrayList<String> variables;
	private HashMap<String, Integer> variableTable;
	private ArrayList<ArrayList<Integer>> clauses;
	
	public SATInstance() {
		variables = new ArrayList<>();
		variableTable = new HashMap<>();
		clauses = new ArrayList<>();
	}
	
	public void parseAndAddClause(String line) {
		ArrayList<Integer> clause = new ArrayList<>();
		for (var literal : line.split(" ")) {
			int negated = literal.startsWith("~")? 1 : 0;
			String variable = "";
			for (int i = negated; i < literal.length(); i++) {
				variable += literal.charAt(i);
			}
			if (!variableTable.containsKey(variable)) {
				variableTable.put(variable, variables.size());
				variables.add(variable);
			}
			var encodedLiteral = variableTable.get(variable) << 1 | negated;
			if ((encodedLiteral == 15 && clause.contains(7)) || (encodedLiteral == 11 && clause.contains(3)) 
					|| (encodedLiteral == 13 && clause.contains(5))) clause.add(0, encodedLiteral);	
			else clause.add(encodedLiteral);
		}
		clauses.add(clause);
	}
	
	public static SATInstance fromFile(String path) throws IOException {
		SATInstance instance = new SATInstance();
		URL url = new SATInstance().getClass().getResource(path);
		File f = new File(url.getPath());
		if (!f.exists()) return null;
		List<String> list = Files.readAllLines(f.toPath());
		for (var line : list) {
			line = line.replaceAll("^[ \t]+|[ \t]+$", "");
			if (line.length() > 0 && !line.startsWith("#")) {
				instance.parseAndAddClause(line);
			}
		}
		return instance;
	}
	
	public String literalToString(int literal) {
        var s = (literal & 1) == 0? " " : "~";
        return s + variables.get(literal >> 1);
	}

    public String clauseToString(ArrayList<Integer> clause) {
    	String ret = "";
    	for (int i = 0; i < clause.size() - 1; i++) {
    		ret += literalToString(clause.get(i)) + " ";
    	}
    	ret += literalToString(clause.get(clause.size() - 1));
    	return ret;
    }

    public String assignmentToString(Assignment assignment, boolean brief, String startingWith) {
    	if (startingWith == null) startingWith = "";
    	ArrayList<String> literals = new ArrayList<>();
    	for (int i = 0; i < variables.size(); i++) {
    		var a = assignment.booleans.get(i);
    		String v = variables.get(i);
    		if (v.startsWith(startingWith) && a != null) {
    			if (!a && !brief) {
    				literals.add("~" + v);
    			} else if (a) {
    				literals.add(v);
    			}
    		}
    	}
    	String ret = "";
    	for (int i = 0; i < literals.size() - 1; i++) {
    		ret += literals.get(i) + " ";
    	}
    	ret += literals.get(literals.size() - 1);
    	return ret;
    }
    
    public ArrayList<String> getVariables() {	return variables;	}
    
    public HashMap<String, Integer> getVariableTable() {	return variableTable;	}
    
    public ArrayList<ArrayList<Integer>> getClauses() {	   return clauses;   }
}