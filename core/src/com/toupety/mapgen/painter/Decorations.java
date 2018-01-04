package com.toupety.mapgen.painter;

import java.util.HashMap;

public class Decorations {
	private HashMap<String, Decoration> elements;
	
	public void setElements(HashMap<String, Decoration> elements) {
		this.elements = elements;
	}
	
	public HashMap<String, Decoration> getElements() {
		return elements;
	}
	
	public Decoration getDecoration(String key) {
		if(elements.containsKey(key))
			return elements.get(key).copy();
		else
			return null;
	}
}
