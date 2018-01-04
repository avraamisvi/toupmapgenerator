package com.toupety.mapgen.drawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;
import com.toupety.mapgen.Room;
import com.toupety.mapgen.Util;

public class RoomScreenDrawner implements ElementDrawner<Room> {

	private ShapeRenderer renderer;
	List<Color> colors;
	HashMap<String, Integer> colorByRoom;
	int colorindex = 0;
	RandomXS128 rand = new RandomXS128();
	
	RoomGridScreenDrawner gridDrawner;
	
	public RoomScreenDrawner() {
		this.colors = new ArrayList<>();
		this.colorByRoom = new HashMap<>();
		renderer = new ShapeRenderer();
		IntStream
		.range(1, 60)//Constants.MAX_ROOMS
		.forEach(c -> this.colors.add(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1)));
		gridDrawner = new RoomGridScreenDrawner();
	}
	
	@Override
	public void draw(Room element) {
		
		if(colorindex == colors.size()) {
			colorindex = 0;
		}
		
//		drawLine(element);
		
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		renderer.begin(ShapeType.Filled);
		renderer.setColor(getColor(element.getIndex()));//
		
		Dimensions world = null;
		if(!Configuration.invert) {			
			world = Util.convertDimmensions(element).toRoomWorldDimmensions();
		} else {
			world = Util.convertDimmensions(element).toInvertedRoomWorldDimmensions();
		}
		
		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		renderer.end();
		
		colorindex++;
		
		gridDrawner.draw(element.getGrid());
		
		element.draw(renderer);
	}
	
	public void drawLine(Room element) {
		
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.RED);//getColor(element.getIndex())
		
		Dimensions world = null;
		world = Util.convertDimmensions(element).toRoomWorldDimmensions();
		
		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		renderer.end();
	}
	
	private Color getColor(Integer index) {
		
		Integer idx = colorByRoom.get(index.toString());
		
		if(idx == null) {
			idx = rand.nextInt(colors.size());
			colorByRoom.put(index.toString(), idx);
		}
		
		return colors.get(idx);
		
	}
}
