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
			
			int h = Util.normalize(ran.nextInt(maxRoomHeight));
			int w = Util.normalize(ran.nextInt(maxRoomWidth));
			
			if(level.size() == 0) {				
				
				int x = Util.normalize(ran.nextInt(level.getWidth() - w));
				int y = Util.normalize(ran.nextInt(level.getHeight() - h));
				
				if(w > minRoomWidth && h > minRoomHeight) {
					level.addRoom(new Room(h, w, x, y));
				}
				
			} else {
				level.addRoom(new Room(h, w, 0, 0));
			}
			
		}
	}
}
