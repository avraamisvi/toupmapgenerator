package com.toupety.mapgen.drawner;

import java.util.Optional;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Constants;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;
import com.toupety.mapgen.Util;

public class LevelScreenDrawner implements ElementDrawner<Level> {
	
	private ShapeRenderer renderer;
	
	public LevelScreenDrawner() {
		renderer = new ShapeRenderer();
	}
	
	public void draw(Level level) {
		
		this.drawnBlocks(level);
		
		Optional<ElementDrawner<Room>> drawner = DrawnerFactory
				.instance()
				.getElementDrawner(Room.class);
		
		drawner.ifPresent(d -> {
			level.stream().forEach(r -> {				
				d.draw(r);
			});			
		});
	}
	
	private void drawnBounds(Level level) {
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);//.rotate(new Vector3(0, 1, 0), 45)
		renderer.begin(ShapeType.Line);
		renderer.setColor(1,1,1,1);
		Dimensions world = Util.convertDimmensions(level).toWorldDimmensions();
		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		renderer.end();
	}
	
	private void drawnBlocks(Level level) {
		
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);

//		float[] colors = {1, 0.5f, 0.8f, 0.3f, 0.4f, 0.7f};
		
//		Dimmensions world = Util.convertDimmensions(level).toWorldDimmensions();
//		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		
		for(int x = 0; x < level.getWidth(); x++) {
			for(int y = 0; y < level.getHeight(); y++) {
				
//				float color = colors[level.getGrid().getColor(x, y)];
				
				Dimensions dim = Util.convertDimmensions(x, y, 1, 1).toWorldDimmensions();
				
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
					renderer.begin(ShapeType.Line);
					renderer.setColor(1,0,0,1);
						
						renderer.rect(dim.getX(),
								 dim.getY(), 
								 dim.getW(), 
								 dim.getH());
					
					renderer.end();					
//				}
			}
		}
	}
}
