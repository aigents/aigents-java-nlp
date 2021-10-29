package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Parser;

public class TestParser {

	public static void main(String[] args) throws IOException {		
		System.out.println("Testing Parser.java");
        Parser.main(new String[] {"en/4.0.dict", "Tuna is a fish"});
        System.out.println("test finished");
	}
}
