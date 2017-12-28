package com.toupety.mapgen;

import java.util.Optional;

import com.toupety.mapgen.algorithms.RoomAlgorithm;
import com.toupety.mapgen.algorithms.RoomAlgorithmResult;

public class LevelGenerator {
	
	private RoomAlgorithm algorithm;
	private DoorGenerator doorGen;
	private RoomPathGenerator pathGen;
	
	public LevelGenerator(RoomAlgorithm algorithm) {
		this.algorithm = algorithm;
		this.doorGen = new DoorGenerator();
//		pathGen = new RoomPathGenerator();
	}

	public void generate(Level level) {
		
		int colorid = 0;
		
		while(true) {

			Optional<RoomAlgorithmResult> op = algorithm.next(level);
			
			if(op.isPresent()) {
				RoomAlgorithmResult result = op.get();
				Dimensions dim = result.getDim();
				if(level.size() > 0) {				
					Room room = new Room(dim);
					room.setIndex(level.size());
					level.addRoom(room);
					
//					doorGen.generate(level, result.getTarget());//TODO maybe should be the new room
//					doorGen.generate(level, room);
					
				} else {
					Dimensions dim2 = new Dimensions(0, 0, dim.getW(), dim.getH());
					Room room = new Room(dim2);
					room.setIndex(level.size());
					level.addRoom(room);
				}

			} else {
				break;
			}
			
			colorid++;
			if(colorid >= 5) {
				colorid = 0;
			}
		}
		
		level.getGrid().configureAdjacentRooms();
		level.forEach(room -> doorGen.generate(level, room));
//		pathGen.generate(level);
	}
	
	public void generatePaths(Level level) {
		level.forEach(room -> room.getGrid().createCave());
	}
}
