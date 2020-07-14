# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/). Constructs a grammatically valid sentence from given parses. Refer to [this issue](https://github.com/aigents/aigents-java/issues/22) for a list of completed tasks and [this repository](https://github.com/aigents/aigents-java-nlp) for the finalized codebase.

To build the project, run `ant war` in the command line.

To test `Generator.java`, choose one of the following two options:

To use the sentences provided in the file `poc_english.txt`, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Generator en/4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and output the sentences given in the file with the path specified in the second argument (`poc_english.txt` in the line above).

To use custom words (must be part of the corpus `en/4.0.dict` for now, other languages may be added in future iterations), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Generator en/4.0.dict food Cake a is now
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then generate a grammatically valid sentence from the words given in the subsequent arguments (`food`, `Cake`, `a`, `is`, and `now` in the line above).

To test `SmallGrammarGen.java`, follow the same steps as with `Generator.java`, but change the last line to

    java main.java.org.aigents.nlp.gen.SmallGrammarGen dict_30C_2018-12-31_0006.4.0.dict <individual words or poc_english.txt>

To test `Loader.java`, run 

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    java main.java.org.aigents.nlp.lg.Loader en/4.0.dict board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above) given the dictionary path specified in the first argument (`en/4.0.dict` in the line above). To test with the Russian dictionary, use `ru/4.0.dict` for the first argument. To test with a small grammar dictionary, use `poc-english_5C_2018-06-06_0004.4.0.dict.txt` for the first argument.
