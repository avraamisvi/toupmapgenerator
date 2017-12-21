package com.toupety.mapgen;

import java.util.Optional;

import com.toupety.mapgen.algorithms.RoomDimmensionsAlgorithm;

public class LevelGenerator {
	
	private RoomDimmensionsAlgorithm algorithm;
	
	public LevelGenerator(RoomDimmensionsAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void generate(Level level) {
		
		int colorid = 0;
		
		while(true) {

			Optional<Dimensions> op = algorithm.next(level);
			
			if(op.isPresent()) {
				Dimensions dim = op.get();
				if(level.size() > 0) {				
					Room rom = new Room(dim.getW(), dim.getH(), dim.getX(), dim.getY());
					rom.setIndex(level.size());
					level.addRoom(rom);
					
				} else {
					Room rom = new Room(dim.getW(), dim.getH(), 0, 0);
					rom.setIndex(level.size());
					level.addRoom(rom);
				}

			} else {
				break;
			}
			
			colorid++;
			if(colorid >= 5) {
				colorid = 0;
			}
		}
	}
}
