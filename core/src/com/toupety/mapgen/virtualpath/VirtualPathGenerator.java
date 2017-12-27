package com.toupety.mapgen.virtualpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Direction;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.mold.MoldMeta;

public class VirtualPathGenerator {

	VirtualPathLevelBlock[][] grid;
	List<VirtualPathLevelBlock> doorsPaths;
	private int w;
	private int h;
	Direction dir = null;
	
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
		this.findDoors(blocks);
		this.generate();
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
	
	private void generate() {
		this.doorsPaths.forEach(source -> {
			this.doorsPaths.forEach(target -> {
				if(target != source && !target.isConnected(source)) {
					new Cursor(source, target).run();
				}
			});
		});
	}
	
	public class Cursor {
		
		VirtualPathLevelBlock target;
		VirtualPathLevelBlock source;
		VirtualPathLevelBlock origin;
		
		public Cursor(VirtualPathLevelBlock source, VirtualPathLevelBlock target) {
			super();
			this.origin = this.source = source;
			this.target = target;
		}

		public void run() {
			
			if(dir == null) {
				if(source.x < target.x) {//right
					source.openRight = true;
//					source = VirtualPathGenerator.this.grid[source.x+1][source.y];
					dir = Direction.RIGHT;
				} else if(source.x > target.x) {//left
					source.openLeft = true;
//					source = VirtualPathGenerator.this.grid[source.x-1][source.y];
					dir = Direction.LEFT;
				} else {
					if(source.y < target.y) {//down
						source.openBottom = true;
//						source = VirtualPathGenerator.this.grid[source.x][source.y+1];
						dir = Direction.DOWN;
					} else if(source.y > target.y) {//up
						source.openTop = true;
//						source = VirtualPathGenerator.this.grid[source.y-1][source.y];
						dir = Direction.UP;
					}				
				}				
			}
			
			//TODO criar path algorithm list, and if there only one door, select any block as the target
			while(source != target) {
				
				if(dir == Direction.RIGHT) {
					source.openRight = true;
					if(source.x == w-1) {
						source.openBottom = true;
						dir = Direction.DOWN;
					} else {
						source = VirtualPathGenerator.this.grid[source.x+1][source.y];
					}
				}
				
				if(dir == Direction.DOWN) {
					source.openBottom = true;
					if(source.y == h-1) {
						source.openLeft = true;
						dir = Direction.LEFT;
					} else {
						source = VirtualPathGenerator.this.grid[source.x][source.y+1];
					}
				}
				
				if(dir == Direction.LEFT) {
					source.openLeft = true;
					if(source.x == 0) {
						source.openTop = true;
						dir = Direction.UP;
					} else {
						source = VirtualPathGenerator.this.grid[source.x-1][source.y];
					}
				}				
				
				if(dir == Direction.UP) {
					source.openBottom = true;
					if(source.y == 0) {
						source.openRight = true;
						dir = Direction.RIGHT;
					} else {
						source = VirtualPathGenerator.this.grid[source.x][source.y-1];
					}
				}
				
				if(doorsPaths.size() <= 1) {
					break;
				}
			}
			
			if(source == target) {
				this.origin.connect(target);
			}
		}
	}
}
