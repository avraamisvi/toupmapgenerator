package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Room {
	
	private List<Block> blocks;
	private String id;
	private List<Rectangle> rects;
	private Rectangle bounds;
	
	private int side;
	//TODO rooms will have some kind of grid also containing all the 
	private Room[] rooms;
	
	public static final int UP    = 0;
	public static final int DOWN  = 1;
	public static final int LEFT  = 2;
	public static final int RIGHT = 3;
	
	public Room(int width, int height, int x, int y) {
		this.rects = new ArrayList<>();
		this.bounds = new Rectangle(x, y, width, height);
		this.id = UUID.randomUUID().toString();
		this.blocks = new ArrayList<>();
		this.rooms = new Room[4];
		this.side = -1;
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
	
	public void normalize() {
		//deve navegar em todos os retangulos gerando os blocks nas posições e associando cada block aos adjacentes
		//talvez seja melhor usar uma formula para achar o bloco adjacente, cada bloco teria um codigo de linha e coluna, e assim ficaria facil achar o adjacente
		//como os blocos vao ficar numa lista então cada linha x coluna = a uma posicao na lista
	}
	
	public void merge(Room room) {
//		rects.stream().filter(r -> {			
//			r.contains();
//		});
		//pega cada um dos retangulos de uma room e verifica se eles estão dentro de lagum na outra room, se nao estiverem então sao incorporados a room atual.
		
	}
//	
//	contains() {
//		
//	}

	public Room getUp() {
		return this.rooms[UP];
	}

	public Room getDown() {
		return this.rooms[DOWN];
	}

	public Room getLeft() {
		return this.rooms[LEFT];
	}

	public Room getRight() {
		return this.rooms[RIGHT];
	}
	
	public boolean insertIntoPositionOrAnyFree(int pos, Room room) {
		
		boolean ret = false;
//		pos = this.DOWN;
		if(this.rooms[pos] == null) {
			put(pos, room);
			ret = true;
		} else {
			for(int i = 0; i < this.rooms.length; i++) {
				if(this.rooms[i] == null) {
					put(pos, room);
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}
	
	private void put(int pos, Room room) {
		switch(pos) {
			case Room.RIGHT:
				putRight(room);
				break;
			case Room.LEFT:
				putLeft(room);
				break;
			case Room.UP:
				putUp(room);
				break;
			case Room.DOWN:
				putDown(room);
				break;				
		}
	}
	
	private void putDown(Room room) {
		room.side = DOWN;
		room.setX(this.getX());//rand?
		room.setY(this.getY() + this.getHeight());
	}

	private void putUp(Room room) {
		room.side = UP;
		room.setX(this.getX());
		room.setY(this.getY() - room.getHeight());// (this.getHeight()/2) - (room.getHeight()/2)		
	}

	private void putLeft(Room room) {
		room.side = LEFT;
		room.setX(this.getX() + this.getWidth());
		room.setY(this.getY());
	}

	private void putRight(Room room) {
		room.side = RIGHT;
		room.setX(this.getX() - room.getWidth());
		room.setY(this.getY());
	}

	public void setDoor() {
		//TODO
	}
	
	public int getSide() {
		return side;
	}
}
