package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.toupety.mapgen.mold.Mold;

public class Room {
	
	private String id;
	private Rectangle bounds;
	
	private int side;
	private List<Vector2> points;
	
	private int index = 0;
	private RoomBlocks grid;
	private Dimensions dim;
	
	public Room(Dimensions dim) {
		this.bounds = new Rectangle(dim.getX(), dim.getY(), dim.getW(), dim.getH());
		this.id = UUID.randomUUID().toString();
		this.side = -1;
		this.grid = new RoomBlocks(dim);
	}
	
	public String getId() {
		return id;
	}

	public int getHeight() {
		return (int) bounds.getHeight();
	}

	public int getWidth() {
		return (int) bounds.getWidth();
	}
	
	public int getX() {
		return (int) bounds.getX();
	}
	
	public int getY() {
		return (int) bounds.getY();
	}
	
	public void setX(int x) {
		this.bounds.setX(x);
	}
	
	public void setY(int y) {
		this.bounds.setY(y);
	}
	
//	public void createDoor(int x, int y, Direction dir) {
//		this.grid.createDoor();
//	}
	
	public int getSide() {
		return side;
	}
	
	public Dimensions getDim() {
		return dim;
	}
	
	public List<Vector2> getClosePoints() {
		
		if(this.points == null) {
			this.points = new ArrayList<>();
		
			IntStream.range(this.getX(), this.getX()+this.getWidth())
			.forEach(x -> {
				this.points.add(new Vector2(x, this.getHeight() + this.getY()));
			});
			
			IntStream.range(this.getY(), this.getY()+this.getHeight())
			.forEach(y -> {
				this.points.add(new Vector2(this.getX()+this.getWidth(), y));
			});	
			
		}
		
		return this.points;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void apply(Mold mold) {
		this.grid.put(mold);
	}
	
	public RoomBlocks getGrid() {//TODO melhorar
		return grid;
	}
}
