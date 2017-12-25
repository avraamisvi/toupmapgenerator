package com.toupety.mapgen;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomLocalPath;

public class RoomPathGenerator {

	RandomXS128 rand = new RandomXS128();
	
	public void generate(Level level) {
		level.forEach(this::generate);
	}
	
	private void generate(Room room) {
		
		RoomBlocks grid = room.getGrid();
		
		grid.getLeftWall().forEachDoor(d -> {
			RoomLocalPath path = grid.createPath(Direction.RIGHT);
			path.setDoorOrigin(d);
			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
			d.forEachBlock(block -> {
				RoomBlock current = block.right;
				for(int i = 0; i < max; i++) {
					path.add(current);
					current = current.right;
					if(current == null || current.isWall() || current.isDoor())
						break;
				}
			});
		});
		
		grid.getRightWall().forEachDoor(d -> {
			RoomLocalPath path = grid.createPath(Direction.LEFT);
			path.setDoorOrigin(d);
			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
			d.forEachBlock(block -> {
				RoomBlock current = block.left;
				for(int i = 0; i < max; i++) {
					path.add(current);
					current = current.left;
					if(current == null || current.isWall() || current.isDoor())
						break;
				}
			});
		});
		
		grid.getTopWall().forEachDoor(d -> {
			RoomLocalPath path = grid.createPath(Direction.DOWN);
			path.setDoorOrigin(d);
			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
			d.forEachBlock(block -> {
				RoomBlock current = block.down;
				for(int i = 0; i < max; i++) {
					path.add(current);
					current = current.down;
					if(current == null || current.isWall() || current.isDoor())
						break;
				}
			});
		});
		
		grid.getBottomWall().forEachDoor(d -> {
			RoomLocalPath path = grid.createPath(Direction.UP);
			path.setDoorOrigin(d);
			int max = rand.nextInt(GeneratorConstants.MAX_DOOR_PATH_SIZE) + GeneratorConstants.MAX_DOOR_PATH_SIZE/2;
			d.forEachBlock(block -> {
				RoomBlock current = block.up;
				for(int i = 0; i < max; i++) {
					path.add(current);
					current = current.up;
					if(current == null || current.isWall() || current.isDoor())
						break;
				}
			});
		});		
	}
}
