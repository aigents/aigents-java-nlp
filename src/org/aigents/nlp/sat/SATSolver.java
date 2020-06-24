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

import java.io.IOException;
import java.util.ArrayList;

public class SATSolver {
	public static void main(String[] args) throws IOException {
		SATInstance instance = SATInstance.fromFile("test.txt"); // args[0] -> path
		ArrayList<Assignment> assignments = generateAssignments(instance, true); // instance, args[1] -> verbose
		int count = 0;
		for (Assignment a : assignments) {
			count++;
			if (true) { // args[1]
				System.err.println("Found satisfying assignment #" + count);
			}
			String assignmentStr = instance.assignmentToString(a, true, null); // a, args[2] -> brief
			System.out.println(assignmentStr);
		}
		if (true && count == 0) { // args[1]
			System.err.println("No satisfying assignment exists.");
		}
	}
	
	private static ArrayList<Assignment> generateAssignments(SATInstance instance, boolean verbose) {
		int n = instance.getVariables().size();
		WatchlistInstance watchlist = Watchlist.setupWatchlist(instance);
		Assignment a = new Assignment();
		for (int i = 0; i < n; i++) {
			a.booleans.add(null);
		}
		return Solve.solve(instance, watchlist, a, 0, verbose);
	}
}