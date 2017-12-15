package com.toupety.mapgen;

import java.util.Random;

public class RoomGenerator {
	
	private int minRoomHeight;
	private int minRoomWidth;
	private int maxRoomHeight;
	private int maxRoomWidth;
	private int maxRooms;	
	
	public RoomGenerator(int maxRoomHeight, 
						int maxRoomWidth, 
						int maxRooms,
						int minRoomHeight, 
						int minRoomWidth						
						) {
		super();
		this.maxRoomHeight = maxRoomHeight;
		this.maxRoomWidth = maxRoomWidth;
		this.minRoomHeight = minRoomHeight;
		this.minRoomWidth = minRoomWidth;
		this.maxRooms = maxRooms;
	}

	public void generate(Level level) {
		Random ran = new Random();
				
		while(level.size() < maxRooms) {
			int h = ran.nextInt(maxRoomHeight);
			int w = ran.nextInt(maxRoomWidth);
			
			int x = ran.nextInt(level.getWidth() - w);
			int y = ran.nextInt(level.getHeight() - h);
			
			if(w > minRoomWidth && h > minRoomHeight) {
				level.addRoom(new Room(h, w, x, y));
			}
		}
	}
}
