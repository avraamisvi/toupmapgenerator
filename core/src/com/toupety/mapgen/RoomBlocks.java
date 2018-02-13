package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.Room.RoomLevelBlockElement;
import com.toupety.mapgen.cave.BlockMetaInfo;
import com.toupety.mapgen.cave.CaveGenerator;
import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;
import com.toupety.mapgen.mold.MoldFactory;
import com.toupety.mapgen.mold.MoldFactory.Molds;
import com.toupety.mapgen.painter.Decoration;
import com.toupety.mapgen.painter.Palette;
import com.toupety.mapgen.room.RoomArea;
import com.toupety.mapgen.room.RoomItem;
import com.toupety.mapgen.virtualpath.VirtualPathGenerator;

public class RoomBlocks {
	
	private RoomBlock[][] grid;
	private int w, h;
	private Dimensions roomDim;
	
//	private List<RoomLocalPath> paths = new ArrayList<>();
//	private List<RoomLocalPath> bottomPaths = new ArrayList<>();
//	private List<RoomLocalPath> leftPaths = new ArrayList<>();
//	private List<RoomLocalPath> rightPaths = new ArrayList<>();
//	private List<RoomLocalPath> topPaths = new ArrayList<>();
	
	private List<RoomDoor> doors = new ArrayList<>();
	public HashSet<String> joinedDoors = new HashSet<>();
	
	private RoomWall topWall;
	private RoomWall leftWall;
	private RoomWall rightWall;
	private RoomWall bottomWall;
	
	RandomXS128 random = new RandomXS128();
	
	Room owner;
	
	public RoomBlocks(Room owner) {
		
		this.owner = owner;
		
		//para definir a quantidade de blocos preciso calcular na quantidade de quadros que uma room ocupa
		//os quadros das rooms sao contados de 960 em 960, mas os quadros internos da room sao de 64 em 64
		//mas no nivel da room quadra quadro eh como 1 (levelblock), so que esse 1 contem 15 dos roomblocks internos
		Dimensions worldDim = owner.getDim().toRoomWorldDimmensions();
		this.w = worldDim.getW() / GeneratorConstants.ROOM_BLOCK_SIZE;
		this.h = worldDim.getH() / GeneratorConstants.ROOM_BLOCK_SIZE;
		
		topWall = new RoomWall(-1, 0, Position.TOP);
		leftWall = new RoomWall(0,-1,  Position.LEFT);
		rightWall = new RoomWall(w-1,-1, Position.RIGHT);
		bottomWall = new RoomWall(-1,h-1, Position.BOTTOM);		
		
		this.roomDim = owner.getDim();//FIXME rever a necessidade desse dim
		
		this.grid = new RoomBlock[w][h];
		
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				this.grid[x][y] = new RoomBlock(x, y);
				
				configureDown(this.grid[x][y], x, y);
				configureUp(this.grid[x][y], x, y);
				configureLeft(this.grid[x][y], x, y);
				configureRight(this.grid[x][y], x, y);
			}			
		}
		
		this.fillWalls();
	}
	
	public Room getOwner() {
		return owner;
	}
	
	public Optional<RoomBlock> getAt(int x, int y) {
		if((x < this.w && y < this.h) && (x >= 0 && y >= 0)) {
			return Optional.of(this.grid[x][y]);
		}
		return Optional.empty();
	}
	
	/**
	 * Aplica os molds aos caminhos.
	 */
	public void createPathApplyMolds() {
		
		System.out.println("criando os paths e settando os molds");
		
		VirtualPathGenerator vph = new VirtualPathGenerator(roomDim.getW(), roomDim.getH());
		vph.process(this);
		System.out.println("teste");

		vph.forEach(path -> {
			
			if(path.isPath()) {
				
				Mold mod = null;
				Molds molds = MoldFactory.get().getMolds(path.getOpens());
				
				if(molds != null) {
					mod = molds.getAny(mo -> {
						return path.isAcceptable(mo.getMeta());
					});			
				}
				
				if(mod == null) {
					mod = MoldFactory.get().getEmpty();
					if(!GeneratorConstants.ITEM_PHANTOM.equals(path.getItem())) {
						System.out.println("################## not found ###################");
						System.out.println("top:" + path.isOpenTop());
						System.out.println("bottom:" + path.isOpenBottom());
						System.out.println("left:" + path.isOpenLeft());
						System.out.println("right:" + path.isOpenRight());
						System.out.println("has ITEM: " + path.getItem());			
					}
				}
				mod = MoldFactory.get().getVerdanio();//FIXME REMOVER usado apenas para testes
				if(mod != null) {
					int lx = path.x * Configuration.getLevelGridElementContentSize();
					int ly = path.y * Configuration.getLevelGridElementContentSize();
					
					RoomLocalPath newPath = new RoomLocalPath(Direction.DOWN, mod);//FIXME PARECE Q nao to usando o RoomLocalPath de fato, ou seja, guardando
					newPath.addFrom(this.grid[lx][ly]);
				}
			}
		});
		
	}	
	
	public void detectBlocksPlaces() {
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				
				if(this.grid[x][y].isEmpty()) {
					if(this.grid[x][y].down != null && !this.grid[x][y].down.isEmpty()) {
						this.grid[x][y].setPlace(Place.OVER_GROUND);
						this.grid[x][y].down.setPlace(Place.GROUND);
					} else if(this.grid[x][y].up != null && !this.grid[x][y].up.isEmpty()) {
						this.grid[x][y].setPlace(Place.UNDER_ROOF);
						this.grid[x][y].up.setPlace(Place.ROOF);
					}
				} else {
					if(this.grid[x][y].getPlace() == Place.NONE) {
						this.grid[x][y].setPlace(Place.WALL);
					}
				}
			}			
		}		
	}
	
	/**
	 * Retorna o primeiro roomblock do levelblock onde se encontra o ponto x, y
	 * @param x
	 * @param y
	 * @return
	 */
	public RoomBlock getFirstRoomBlockInsideLevelBlock(int x, int y) {
		
		int lx = x - (x%Configuration.getLevelGridElementContentSize());
		int ly = y - (y%Configuration.getLevelGridElementContentSize());
		
		return this.grid[lx][ly];
	}
	
	public List<RoomDoor> getDoors() {
		return doors;
	}
	
	public void forEachDoor(Consumer<RoomDoor> con) {
		this.doors.forEach(con);
	}
	
	public void createCave() {
		new CaveGenerator().generate(this);
	}
	
	
	public void addJoinedDoorPath(RoomDoor door1, RoomDoor door2) {
		this.joinedDoors.add(door1.id);
		this.joinedDoors.add(door2.id);
	}
	
	public boolean isDoorsPathJoined() {
		return this.doors.size() == 1 || this.doors.size() <= joinedDoors.size();
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
	
	public Dimensions getDim() {
		return roomDim;
	}
	
	private void fillWalls() {
		
		//sides left
		for(int y = 0; y < this.h; y++) {
			leftWall.add(this.grid[0][y]);
		}
		
		//sides right
		for(int y = 0; y < this.h; y++) {
			rightWall.add(this.grid[this.w-1][y]);
		}
		
		//sides top
		for(int x = 0; x < this.w; x++) {
			topWall.add(this.grid[x][0]);
		}
		
		//sides bottom
		for(int x = 0; x < this.w; x++) {
			bottomWall.add(this.grid[x][this.h-1]);
		}		
		
	}
	
	public RoomWall getLeftWall() {
		return leftWall;
	}
	
	public RoomWall getRightWall() {
		return rightWall;
	}
	
	public RoomWall getTopWall() {
		return topWall;
	}
	
	public RoomWall getBottomWall() {
		return bottomWall;
	}
	
	private void configureLeft(RoomBlock el, int x, int y) {
		x = x - 1;
		if(x >= 0 && this.grid[x][y] != null) {
			el.left = this.grid[x][y];
			this.grid[x][y].right = el;
		}
	}
	
	private void configureRight(RoomBlock el, int x, int y) {
		x = x + 1;
		if(x < w && this.grid[x][y] != null) {
			el.right = this.grid[x][y];
			this.grid[x][y].left = el;
		}
	}	
	
	private void configureUp(RoomBlock el, int x, int y) {
		y = y - 1;
		if(y >= 0 && this.grid[x][y] != null) {
			el.up = this.grid[x][y];
			this.grid[x][y].down = el;
		}
	}	
	
	private void configureDown(RoomBlock el, int x, int y) {
		y = y + 1;
		if(y < h && this.grid[x][y] != null) {
			el.down = this.grid[x][y];
			this.grid[x][y].up = el;
		}
	}
	
	public void forEach(Consumer<RoomBlock> c) {
		for(int x = 0; x < this.grid.length; x++) {
			for(int y = 0; y < this.grid[x].length; y++) {
				c.accept(this.grid[x][y]);
			}			
		}		
	}
	
	public Dimensions getDimensions() {
		return this.roomDim;
	}
	
	public void connectDoors() {
		//conecta duas portas gerando um path, esse path pode ser esparramado
		boolean done = false;
		while(!done) {
			
		}
	}
	
	public Point parsePosition(int ox, int oy) {
		
		Dimensions dim = getDimensions().toRoomWorldDimmensions();
		
		int x =  (GeneratorConstants.ROOM_BLOCK_SIZE * ox) + GeneratorConstants.ROOM_BLOCK_SIZE;
		x = (dim.getW() - x) + dim.getX();
		int pseudWorldRoomY = getDimensions().getY() * Configuration.getLevelGridElementContentSize();
		int y = (oy + pseudWorldRoomY) * (GeneratorConstants.ROOM_BLOCK_SIZE);
		
		return new Point(x, y);
	}	
	
	public class RoomBlock {
		
		int x, y;
		
		RoomBlock up;
		RoomBlock down;
		RoomBlock left;
		RoomBlock right;
		
		RoomLocalPath path;
		RoomDoor door;
		List<RoomWall> walls = new ArrayList<>();
		BlockMetaInfo metaInfo = new BlockMetaInfo("x");//TODO metainfo
		private Place place = Place.NONE;
		private MoldBlock owner;
		
		public RoomBlock(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public BlockMetaInfo getMetaInfo() {
			return metaInfo;
		}
		
		public void setMetaInfo(BlockMetaInfo metaInfo) {
			this.metaInfo = metaInfo;
		}
		
		public void setMold(MoldBlock mold) {
			this.owner = mold;
			this.metaInfo = BlockMetaInfo.parse(mold);
		}
//		
		public MoldBlock getOwner() {
			return owner;
		}
		
//		public boolean isUsed() {
//			return owner != null;
//		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getWorldX() {
			return x + RoomBlocks.this.roomDim.getX();
		}
		
		public int getWorldY() {
			return y + RoomBlocks.this.roomDim.getY();
		}		
		
		public boolean isPath() {
			return this.path != null;
		}
		
		public boolean isDoor() {
			return door != null;
		}
		
		public RoomDoor getDoor() {
			return door;
		}
		
		public boolean isCorner() {
			return this.walls.size() > 1;
		}
		
		void setPath(RoomLocalPath path) {
			this.path = path;
		}
//		
		public RoomLocalPath getPath() {
			return path;
		}

		void setDoor(RoomDoor door) {
			this.door = door;
		}
		
		public boolean isWall() {
			return this.walls.size() > 0;
		}
		
		void setWall(RoomWall wall) {
			this.walls.add(wall);
		}
		
		public boolean isOwnered(RoomWall wall) {
			return this.walls.stream().filter(w -> w.pos == wall.pos).findFirst().isPresent();
		}
		
		public Place getPlace() {
			return place;
		}
		
		public void setPlace(Place place) {
			this.place = place;
		}
		
		public boolean isEmpty() {
			return metaInfo.getType().equals(BlockMetaInfo.EMPTY.getType());
		}
	}
	
	
	public class RoomLocalPathElement {
		
		public RoomBlock block;
		public int x, y;
		
		public RoomLocalPathElement(RoomBlock block, int x, int y) {
			super();
			this.block = block;
			this.x = x;
			this.y = y;
		}
		
		void copyInto(RoomLocalPathElement path) {
			path.block = block;
			path.x = x;
			path.y = y;
		}
	}
	
	public class RoomLocalPath {
//		private List<RoomBlock> blocks = new ArrayList<>();
		private RoomLocalPathElement[][] grid;
		private RoomDoor door;
		private Direction dir;
		private Mold mold;
		private RoomBlock originalBlock;
		
		public RoomLocalPath(Direction dir, Mold mold) {
			this.dir = dir;
			this.grid = new RoomLocalPathElement[mold.getWidth()][mold.getHeight()];
			this.mold = mold;
		}
		
		public int getWidth() {
			return this.grid.length;
		}
		
		public int getHeight() {
			return this.grid[0].length;
		}		
		
		public void setDoorOrigin(RoomDoor door) {
			this.door = door; 
		}
		
		public RoomDoor getDoorOrigin() {
			return this.door;
		}
		
		public boolean isOwneredBy(RoomDoor door) {
			return this.door == door;
		}
		
		public Direction getDirection() {
			return dir;
		}
		
		public void addFrom(RoomBlock block) {
			
			if(block == null) {
				return;
			}
			
			originalBlock = block;
			
			RoomBlock column = block;
			RoomBlock line   = block;
			
			for(int x = 0; x < this.mold.getWidth(); x++) {
				for(int y = 0; y < this.mold.getHeight(); y++) {
					
					this.grid[x][y] = new RoomLocalPathElement(column, x, y);
					column.setMold(this.mold.getGrid()[x][y]);
					
					this.setItems(this.mold.getGrid()[x][y], column);
					this.setAreas(this.mold.getGrid()[x][y], column);
					
					column = column.down;
					if(column == null)
						break;
				}
				
				line = column = line.right;
				if(line == null)
					break;				
			}
			
		}
		
		public void setItems(MoldBlock moldBlock, RoomBlock roomBlock) {
			//TODO TA CONSUMINDO UMA BAITA MEMORIA AHHAHAAH
			moldBlock.forEachItems(it -> {
				RoomItem roomItem = new RoomItem();
				roomItem.x = roomBlock.x;
				roomItem.y = roomBlock.y;
				roomItem.name = it.name;
				roomItem.dx = it.dx;
				roomItem.dy = it.dy;
				
				RoomBlocks.this.owner.addItemPosition(roomItem);
			});
		}
		
		public void setAreas(MoldBlock moldBlock, RoomBlock roomBlock) {
			//TODO TA CONSUMINDO UMA BAITA MEMORIA AHHAHAAH
			moldBlock.getArea(moldBlock.getMeta().maxWidth - moldBlock.getX(), moldBlock.getY()).ifPresent(area -> {
				RoomArea bean = new RoomArea();
				bean.x = roomBlock.x;
				bean.y = roomBlock.y;
				bean.name = area.name;
				bean.width = area.width;
				bean.height = area.height;
				RoomBlocks.this.owner.addAreaPosition(bean);				
			});
		}		
		
//		public void add(RoomBlock block, MoldBlock moldBlock) {//FIXME BEM LIXO ESSE CODIGO
//			if(block == null)
//				return;
//			
//			if(!block.isWall() && !block.isDoor() && !block.isPath()) {
//				block.setPath(this);
//				block.setMold(moldBlock);
//				this.grid[moldBlock.getX()][moldBlock.getY()] = new RoomLocalPathElement(block, moldBlock.getX(), moldBlock.getY());//TODO is this pathele really needed?
//			} else {
//				if(block.isPath()) {
//					if(block.getPath() != null) {
//						if(block.getPath().getDoorOrigin() != null) {
//							if(this.door != null) {
//								RoomBlocks.this.addJoinedDoorPath(this.door, block.getPath().getDoorOrigin());
//							}
//						}
//					}
//				}
//			}
//		}		
			
	}
	
	public class RoomDoor {
		
		Position position;
		String id = UUID.randomUUID().toString();
		
		private List<RoomBlock> blocks = new ArrayList<>();
		
		public RoomDoor(Position position) {
			this.position = position;
		}
		
		public Position getPosition() {
			return position;
		}
		
		public void add(RoomBlock b) {
			blocks.add(b);
			b.setDoor(this);
			
			if(blocks.size() == 1) {
				RoomBlock bl = getFirstRoomBlockInsideLevelBlock(b.getX(), b.getY());
				RoomLevelBlockElement lvl = owner.getLevelBlock(bl.x/Configuration.getLevelGridElementContentSize(), 
						bl.y/Configuration.getLevelGridElementContentSize());
				lvl.door = true;
				System.out.println("Door level block: " + lvl.x + " " + lvl.y);
			}
		}
		
		public void forEachBlock(Consumer<RoomBlock> c) {
			if(blocks != null) {
				blocks.forEach(c);
			}
		}
		
		public void sort() {
			this.blocks.sort((c1, c2) -> {
				return c1.x - c2.x;
			});
			this.blocks.sort((c1, c2) -> {
				return c1.y - c2.y;
			});
		}
		
		public List<RoomBlock> getBlocks() {
			return blocks;
		}
	}
	
	public class RoomWall {
		
		private String id = UUID.randomUUID().toString();
		private List<RoomBlock> blocksList = new ArrayList<>();
		private Map<String, RoomBlock> blocks = new LinkedHashMap<>();
		private Map<String, RoomDoor> doors = new HashMap<>();
		private List<RoomDoor> doorsList = new ArrayList<>();
		
		private Direction dir;
		private Position pos;
		
		private int x = -1;
		private int y = -1;
		
		public RoomBlock getAny() {
			return blocksList.get(random.nextInt(blocks.size()));
		}
		
		public RoomWall(int x, int y, Position pos) {
			this.x = x;
			this.y = y;
			this.pos = pos;
		}
		
		public void add(RoomBlock b) {
			b.setWall(this);
			
			int x = b.x;
			int y = b.y;
			
			String key = getKeyFor(b.x, b.y);
			
			blocksList.add(b);
			blocks.put(key, b);
		}
		
		private String getKeyFor(int lx, int ly) {
			
			int pseudWorldRoomX = RoomBlocks.this.roomDim.getX() * Configuration.getLevelGridElementContentSize();
			int pseudWorldRoomY = RoomBlocks.this.roomDim.getY() * Configuration.getLevelGridElementContentSize();
			
			lx = lx + pseudWorldRoomX;
			ly = ly + pseudWorldRoomY;			
			
			return lx+","+ly;
		}
		
		public RoomDoor getAnyDoor() {
			return this.doorsList.get(random.nextInt(this.doorsList.size()));
		}

		/**
		 * IF this wall contains the corresponding world point
		 * @param x
		 * @param y
		 * @return 
		 */
		public RoomBlock containsWorldPoint(int wx, int wy) {	
			return this.blocks.get(wx+","+wy);
		}
		
		public void forEach(Consumer<RoomBlock> c) {
			this.blocks.values().forEach(c);
		}
		
		public RoomDoor createDoorFor(RoomWall destiny) {
			RoomDoor door = new RoomDoor(this.pos);
			this.doors.put(destiny.id, door);
			this.doorsList.add(door);
			RoomBlocks.this.doors.add(door);
			return door;
		}
		
		public boolean containsDoorFor(RoomWall destiny) {
			return this.doors.containsKey(destiny.id);
		}
		
		
		public void forEachDoor(Consumer<RoomDoor> consumer) {
			if(this.doors != null)
				this.doors.values().forEach(consumer);
		}
	}	
}
	
