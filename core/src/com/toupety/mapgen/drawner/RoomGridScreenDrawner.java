package com.toupety.mapgen.drawner;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Constants;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.RoomGrid;

public class RoomGridScreenDrawner implements ElementDrawner<RoomGrid> {

	private ShapeRenderer renderer;
	List<Color> colors;
//	HashMap<String, Integer> colorByRoom;
//	int colorindex = 0;
	RandomXS128 rand = new RandomXS128();
	
	public RoomGridScreenDrawner() {
		this.colors = new ArrayList<>();
//		this.colorByRoom = new HashMap<>();
		renderer = new ShapeRenderer();
//		IntStream
//		.range(1, Constants.MAX_ROOMS)
//		.forEach(c -> this.colors.add(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1)));
	}
	
	@Override
	public void draw(RoomGrid grid) {
		
//		if(colorindex == colors.size()) {
//			colorindex = 0;
//		}

		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		
		Dimensions dim = grid.getDimensions().toWorldDimmensions();
		grid.forEach(rbl -> {
			rbl.forEach(bl -> {
				if(bl.getOwner().getValue() == 'x') {//TODO
					renderer.begin(ShapeType.Filled);
					renderer.setColor(1,1,1,1);
					
					renderer.rect(bl.getX() + dim.getX(), bl.getY() + dim.getY(), Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
					
					renderer.end();
				}
			});
		});
		
//		Dimmensions world = Util.convertDimmensions(element).toWorldDimmensions();
//		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		
//		colorindex++;
	}
	
//	private Color getColor(Integer index) {
//		
//		Integer idx = colorByRoom.get(index.toString());
//		
//		if(idx == null) {
//			idx = rand.nextInt(colors.size());
//			colorByRoom.put(index.toString(), idx);
//		}
//		
//		return colors.get(idx);
//		
//	}

//	private void drawnBlocks(Level level) {
//		
//		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
//
//		for(int x = 0; x < level.getWidth(); x++) {
//			for(int y = 0; y < level.getHeight(); y++) {
//				
//				float color = colors[level.getGrid().getColor(x, y)];
//				
//				Dimmensions dim = Util.convertDimmensions(x, y, 1, 1).toWorldDimmensions();
//				
//				if(level.getGrid().isUsed(x, y)) {
//					
//					renderer.begin(ShapeType.Filled);
//					renderer.setColor(1,color,1,1);
//					
//						renderer.rect(dim.getX(),
//									 dim.getY(), 
//									 dim.getW(), 
//									 dim.getH());
//					renderer.end();
//				} else {
//					renderer.begin(ShapeType.Line);
//					renderer.setColor(1,0,0,1);
//						
//						renderer.rect(dim.getX(),
//								 dim.getY(), 
//								 dim.getW(), 
//								 dim.getH());
//					
//					renderer.end();					
//				}
//			}
//		}
//	}	
}