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

import java.util.ArrayList;
import java.util.HashMap;

public class Watchlist {
	public static WatchlistInstance setupWatchlist(SATInstance instance) {
		WatchlistInstance watchlist = new WatchlistInstance(instance);
		for (ArrayList<Integer> clause : instance.getClauses()) {
			watchlist.watchlist.get(clause.get(0)).add(clause);
		}
		return watchlist;
	}
	
	public static void dumpWatchlist(SATInstance instance, WatchlistInstance watchlist) {
		System.err.println("Current watchlist:");
		String literalString = "", clausesString = "";
		for (int l = 0; l < watchlist.watchlist.size(); l++) {
			var w = watchlist.watchlist.get(l);
			System.err.println(l + ", " + w);
			literalString = instance.literalToString(l);
			clausesString = "";
			for (int i = 0; i < w.size() - 1; i++) {
				var c = w.get(i);
				clausesString += instance.clauseToString(c) + ", ";
			}
			if (w.size() > 0) clausesString += instance.clauseToString(w.get(w.size() - 1));
			System.err.println(literalString + ": " + clausesString);
		}
	}
	
	public static boolean updateWatchlist(SATInstance instance, WatchlistInstance watchlist, 
			int falseLiteral, Assignment assignment, boolean verbose) {
		int count = 0;
		HashMap<Integer, ArrayList<Integer>> toAdd = new HashMap<>();
		for (var i : watchlist.watchlist.get(falseLiteral)) {
			var clause = watchlist.watchlist.get(falseLiteral).get(count);
			boolean foundAlternative = false;
			for (var alternative : clause) {
				var v = alternative >> 1;
				var a = alternative & 1;
				if (assignment.booleans.get(v) == null || assignment.booleans.get(v) == ((a ^ 1) == 0? false : true)) {
					foundAlternative = true;
					count++;
					toAdd.put(alternative, clause);
					break;
				}
			}
			if (!foundAlternative) {
				if (verbose) {
					dumpWatchlist(instance, watchlist);
					System.err.println("Current assignment: " + instance.assignmentToString(assignment, false, null));
					System.err.println("Clause " + instance.clauseToString(clause) + " contradicted.");
				}
				for (int idx = 0; idx < count; idx++) {
					watchlist.watchlist.get(falseLiteral).remove(0);
				}
				for (int alt : toAdd.keySet()) {
					watchlist.watchlist.get(alt).add(toAdd.get(alt));
				}
				return false;
			}
		}
		for (int idx = 0; idx < count; idx++) {
			watchlist.watchlist.get(falseLiteral).remove(0);
		}
		for (int alt : toAdd.keySet()) {
			watchlist.watchlist.get(alt).add(toAdd.get(alt));
		}
		return true;
	}
}