# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/). Constructs a grammatically valid sentence from given parses. Refer to [this issue](https://github.com/aigents/aigents-java/issues/22) for a list of completed tasks and [this repository](https://github.com/aigents/aigents-java-nlp) for the finalized codebase.

To build the project, run `ant war` in the command line.

To test `Generator.java`, choose one of the following two options:

To use the sentences provided in the file `poc_english.txt`, run

    cd src
    javac org/aigents/nlp/lg/*.java
    javac org/aigents/nlp/gen/*.java
    java org.aigents.nlp.gen.Generator dict_30C_2018-12-31_0006.4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`dict_30C_2018-12-31_0006.4.0.dict` in the line above) and will then load, tokenize, and output the sentences given in the file with the path specified in the second argument (`poc_english.txt` in the line above).

To use custom words (must be part of the small grammar corpus `dict_30C_2018-12-31_0006.4.0.dict` for now, larger dictionaries will be added in future iterations), run

    cd src
    javac org/aigents/nlp/lg/*.java
    javac org/aigents/nlp/gen/*.java
    java org.aigents.nlp.gen.Generator dict_30C_2018-12-31_0006.4.0.dict food Cake a is now
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`dict_30C_2018-12-31_0006.4.0.dict` in the line above) and will then generate a grammatically valid sentence from the words given in the subsequent arguments (`food`, `Cake`, `a`, `is`, and `now` in the line above).

To test `Loader.java`, run 

    cd src
    javac org/aigents/nlp/lg/*.java
    java org.aigents.nlp.lg.Loader 4.0.dict board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above) given the dictionary path specified in the first argument (`4.0.dict` in the line above).
