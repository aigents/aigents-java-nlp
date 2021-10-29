package test.java.org.aigents.nlp.gen;

import java.io.IOException;

import main.java.org.aigents.nlp.gen.Parser;

public class TestParser {

	public static void main(String[] args) throws IOException {		
		System.out.println("Testing Parser.java");
        Parser parser = new Parser();                 //Create parser object
        parser.loadDict();                            //Load default dictionary
        if(parser.isDictLoaded())
        	System.out.println("Dictionary loaded. "+parser.printDictInfo());
        System.out.println("test finished");
	}
}
