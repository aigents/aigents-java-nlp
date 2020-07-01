# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/). Constructs a grammatically valid sentence from given parses. Refer to [this issue](https://github.com/aigents/aigents-java/issues/22) for a list of completed tasks and [this repository](https://github.com/aigents/aigents-java-nlp) for the finalized codebase.

To build the project, run `ant war` in the command line.

To test `Generator.java`, choose one of the following two options:

To use the sentences provided in the file `poc_english.txt`, run

    cd src
    javac org/aigents/nlp/lg/*.java
    javac org/aigents/nlp/sat/*.java
    java org.aigents.nlp.sat.Generator dict_30C_2018-12-31_0006.4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`dict_30C_2018-12-31_0006.4.0.dict` in the line above) and will then load, tokenize, and output the sentences given in the file with the path specified the second argument (`poc_english.txt` in the line above).

To use custom words (must be part of the small grammar corpus `dict_30C_2018-12-31_0006.4.0.dict poc_english.txt` for now, larger dictionaries will be added in future iterations), run

    cd src
    javac org/aigents/nlp/lg/*.java
    javac org/aigents/nlp/sat/*.java
    java org.aigents.nlp.sat.Generator dict_30C_2018-12-31_0006.4.0.dict food Cake a is now
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`dict_30C_2018-12-31_0006.4.0.dict` in the line above) and will then generate a grammatically valid sentence from the words given in the subsequent arguments (`food`, `Cake`, `a`, `is`, and `now` in the line above).

To test `SATSolver.java`, run 

    cd src
    javac org/aigents/nlp/lg/*.java
    javac org/aigents/nlp/sat/*.java
    java org.aigents.nlp.sat.SATSolver test2.txt false true
    
The script above will display the SAT solver's output given the input file specified in the first argument (`test2.txt` in the line above), a true or false value for whether the output should be verbose as specified in the second argument (`false` in the line above), and a true or false value for whether the output should be brief (`true` in the line above).

To test `Loader.java`, run 

    cd src
    javac org/aigents/nlp/lg/*.java
    java org.aigents.nlp.lg.Loader poc-english_5C_2018-06-06_0004.4.0.dict.txt board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above) given the dictionary path specified in the first argument (`poc-english_5C_2018-06-06_0004.4.0.dict.txt` in the line above). This project currently only supports files with the same structure as `poc-english_5C_2018-06-06_0004.4.0.dict.txt`, but full dictionaries such as `4.0.dict` will be added in future iterations.
