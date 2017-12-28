package com.toupety.mapgen;

import com.badlogic.gdx.math.RandomXS128;

@Deprecated
public class RoomPathGenerator {

	RandomXS128 rand = new RandomXS128();
	
	public void generate(Level level) {
//		level.forEach(this::generate);
//		level.forEach(room -> {
//			this.generate(room.getGrid());
//		});
	}
	
//	private void generate(Room room) {
//		
//		RoomBlocks grid = room.getGrid();
//		
//		grid.getLeftWall().forEachDoor(d -> {
//			RoomLocalPath path = grid.createPath(Direction.RIGHT);
//			path.setDoorOrigin(d);
//			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
//			d.forEachBlock(block -> {
//				RoomBlock current = block.right;
//				for(int i = 0; i < max; i++) {
//					path.add(current);
//					current = current.right;
//					if(current == null || current.isWall() || current.isDoor())
//						break;
//				}
//			});
//		});
//		
//		grid.getRightWall().forEachDoor(d -> {
//			RoomLocalPath path = grid.createPath(Direction.LEFT);
//			path.setDoorOrigin(d);
//			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
//			d.forEachBlock(block -> {
//				RoomBlock current = block.left;
//				for(int i = 0; i < max; i++) {
//					path.add(current);
//					current = current.left;
//					if(current == null || current.isWall() || current.isDoor())
//						break;
//				}
//			});
//		});
//		
//		grid.getTopWall().forEachDoor(d -> {
//			RoomLocalPath path = grid.createPath(Direction.DOWN);
//			path.setDoorOrigin(d);
//			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
//			d.forEachBlock(block -> {
//				RoomBlock current = block.down;
//				for(int i = 0; i < max; i++) {
//					path.add(current);
//					current = current.down;
//					if(current == null || current.isWall() || current.isDoor())
//						break;
//				}
//			});
//		});
//		
//		grid.getBottomWall().forEachDoor(d -> {
//			RoomLocalPath path = grid.createPath(Direction.UP);
//			path.setDoorOrigin(d);
//			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
//			d.forEachBlock(block -> {
//				RoomBlock current = block.up;
//				for(int i = 0; i < max; i++) {
//					path.add(current);
//					current = current.up;
//					if(current == null || current.isWall() || current.isDoor())
//						break;
//				}
//			});
//		});		
//	}
//	
//	private void generate(RoomBlocks grid) {
//		
//		int max = rand.nextInt(3) + 1;
//		
//		for(int i = 0; i < max; i++) {
//			this.generateBottom(grid);
//			this.generateRight(grid);
//			this.generateLeft(grid);
//			this.generateTop(grid);
//		}
//		
//	}
//	
//	private void generateBottom(RoomBlocks grid) {
//		RoomBlock current = grid.getBottomWall().getAny().up;
////		RoomBlock old = null;
//		RoomLocalPath path = grid.createPath(Direction.UP);
//		
//		while(current != null ) {
//			
//			if(add(current, path)) {				
//				current = current.up;
//			} else {
//				break;
//			}
//			
//		}			
//	}
//	
//	boolean add(RoomBlock current, RoomLocalPath path) {
//		if(current.isPath()) {
//			path.setDoorOrigin(current.getPath().getDoorOrigin());
//		} else if(current.isWall() 
//			|| current.isDoor()) {
//			return false;
//		} else {
//			path.add(current);
//		}		
//		
//		return true;
//	}
//	
//	private void generateTop(RoomBlocks grid) {
//		RoomBlock current = grid.getTopWall().getAny().down;
//		RoomLocalPath path = grid.createPath(Direction.DOWN);
//		
//		while(current != null ) {
//			if(add(current, path)) {				
//				current = current.down;
//			} else {
//				break;
//			}
//		}			
//	}
//	
//	private void generateLeft(RoomBlocks grid) {
//		RoomBlock current = grid.getLeftWall().getAny().right;
//		RoomLocalPath path = grid.createPath(Direction.RIGHT);
//		
//		while(current != null ) {
//			if(add(current, path)) {				
//				current = current.right;
//			} else {
//				break;
//			}
//		}			
//	}	
//	
//	private void generateRight(RoomBlocks grid) {
//		RoomBlock current = grid.getRightWall().getAny().left;
//		RoomLocalPath path = grid.createPath(Direction.LEFT);
//		
//		while(current != null ) {
//			if(add(current, path)) {				
//				current = current.left;
//			} else {
//				break;
//			}
//		}			
//	}	
}
