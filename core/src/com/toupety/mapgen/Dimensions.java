package com.toupety.mapgen;

public class Dimensions {

	private int x, y, w, h;

	public Dimensions(int x, int y, int w, int h) {
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
	//FIX corrigir isso para aceitar dimensoes para blocos menores das salas
	public Dimensions toRoomWorldDimmensions() {
		
		int lx = (Constants.WIDTH * Constants.LEVEL_BLOCK_WIDTH) - (x * Constants.LEVEL_BLOCK_WIDTH);
	    int ly = y * Constants.LEVEL_BLOCK_HEIGHT;
	    int lw = w * Constants.LEVEL_BLOCK_WIDTH;
	    int lh = h * Constants.LEVEL_BLOCK_HEIGHT;
		
		return new Dimensions(lx - lw, ly, lw, lh);
	}
	
	public Dimensions toInvertedRoomWorldDimmensions() {
		
		int lx = x * Constants.LEVEL_BLOCK_WIDTH;
	    int ly = y * Constants.LEVEL_BLOCK_HEIGHT;
	    int lw = w * Constants.LEVEL_BLOCK_WIDTH;
	    int lh = h * Constants.LEVEL_BLOCK_HEIGHT;
		
		return new Dimensions(lx, ly, lw, lh);
	}	
}
