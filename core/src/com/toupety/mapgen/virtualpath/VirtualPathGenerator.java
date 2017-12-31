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
			}
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
		
		this.findDoors(blocks);
		this.generatePathFromItems(blocks);
		this.generate(blocks);
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
//		VirtualPathLevelBlock target = doorsPaths.get(rand.nextInt(blocks.getDoors().size()));
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
		
		boolean ignoreItems = false;
		VirtualPathLevelBlock target;
		VirtualPathLevelBlock source;
		VirtualPathLevelBlock previous;
		VirtualPathLevelBlock origin;
		
		public Cursor(VirtualPathLevelBlock source, VirtualPathLevelBlock target, boolean ignore) {
			super();
			this.origin = this.source = source;
			this.target = target;
			this.ignoreItems = ignore;
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
				
				if(!ignoreItems) {
					if(containsItem()) {
						continue;
					}
				}
				
				previous = source;
				
				if(dir == Direction.RIGHT) {
					source.openRight = true;
					source.openLeft = true;
					if(source.x == w-1) {
//						source.openBottom = true;
						if(target.y < source.y)
							dir = Direction.UP;
						else
							dir = Direction.DOWN;
					} else {
						source = VirtualPathGenerator.this.grid[source.x+1][source.y];
						source.openLeft = true;
					}
				}else 				
				if(dir == Direction.DOWN) {
					source.openBottom = true;
					source.openTop = true;
					if(source.y == h-1) {
//						source.openLeft = true;
						if(target.x < source.x)
							dir = Direction.LEFT;
						else
							dir = Direction.RIGHT;
					} else {
						source = VirtualPathGenerator.this.grid[source.x][source.y+1];
						source.openTop= true;
					}
				}else
				if(dir == Direction.LEFT) {
					source.openRight = true;
					source.openLeft = true;
					if(source.x == 0) {
//						source.openTop = true;
						if(target.y < source.y)
							dir = Direction.UP;
						else
							dir = Direction.DOWN;
					} else {
						source = VirtualPathGenerator.this.grid[source.x-1][source.y];
						source.openRight = true;
					}
				}else
				if(dir == Direction.UP) {
					source.openTop = true;
					source.openBottom = true;
					if(source.y == 0) {
//						source.openRight = true;
						if(target.x < source.x)
							dir = Direction.LEFT;
						else
							dir = Direction.RIGHT;
					} else {
						source = VirtualPathGenerator.this.grid[source.x][source.y-1];
						source.openTop = true;
					}
				}
				
				if(doorsPaths.size() <= 1 && !ignoreItems) {
					break;
				}
			}
			
			if(source == target) {
				this.origin.connect(target);
			}
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
