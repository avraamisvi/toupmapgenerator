package com.toupety.mapgen.drawner;

import java.util.Optional;

import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;

public class LevelScreenDrawner implements ElementDrawner<Level> {
	
	public void draw(Level level) {
		Optional<ElementDrawner<Room>> drawner = DrawnerFactory
				.instance()
				.getElementDrawner(Room.class);
		
		drawner.ifPresent(d -> {
			level.stream().forEach(r -> {				
				d.draw(r);
			});			
		});
	}
}
