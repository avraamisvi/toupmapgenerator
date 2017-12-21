package com.toupety.mapgen.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;

public class BoundedRoomDimmensionsAlgorithm implements RoomDimmensionsAlgorithm {

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
	public Optional<Dimensions> next(Level level) {
		
		Dimensions dim = null;
		
		if(maxIterations > 0 && level.size() < maxRooms) {
		
			Room last = level.getAny();

			int h = 0;
			int w = 0;
		
			int x = 0;
			int y = 0;
			
			h = ran.nextInt(maxRoomHeight);
			w = ran.nextInt(maxRoomWidth);
			
			if(last != null) {
				
				List<Vector2> points = last.getClosePoints();
				Vector2 point = points.get(ran.nextInt(points.size()));
				
				x = (int) point.x;
				y = (int) point.y;
				
			} else {
				x = ran.nextInt(level.getWidth());
				y = ran.nextInt(level.getHeight());
			}
			
			if(w < this.minRoomWidth) {
				w = this.minRoomWidth;
			}
			
			if(h < this.minRoomHeight) {
				h = this.minRoomHeight;
			}			
			
			if(h == this.minRoomHeight) {
				if(w == this.minRoomWidth) {
					w = this.minRoomWidth * 2;
				}
			}
			
			if(w == this.minRoomWidth) {
				if(h == this.minRoomHeight) {
					h = this.minRoomHeight * 2;
				}
			}
			
			maxIterations--;
			
			dim = new Dimensions(x, y, w, h);
		}
		
		return Optional.ofNullable(dim);
	}

}
