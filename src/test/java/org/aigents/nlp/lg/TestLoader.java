package test.java.org.aigents.nlp.lg;

import java.io.IOException;

import main.java.org.aigents.nlp.lg.Loader;

public class TestLoader {
    public static void main(String[] args) throws IOException {
        System.out.println("Testing Loader.java on the word \"board.\"");
        Loader.main(new String[] {"en/4.0.dict", "board"});
    }
}
