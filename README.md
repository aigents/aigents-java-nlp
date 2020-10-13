# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/).

**Natural Language Segmentation (NLS)**

To test `Segment.java`, choose one of the following three options:

**ONE:** To test on "Anne's House of Dreams" by Lucy Maud Montgomery (`gutenberg544.txt`), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict gutenberg544.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and segment the text given in the file with the path specified in the second argument (`gutenberg544.txt` in the line above).

**TWO:** To test on SingularityNET's "small world" corpus (`poc_english.txt`), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and segment the text given in the file with the path specified in the second argument (`poc_english.txt` in the line above).

**THREE:** To use custom words (must be part of the corpus `en/4.0.dict` for now, other languages may be added in future iterations), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict tuna is a fish eagle is a bird dog is a mammal
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then segment the sentence specified by the subsequent arguments (`tuna`, `is`, `a`, etc. in the line above).

**Loader - Utility for NLS and NLG**

To test `Loader.java`, run 

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    java main.java.org.aigents.nlp.lg.Loader en/4.0.dict board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above) given the dictionary path specified in the first argument (`en/4.0.dict` in the line above). To test with the Russian dictionary, use `ru/4.0.dict` for the first argument. To test with a small grammar dictionary, use `poc-english_5C_2018-06-06_0004.4.0.dict.txt` for the first argument.
