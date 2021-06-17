package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Responder;

public class TestResponder {
    public static void main(String[] args) throws IOException {
        System.out.println("Context: Relationships and Food, Question: mom cake");
        Responder.main(new String[] {"dict_30C_2018-12-31_0006.4.0.dict", "relationships_and_food.txt", "mom", "cake"});
        System.out.println("\nContext: Tools and Possession, Question: hammer tool");
        Responder.main(new String[] {"dict_30C_2018-12-31_0006.4.0.dict", "tools_and_possession.txt", "hammer", "tool"});
        System.out.println("\nContext: Professions, Question: daughter board of directors");
        Responder.main(new String[] {"dict_30C_2018-12-31_0006.4.0.dict", "professions.txt", 
        		"daughter", "board", "of", "directors"});
    }
}