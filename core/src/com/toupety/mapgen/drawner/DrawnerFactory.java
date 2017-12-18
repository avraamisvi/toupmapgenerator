package com.toupety.mapgen.drawner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;

public class DrawnerFactory {

	private static DrawnerFactory inst;
	private Map<String, ElementDrawner<?>> elements;
	
	private DrawnerFactory() {
		elements = new HashMap<>();
	}
	
	public static DrawnerFactory instance() {
		synchronized (DrawnerFactory.class) {
			
			if(inst == null) {
				inst = new DrawnerFactory();
			}
			
			return inst;
		}
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
					dranwer.draw((Level) element);
				}
			};
		} else if(clazz.equals(Room.class)) {
			ret = new ElementDrawner<T>() {
				RoomScreenDrawner dranwer = new RoomScreenDrawner();
				@Override
				public void draw(T element) {
					dranwer.draw((Room) element);
				}
			};
		} 
		
		return ret;
	}
	
}
