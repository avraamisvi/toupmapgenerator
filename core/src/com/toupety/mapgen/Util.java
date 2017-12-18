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
}
