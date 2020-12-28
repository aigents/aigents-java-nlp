package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Generator;

public class TestGenerator {
    public static void main(String[] args) throws IOException {
        System.out.println("Testing Generator.java on gutenberg544.txt.");
        Generator.main(new String[] {"en/4.0.dict", "gutenberg544.txt"});
        System.out.println("\nTesting Generator.java on SingularityNET's \"small world\" corpus.");
        Generator.main(new String[] {"en/4.0.dict", "poc_english.txt"});
        System.out.println("\nTesting Generator.java on custom words.");
        Generator.main(new String[] {"en/4.0.dict", "food", "Cake", "a", "is", "now"});
    }
}

