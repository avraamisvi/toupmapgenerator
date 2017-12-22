package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;

public class RoomBlocks {
	
	private RoomBlock[][] grid;
	private int w, h;
	private Dimensions dim;
	
	private List<RoomBlock> path;
	private List<RoomBlock> doors;
	
	private RoomWall top;
	private RoomWall left;
	private RoomWall right;
	private RoomWall bottom;
	
	public RoomBlocks(Dimensions dim) {
		
		top = new RoomWall();
		left = new RoomWall();
		right = new RoomWall();
		bottom = new RoomWall();		
		
		this.w = dim.getW() * Constants.ROOM_BLOCK_SIZE;
		this.h = dim.getH() * Constants.ROOM_BLOCK_SIZE;
		
		this.dim = dim;
		
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
		
		this.fillWalls();
	}
	
	private void fillWalls() {
		
		//sides left
		for(int y = 0; y < this.h; y++) {
			left.add(this.grid[0][y]);
		}
		
		//sides right
		for(int y = 0; y < this.h; y++) {
			right.add(this.grid[this.w-1][y]);
		}
		
		//sides top
		for(int x = 0; x < this.w; x++) {
			top.add(this.grid[x][0]);
		}
		
		//sides bottom
		for(int x = 0; x < this.w; x++) {
			bottom.add(this.grid[x][this.h-1]);
		}		
		
	}
	
	public boolean isUsed(int x, int y) {
		return this.grid[x][y].isUsed();
	}
	
	public synchronized boolean put(Mold room) {
		return this.grid[0][0].fit(room);//TODO algoritmo precisa analisar e procurar regiões com path e ai aplica nessas regioes
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
	
	public Dimensions getDimensions() {
		return this.dim;
	}
	
	public void createDoor() {
		//cria as portas nos elementos conectados
		//------
	}
	
	public void connectDoors() {
		//conecta duas portas gerando um path, esse path pode ser esparramado
		boolean done = false;
		while(!done) {
			
		}
	}	
	
	public class RoomBlock {
		
		int x, y;
		
		RoomBlock up;
		RoomBlock down;
		RoomBlock left;
		RoomBlock right;
		
		boolean path;
		boolean door;
		boolean wall;
		
		
		private MoldBlock owner;
		
		public RoomBlock(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean fit(Mold mold) {
			
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
		
		public boolean isPath() {
			return path;
		}
		
		public boolean isDoor() {
			return door;
		}
		
		void setPath(boolean path) {
			this.path = path;
		}

		void setDoor(boolean door) {
			this.door = door;
		}
		
		void setWall(boolean wall) {
			this.wall = wall;
		}
		
		int countNextDown() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.wall) {
				count++;
				b = b.down;
			}
			
			return count;
		}
		
		int countNextUp() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.wall) {
				count++;
				b = b.up;
			}
			
			return count;
		}		
		
		int countNextRight() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.wall) {
				count++;
				b = b.right;
			}
			
			return count;
		}
		
		int countNextLeft() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.wall) {
				count++;
				b = b.left;
			}
			
			return count;
		}		
		
		RoomDoor makeDoor(Direction dir) {
			RoomDoor door = new RoomDoor();			
			RoomBlock b = this;
			for(int i = 0; i < Constants.DOOR_BLOCKS_SIZE; i++) {
				
				if(b == null)
					break;
				door.add(b);
				switch (dir) {
				case DOWN:
					b = b.down;
					break;
				case LEFT:
					b = b.left;
					break;
				case RIGHT:
					b = b.right;
					break;
				case UP:
					b = b.up;
					break;
				}
			}
			
			return door;
		}
	}
	
	public class RoomLocalPath {
		private List<RoomBlock> blocks;
		
		public void spread() {
			//gera caminhos alterantivos que nao tem o objetivo de chegar na porta
		}
		
		public boolean fit(Mold mold) {
//			RoomBlocks.this.put(mold);
//			
//			RoomBlock block = null;
////			RoomBlock block = blocks.stream().filter(b -> !b.isUsed()).findFirst();
//			
//			for(RoomBlock b : blocks) {
//				if()
//			}
//			
//			block.fit(mold);
			
			return false;
		}		
	}
	
	public class RoomDoor {
		private List<RoomBlock> blocks;
		
		public void add(RoomBlock b) {
			blocks.add(b);
			b.setDoor(true);
		}
	}
	
	public class RoomWall {
		private List<RoomBlock> blocks = new ArrayList<>();
		private List<RoomDoor> doors = new ArrayList<>();
		
		public void add(RoomBlock b) {
			b.setWall(true);
			blocks.add(b);
		}
		
		public boolean createDoor(int x, int y, Direction dir) {
			RoomBlock start = null;
			
			for(RoomBlock b : blocks) {
				if(b.getX() == x && b.getY() == y) {
					start = b;
					break;
				}
			}
			
			if(start!= null) {
				
				int count = countValid(start, dir);		
					
				if(count >= Constants.DOOR_BLOCKS_SIZE + 2) {
					start.makeDoor(dir);
				}
			}
			
			return false;
		}
		
		private int countValid(RoomBlock b, Direction dir) {
			int count = 0;
			
			switch (dir) {
			case DOWN:
				count = b.countNextDown();
				break;
			case LEFT:
				count = b.countNextLeft();
				break;
			case RIGHT:
				count = b.countNextRight();
				break;
			case UP:
				count = b.countNextUp();
				break;
			}
			
			return count;
		}
	}	
}
	
