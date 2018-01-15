package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.badlogic.gdx.math.RandomXS128;

public class Level {
	private int height;
	private int width;
	private LinkedHashMap<String, Room> rooms;
	private List<Room> roomsList;
	private List<String> ids;
	private RandomXS128 rand;
	private LevelGrid grid;
	//960x960
	
	public Level(int width, int height) {
		this.height = height;
		this.width = width;
		this.rooms = new LinkedHashMap<>();
		this.roomsList = new ArrayList<>();
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
		
		synchronized (this) {
			if(grid.addRoom(room)) {			
				this.ids.add(room.getId());
				rooms.put(room.getId(), room);
				roomsList.add(room);
			}
		}
	}
	
	public Room getRoom(int pos) {
		return roomsList.get(pos);
	}
	
	public void forEach(Consumer<Room> consumer) {
//		return rooms.values().stream();
		synchronized (this) {
			rooms.values().forEach(consumer);
		}
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
	
	public Room getAny() {
		int idx = 0;
		if(this.ids.size() > 0) {
			idx = rand.nextInt(this.ids.size());
		}
		
		if(this.ids.size() == 0) {
			return null;
		}
		
		return rooms.get(ids.get(idx));
	}
	
	public Room getLast() {
		if(ids.size() > 0)
			return rooms.get(ids.get(ids.size() - 1));
		else
			return null;
	}
	
//	public Optional<Room> getAt(int wx, int wy) {
//		//TODO otimizar, fazer as salas saberem quem esta ao seu lado, tipo, left, right, top, down, etc isso ira facilitar a criacao das portas 
//		synchronized (this) {
//			return rooms.values().stream().filter( r -> {
//				return r.containsWorldPoint(wx, wy);
//			}).findFirst();
//		}
//	}	
}
