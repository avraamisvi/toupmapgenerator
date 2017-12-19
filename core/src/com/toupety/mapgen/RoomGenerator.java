package com.toupety.mapgen;

import java.util.Random;

public class RoomGenerator {
	
	private int minRoomHeight;
	private int minRoomWidth;
	private int maxRoomHeight;
	private int maxRoomWidth;
	private int maxRooms;
	private int maxIterations;	
	
	public RoomGenerator(int maxRoomWidth, 
						int maxRoomHeight, 
						int minRoomWidth,
						int minRoomHeight,
						int maxRooms,
						int maxIterations
						) {
		super();
		this.maxRoomHeight = maxRoomHeight;
		this.maxRoomWidth = maxRoomWidth;
		this.minRoomHeight = minRoomHeight;
		this.minRoomWidth = minRoomWidth;
		this.maxRooms = maxRooms;
		this.maxIterations = maxIterations;
	}

	public void generate(Level level) {
		Random ran = new Random();
		
		while(level.size() < maxRooms) {
			
			if(maxIterations == 0) {
				break;
			}
			
			int h = ran.nextInt(maxRoomHeight);
			int w = ran.nextInt(maxRoomWidth);
			
			if(level.size() > 0) {				
				
				int x = ran.nextInt(level.getWidth());
				int y = ran.nextInt(level.getHeight());
				
//				if(w >= minRoomWidth && h >= minRoomHeight) {
					level.addRoom(new Room(w, h, x, y));
//				}
				
			} else {
				level.addRoom(new Room(w, h, 0, 0));
			}
			
			maxIterations--;
		}
	}
}
