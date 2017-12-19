package com.toupety.mapgen;

public class Dimmensions {

	private int x, y, w, h;

	public Dimmensions(int x, int y, int w, int h) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getW() {
		return w;
	}


	public int getH() {
		return h;
	}
	
	public Dimmensions toWorldDimmensions() {
		
		int lx = x * Constants.LEVEL_BLOCK_WIDTH;
	    int ly = y * Constants.LEVEL_BLOCK_HEIGHT;
	    int lw = w * Constants.LEVEL_BLOCK_WIDTH;
	    int lh = h * Constants.LEVEL_BLOCK_HEIGHT;
		
		return new Dimmensions(lx - lw, ly, lw, lh);
	}
}
