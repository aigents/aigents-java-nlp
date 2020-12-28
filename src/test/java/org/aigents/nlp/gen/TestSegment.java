package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Segment;

public class TestSegment {
    public static void main(String[] args) throws IOException {
        System.out.println("Testing Segment.java on gutenberg544.txt.");
        Segment.main(new String[] {"en/4.0.dict", "gutenberg544.txt"});
        System.out.println("\nTesting Segment.java on SingularityNET's \"small world\" corpus.");
        Segment.main(new String[] {"en/4.0.dict", "poc_english.txt"});
        System.out.println("\nTesting Segment.java on custom words.");
        Segment.main(new String[] {"en/4.0.dict", "tuna", "is", "a", "fish", "eagle", "is",
                "a", "bird", "dog", "is", "a", "mammal"});
    }
}
