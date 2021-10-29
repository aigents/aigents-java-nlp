package main.java.org.aigents.nlp.gen;

import java.io.IOException;
import main.java.org.aigents.nlp.lg.Dictionary;
import main.java.org.aigents.nlp.lg.Loader;

public class Parser {

	private static final String defaultDictName="en/4.0.dict";
	private String dictName = "";
	private Dictionary dict = null;
	private boolean dictLoaded = false;
	public boolean tokenize = true;                   // Indicates that to need tokenize input sentence, default value=true
	
	public void loadDict() throws IOException {
		if(dictLoaded==true) {
			System.out.println("Dictionary already loaded");
			return;
		}		
		try {
			dictName=defaultDictName;
			Dictionary[] dicts = Loader.buildLGDict(dictName);
			dict = dicts[0];
			dictLoaded=true;
		}
		catch(Exception e) {
			System.err.println("Can't load dictionary '"+dictName+"'");
		}		
	}
	
	public boolean isDictLoaded(){
		return dictLoaded;
	}
	
	public String printDictInfo() {
		if(dictLoaded)
			return "DictName="+defaultDictName+"; DictSize="+dict.getWords().size()+"; Version="+dict.getVersionNumber();
		else
			return "Dictionary not loaded";
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
