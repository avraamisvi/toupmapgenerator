package com.toupety.mapgen;

public class Configuration {

	public static boolean invert = false;
	
	public static int getLevelGridElementContentSize() {
		return (GeneratorConstants.LEVEL_BLOCK_HEIGHT / GeneratorConstants.ROOM_BLOCK_SIZE);
	}
}
