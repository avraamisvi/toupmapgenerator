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
		//TODO mover isso para os elementos como room, level etc com um metodo drawn
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
//		Cont tot = new Cont();
		Color color = new Color();

		
		Dimensions dim = grid.getDimensions().toRoomWorldDimmensions();
		grid.forEach(bl -> {
//			
			int x =  (Constants.ROOM_BLOCK_SIZE * bl.getX()) + Constants.ROOM_BLOCK_SIZE;
			
			x = (dim.getW() - x) + dim.getX();
			int y = (bl.getY() * Constants.ROOM_BLOCK_SIZE) + dim.getY();
			
			ShapeType shType = ShapeType.Filled;
			boolean render = false;
			
			if(bl.isDoor()) {
				render = true;
				color.set(0, 0, 0, 1);
			} else if(bl.getOwner() != null && bl.getOwner().getValue() == 'x') {//TODO
				render = true;
				color.set(1, 1, 1, 1);
			} else if(bl.getOwner() != null && bl.getOwner().getValue() == '.') {//TODO mudar logica para pegar cor do metadado
				render = true;
				color.set(0, 0, 0, 1);				
			}			
			
			if(render) {
				renderer.begin(shType);
				renderer.setColor(color);
				renderer.rect(x, y, Constants.ROOM_BLOCK_SIZE, Constants.ROOM_BLOCK_SIZE);
				renderer.end();				
			}
		});
	}
}
