package com.toupety.mapgen;

@Deprecated
public class RoomBlockPath {

	private Direction direction;
	private int length = 6;
	
	public RoomBlockPath(Direction direction, int length) {
		super();
		this.direction = direction;
		this.length = length;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getLength() {
		return length;
	}
}
