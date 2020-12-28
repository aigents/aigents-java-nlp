package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.SmallGrammarGen;

public class TestSmallGrammarGen {
    public static void main(String[] args) throws IOException {
        System.out.println("\nTesting SmallGrammarGen.java on SingularityNET's \"small world\" corpus.");
        SmallGrammarGen.main(new String[] {"dict_30C_2018-12-31_0006.4.0.dict", "poc_english.txt"});
        System.out.println("\nTesting SmallGrammarGen.java on custom words.");
        SmallGrammarGen.main(new String[] {"dict_30C_2018-12-31_0006.4.0.dict", "food", "Cake", "a", "is", "now"});
    }
}
