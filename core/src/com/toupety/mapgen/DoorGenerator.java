package com.toupety.mapgen;

import com.toupety.mapgen.RoomBlocks.RoomWall;

public class DoorGenerator {

	public void generate(Level level, Room newRoom) {
		//pega todas as salas adjacentes e tenta criar portas
		
		int x = newRoom.getX() + newRoom.getWidth();
		int y = newRoom.getY() + newRoom.getHeight()/2;
		
		Room target = level.getAt(x, y);
		
		int size = target.getY() - newRoom.getY();
		
//		target.createDoor(x,y, Direction.DOWN);
		
		RoomBlocks grid = target.getGrid();
		RoomWall wall = grid.getRightWall();
		
		wall.createDoor(5, 3, Direction.DOWN);
		
		System.out.println("x");
	}
	
//	Direction getDirection(Room source, Room target) {
//		Direction ret;
//		
//		if(source.getWidth()) {
//			
//		}
//		
//		return ret;
//	}
}
