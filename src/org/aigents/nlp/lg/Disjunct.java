package org.aigents.nlp.lg;

import java.util.*;
import java.io.*;

public class Disjunct {
	String disjunct, classname;
	double cost;
	
	public Disjunct(String disjunct, String classname, double cost) {
		this.disjunct = disjunct;
		this.classname = classname;
		this.cost = cost;
	}
}
