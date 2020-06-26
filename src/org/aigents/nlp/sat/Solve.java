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
import java.util.Collections;

public class Solve {
	public static ArrayList<ArrayList<Boolean>> solve(SATInstance instance, WatchlistInstance watchlist, Assignment assignment, int d, boolean verbose) {
		int n = instance.getVariables().size();
		int[] state = new int[n];
		ArrayList<ArrayList<Boolean>> ret = new ArrayList<>();
		while (true) {
			if (d == n) {		
				ArrayList<Boolean> b = new ArrayList<>();
				for (boolean bool : assignment.booleans) {
					b.add(bool);
				}
				ret.add(b);
				d -= 1;
				continue;
			}
			boolean triedSomething = false;
			for (var a : new int[] {0, 1}) {
				if (((state[d] >> a) & 1) == 0) {
					if (verbose) {
						System.err.println("Trying " + instance.getVariables().get(d) + " = " + a);
					}
					triedSomething = true;
					state[d] |= 1 << a;
					assignment.booleans.set(d, a == 0? false : true);
					if (!Watchlist.updateWatchlist(instance, watchlist, d << 1 | a, assignment, verbose)) {
						assignment.booleans.set(d, null);
					} else {
						d += 1;
						break;
					}
				}
			}
			if (!triedSomething) {
				if (d == 0) {
					return ret;
				} else {
					state[d] = 0;
					assignment.booleans.set(d, null);
					d -= 1;
				}
			}
		}
	}
}