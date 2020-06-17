/*******************************************************************
 * Disjunct.java                                                   *
 * Stores the connectors and cost of a disjunct part of the        *
 * Link Grammar dictionary.                                        *
 *                                                                 *
 * Written by Vignav Ramesh                                        *
 *******************************************************************/

package org.aigents.nlp.lg;

import java.util.ArrayList;

public class Disjunct {
	private ArrayList<String> connectors;
	private double cost;
	
	public Disjunct(ArrayList<String> connectors, double cost) {
		this.connectors = connectors;
		this.cost = cost;
	}
	
	public Disjunct() {
		connectors = new ArrayList<>();
		cost = 0;
	}
	
	public void addConnector(String s) {
		connectors.add(s);
	}
	
	public ArrayList<String> getConnectors() {	return connectors;	}
		
	public double getCost() {	return cost;	}
	
	@Override
	public String toString() {
		return connectors.toString();
	}
}
