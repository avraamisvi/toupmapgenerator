package com.toupety.mapgen;

public interface Util {

	static int[] normalizePoints(int x, int y) {
		return new int[] {
				normalize(x)
				,normalize(y)};
	}
	
	static int normalize(int val) {
		return (val%64) + val;
	}
	
	static Dimensions convertDimmensions(int x, int y, int w, int h) {
		return new Dimensions(x, y, w, h);
	}
	
	static Dimensions convertDimmensions(Room element) {
		return new Dimensions(element.getX(), element.getY(), element.getWidth(), element.getHeight());
	}
	
	static Dimensions convertDimmensions(Level element) {
		return new Dimensions(0, 0, element.getWidth(), element.getHeight());
	}	
}
