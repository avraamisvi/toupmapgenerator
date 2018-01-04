package com.toupety.mapgen.painter;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.toupety.mapgen.Place;

public class Decoration {
	
	public String name;
	public int width;
	public int height;
	public float[] color;
	public int x;
	public int y;
	public List<String> place;
	String placeString;
	
	public void draw(ShapeRenderer renderer) {
		renderer.begin(ShapeType.Filled);
		renderer.setColor(color[0], color[1], color[2], 1);
		renderer.rect(x, y, width, height);
		renderer.end();
	}
	
	public boolean canBePlaced(Place p) {
		return place != null && place.stream().anyMatch(s -> s.equals(p.name()));
	}
	
	public String getPlaceString() {
		if(placeString == null && place != null) {
			synchronized(this) {
				placeString = place.stream().sorted().collect(Collectors.joining("_"));
			}
		} else {
			placeString = "";
		}
		
		return placeString;
	}
	
	public Decoration copy() {
		Decoration dec = new Decoration();
		dec.color = color;
		dec.x = x;
		dec.y = y;
		dec.height = height;
		dec.width= width;
		dec.name = name;
		dec.placeString = placeString;
		dec.place = place;
		
		return dec;
	}
}
