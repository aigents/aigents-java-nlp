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
import java.util.Iterator;
import java.util.List;

import org.aigents.nlp.lg.Dictionary;
import org.aigents.nlp.lg.Loader;


public class Generator {
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			Dictionary dict = Loader.grammarBuildLinks(args[0], true);
			List<String[]> words = processSentences(args[1]);
			if (words == null) {
				System.err.println("Error loading and tokenizing sentences.");
				return;
			}
			System.out.println(generateSentence(words.get(0)));
		} else {
			System.out.println("No command line parameters given.");
		}
	}
	
	public static String generateSentence(String[] words) {
		
		return null;
	}
	
	public static List<String[]> processSentences(String path) throws IOException {
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
	}
}
