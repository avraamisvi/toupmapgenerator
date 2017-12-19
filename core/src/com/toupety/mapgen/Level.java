package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import com.badlogic.gdx.math.RandomXS128;

public class Level {
	private int height;
	private int width;
	private Map<String, Room> rooms;
	private List<String> ids;
	private RandomXS128 rand;
	private LevelGrid grid;
	//960x960
	
	public Level(int width, int height) {
		this.height = height;
		this.width = width;
		this.rooms = new HashMap<>();
		this.ids = new ArrayList<>();
		this.rand = new RandomXS128();
		
		grid = new LevelGrid(width, height);
		
	}
	
//	public void addRoom(Room room) {
//		
////		if(rooms.size() == 0) {
////			this.add(room);
////		} else {
////			if(getOne()
////			   .insertIntoPositionOrAnyFree(rand
////					   .nextInt(Constants.MAX_ROOM_SIDES), room)) {
////				this.add(room);	
////			}
////		}
//	}
	
	public LevelGrid getGrid() {
		return grid;
	}
	
	public void addRoom(Room room) {
		
		if(grid.addRoom(room)) {			
			this.ids.add(room.getId());
			rooms.put(room.getId(), room);
		}
		
	}
	
	public Stream<Room> stream() {
		return rooms.values().stream();
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int size() {
		return rooms.size();
	}
	
	public Room getOne() {
		int idx = 0;
		if(this.ids.size() > 0) {
			idx = rand.nextInt(this.ids.size());
		}
		return rooms.get(ids.get(idx));
	}
}
