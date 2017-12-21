package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Room {
	
	private List<Block> blocks;
	private String id;
//	private List<Rectangle> rects;
	private Rectangle bounds;
	
	private int side;
	//TODO rooms will have some kind of grid also containing all the 
//	private Room[] rooms;
	private List<Vector2> points;
	
	int index = 0;//TODO sair disso
	
	public Room(int width, int height, int x, int y) {
//		this.rects = new ArrayList<>();
		this.bounds = new Rectangle(x, y, width, height);
		this.id = UUID.randomUUID().toString();
		this.blocks = new ArrayList<>();
//		this.rooms = new Room[4];
		this.side = -1;
	}
	
	private void createBlocks() {
		
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
	
	public void apply(Consumer<Block> c) {//esse algoritmo deve gerar plataformas e estruturas dentro das rooms, considerando os retangulos
		blocks.forEach(c);
	}

	public void setDoor() {
		//TODO
	}
	
	public int getSide() {
		return side;
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
}
