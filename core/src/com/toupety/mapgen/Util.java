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
	
	static Dimmensions convertDimmensions(Room element) {
		return new Dimmensions(element.getX(), element.getY(), element.getWidth(), element.getHeight());
	}
	
	static Dimmensions convertDimmensions(Level element) {
		return new Dimmensions(0, 0, element.getWidth(), element.getHeight());
	}	
}
