package com.toupety.mapgen;

import java.util.Optional;

import com.toupety.mapgen.algorithms.RoomAlgorithm;
import com.toupety.mapgen.algorithms.RoomAlgorithmResult;

public class LevelGenerator {
	
	private RoomAlgorithm algorithm;
	private DoorGenerator doorGen;
	
	public LevelGenerator(RoomAlgorithm algorithm) {
		this.algorithm = algorithm;
		this.doorGen = new DoorGenerator();
	}

	public void generate(Level level) {
		
		int colorid = 0;
		
		while(true) {

			Optional<RoomAlgorithmResult> op = algorithm.next(level);
			
			if(op.isPresent()) {
				RoomAlgorithmResult result = op.get();
				Dimensions dim = result.getDim();
				if(level.size() > 0) {				
					Room rom = new Room(dim);
					rom.setIndex(level.size());
					level.addRoom(rom);
					
					doorGen.generate(level, result.getTarget());//TODO maybe should be the new room
					
				} else {
					Dimensions dim2 = new Dimensions(0, 0, dim.getW(), dim.getH());
					Room rom = new Room(dim2);
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
