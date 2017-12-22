package com.toupety.mapgen;

public class DoorGenerator {

	public void generate(Level level, Room newRoom) {
		//pega todas as salas adjacentes e tenta criar portas
		
		int x = newRoom.getX() + newRoom.getWidth();
		int y = newRoom.getY() + newRoom.getHeight()/2;
		
		Room target = level.getAt(x, y);
		
		int size = target.getY() - newRoom.getY();
		
		target.createDoor( ,y);
		System.out.println("x");
	}
	
	Direction getDirection(Room newRoom, Room target) {
		
	}
}
