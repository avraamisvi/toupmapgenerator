package com.toupety.mapgen;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class Level {
	int height;
	int width;
	ConcurrentMap<String, Room> rooms;
	
	public Level(int height, int width) {
		this.height = height;
		this.width = width;
		this.rooms = new ConcurrentHashMap<>();
	}
	
	public void addRoom(Room room) {
		rooms.putIfAbsent(room.id, room);
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
}
