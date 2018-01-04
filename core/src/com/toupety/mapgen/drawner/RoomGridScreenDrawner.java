package com.toupety.mapgen.drawner;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Configuration.Brush;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.RoomBlocks;

public class RoomGridScreenDrawner implements ElementDrawner<RoomBlocks> {

	private ShapeRenderer renderer;
	List<Color> colors;
//	HashMap<String, Integer> colorByRoom;
//	int colorindex = 0;
	RandomXS128 rand = new RandomXS128();
	
	class Cont {
		int tot = 0;
		public void start() {
			tot = 0;
		}
		public void plus() {
			tot++;
		}
	}
	
	public RoomGridScreenDrawner() {
		this.colors = new ArrayList<>();
//		this.colorByRoom = new HashMap<>();
		renderer = new ShapeRenderer();
//		IntStream
//		.range(1, Constants.MAX_ROOMS)
//		.forEach(c -> this.colors.add(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1)));
	}
	
	@Override
	public void draw(RoomBlocks grid) {
		
		//TODO mover isso para os elementos como room, level etc com um metodo drawn
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
//		Cont tot = new Cont();
		Color color = new Color();

		
		Dimensions dim = grid.getDimensions().toRoomWorldDimmensions();
		grid.forEach(bl -> {
//			
			int x =  (GeneratorConstants.ROOM_BLOCK_SIZE * bl.getX()) + GeneratorConstants.ROOM_BLOCK_SIZE;
			
			x = (dim.getW() - x) + dim.getX();
//			int y = (bl.getY() * Constants.ROOM_BLOCK_SIZE) + dim.getY();
			
			int pseudWorldRoomY = grid.getDimensions().getY() * Configuration.getLevelGridElementContentSize();
			int y = (bl.getY() + pseudWorldRoomY) * (GeneratorConstants.ROOM_BLOCK_SIZE);
			
			ShapeType shType = ShapeType.Filled;
			boolean render = false;
			
			if(bl.isDoor()) {
				render = true;
				color.set(0, 0, 0, 1);
			} else if(bl.isWall()) {
//				render = true;
//				color.set(1, 1, 1, 1);
//				shType = ShapeType.Line;
			} else if(bl.getMetaInfo() != null) {//TODO
				
				if(bl.getMetaInfo().getType().equals(".")) {
					render = true;
					color.set(0, 0, 0, 1);
				} else if(!bl.getMetaInfo().getType().equals("x")) {
					
					Brush brush = Configuration.brushesPerTile.get(bl.getMetaInfo().getType().charAt(0));
					if(brush != null && bl.getOwner() != null) {
						
//						if(bl.getOwner() != null) {
//							System.out.println("owner: " + brush.tile);
//						}						
//						
						render = true;
						color.set(brush.color[0], brush.color[1], brush.color[2], 1);	
					}
				}
			}
			
			if(render) {
				
				int size = GeneratorConstants.ROOM_BLOCK_SIZE;
				
				renderer.begin(shType);
				renderer.setColor(color);
				renderer.rect(x, y, size, size);
				renderer.end();				
			}
		});
	}
}
