package com.toupety.mapgen.virtualpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Direction;
import com.toupety.mapgen.Room.RoomLevelBlockElement;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomDoor;
import com.toupety.mapgen.mold.MoldMeta;

public class VirtualPathGenerator {

	VirtualPathLevelBlock[][] grid;
	List<VirtualPathLevelBlock> doorsPaths;
	private int w;
	private int h;
	Direction dir = null;
	RandomXS128 rand = new RandomXS128();
	RoomBlocks blocks;
	
	public VirtualPathGenerator(int w, int h) {
		grid = new VirtualPathLevelBlock[w][h];
		doorsPaths = new ArrayList<>();
		this.w = w;
		this.h = h;
		
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				grid[x][y] = new VirtualPathLevelBlock(x, y);
				
				configureDown(this.grid[x][y], x, y);
				configureUp(this.grid[x][y], x, y);
				configureLeft(this.grid[x][y], x, y);
				configureRight(this.grid[x][y], x, y);				
			}
		}
	}
	
	private void configureLeft(VirtualPathLevelBlock el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.grid[x][y] != null) {
			el.left = this.grid[x][y];
			this.grid[x][y].right = el;
		}
	}
	
	private void configureRight(VirtualPathLevelBlock el, int x, int y) {
		x = x + 1;
		if(x < w && this.grid[x][y] != null) {
			el.right = this.grid[x][y];
			this.grid[x][y].left = el;
		}
	}	
	
	private void configureUp(VirtualPathLevelBlock el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.grid[x][y] != null) {
			el.top = this.grid[x][y];
			this.grid[x][y].bottom = el;
		}
	}	
	
	private void configureDown(VirtualPathLevelBlock el, int x, int y) {
		y = y + 1;
		if(y < h && this.grid[x][y] != null) {
			el.bottom = this.grid[x][y];
			this.grid[x][y].top = el;
		}
	}	
	
	public void forEach(Consumer<VirtualPathLevelBlock> c) {
		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				c.accept(grid[x][y]);			
			}
		}
	}
	
	public void process(RoomBlocks blocks) {
		
		this.blocks = blocks;
		
		System.out.println("findDoors");
		this.findDoors(blocks);
		
		System.out.println("findDoors");
		this.findItems(blocks);
		
		System.out.println("generatePathFromItems");
		this.generatePathFromItems(blocks);
		
		System.out.println("generate");
		this.generate(blocks);
		
		System.out.println("virtualpath fim process");
	}
	
	public boolean hasItem(int x, int y) {
		if(x >= 0 && x < this.grid.length && y >= 0 && y < this.grid[0].length) {
			return this.grid[x][y].getItem() != null;
		}
		
		return false;
	}
	
	public boolean hasNext(int x, int y) {
		return (x >= 0 && x < this.grid.length && y >= 0 && y < this.grid[0].length);
	}	
	
	public boolean pointNotAllowed(int x, int y) {
		return (x < 0 || x >= this.grid.length || y < 0 || y >= this.grid[0].length);
	}		
	
	private void findDoors(RoomBlocks blocks) {
		blocks.forEachDoor(d -> {
			RoomBlock b = blocks.getFirstRoomBlockInsideLevelBlock(d.getBlocks().get(0).getX(), d.getBlocks().get(0).getY());
			
			int x = b.getX()/Configuration.getLevelGridElementContentSize();
			int y = b.getY()/Configuration.getLevelGridElementContentSize();
			
			grid[x][y].setDoor(d);
			doorsPaths.add(grid[x][y]);
		});
	}
	
	private void findItems(RoomBlocks blocks) {
		
		forEach(b -> {
			RoomLevelBlockElement lb = blocks.getOwner().getLevelBlock(b.x, b.y);
			b.setItem(lb.item);
		});
		
	}	
	
	private void generate(RoomBlocks blocks) {
		
		VirtualPathLevelBlock target = doorsPaths.get(rand.nextInt(blocks.getDoors().size()));
		
		this.doorsPaths.forEach(source -> {
//			this.doorsPaths.forEach(target -> {
				if(target != source && !target.isConnected(source)) {
					new Cursor(source, target).run();
				}
//			});
		});
	}
	
	private void generatePathFromItems(RoomBlocks blocks) {
		List<RoomLevelBlockElement> list = VirtualPathGenerator.this.blocks.getOwner().getLevelBlocksList().stream().filter(ll -> ll.item != null).collect(Collectors.toList());
		if(list.size() > 0) {
			RoomLevelBlockElement llTarget = list.get(rand.nextInt(list.size()));
			VirtualPathLevelBlock target =  grid[llTarget.x][llTarget.y];
			
			if(this.doorsPaths.size() == 1) {
				System.out.println("this.doorsPaths");
			}
			
			this.doorsPaths.forEach(source -> {
				if(target != source) {
					new Cursor(source, target, true).run();
				}
			});
			
			 list.forEach(ll ->{ 
				 if(ll != llTarget) {
				 	new Cursor(target, grid[ll.x][ll.y], true).run();
				 }
			 });
		}
	}
	
	public class Cursor {
		
		boolean itemPath = false;
		VirtualPathLevelBlock target;
		VirtualPathLevelBlock source;
		VirtualPathLevelBlock previous;
		VirtualPathLevelBlock origin;
		
		public Cursor(VirtualPathLevelBlock source, VirtualPathLevelBlock target, boolean ignore) {
			super();
			this.origin = this.source = source;
			this.target = target;
			this.itemPath = ignore;
		}
		
		public Cursor(VirtualPathLevelBlock source, VirtualPathLevelBlock target) {
			this(source, target, false);
		}

		public void run() {
			
			dir = getInitialDirection();
			
			int iterations = 100000;
			
			while(source != target) {
				
				if(iterations <= 0)
					break;
				
				iterations--;
				
				previous = source;

			     if(dir == Direction.RIGHT) {
					source.openRight = true;
					if(source.right != null && (source.right.getItem() == null || itemPath)) {
						source.right.openLeft = true;
						source = source.right;
					} else {
						if(source.y < target.y && source.bottom != null ) {//&& source.bottom.getItem() == null
							source.openBottom = true;
							source.bottom.openTop = true;
							source = source.bottom;
							dir = Direction.DOWN;
						} else if(source.y > target.y && source.top != null ) {//&& source.top.getItem() == null
							source.openTop = true;
							source.top.openBottom = true;
							source = source.top;
							dir = Direction.UP;
						} else {
							if(source.right == null) {
								dir = Direction.LEFT;
							} else {
								source.right.openLeft = true;
								source = source.right;
							}
						}
					}
				 } else if(dir == Direction.LEFT) {
						source.openLeft = true;
						if(source.left != null && (source.left.getItem() == null || itemPath)) {
							source.left.openRight = true;
							source = source.left;
						} else {
							if(source.y < target.y && source.bottom != null ) {//&& source.bottom.getItem() == null
								source.openBottom = true;
								source.bottom.openTop = true;
								source = source.bottom;
								dir = Direction.DOWN;
							} else if(source.y > target.y && source.top != null ) {//&& source.top.getItem() == null
								source.openTop = true;
								source.top.openBottom = true;
								source = source.top;
								dir = Direction.UP;
							} else {
								
								if(source.left == null) {
									dir = Direction.RIGHT;
								} else {								
									source.left.openRight = true;//continua nao tem oq fazer
									source = source.left;
								}
							}
						}
				 } else if(dir == Direction.UP) {
						source.openTop = true;
						if(source.top != null && (source.top.getItem() == null  || itemPath)) {
							source.top.openBottom = true;
							source = source.top;
						} else {
							if(source.x < target.x && source.right != null ) {//&& source.bottom.getItem() == null
								source.openRight = true;
								source.right.openLeft = true;
								source = source.right;
								dir = Direction.RIGHT;
							} else if(source.x > target.x && source.left != null ) {//&& source.top.getItem() == null
								source.openLeft = true;
								source.left.openRight = true;
								source = source.left;
								dir = Direction.LEFT;
							} else {
								
								if(source.top == null) {
									dir = Direction.DOWN;
								} else {								
									source.top.openBottom = true;
									source = source.top;
								}
							}
						}
				 } else if(dir == Direction.DOWN) {
						source.openBottom = true;
						if(source.bottom != null && ( source.bottom.getItem() == null  || itemPath)) {
							source.bottom.openTop = true;
							source = source.bottom;
						} else {
							if(source.x < target.x && source.right != null ) {//&& source.bottom.getItem() == null
								source.openRight = true;
								source.right.openLeft = true;
								source = source.right;
								dir = Direction.RIGHT;
							} else if(source.x > target.x && source.left != null ) {//&& source.top.getItem() == null
								source.openLeft = true;
								source.left.openRight = true;
								source = source.left;
								dir = Direction.LEFT;
							} else {
								
								if(source.bottom == null) {
									dir = Direction.UP;
								} else {								
									source.bottom.openTop = true;
									source = source.bottom;
								}
							}
						}
				 }
			     
				if(doorsPaths.size() <= 1 && !itemPath) {
					break;
				}			     
				
			}
			
			if(source == target) {
				this.origin.connect(target);
			}
		}
		
		/***
		 * 				 dir = getNextDirection(source, target);
			     if(dir == Direction.RIGHT) {
						source.openRight = true;
						source = VirtualPathGenerator.this.grid[source.x+1][source.y];
						source.openLeft = true;
				 } else if(dir == Direction.LEFT) {
						source.openLeft = true;
						source = VirtualPathGenerator.this.grid[source.x-1][source.y];
						source.openRight = true;
				 } else if(dir == Direction.UP) {
						source.openTop = true;
						source = VirtualPathGenerator.this.grid[source.x][source.y-1];
						source.openBottom = true;
				 } else if(dir == Direction.DOWN) {
						source.openBottom = true;
						source = VirtualPathGenerator.this.grid[source.x][source.y+1];
						source.openTop = true;
				 }
			      
				if(doorsPaths.size() <= 1 && !itemPath) {
					break;
				}
		 */
		
		Direction getNextDirection(VirtualPathLevelBlock source, VirtualPathLevelBlock targ) {
			Direction ret;
			int nx, ny;
			
			if(source.x == targ.x) {
				if(source.y < targ.y) {
					ret = Direction.DOWN; 
					nx = source.x;
					ny = source.y+1;
					
				} else {
					ret = Direction.UP;
					nx = source.x;
					ny = source.y-1;					
				}
			} else if(source.x < targ.x) {
				ret = Direction.RIGHT;
				nx = source.x+1;
				ny = source.y;				
			} else {//if(source.x > targ.x) 
				ret = Direction.LEFT;
				nx = source.x-1;
				ny = source.y;
			} 
			
			if(!nextIsTarget(nx, ny, targ)) {
				if((hasItem(nx, ny) && !itemPath) || pointNotAllowed(nx, ny)) {
					if(ret == Direction.LEFT || ret == Direction.RIGHT) {
						if(source.y < targ.y) {
							ret = Direction.DOWN; 
						} else {
							ret = Direction.UP;
						}						
					} else {
						if(source.x > targ.x) {
							ret = Direction.LEFT; 
						} else {
							ret = Direction.RIGHT;
						}
					}
				}
			}			
			
			return ret;
		}
		
		boolean nextIsTarget(int x, int y, VirtualPathLevelBlock targ) {
			return targ.x == x && targ.y == y;
		}
		
		private boolean contains(int x, int y) {
			try {
				RoomLevelBlockElement ll = VirtualPathGenerator.this.blocks.getOwner().getLevelBlock(x, y);
				return ll != null && ll.item != null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private boolean containsItem() {
			
			boolean ret = false;
			
			if(dir == Direction.RIGHT) {
				source.openRight = true;
				source.openLeft = true;
				ret = contains(source.x+1, source.y);
				if(ret) {
					if(target.y < source.y)
						dir = Direction.UP;
					else
						dir = Direction.DOWN;
				}
			} else if(dir == Direction.DOWN) {
				source.openBottom = true;
				source.openTop = true;
				ret = contains(source.x+1, source.y);
				if(ret) {
					if(target.x < source.x)
						dir = Direction.LEFT;
					else
						dir = Direction.RIGHT;
				}
			} else if(dir == Direction.LEFT) {
				source.openRight = true;
				source.openLeft = true;
				ret = contains(source.x+1, source.y);
				if(ret) {
					if(target.y < source.y)
						dir = Direction.UP;
					else
						dir = Direction.DOWN;
				}
			} else if(dir == Direction.UP) {
				source.openTop = true;
				source.openBottom = true;
				ret = contains(source.x+1, source.y);
				if(ret) {
					if(target.x < source.x)
						dir = Direction.LEFT;
					else
						dir = Direction.RIGHT;
				}
			}
			
			return ret;
		}

		private Direction getInitialDirection() {
			rand.nextInt(4);
			if(dir == null) {
				int direction = rand.nextInt(4);
				
				switch (Direction.values()[direction]) {
				case RIGHT:
					source.openRight = true;
					dir = Direction.RIGHT;
					break;
				case UP:
					source.openTop= true;
					dir = Direction.UP;
					break;
				case LEFT:
					source.openLeft = true;
					dir = Direction.LEFT;
					break;
				case DOWN:
					source.openBottom = true;
					dir = Direction.DOWN;
					break;					
				default:
					break;
				}
			}
			
			return dir;
		}
	}
}
