package com.toupety.mapgen;

public class Point {
	public int x, y;
	
	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point copy() {
		return new Point(x, y);
	}
}
