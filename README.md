# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/). Constructs a grammatically valid sentence from given parses. Refer to [this issue](https://github.com/aigents/aigents-java/issues/22) for a list of completed tasks and [this repository](https://github.com/aigents/aigents-java-nlp) for the finalized codebase.

To build the project, run `ant war` in the command line.

To test `Loader.java`, run 

    cd src
    javac org/aigents/nlp/lg/*.java
    java org.aigents.nlp.lg.Loader 4.0.dict board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above).
