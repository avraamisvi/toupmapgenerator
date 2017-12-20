package com.toupety.mapgen;

public class LevelGrid {
	
	private GridElement[][] grid;
	private int w, h;
	
	public LevelGrid(int w, int h) {
		
		this.w = w;
		this.h= h;
		
		this.grid = new GridElement[w][h];
		
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				this.grid[x][y] = new GridElement(x, y);
				
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
	
	public synchronized boolean addRoom(Room room) {
		return this.grid[room.getX()][room.getY()].fit(room);
	}
	
	private void configureLeft(GridElement el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.grid[x][y] != null) {
			el.left = this.grid[x][y];
			this.grid[x][y].right = el;
		}
	}
	
	private void configureRight(GridElement el, int x, int y) {
		x = x + 1;
		if(x < w && this.grid[x][y] != null) {
			el.right = this.grid[x][y];
			this.grid[x][y].left = el;
		}
	}	
	
	private void configureUp(GridElement el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.grid[x][y] != null) {
			el.up = this.grid[x][y];
			this.grid[x][y].down = el;
		}
	}	
	
	private void configureDown(GridElement el, int x, int y) {
		y = y + 1;
		if(y < h && this.grid[x][y] != null) {
			el.down = this.grid[x][y];
			this.grid[x][y].up = el;
		}
	}
	
	class GridElement {
		
		int x, y;
		
		GridElement up;
		GridElement down;
		GridElement left;
		GridElement right;

		private Room owner;
		
		public GridElement(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public boolean fit(Room room) {
			
			int w = room.getWidth(); 
			int h = room.getHeight();
			
			GridElement cell = this;
			GridElement line = this;
			
			//TODO CHANGE THIS CODE
			
			for(int ly = 0; ly < h; ly++) {
				
				for(int lx = 0; lx < w; lx++) {
					
					if(cell == null) {
						break;
					}
					
					if(cell.isUsed()) {
						return false;
					}
					
					cell = cell.right;
					
				}
				
				if(line != null) {
					line = line.down;
					cell = line;
				} else {
					break;
				}
			}
			
			//TODO optmize thread?
			cell = this;
			line = this;
			
			for(int ly = 0; ly < h; ly++) {
				
				for(int lx = 0; lx < w; lx++) {
					
					if(cell == null) {
						break;
					}
					
					if(!cell.isUsed()) {
						cell.owner = room;
					}
					
					cell = cell.right;
					
				}
				
				if(line != null) {
					line = line.down;
					cell = line;
				} else {
					break;
				}
			}			
			
			return true;
		}
		
		public boolean isUsed() {
			return owner != null;
		}
	}
}
