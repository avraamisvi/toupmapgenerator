package com.toupety.mapgen.algorithms;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;

public class BoundedRoomAlgorithm implements RoomAlgorithm {

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
	
	public BoundedRoomAlgorithm(Map<String, Object> config) {//TODO mudar tudo para o arquivo de config
		this.maxRoomWidth = Integer.parseInt(config.get(MAX_ROOM_WIDTH).toString());
		this.maxRoomHeight = Integer.parseInt(config.get(MAX_ROOM_HEIGHT).toString());
		this.minRoomWidth = Integer.parseInt(config.get(MIN_ROOM_WIDTH).toString());
		this.minRoomHeight = Integer.parseInt(config.get(MIN_ROOM_HEIGHT).toString());
		this.maxRooms = Configuration.properties.rooms;//Integer.parseInt(config.get(MAX_ROOMS).toString());
		this.maxIterations = Integer.parseInt(config.get(MAX_ITERATIONS).toString());
	}
	
	@Override
	public Optional<RoomAlgorithmResult> next(Level level) {
		
		Dimensions dim = null;
		RoomAlgorithmResult res = null;
		
		if(maxIterations > 0 && level.size() < maxRooms) {
		
			Room any = level.getAny();

			int h = 0;
			int w = 0;
		
			int x = 0;
			int y = 0;
			
			h = ran.nextInt(maxRoomHeight);
			w = ran.nextInt(maxRoomWidth);
			
			if(any != null) {
				
				List<Vector2> points = any.getClosePoints();
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
			res = new RoomAlgorithmResult(any, dim);
		}
		
		return Optional.ofNullable(res);
	}

}
