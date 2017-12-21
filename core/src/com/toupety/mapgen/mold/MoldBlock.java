package com.toupety.mapgen.mold;

public class MoldBlock {

	private char value;
	private int x, y;
	
	public MoldBlock(char value, int x, int y) {
		super();
		this.value = value;
		this.x = x;
		this.y = y;
	}

	public char getValue() {
		return value;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}
