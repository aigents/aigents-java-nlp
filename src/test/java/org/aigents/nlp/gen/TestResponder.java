package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Responder;

public class TestResponder {
    public static void main(String[] args) throws IOException {
    	System.out.println("SMALL WORLD GRAMMAR");
    	System.out.println("===================");
        runTests("dict_30C_2018-12-31_0006.4.0.dict");
    	
    	System.out.println("\nSQuAD");
    	System.out.println("===================");
    	squad("en/4.0.dict");
    }
    
    private static void runTests(String fname) throws IOException {
    	System.out.println("Context: Relationships and Food, Question: mom cake");
        Responder.main(new String[] {fname, "relationships_and_food.txt", "mom", "cake"});
        System.out.println("\nContext: Tools and Possession, Question: hammer tool");
        Responder.main(new String[] {fname, "tools_and_possession.txt", "hammer", "tool"});
        System.out.println("\nContext: Professions, Question: daughter board of directors");
        Responder.main(new String[] {fname, "professions.txt", 
        		"daughter", "board", "of", "directors"});
    }
    
    private static void squad(String fname) throws IOException {
    	System.out.println("Context: Normans, Question: monks fled to");
        Responder.main(new String[] {"en/4.0.dict", "squad/normans_cleaned.txt", "monks", "fled", "to"});
        System.out.println("\nContext: Imperialism, Question: imperialism focused on");
        Responder.main(new String[] {fname, "squad/imperialism_cleaned.txt", "imperialism", "focused", "on"});
    }
}