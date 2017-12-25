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
		
		int lx = (GeneratorConstants.WIDTH * GeneratorConstants.LEVEL_BLOCK_WIDTH) - (x * GeneratorConstants.LEVEL_BLOCK_WIDTH);
	    int ly = y * GeneratorConstants.LEVEL_BLOCK_HEIGHT;
	    int lw = w * GeneratorConstants.LEVEL_BLOCK_WIDTH;
	    int lh = h * GeneratorConstants.LEVEL_BLOCK_HEIGHT;
		
		return new Dimensions(lx - lw, ly, lw, lh);
	}
	
	public Dimensions toInvertedRoomWorldDimmensions() {
		
		int lx = x * GeneratorConstants.LEVEL_BLOCK_WIDTH;
	    int ly = y * GeneratorConstants.LEVEL_BLOCK_HEIGHT;
	    int lw = w * GeneratorConstants.LEVEL_BLOCK_WIDTH;
	    int lh = h * GeneratorConstants.LEVEL_BLOCK_HEIGHT;
		
		return new Dimensions(lx, ly, lw, lh);
	}	
}
