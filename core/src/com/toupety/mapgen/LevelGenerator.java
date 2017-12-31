package com.toupety.mapgen;

import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration.Item;
import com.toupety.mapgen.Configuration.Key;
import com.toupety.mapgen.Configuration.Tag;
import com.toupety.mapgen.algorithms.RoomAlgorithm;
import com.toupety.mapgen.algorithms.RoomAlgorithmResult;

public class LevelGenerator {
	
	private RoomAlgorithm algorithm;
	private DoorGenerator doorGen;
	private RoomPathGenerator pathGen;
	RandomXS128 rand = new RandomXS128();
	
	
	public LevelGenerator(RoomAlgorithm algorithm) {
		this.algorithm = algorithm;
		this.doorGen = new DoorGenerator();
//		pathGen = new RoomPathGenerator();
	}

	public void generate(Level level) {
		rand.nextInt();rand.nextInt();
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
		
		level.forEach(room -> {
			applyItems(level, room);
			applyKey(level, room);
			applyTags(level, room);
		});
		
		level.forEach(room -> {
			room.processItems();
		});
		
//		pathGen.generate(level);
	}
	
	public void generatePaths(Level level) {
//		level.forEach(room -> room.getGrid().createCave());
		level.forEach(room -> room.getGrid().createPath());
	}
	
	void applyItems(Level level, Room room) {
		
		List<Item> items = Configuration.properties.items;
		
		items.forEach(it -> {
			if(it.isAvaiable()) {
				if(it.from <= room.getIndex()) {
					if(rand.nextInt() < it.chance) {
						room.getItems().add(it);
					}
				}
			}
		});
	}
	
	void applyTags(Level level, Room room) {
		List<Tag> items = Configuration.properties.tags;
		
		items.forEach(it -> {
			if(it.isAvaiable()) {
				if(it.room <= room.getIndex()) {
					if(room.containsAdjacentRoomIndex(it.room) || it.room == room.getIndex()) {
						room.getTags().add(it);
					}
				}
			}
		});		
	}	
	
	void applyKey(Level level, Room room) {
		Key key = Configuration.properties.key;
		
		if(key.isAvaiable()) {
			if(key.room <= room.getIndex()) {
				room.setKey(key);
			}
		}
	}	
}
