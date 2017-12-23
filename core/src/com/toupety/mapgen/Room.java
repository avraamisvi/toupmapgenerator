package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
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
	private Rectangle worldBounds;//TODO rever
	
	private Set<Room> roomsLeft;
	private Set<Room> roomsRight;
	private Set<Room> roomsBottom;
	private Set<Room> roomsTop;
	
	public Room(Dimensions dim) {
		this.bounds = new Rectangle(dim.getX(), dim.getY(), dim.getW(), dim.getH());
		
		int worldMultiplier = (Constants.LEVEL_BLOCK_WIDTH / Constants.ROOM_BLOCK_SIZE);//15
		
		this.worldBounds = new Rectangle(dim.getX(), dim.getY(), dim.getW() * worldMultiplier, dim.getH() * worldMultiplier);
		this.id = UUID.randomUUID().toString();
		this.side = -1;
		this.grid = new RoomBlocks(dim);
		
		this.roomsLeft = new HashSet<>();
		this.roomsRight = new HashSet<>();
		this.roomsBottom = new HashSet<>();
		this.roomsTop = new HashSet<>();		
	}
	
	public void addLeft(Room room) {
		this.roomsLeft.add(room);
	}

	public void addRight(Room room) {
		this.roomsRight.add(room);
	}
	
	public void addBottom(Room room) {
		this.roomsBottom.add(room);
	}
	
	public void addTop(Room room) {
		this.roomsTop.add(room);
	}
	
	public void forEachLeft(Consumer<Room> c) {
		this.roomsLeft.forEach(c);
	}
	
	public void forEachRight(Consumer<Room> c) {
		this.roomsRight.forEach(c);
	}
	
	public void forEachTop(Consumer<Room> c) {
		this.roomsTop.forEach(c);
	}
	
	public void forEachBottom(Consumer<Room> c) {
		this.roomsBottom.forEach(c);
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

	//TODO World Points sao o tamanho da room vezes 15 ou seja, a quantidade de quadros por bloco que a room ocupa,
	//pq no nivel da room cada quadro corresponde a 960
	/**
	 * Verifies if it contains the wx, and wy, but considering it as world points, for example
	 * @param wx
	 * @param wy
	 */
	public boolean containsWorldPoint(int wx, int wy) {
		// TODO Auto-generated method stub
		return this.worldBounds.contains(wx, wy);
	}
}
