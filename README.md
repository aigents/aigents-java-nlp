# aigents-java-nlp
Natural language processing components and tools for [Aigents](https://aigents.com/). Refer to [this issue](https://github.com/aigents/aigents-java/issues/22) for a list of completed tasks.

Table of Contents
=================

<!--ts-->
   * [Build Script](#build-script)
   * [Usage](#usage)
      * [Small World Question Answering](#small-world-question-answering)
      * [Natural Language Segmentation](#natural-language-segmentation)
      * [Natural Language Generation](#natural-language-generation)
      * [Small World NLG](#small-world-nlg)
      * [Loader](#loader)
   * [Citation](#citation)
<!--te-->


Build Script
============

To build the project, run `ant war` in the command line.


Usage
=====

Small World Question Answering
-----

To test `Responder.java`, choose one of the following two options:

**ONE:** To execute a series of unit tests, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java test.java.org.aigents.nlp.gen.TestResponder

**TWO:** To obtain the response for a particular question, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Responder dict_30C_2018-12-31_0006.4.0.dict <context filename, e.g. relationships_and_food.txt> <seed words separated by spaces, e.g. mom cake>

For a list of context filenames, see [`src/test/resources/contexts`](https://github.com/aigents/aigents-java-nlp/tree/master/src/test/resources/contexts).

Natural Language Segmentation
-----

To test `Segment.java`, choose one of the following four options:

**ONE:** To execute a series of unit tests, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java test.java.org.aigents.nlp.gen.TestSegment

**TWO:** To test on "Anne's House of Dreams" by Lucy Maud Montgomery (`gutenberg544.txt`), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict gutenberg544.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and segment the text given in the file with the path specified in the second argument (`gutenberg544.txt` in the line above).

**THREE:** To test on SingularityNET's "small world" corpus (`poc_english.txt`), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and segment the text given in the file with the path specified in the second argument (`poc_english.txt` in the line above).

**FOUR:** To use custom words (must be part of the corpus `en/4.0.dict` for now, other languages may be added in future iterations), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Segment en/4.0.dict tuna is a fish eagle is a bird dog is a mammal
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then segment the sentence specified by the subsequent arguments (`tuna`, `is`, `a`, etc. in the line above).

Natural Language Generation
-----------

To test `Generator.java`, choose one of the following four options:

**ONE:** To execute a series of unit tests, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java test.java.org.aigents.nlp.gen.TestGenerator

**TWO:** To test on "Anne's House of Dreams" by Lucy Maud Montgomery (`gutenberg544.txt`), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Generator en/4.0.dict gutenberg544.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and output the sentences given in the file with the path specified in the second argument (`gutenberg544.txt` in the line above).

**THREE:** To use the sentences provided in the file `poc_english.txt`, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Generator en/4.0.dict poc_english.txt
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then load, tokenize, and output the sentences given in the file with the path specified in the second argument (`poc_english.txt` in the line above).

**FOUR:** To use custom words (must be part of the corpus `en/4.0.dict` for now, other languages may be added in future iterations), run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    java main.java.org.aigents.nlp.gen.Generator en/4.0.dict food Cake a is now
    
The script above will create a `Dictionary` object given the dictionary path specified in the first argument (`en/4.0.dict` in the line above) and will then generate a grammatically valid sentence from the words given in the subsequent arguments (`food`, `Cake`, `a`, `is`, and `now` in the line above).

Small World NLG
-----------

To test `SmallGrammarGen.java`, choose one of the following two options:

**ONE:** To execute a series of unit tests, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java test.java.org.aigents.nlp.gen.SmallGrammarGen

**TWO:** Follow the same steps as with `Generator.java`, but change the last line to

    java main.java.org.aigents.nlp.gen.SmallGrammarGen dict_30C_2018-12-31_0006.4.0.dict <individual words or poc_english.txt>

Loader
-----------

Utility for NLS and NLG. To test `Loader.java`, choose one of the following two options:

**ONE:** To execute a series of unit tests, run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    javac main/java/org/aigents/nlp/gen/*.java
    javac test/java/org/aigents/nlp/gen/*.java
    java test.java.org.aigents.nlp.gen.TestSegment

**TWO**: Run

    cd src
    javac main/java/org/aigents/nlp/lg/*.java
    java main.java.org.aigents.nlp.lg.Loader en/4.0.dict board
    
The script above will output the rule and disjuncts associated with the word passed in as the second argument (`board` in the line above) given the dictionary path specified in the first argument (`en/4.0.dict` in the line above). To test with the Russian dictionary, use `ru/4.0.dict` for the first argument. To test with a small grammar dictionary, use `poc-english_5C_2018-06-06_0004.4.0.dict.txt` for the first argument.

Citation
=====

[NLG Paper (Natural Language Generation Using Link Grammar for General Conversational Intelligence)](https://arxiv.org/abs/2105.00830):
```
@misc{ramesh2021natural,
  title={Natural Language Generation Using Link Grammar for General Conversational Intelligence},
  author={Vignav Ramesh and Anton Kolonin},
  year={2021},
  eprint={2105.00830},
  archivePrefix={arXiv},
  primaryClass={cs.CL}
}
```

[NLS Paper (Interpretable Natural Language Segmentation Based on Link Grammar)](https://ieeexplore.ieee.org/document/9303220):
```
@INPROCEEDINGS{9303220,
  author={V. {Ramesh} and A. {Kolonin}},
  booktitle={2020 Science and Artificial Intelligence conference (S.A.I.ence)}, 
  title={Interpretable Natural Language Segmentation Based on Link Grammar}, 
  year={2020},
  volume={},
  number={},
  pages={25-32},
  doi={10.1109/S.A.I.ence50533.2020.9303220}
}
```
