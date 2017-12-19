package com.toupety.mapgen.algorithms;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.toupety.mapgen.Dimmensions;
import com.toupety.mapgen.Level;

public class BoundedRoomDimmensionsAlgorithm implements RoomDimmenstionsAlgorithm {

	public static final String MAX_ROOM_WIDTH = "MAX_ROOM_WIDTH";
	public static final String MAX_ROOM_HEIGHT = "MAX_ROOM_HEIGHT";
	public static final String MIN_ROOM_WIDTH = "MIN_ROOM_WIDTH";
	public static final String MIN_ROOM_HEIGHT = "MIN_ROOM_HEIGHT";
	public static final String MAX_ROOMS 	   = "MAX_ROOMS";
	public static final String MAX_ITERATIONS  = "MAX_ITERATIONS";
	
	private int maxRoomWidth;
	private int maxRoomHeight; 
	private int minRoomWidth;
	private int minRoomHeight;
	private int maxRooms;
	private int maxIterations;
	
	private Random ran = new Random();
	
	public BoundedRoomDimmensionsAlgorithm(Map<String, Object> config) {
		this.maxRoomWidth = Integer.parseInt(config.get(MAX_ROOM_WIDTH).toString());
		this.maxRoomHeight = Integer.parseInt(config.get(MAX_ROOM_HEIGHT).toString());
		this.minRoomWidth = Integer.parseInt(config.get(MIN_ROOM_WIDTH).toString());
		this.minRoomHeight = Integer.parseInt(config.get(MIN_ROOM_HEIGHT).toString());
		this.maxRooms = Integer.parseInt(config.get(MAX_ROOMS).toString());
		this.maxIterations = Integer.parseInt(config.get(MAX_ITERATIONS).toString());
	}
	
	@Override
	public Optional<Dimmensions> next(Level level) {
		
		Dimmensions dim = null;
		
		if(maxIterations > 0 && level.size() < maxRooms) {
		
			int h = ran.nextInt(maxRoomHeight);
			int w = ran.nextInt(maxRoomWidth);
		
			int x = ran.nextInt(level.getWidth());
			int y = ran.nextInt(level.getHeight());
			
			if(w < this.minRoomWidth) {
				w = this.minRoomWidth;
			}
			
			if(h < this.minRoomHeight) {
				h = this.minRoomHeight;
			}			
			
			maxIterations--;
			
			dim = new Dimmensions(x, y, w, h);
		}
		
		return Optional.ofNullable(dim);
	}

}
