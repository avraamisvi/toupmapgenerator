package com.toupety.mapgen;

import java.util.function.Consumer;

import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;

public class RoomBlocks {
	
	private RoomBlock[][] grid;
	private int w, h;
	
	public RoomBlocks() {
		
		this.w = Constants.LEVEL_BLOCK_WIDTH / Constants.ROOM_BLOCK_SIZE;
		this.h= Constants.LEVEL_BLOCK_HEIGHT / Constants.ROOM_BLOCK_SIZE;
		
		this.grid = new RoomBlock[w][h];
		
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				this.grid[x][y] = new RoomBlock(x, y);
				
				configureDown(this.grid[x][y], x, y);
				configureUp(this.grid[x][y], x, y);
				configureLeft(this.grid[x][y], x, y);
				configureRight(this.grid[x][y], x, y);
			}			
		}
	}
	
	public boolean isUsed(int x, int y) {
		return this.grid[x][y].isUsed();
	}
	
	public synchronized boolean put(Mold room) {
		return this.grid[0][0].fit(room);
	}
	
	private void configureLeft(RoomBlock el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.grid[x][y] != null) {
			el.left = this.grid[x][y];
			this.grid[x][y].right = el;
		}
	}
	
	private void configureRight(RoomBlock el, int x, int y) {
		x = x + 1;
		if(x < w && this.grid[x][y] != null) {
			el.right = this.grid[x][y];
			this.grid[x][y].left = el;
		}
	}	
	
	private void configureUp(RoomBlock el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.grid[x][y] != null) {
			el.up = this.grid[x][y];
			this.grid[x][y].down = el;
		}
	}	
	
	private void configureDown(RoomBlock el, int x, int y) {
		y = y + 1;
		if(y < h && this.grid[x][y] != null) {
			el.down = this.grid[x][y];
			this.grid[x][y].up = el;
		}
	}
	
	public void forEach(Consumer<RoomBlock> c) {
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				c.accept(this.grid[x][y]);
			}			
		}		
	}
//	
//	public int getColor(int x, int y) {//TODO remove this
//		if(grid[x][y] != null && grid[x][y].owner != null)
//			return grid[x][y].owner.colorid;
//		else
//			return 0;
//	}
	
	public class RoomBlock {
		
		int x, y;
		
		RoomBlock up;
		RoomBlock down;
		RoomBlock left;
		RoomBlock right;

		private MoldBlock owner;
		
		public RoomBlock(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public boolean fit(Mold mold) {
			
			int w = mold.getWidth(); 
			int h = mold.getHeight();
			
			mold
			.stream()
			.forEach(bl -> {
				grid[bl.getX()][bl.getY()].owner = bl;
			});
			
			return true;
		}
		
		public MoldBlock getOwner() {
			return owner;
		}
		
		public boolean isUsed() {
			return owner != null;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
}
