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
	
	public String getDisjunct() {	return disjunct;	}
	
	public String getClassname() {	return classname;	}
	
	public double getCost() {	return cost;	}
}
