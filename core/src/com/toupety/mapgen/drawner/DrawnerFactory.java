package com.toupety.mapgen.drawner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.toupety.mapgen.Level;

public class DrawnerFactory {

	Map<String, ElementDrawner<?>> elements;
	
	public DrawnerFactory() {
		elements = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> Optional<ElementDrawner<T>> getElementDrawner(Class<T> clazz) {
		Optional<ElementDrawner<T>> ret = Optional.empty();
		
		if(elements.containsKey(clazz.getName())) {
			ElementDrawner<T> ele = (ElementDrawner<T>) elements.get(clazz.getName());
			ret = Optional.ofNullable(ele);
		} else {
			elements.put(clazz.getName(), createDrawner(clazz));
		}
		
		return ret;
	}

	private <T> ElementDrawner<T> createDrawner(Class<T> clazz) {
		ElementDrawner<T> ret = null;
		
		if(clazz.equals(Level.class)) {
			ret = new ElementDrawner<T>() {
				LevelScreenDrawner dranwer = new LevelScreenDrawner();
				@Override
				public void draw(T element) {
					draw(element);
				}
			};
		}
		
		return ret;
	}
	
}
