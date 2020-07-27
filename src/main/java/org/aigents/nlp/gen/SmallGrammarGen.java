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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import main.java.org.aigents.nlp.lg.Dictionary;
import main.java.org.aigents.nlp.lg.Disjunct;
import main.java.org.aigents.nlp.lg.Loader;
import main.java.org.aigents.nlp.lg.Rule;
import main.java.org.aigents.nlp.lg.Word;

public class SmallGrammarGen {
  public static Dictionary dict, hyphenated;

  public static void main(String[] args) throws IOException {
    long startTime = System.currentTimeMillis();
    if (args.length == 2) {
      int single = 0;
      int multOne = 0;
      int multNo = 0;
      int no = 0;
      try {
        if (args[0].contains("/4.0.dict")) {
          Dictionary[] dicts = Loader.buildLGDict(args[0]);
          dict = dicts[0];
          hyphenated = dicts[1];
        } else {
          dict = Loader.grammarBuildLinks(args[0], true);
        }
        List<String> list = getList(args[1]);
        List<String[]> words = processSentences(args[1]);
        if (words == null) {
          System.err.println("Error loading and tokenizing sentences.");
          return;
        }
        int idx = -1;
        for (String[] w : words) {
          idx++;
          HashSet<String> sentence = generateSentence(w);
          System.out.println(Arrays.toString(w) + ": " + sentence);
          String s = list.get(idx);
          if (sentence.size() > 1) {
            boolean one = false;
            for (String sen2 : sentence) {
              if (sen2.equals(s)) {
                one = true;
                continue;
              }
              String sen = sen2.substring(0, sen2.length() - 1);
              String[] senParts = sen.split(" ");
              String[] sParts = s.substring(0, s.length() - 1).split(" ");
              ArrayList<String> mismatches = new ArrayList<>();
              for (int i = 0; i < sParts.length; i++) {
                if (!senParts[i].equals(sParts[i])) {
                  mismatches.add(senParts[i]);
                }
              }
              System.out.println("      The words " + mismatches + " are in the wrong place.");
              System.out.println("      While the sentence \"" + sen2
                  + "\" is grammatically valid, it is contextually wrong.");
            }
            if (!one) multNo++;
            else multOne++;
          } else if (sentence.size() == 1) {
            single++;
          } else {
            no++;
          }
        }
        System.out.println("Single correct: " + single + "/" + words.size());
        System.out.println("Multiple with one correct: " + multOne + "/" + words.size());
        System.out.println("Multiple with none correct: " + multNo + "/" + words.size());
        System.out.println("None correct: " + no + "/" + words.size());
        System.out.println("Accuracy: " + ((double) single) / words.size());
        long runtime = System.currentTimeMillis() - startTime;
        String t = String.format("%d min, %d sec", 
              TimeUnit.MILLISECONDS.toMinutes(runtime),
              TimeUnit.MILLISECONDS.toSeconds(runtime) - 
              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runtime))
          );
        String t2 = String.format("%d min, %d sec", 
              TimeUnit.MILLISECONDS.toMinutes(runtime/words.size()),
              TimeUnit.MILLISECONDS.toSeconds(runtime/words.size()) - 
              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runtime/words.size()))
          );
        System.out.println("Overall runtime: " + t);
        System.out.println("Avg. runtime per sentence: " + t2);
      } catch (Exception e) {
        System.err.println("Error building dictionary. Please try again with a different filename.");
      }
    } else if (args.length > 2) {
      try {
        if (args[0].contains("/4.0.dict")) {
          Dictionary[] dicts = Loader.buildLGDict(args[0]);
          dict = dicts[0];
          hyphenated = dicts[1];
        } else {
          dict = Loader.grammarBuildLinks(args[0], true);
        }
        String[] words = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
          words[i - 1] = args[i];
        }
        System.out.println(Arrays.toString(words) + ": " + generateSentence(words));
      } catch (Exception e) {
        System.err.println("Error building dictionary. Please try again with a different filename.");
      }
    } else {
      System.out.println("No command line parameters given.");
    }
  }
  
  public static HashSet<String> generateSentence(String[] elements) {
    boolean not = false;
    boolean now = false;
    HashSet<String> ret = new HashSet<>();
    int n = elements.length;
    int[] indexes = new int[n];
    for (int i = 0; i < n; i++) {
      indexes[i] = 0;
    }
    ArrayList<String> w = new ArrayList<>(Arrays.asList(elements));
    if (w.contains("not") && w.contains("a")) {
      not = true;
      w.remove("not");
    }
    if (w.contains("now") && w.contains("a")) {
      now = true;
      w.remove("now");
    }
    if (not || now) {
      int i = 0;
      String[] input = new String[w.size()];
      for (String word : w) {
        input[i] = word;
        i++;
      }
      if (now && !not) {
        if (check(input) && isValid(input)) {
          String str = makeSentence(input);
          int nowId = 0;
          String[] parts = str.split(" ");
          for (int m = 0; m < parts.length; m++) {
            if (parts[m].equals("a")) {
              nowId = str.indexOf(" a") + 2 + parts[m + 1].length();
            }
          }
          String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
          ret.add(str3);
        }
      }
      if (not && !now) {
        if (check(input) && isValid(input)) {
          String str = makeSentence(input);
          str = str.replace(" a", " not a");
          ret.add(str);
        }
      }
      if (not && now) {
        if (check(input) && isValid(input)) {
          String str = makeSentence(input);
          int nowId = 0;
          String[] parts = str.split(" ");
          for (int m = 0; m < parts.length; m++) {
            if (parts[m].equals("a")) {
              nowId = str.indexOf(" a") + 2 + parts[m + 1].length();
            }
          }
          String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
          str3 = str3.replace(" a", " not a");
          ret.add(str3);
        }
      }
    } else {
      if (check(elements) && isValid(elements)) {
        ret.add(makeSentence(elements));
      }
    }
    int i = 0;
    while (i < n) {
      if (indexes[i] < i) {
        swap(elements, i % 2 == 0 ? 0 : indexes[i], i);
        ArrayList<String> w2 = new ArrayList<>(Arrays.asList(elements));
        if (w2.contains("not") && w2.contains("a")) {
          w2.remove("not");
        }
        if (w2.contains("now") && w2.contains("a")) {
          w2.remove("now");
        }
        if (not || now) {
          int id = 0;
          String[] input = new String[w2.size()];
          for (String word : w2) {
            input[id] = word;
            id++;
          }
          if (now && !not) {
            if (check(input) && isValid(input)) {
              String str = makeSentence(input);
              int nowId = 0;
              String[] parts = str.split(" ");
              for (int m = 0; m < parts.length; m++) {
                if (parts[m].equals("a")) {
                  nowId = str.indexOf(" a") + 2 + parts[m + 1].length();
                }
              }
              String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
              ret.add(str3);
            }
          }
          if (not && !now) {
            if (check(input) && isValid(input)) {
              String str = makeSentence(input);
              str = str.replace(" a", " not a");
              ret.add(str);
            }
          }
          if (not && now) {
            if (check(input) && isValid(input)) {
              String str = makeSentence(input);
              int nowId = 0;
              String[] parts = str.split(" ");
              for (int m = 0; m < parts.length; m++) {
                if (parts[m].equals("a")) {
                  nowId = str.indexOf(" a") + 2 + parts[m + 1].length();
                }
              }
              String str3 = str.substring(0, nowId) + " now" + str.substring(nowId);
              str3 = str3.replace(" a", " not a");
              ret.add(str3);
            }
          }
        } else {
          if (check(elements) && isValid(elements)) {
            ret.add(makeSentence(elements));
          }
        }
        indexes[i]++;
        i = 0;
      } else {
        indexes[i] = 0;
        i++;
      }
    }
    return ret;
  }
  
  private static boolean isValid(String[] input) {
    outer: for (int i = 0; i < input.length - 1; i++) {
      String left = input[i];
      String right = input[i + 1];
      if ((dict.getRule(left.toLowerCase()).toString().equals(dict.getRule("sawed").toString())
          || (dict.getRule(left.toLowerCase()).toString().equals(dict.getRule("writes").toString()) && contains(input, "to"))
          || left.equals("saw"))
          && (idx(input, "with") == i+2 || idx(input, "with") == i+3)) {
        int idx = idx(input, "with");
        if (idx == i + 2) {
          i = idx-1;
          if (connectsLeft(left, right, input[i+1]))
            continue outer;
        } else {
          i = idx-1;
          if (right.equals("to")) {
            if (connectsLeft(left, right, input[i+1]) && connects(right, input[i]))
              continue outer;
          } else if (right.equals("the")) {
            if (connectsAll(left, right, input[i], input[i+1]))
              continue outer;
          }
        }
      } else if ((right.equals("a") || right.equals("the")) && i + 2 < input.length) {
        i++;
        if ((connects(left, input[i + 1]) && connects(right, input[i + 1]))
            || connects(left, right, input[i + 1]))
          continue outer;
      } else if (i + 2 < input.length && (input[i + 2].equals("before") || input[i + 2].equals("now"))
          && !contains(input, "a")) {
        i++;
        if ((connects(left, right) && connects(left, input[i + 1])) || connectsLeft(left, right, input[i + 1]))
          continue outer;
      } else if (right.equals("on") && i >= 2 && (input[i - 1].equals("a") || input[i - 1].equals("with"))) {
        if (input[i - 1].equals("a") && i >= 3) {
          if (connectsLeft(input[i - 3], input[i - 2], right))
            continue outer;
        } else {
          if (connectsLeft(input[i - 2], input[i - 1], right))
            continue outer;
          else {
            if (i >= 4 && connectsMin(input[i-4], right)) continue outer;
          }
        }
      } else if (left.equals("wants") && i + 2 < input.length) {
        if (right.equals("to")) {
          i += 2;
          if (connectsLeft("wants", "to", input[i]))
            continue outer;
        } else if (input[i + 2].equals("to")) {
          if (i + 3 < input.length) {
            i += 3;
            if (connectsFour(left, right, input[i - 1], input[i]))
              continue outer;
          }
        } else if (input[i + 3].equals("to")) {
          if (i + 4 < input.length) {
            i += 4;
            if (connectsFour(left, input[i-2], input[i - 1], input[i])) {
              continue outer;
            }
          }
        }
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
    Rule leftRule = new Rule(), rightRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    for (Disjunct dl : leftRule.getDisjuncts()) {
      for (Disjunct dr : rightRule.getDisjuncts()) {
        String wl = "";
        String wr = "";
        if (dl.getConnectors().size() > 1) {
          for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
            String c = dl.getConnectors().get(ci);
            if (!c.contains("-")) {
              wl += c + " & ";
            }
          }
          String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
          if (!c.contains("-")) {
            wl += c;
          } else {
            if (wl.contains("&"))
              wl = wl.substring(0, wl.length() - 3);
          }
        } else {
          wl = dl.getConnectors().get(0);
        }
        if (dr.getConnectors().size() > 1) {
          for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
            String c = dr.getConnectors().get(ci);
            if (c.contains("-")) {
              wr += c + " & ";
            }
          }
          String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
          if (c.contains("-")) {
            wr += c;
          } else {
            if (wr.contains("&"))
              wr = wr.substring(0, wr.length() - 3);
          }
        } else {
          wr = dr.getConnectors().get(0);
        }
        String wlu = wl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
        if (equals(wlu, wr)) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static boolean connectsMin(String left, String right) {
    Rule leftRule = new Rule(), rightRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    for (Disjunct dl : leftRule.getDisjuncts()) {
      for (Disjunct dr : rightRule.getDisjuncts()) {
        String wl = "";
        String wr = "";
        if (dl.getConnectors().size() > 1) {
          for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
            String c = dl.getConnectors().get(ci);
            if (!c.contains("-")) {
              wl += c + " & ";
            }
          }
          String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
          if (!c.contains("-")) {
            wl += c;
          } else {
            if (wl.contains("&"))
              wl = wl.substring(0, wl.length() - 3);
          }
        } else {
          wl = dl.getConnectors().get(0);
        }
        if (dr.getConnectors().size() > 1) {
          for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
            String c = dr.getConnectors().get(ci);
            if (c.contains("-")) {
              wr += c + " & ";
            }
          }
          String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
          if (c.contains("-")) {
            wr += c;
          } else {
            if (wr.contains("&"))
              wr = wr.substring(0, wr.length() - 3);
          }
        } else {
          wr = dr.getConnectors().get(0);
        }
        for (String lp : wl.split(" & ")) {
          for (String rp : wr.split(" & ")) {
            String wlu = lp.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
            if (equals(wlu, rp)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private static boolean connects(String left, String mid, String right) {
    Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      midRule = dict.getRule(mid.toLowerCase(), true);
      if (midRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + mid + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    boolean[] leftTrue = new boolean[rightRule.getDisjuncts().size()];
    boolean[] midTrue = new boolean[rightRule.getDisjuncts().size()];
    int[] leftId = new int[rightRule.getDisjuncts().size()];
    int[] midId = new int[rightRule.getDisjuncts().size()];
    for (int ri = 0; ri < rightRule.getDisjuncts().size(); ri++) {
      Disjunct dr = rightRule.getDisjuncts().get(ri);
      String wr = "";
      if (dr.getConnectors().size() > 1) {
        for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
          String c = dr.getConnectors().get(ci);
          if (c.contains("-")) {
            wr += c + " & ";
          }
        }
        String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
        if (c.contains("-")) {
          wr += c;
        } else {
          if (wr.contains("&"))
            wr = wr.substring(0, wr.length() - 3);
        }
      } else {
        wr = dr.getConnectors().get(0);
      }
      if (!wr.contains("&"))
        continue;
      for (Disjunct dl : leftRule.getDisjuncts()) {
        for (Disjunct dm : midRule.getDisjuncts()) {
          String[] parts = wr.split(" & ");
          for (int idp = 0; idp < parts.length; idp++) {
            String part = parts[idp];
            String wl = "";
            if (dl.getConnectors().size() > 1) {
              for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
                String c = dl.getConnectors().get(ci);
                if (!c.contains("-")) {
                  wl += c + " & ";
                }
              }
              String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
              if (!c.contains("-")) {
                wl += c;
              } else {
                if (wl.contains("&"))
                  wl = wl.substring(0, wl.length() - 3);
              }
            } else {
              wl = dl.getConnectors().get(0);
            }
            String wm = "";
            if (dm.getConnectors().size() > 1) {
              for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
                String c = dm.getConnectors().get(ci);
                if (!c.contains("-")) {
                  wm += c + " & ";
                }
              }
              String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
              if (!c.contains("-")) {
                wm += c;
              } else {
                if (wm.contains("&"))
                  wm = wm.substring(0, wm.length() - 3);
              }
            } else {
              wm = dm.getConnectors().get(0);
            }
            String wlu = wl.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
            String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
            if (equals(wlu, part) && !leftTrue[ri]) {
              leftTrue[ri] = true;
              leftId[ri] = idp;
            }
            if (equals(wmu, part) && !midTrue[ri]) {
              midTrue[ri] = true;
              midId[ri] = idp;
            }
          }
        }
      }
    }
    for (int i = 0; i < leftTrue.length; i++) {
      if (leftTrue[i] && midTrue[i] && midId[i]<leftId[i]) return true;
    }
    return false;
  }

  private static boolean connectsLeft(String left, String mid, String right) {
    Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      midRule = dict.getRule(mid.toLowerCase(), true);
      if (midRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + mid + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    boolean[] midTrue = new boolean[leftRule.getDisjuncts().size()];
    boolean[] rightTrue = new boolean[leftRule.getDisjuncts().size()];
    int[] rightId = new int[leftRule.getDisjuncts().size()];
    int[] midId = new int[leftRule.getDisjuncts().size()];
    for (int li = 0; li < leftRule.getDisjuncts().size(); li++) {
      Disjunct dl = leftRule.getDisjuncts().get(li);
      String wl = "";
      if (dl.getConnectors().size() > 1) {
        for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
          String c = dl.getConnectors().get(ci);
          if (!c.contains("-")) {
            wl += c + " & ";
          }
        }
        String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
        if (!c.contains("-")) {
          wl += c;
        } else {
          if (wl.contains("&"))
            wl = wl.substring(0, wl.length() - 3);
        }
      } else {
        wl = dl.getConnectors().get(0);
      }
      if (!wl.contains("&"))
        continue;
      for (Disjunct dr : rightRule.getDisjuncts()) {
        for (Disjunct dm : midRule.getDisjuncts()) {
          String[] parts = wl.split(" & ");
          for (int idp = 0; idp < parts.length; idp++) {
            String part = parts[idp];
            String wr = "";
            if (dr.getConnectors().size() > 1) {
              for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
                String c = dr.getConnectors().get(ci);
                if (c.contains("-")) {
                  wr += c + " & ";
                }
              }
              String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
              if (c.contains("-")) {
                wr += c;
              } else {
                if (wr.contains("&"))
                  wr = wr.substring(0, wr.length() - 3);
              }
            } else {
              wr = dr.getConnectors().get(0);
            }
            String wm = "";
            if (dm.getConnectors().size() > 1) {
              for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
                String c = dm.getConnectors().get(ci);
                if (c.contains("-")) {
                  wm += c + " & ";
                }
              }
              String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
              if (c.contains("-")) {
                wm += c;
              } else {
                if (wm.contains("&"))
                  wm = wm.substring(0, wm.length() - 3);
              }
            } else {
              wm = dm.getConnectors().get(0);
            }
            String wru = wr.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
            String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
            if (equals(wru, part) && !rightTrue[li]) {
              rightTrue[li] = true;
              rightId[li] = idp;
            }
            if (equals(wmu, part) && !midTrue[li]) {
              midTrue[li] = true;
              midId[li] = idp;
            }
          }
        }
      }
    }
    for (int i = 0; i < rightTrue.length; i++) {
      if (rightTrue[i] && midTrue[i] && midId[i]<rightId[i]) return true;
    }
    return false;
  }

  private static boolean connectsFour(String left, String mid, String right, String next) {
    Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule(), nextRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      midRule = dict.getRule(mid.toLowerCase(), true);
      if (midRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + mid + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      nextRule = dict.getRule(next.toLowerCase(), true);
      if (nextRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + next + "' not found in dictionary.");
      System.exit(0);
    }
    boolean midTrue = false;
    boolean rightTrue = false;
    boolean nextTrue = false;
    int rightId = 0;
    int midId = 0;
    int nextId = 0;
    for (Disjunct dl : leftRule.getDisjuncts()) {
      String wl = "";
      if (dl.getConnectors().size() > 1) {
        for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
          String c = dl.getConnectors().get(ci);
          if (!c.contains("-")) {
            wl += c + " & ";
          }
        }
        String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
        if (!c.contains("-")) {
          wl += c;
        } else {
          if (wl.contains("&"))
            wl = wl.substring(0, wl.length() - 3);
        }
      } else {
        wl = dl.getConnectors().get(0);
      }
      if (!wl.contains("&"))
        continue;
      for (Disjunct dr : rightRule.getDisjuncts()) {
        for (Disjunct dm : midRule.getDisjuncts()) {
          for (Disjunct dn : nextRule.getDisjuncts()) {
            String[] parts = wl.split(" & ");
            for (int idp = 0; idp < parts.length; idp++) {
              String part = parts[idp];
              String wr = "";
              if (dr.getConnectors().size() > 1) {
                for (int ci = 0; ci < dr.getConnectors().size() - 1; ci++) {
                  String c = dr.getConnectors().get(ci);
                  if (c.contains("-")) {
                    wr += c + " & ";
                  }
                }
                String c = dr.getConnectors().get(dr.getConnectors().size() - 1);
                if (c.contains("-")) {
                  wr += c;
                } else {
                  if (wr.contains("&"))
                    wr = wr.substring(0, wr.length() - 3);
                }
              } else {
                wr = dr.getConnectors().get(0);
              }
              String wm = "";
              if (dm.getConnectors().size() > 1) {
                for (int ci = 0; ci < dm.getConnectors().size() - 1; ci++) {
                  String c = dm.getConnectors().get(ci);
                  if (c.contains("-")) {
                    wm += c + " & ";
                  }
                }
                String c = dm.getConnectors().get(dm.getConnectors().size() - 1);
                if (c.contains("-")) {
                  wm += c;
                } else {
                  if (wm.contains("&"))
                    wm = wm.substring(0, wm.length() - 3);
                }
              } else {
                wm = dm.getConnectors().get(0);
              }
              String wn = "";
              if (dn.getConnectors().size() > 1) {
                for (int ci = 0; ci < dn.getConnectors().size() - 1; ci++) {
                  String c = dn.getConnectors().get(ci);
                  if (c.contains("-")) {
                    wn += c + " & ";
                  }
                }
                String c = dn.getConnectors().get(dn.getConnectors().size() - 1);
                if (c.contains("-")) {
                  wn += c;
                } else {
                  if (wn.contains("&"))
                    wn = wn.substring(0, wn.length() - 3);
                }
              } else {
                wn = dn.getConnectors().get(0);
              }
              String wru = wr.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
              String wmu = wm.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
              String wnu = wn.replaceAll("\\+", "/").replaceAll("-", "\\+").replaceAll("/", "-");
              if (equals(wru, part) && !rightTrue) {
                rightTrue = true;
                rightId = idp;
              }
              if (equals(wmu, part) && !midTrue) {
                midTrue = true;
                midId = idp;
              }
              if (equals(wnu, part) && !nextTrue) {
                nextTrue = true;
                nextId = idp;
              } else if (!nextTrue) {
                ifc: if (wnu.contains("&")) {
                  String[] p = wnu.split(" & ");
                  for (String s : p) {
                    if (equals(s, part)) {
                      nextTrue = true;
                      nextId = idp;
                      break ifc;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return rightTrue && midTrue && nextTrue && (midId < rightId) && (rightId < nextId);
  }
  
  private static boolean connectsAll(String left, String mid, String right, String next) {
    Rule leftRule = new Rule(), midRule = new Rule(), rightRule = new Rule(), nextRule = new Rule();
    try {
      leftRule = dict.getRule(left.toLowerCase(), true);
      if (leftRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + left + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      midRule = dict.getRule(mid.toLowerCase(), true);
      if (midRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + mid + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      rightRule = dict.getRule(right.toLowerCase(), true);
      if (rightRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + right + "' not found in dictionary.");
      System.exit(0);
    }
    try {
      nextRule = dict.getRule(next.toLowerCase(), true);
      if (nextRule == null)
        throw new Exception();
    } catch (Exception e) {
      System.err.println("Word '" + next + "' not found in dictionary.");
      System.exit(0);
    }
    boolean one = false;
    boolean two = false;
    for (Disjunct dl : leftRule.getDisjuncts()) {
      String wl = "";
      if (dl.getConnectors().size() > 1) {
        for (int ci = 0; ci < dl.getConnectors().size() - 1; ci++) {
          String c = dl.getConnectors().get(ci);
          if (!c.contains("-")) {
            wl += c + " & ";
          }
        }
        String c = dl.getConnectors().get(dl.getConnectors().size() - 1);
        if (!c.contains("-")) {
          wl += c;
        } else {
          if (wl.contains("&"))
            wl = wl.substring(0, wl.length() - 3);
        }
      } else {
        wl = dl.getConnectors().get(0);
      }
      if (!wl.contains("&")) continue;
      String[] parts = wl.split(" & ");
      for (int idp = 0; idp < parts.length; idp++) {
        String part = parts[idp];
        Rule r = new Rule();
        r.addWord(part);
        dict.addWord(new Word(part.toLowerCase(), r));
        if (connects(part, mid, right)) one = true;
        if (connects(part, next)) two = true;
      }
    }
    return one && two;
  }
  
  private static boolean equals(String wlu, String wr) {
    if (wlu.equals(wr)) {
      return true;
    }
    if (wlu.contains("*")) {
      int idx = wlu.indexOf("*");
      int lid = wlu.lastIndexOf("*");
      if (wr.length() == wlu.length()) {
        if (wlu.substring(0, idx).equals(wr.substring(0, idx)) 
            && wlu.substring(lid+1).equals(wr.substring(lid+1))) {
          return true;
        }
      } else {
        if (wlu.substring(0, idx).equals(wr.substring(0, idx))) return true;
      }
    } 
    if (wr.contains("*")) {
      int idx = wr.indexOf("*");
      int lid = wr.lastIndexOf("*");
      if (wr.length() == wlu.length()) {
        if (wlu.substring(0, idx).equals(wr.substring(0, idx)) 
            && wlu.substring(lid+1).equals(wr.substring(lid+1))) {
          return true;
        }
      } else {
        if (wlu.substring(0, idx).equals(wr.substring(0, idx))) return true;
      }
    }
    return false;
  }

  public static List<String[]> processSentences(String path) throws IOException {
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
      if (s.equals(str)) return i;
    }
    return -1;
  }
  
  private static void swap(String[] input, int a, int b) {
    String tmp = input[a];
    input[a] = input[b];
    input[b] = tmp;
  }

  private static String makeSentence(String[] arr) {
    String ret = "";
    for (int i = 0; i < arr.length - 1; i++) {
      ret += arr[i] + " ";
    }
    ret += arr[arr.length - 1] + ".";
    ret = ret.toLowerCase();
    ret = ret.substring(0,1).toUpperCase() + ret.substring(1);
    return ret;
  }
  
  private static boolean check(String[] input) {
    String first = input[0].toLowerCase();
    String last = input[input.length - 1].toLowerCase();
    boolean firstTrue = false;
    boolean lastTrue = false;
    for (Disjunct df : dict.getRule(first, true).getDisjuncts()) {
      if (!df.toString().contains("-")) {
        firstTrue = true;
        break;
      }
    }
    for (Disjunct dl : dict.getRule(last, true).getDisjuncts()) {
      if (!dl.toString().contains("+")) {
        lastTrue = true;
        break;
      }
    }
    return firstTrue && lastTrue;
  }
}