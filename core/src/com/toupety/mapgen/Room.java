package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.toupety.mapgen.Configuration.ItemConfiguration;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.Configuration.Key;
import com.toupety.mapgen.Configuration.Tag;
import com.toupety.mapgen.LevelGeneratorPersistence.RoomInfo;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.painter.Decoration;
import com.toupety.mapgen.room.RoomArea;
import com.toupety.mapgen.room.RoomElement;
import com.toupety.mapgen.room.RoomItem;

public class Room {
	
	private String id;
	private Rectangle bounds;
	
	private int side;
	private List<Vector2> points;
	
	private int index = 0;
	private RoomBlocks grid;
	private Dimensions dim;
//	private Rectangle worldBounds;//TODO rever
	
	private Set<Room> roomsLeft;
	private Set<Room> roomsRight;
	private Set<Room> roomsBottom;
	private Set<Room> roomsTop;
	
	private Key key;
	private HashSet<Tag> tags;
	private List<ItemConfiguration> items;
	RandomXS128 rand = new RandomXS128();
	
	private RoomLevelBlockElement[][] levelBlocks;
	private List<RoomLevelBlockElement> levelBlocksList;
	private String tagString;
	private List<Decoration> decorations;
	
	/**
	 * Metadata to help generate the correct file info for room, contains position for elements and other things like itens etc.
	 */
	//Position of the items and elements in the room
	private List<RoomItem> roomItemsPosition = new ArrayList<>(); //receive from mold
	private List<RoomArea> roomAreasPosition = new ArrayList<>();//receive from mold
	private List<RoomElement> roomElementsPosition = new ArrayList<>();//receive from mold
	
	public Room(Dimensions dim) {
		rand.nextInt();rand.nextLong();
		this.bounds = new Rectangle(dim.getX(), dim.getY(), dim.getW(), dim.getH());
		
		levelBlocks = new RoomLevelBlockElement[dim.getW()][dim.getH()];
		levelBlocksList = new ArrayList<>();
		decorations = new ArrayList<>();
		
		this.tags = new HashSet<>();
		this.items = new ArrayList<>();
//		int worldMultiplier = (Constants.LEVEL_BLOCK_WIDTH / Constants.ROOM_BLOCK_SIZE);//15
//		this.worldBounds = new Rectangle(dim.getX(), dim.getY(), dim.getW() * worldMultiplier, dim.getH() * worldMultiplier);
		this.id = UUID.randomUUID().toString();
		this.side = -1;
		this.dim = dim;
		
		this.roomsLeft = new HashSet<>();
		this.roomsRight = new HashSet<>();
		this.roomsBottom = new HashSet<>();
		this.roomsTop = new HashSet<>();
		
		this.grid = new RoomBlocks(this);
		
		for(int x = 0; x < dim.getW(); x++) {
			for(int y = 0; y < dim.getH(); y++) {
				this.levelBlocks[x][y] = new RoomLevelBlockElement(x, y);
				levelBlocksList.add(this.levelBlocks[x][y]);
				
				configureDown(this.levelBlocks[x][y], x, y);
				configureUp(this.levelBlocks[x][y], x, y);
				configureLeft(this.levelBlocks[x][y], x, y);
				configureRight(this.levelBlocks[x][y], x, y);
				
			}
		}
	}
	
	public void draw(ShapeRenderer renderer) {
		
		Dimensions dim = grid.getDimensions().toRoomWorldDimmensions();

		int x = 0;
		int y = 0;
		for(Decoration dec : decorations) {
			x =  (GeneratorConstants.ROOM_BLOCK_SIZE * dec.x) + GeneratorConstants.ROOM_BLOCK_SIZE;
			x = (dim.getW() - x) + dim.getX();
			int pseudWorldRoomY = grid.getDimensions().getY() * Configuration.getLevelGridElementContentSize();
			y = (dec.y + pseudWorldRoomY) * (GeneratorConstants.ROOM_BLOCK_SIZE);
			
			renderer.begin(ShapeType.Filled);
			renderer.setColor(dec.color[0], dec.color[1], dec.color[2], 1);
			renderer.rect(x, y, dec.width, dec.height);
			renderer.end();			
		}
		
		for(RoomItem itm : roomItemsPosition) {
			
			Point point = parsePosition(itm.x, itm.y);
			
			ItemDefinition item = Configuration.items.items.get(itm.name);
			renderer.begin(ShapeType.Filled);
			renderer.setColor(item.color[0], item.color[1], item.color[2], 1);
			renderer.rect(point.x, point.y, item.width, item.height);
			renderer.end();
		}		
		
	}
	
	//TODO codigo repetido colocar num util
	public Point parsePosition(int ox, int oy) {
		Dimensions dim = this.dim.toRoomWorldDimmensions();
		int x =  (GeneratorConstants.ROOM_BLOCK_SIZE * ox) + GeneratorConstants.ROOM_BLOCK_SIZE;
		x = (dim.getW() - x) + dim.getX();
		int pseudWorldRoomY = this.dim.getY() * Configuration.getLevelGridElementContentSize();
		int y = (oy + pseudWorldRoomY) * (GeneratorConstants.ROOM_BLOCK_SIZE);
		return new Point(x, y);
	}	
	
	public List<Decoration> getDecorations() {
		return decorations;
	}
	
	private void configureLeft(RoomLevelBlockElement el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.levelBlocks[x][y] != null) {
			el.left = this.levelBlocks[x][y];
			this.levelBlocks[x][y].right = el;
		}
	}
	
	private void configureRight(RoomLevelBlockElement el, int x, int y) {
		x = x + 1;
		if(x < this.dim.getW() && this.levelBlocks[x][y] != null) {
			el.right = this.levelBlocks[x][y];
			this.levelBlocks[x][y].left = el;
		}
	}	
	
	private void configureUp(RoomLevelBlockElement el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.levelBlocks[x][y] != null) {
			el.top = this.levelBlocks[x][y];
			this.levelBlocks[x][y].bottom = el;
		}
	}	
	
	private void configureDown(RoomLevelBlockElement el, int x, int y) {
		y = y + 1;
		if(y < this.dim.getH() && this.levelBlocks[x][y] != null) {
			el.bottom = this.levelBlocks[x][y];
			this.levelBlocks[x][y].top = el;
		}
	}	
	
	public boolean containsAdjacentRoomIndex(int id) {
		
		long ret = this.roomsLeft.stream().filter(r -> r.getIndex() == id).count();
		ret = ret + this.roomsRight.stream().filter(r -> r.getIndex() == id).count();
		ret = ret + this.roomsBottom.stream().filter(r -> r.getIndex() == id).count();
		ret = ret + this.roomsTop.stream().filter(r -> r.getIndex() == id).count();
		
		return ret > 0;
	}
	
	public List<ItemConfiguration> getItems() {
		return items;
	}
	
	public HashSet<Tag> getTags() {
		return tags;
	}
	
	public void setKey(Key key) {
		this.key = key;
	}
	
	public Key getKey() {
		return key;
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
				
				if(this.getY() > 0)
					this.points.add(new Vector2(x, this.getY() - 1));
			});
			
			IntStream.range(this.getY(), this.getY()+this.getHeight())
			.forEach(y -> {
				this.points.add(new Vector2(this.getX()+this.getWidth(), y));
				
				if(this.getX() > 0)
					this.points.add(new Vector2(this.getX() - 1, y));
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
	
	public RoomBlocks getGrid() {//TODO melhorar
		return grid;
	}
	
	public RoomLevelBlockElement getLevelBlock(int x, int y) {
		if(x < this.levelBlocks.length && y < this.levelBlocks[0].length)
			return this.levelBlocks[x][y];
		return null;
	}
	
	public class RoomLevelBlockElement {
		
		public String item;
		public boolean door;
		public int x, y;
		
		RoomLevelBlockElement left, top, bottom, right;

		public RoomLevelBlockElement(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
	}
	
	public List<RoomLevelBlockElement> getLevelBlocksList() {
		return levelBlocksList;
	}
	
	/**
	 * Processa todos os items colocando cada um em um levelblock
	 */
	public void processItems() {
		rand.nextDouble();
		List<RoomLevelBlockElement> list = this.levelBlocksList.stream().filter(ll -> {
			return !ll.door;
		}).collect(Collectors.toList());
		
		items.forEach(it -> {
			list.forEach(ll -> {
				if(rand.nextDouble() < it.chance) {
					ll.item = it.id;
				}
			});
		});
	}
	
	public String getTagString() {
		if(tagString == null && tags != null) {
			synchronized(this) {
				tagString = tags.stream().map(t -> t.id).sorted().collect(Collectors.joining("_"));
			}
		} else {
			tagString = "";
		}
		
		return tagString;
	}
	
	public void addItemPosition(RoomItem item) {
		this.roomItemsPosition.add(item);
	}

	public void addAreaPosition(RoomArea area) {
		this.roomAreasPosition.add(area);
	}
	
	public void addItemPosition(RoomElement element) {
		this.roomElementsPosition.add(element);
	}
	
	public RoomInfo generateRoomInfo() {
		RoomInfo ret = new RoomInfo();
		
		ret.index = this.index;
		ret.areas    = this.roomAreasPosition;
		ret.items    = this.roomItemsPosition;
		ret.elements = this.roomElementsPosition;
		ret.x = this.getX();
		ret.y = this.getY();
		ret.width = this.getWidth();
		ret.heigth = this.getHeight();
		if(this.key != null)
			ret.key = this.key.color;
		if(this.tags != null)
			ret.tags = this.tags.stream().map(t -> t.id).collect(Collectors.toList());
		
		return ret;
	}
}
