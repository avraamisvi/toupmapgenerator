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
	//960x960
	private GridElement[][] grid;
	
	public Level(int height, int width) {
		this.height = height;
		this.width = width;
		this.rooms = new HashMap<>();
		this.ids = new ArrayList<>();
		this.rand = new RandomXS128();
		
		this.createGrid();
		
	}
	
	private void createGrid() {
		this.grid = new GridElement[width][height];
		
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				this.grid[x][y] = new GridElement(x, y);
				
			}			
		}
	}
	
	public void configureLeft(GridElement el, int x, int y) {
		
	}
	
	public void addRoom(Room room) {
		
//		if(rooms.size() == 0) {
//			this.add(room);
//		} else {
//			if(getOne()
//			   .insertIntoPositionOrAnyFree(rand
//					   .nextInt(Constants.MAX_ROOM_SIDES), room)) {
//				this.add(room);	
//			}
//		}
	}
	
	private void add(Room room) {
		this.ids.add(room.getId());
		rooms.put(room.getId(), room);
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
	
	class GridElement {
		
		int x, y;
		
		GridElement up;
		GridElement down;
		GridElement left;
		GridElement right;
		
		public GridElement(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public int[] canFit(int w, int h) {
			return null;
		}
	}
}
