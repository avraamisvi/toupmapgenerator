package com.toupety.mapgen;

public class LevelGrid {
	
	private LevelBlock[][] grid;
	private int w, h;
	
	public LevelGrid(int w, int h) {
		
		this.w = w;
		this.h= h;
		
		this.grid = new LevelBlock[w][h];
		
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				this.grid[x][y] = new LevelBlock(x, y);
				
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
		
		if(this.grid[0].length <= (room.getY() + room.getHeight())) {
			return false;
		}
		
		if(this.grid.length <= (room.getX() + room.getWidth())) {
			return false;
		}		
		
		return this.grid[room.getX()][room.getY()].fit(room);
	}
	
	private void configureLeft(LevelBlock el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.grid[x][y] != null) {
			el.left = this.grid[x][y];
			this.grid[x][y].right = el;
		}
	}
	
	private void configureRight(LevelBlock el, int x, int y) {
		x = x + 1;
		if(x < w && this.grid[x][y] != null) {
			el.right = this.grid[x][y];
			this.grid[x][y].left = el;
		}
	}	
	
	private void configureUp(LevelBlock el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.grid[x][y] != null) {
			el.up = this.grid[x][y];
			this.grid[x][y].down = el;
		}
	}	
	
	private void configureDown(LevelBlock el, int x, int y) {
		y = y + 1;
		if(y < h && this.grid[x][y] != null) {
			el.down = this.grid[x][y];
			this.grid[x][y].up = el;
		}
	}

	public void configureAdjacentRooms() {
		for(int ly = 0; ly < h; ly++) {
			for(int lx = 0; lx < w; lx++) {
				
				if(this.grid[lx][ly].roomOwner == null)
					continue;
				
				if(this.grid[lx][ly].up != null && this.grid[lx][ly].up.roomOwner != null ) {
					if(this.grid[lx][ly].up.roomOwner != this.grid[lx][ly].roomOwner ) { //TOP
						this.grid[lx][ly].roomOwner.addTop(this.grid[lx][ly].up.roomOwner);
					}
				}
				
				if(this.grid[lx][ly].down != null && this.grid[lx][ly].down.roomOwner != null ) {
					if(this.grid[lx][ly].down.roomOwner != this.grid[lx][ly].roomOwner ) { //TOP
						try {
							this.grid[lx][ly].roomOwner.addBottom(this.grid[lx][ly].down.roomOwner);
						} catch (Exception e) {
							System.out.println("this.grid[lx][ly].roomOwner: " + this.grid[lx][ly].roomOwner);
						}
					}
				}				
				
				if(this.grid[lx][ly].left != null && this.grid[lx][ly].left.roomOwner != null ) {
					if(this.grid[lx][ly].left.roomOwner != this.grid[lx][ly].roomOwner ) { //TOP
						this.grid[lx][ly].roomOwner.addLeft(this.grid[lx][ly].left.roomOwner);
					}
				}
				
				if(this.grid[lx][ly].right != null && this.grid[lx][ly].right.roomOwner != null ) {
					if(this.grid[lx][ly].right.roomOwner != this.grid[lx][ly].roomOwner ) { //TOP
						this.grid[lx][ly].roomOwner.addRight(this.grid[lx][ly].right.roomOwner);
					}
				}				
				
			}
		}
	}
	
	class LevelBlock {
		
		int x, y;
		
		LevelBlock up;
		LevelBlock down;
		LevelBlock left;
		LevelBlock right;

		private Room roomOwner;
		
		public LevelBlock(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public boolean fit(Room room) {
			
			int w = room.getWidth(); 
			int h = room.getHeight();
			
			LevelBlock cell = this;
			LevelBlock line = this;
			
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
						cell.roomOwner = room;
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
		
		@Deprecated
		public void checkNextRoom(Room target, int x, int y) {//TODO melhorar

			LevelBlock el = null; 
			
			if(x - 1 >= 0) {//left
				el = LevelGrid.this.grid[x-1][y];
				
				if(el != null && el.roomOwner != null) {
					if(el.roomOwner != target) {
						target.addLeft(el.roomOwner);
						el.roomOwner.addRight(target);
					}
				}
			}
			
			if(x + 1 < LevelGrid.this.grid.length) {//right
				el = LevelGrid.this.grid[x+1][y];
				
				if(el != null && el.roomOwner != null) {
					if(el.roomOwner != target) {
						target.addRight(el.roomOwner);
						el.roomOwner.addLeft(target);
					}
				}
			}
			
			if(y - 1 >= 0) {//top
				el = LevelGrid.this.grid[x][y-1];
				
				if(el != null && el.roomOwner != null) {
					if(el.roomOwner != target) {
						target.addTop(el.roomOwner);
						el.roomOwner.addBottom(target);
					}
				}
			}
			
			if(y + 1 < LevelGrid.this.grid[0].length) {//bottom
				el = LevelGrid.this.grid[x][y+1];
				
				if(el != null && el.roomOwner != null) {
					if(el.roomOwner != target) {
						target.addBottom(el.roomOwner);
						el.roomOwner.addTop(target);
					}
				}
			}			
		}
		
		public boolean isUsed() {
			return roomOwner != null;
		}
	}
	
	public Room getAt(int x, int y) {
		return this.grid[x][y].roomOwner;
	}
}
