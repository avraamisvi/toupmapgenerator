package com.toupety.mapgen.drawner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Dimmensions;
import com.toupety.mapgen.Room;
import com.toupety.mapgen.Util;

public class RoomScreenDrawner implements ElementDrawner<Room> {

	private ShapeRenderer renderer;
//	private float sizeFactor = 0.1f;
	
	public RoomScreenDrawner() {
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void draw(Room element) {
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(1,0,0,1);
		
		Dimmensions world = Util.convertDimmensions(element).toWorldDimmensions();
		
		renderer.rect(world.getX(), world.getY(), world.getW(), world.getH());
		renderer.end();
	}

	private Color getColor(Room room) {
		Color ret = Color.YELLOW;
		switch (room.getSide()) {
		case Room.UP:
			ret = Color.RED;
			break;
		case Room.DOWN:
			ret = Color.LIME;
			break;
		case Room.RIGHT:
			ret = Color.BLUE;
			break;			
		case Room.LEFT:
			ret = Color.CYAN;
			break;
		}
		
		return ret;
	}
}
