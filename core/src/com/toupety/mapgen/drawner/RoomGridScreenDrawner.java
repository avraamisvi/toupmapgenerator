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
		
//		if(colorindex == colors.size()) {
//			colorindex = 0;
//		}

		
//		for (int i = 0; i < 10; i++) {
//			renderer.begin(ShapeType.Filled);
//			renderer.setColor(1,1,1,1);
//			
//			int x = bl.getX() + dim.getX();//
//			int y = bl.getY() + dim.getY();//
//			
//			renderer.rect(x, y, Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
//			
//			renderer.end();			
//		}
		
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
//		Cont tot = new Cont();
		Dimensions dim = grid.getDimensions().toWorldDimmensions();
		grid.forEach(bl -> {
			
			if(bl.isDoor()) {
				System.out.println("door");
				renderer.begin(ShapeType.Filled);
				renderer.setColor(0,0,0,1);
				
				int x = (bl.getX() * Constants.ROOM_BLOCK_SIZE) + dim.getX() + dim.getW() - Constants.LEVEL_BLOCK_WIDTH;
				int y = (bl.getY() * Constants.ROOM_BLOCK_SIZE) + dim.getY();
				
				renderer.rect(x, y, Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
				
				renderer.end();				
			} else if(bl.getOwner() != null && bl.getOwner().getValue() == 'x') {//TODO
				renderer.begin(ShapeType.Filled);
				renderer.setColor(1,1,1,1);
				
				int x = (bl.getX() * Constants.ROOM_BLOCK_SIZE) + dim.getX() + dim.getW() - Constants.LEVEL_BLOCK_WIDTH;
				int y = (bl.getY() * Constants.ROOM_BLOCK_SIZE) + dim.getY();
				
				renderer.rect(x, y, Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
				
				renderer.end();
//				tot.plus();
			} else if(bl.getOwner() != null && bl.getOwner().getValue() == '.') {//TODO mudar logica para pegar cor do metadado
				renderer.begin(ShapeType.Filled);
				renderer.setColor(0,0,0,1);
				
				int x = (bl.getX() * Constants.ROOM_BLOCK_SIZE) + dim.getX() + dim.getW() - Constants.LEVEL_BLOCK_WIDTH;
				int y = (bl.getY() * Constants.ROOM_BLOCK_SIZE) + dim.getY();
				
				renderer.rect(x, y, Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
				
				renderer.end();				
			}
		});
		
		
//		System.out.println("tot: " + tot.tot);
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
