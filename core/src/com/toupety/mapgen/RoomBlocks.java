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

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;

public class RoomBlocks {
	
	private RoomBlock[][] grid;
	private int w, h;
	private Dimensions dim;
	
	private List<RoomLocalPath> paths = new ArrayList<>();
	private List<RoomDoor> doors = new ArrayList<>();
	public HashSet<String> joinedDoors = new HashSet<>();
	
	private RoomWall topWall;
	private RoomWall leftWall;
	private RoomWall rightWall;
	private RoomWall bottomWall;
	
	RandomXS128 random = new RandomXS128();
	
	public RoomBlocks(Dimensions dim) {
				
		this.paths = new ArrayList<>();
		
		//para definir a quantidade de blocos preciso calcular na quantidade de quadros que uma room ocupa
		//os quadros das rooms sao contados de 960 em 960, mas os quadros internos da room sao de 64 em 64
		//mas no nivel da room quadra quadro eh como 1 (levelblock), so que esse 1 contem 15 dos roomblocks internos
		Dimensions worldDim = dim.toRoomWorldDimmensions();
		this.w = worldDim.getW() / GeneratorConstants.ROOM_BLOCK_SIZE;
		this.h = worldDim.getH() / GeneratorConstants.ROOM_BLOCK_SIZE;
		
		topWall = new RoomWall(-1, 0, Position.TOP);
		leftWall = new RoomWall(0,-1,  Position.LEFT);
		rightWall = new RoomWall(w-1,-1, Position.RIGHT);
		bottomWall = new RoomWall(-1,h-1, Position.BOTTOM);		
		
		this.dim = dim;//FIXME rever a necessidade desse dim
		
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
	
	private void createRoomPathFrom(int lw, int lh, RoomBlock block, Direction dir) {
		RoomLocalPath newPath = new RoomLocalPath(dir, lw, lh);
		this.paths.add(newPath);
		newPath.addFrom(block);
	}
	
	public void createPath() {
		if(paths.size() == 0) {
			this.getBottomWall().forEachDoor(door -> {
				createPathBottom(door);
			});
			this.getTopWall().forEachDoor(door -> {
				createPathTop(door);
			});
			this.getRightWall().forEachDoor(door -> {
				createPathRight(door);
			});
			this.getLeftWall().forEachDoor(door -> {
				createPathLeft(door);
			});			
		}
		
		int maxPaths = 1000;
		while(!isDoorsPathJoined()) {
			RoomLocalPath path = this.paths.get(random.nextInt(this.paths.size()));
			RoomLocalPathElement any = new RoomLocalPathElement(null, -1, -1);
			Direction dir = path.getAny(any);
			
			int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
			int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
			
			if(any.block == null)
				continue;
			
			RoomLocalPath newPath = new RoomLocalPath(dir, lw, lh);
			this.paths.add(newPath);
			newPath.addFrom(any.block);
			
			if(maxPaths <= 0)
				break;
			
			maxPaths--;
		}
	}
	
	private void createPathBottom(RoomDoor door) {
		int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		
		Optional<RoomBlock> bloc = door.blocks.stream().min((c1, c2) -> c1.x - c2.x);
		
		bloc.ifPresent(b -> {
			createRoomPathFrom(lw, lh, b, Direction.UP);
		});
	}
	
	private void createPathTop(RoomDoor door) {
		int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		
		Optional<RoomBlock> bloc = door.blocks.stream().min((c1, c2) -> c1.x - c2.x);
		
		bloc.ifPresent(b -> {
			createRoomPathFrom(lw, lh, b, Direction.DOWN);
		});
	}
	
	private void createPathLeft(RoomDoor door) {
		int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		
		Optional<RoomBlock> bloc = door.blocks.stream().min((c1, c2) -> c1.y - c2.y);
		
		bloc.ifPresent(b -> {
			createRoomPathFrom(lw, lh, b, Direction.RIGHT);
		});
	}
	
	private void createPathRight(RoomDoor door) {
		int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
		
		Optional<RoomBlock> bloc = door.blocks.stream().min((c1, c2) -> c1.y - c2.y);
		
		bloc.ifPresent(b -> {
			createRoomPathFrom(lw, lh, b, Direction.LEFT);
		});
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
		return dim;
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
	
	public boolean isUsed(int x, int y) {
		return this.grid[x][y].isUsed();
	}
	
	public synchronized boolean put(Mold room) {
		return this.grid[0][0].fit(room);//TODO algoritmo precisa analisar e procurar regiões com path e ai aplica nessas regioes
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
		return this.dim;
	}
	
	public void createDoor() {
		//cria as portas nos elementos conectados
		//------
	}
	
	public void connectDoors() {
		//conecta duas portas gerando um path, esse path pode ser esparramado
		boolean done = false;
		while(!done) {
			
		}
	}	
	
	public class RoomBlock {
		
		int x, y;
		
		RoomBlock up;
		RoomBlock down;
		RoomBlock left;
		RoomBlock right;
		
		RoomLocalPath path;
		boolean door;
		List<RoomWall> walls = new ArrayList<>();
		
		private MoldBlock owner;
		
		public RoomBlock(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean fit(Mold mold) {
			
			mold
			.stream()
			.forEach(bl -> {
				grid[bl.getX()][bl.getY()].owner = bl;
			});
			
			return true;
		}
		
		public MoldBlock getOwner() {
			return owner;
		}
		
		public boolean isUsed() {
			return owner != null;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getWorldX() {
			return x + RoomBlocks.this.dim.getX();
		}
		
		public int getWorldY() {
			return y + RoomBlocks.this.dim.getY();
		}		
		
		public boolean isPath() {
			return this.path != null;
		}
		
		public boolean isDoor() {
			return door;
		}
		
		public boolean isCorner() {
			return this.walls.size() > 1;
		}
		
		void setPath(RoomLocalPath path) {
			this.path = path;
		}
		
		public RoomLocalPath getPath() {
			return path;
		}

		void setDoor(boolean door) {
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
		
		public RoomLocalPath(Direction dir, int w, int h) {
			this.dir = dir;
			this.grid = new RoomLocalPathElement[w][h];
		}
		
		void addNextLefts(int size, int y, RoomBlock current) {
			for(int x = 0; x < size ; x++) {
					add(current, x, y);
					current = current.left;
					if(current == null)
						break;
			}			
		}
		
		void addNextRights(int size, int y, RoomBlock current) {
			for(int x = 0; x < size ; x++) {
					add(current, x, y);
					current = current.right;
					if(current == null)
						break;
			}			
		}
		
		void addNextTops(int size, int x, RoomBlock current) {
			for(int y = 0; y < size ; y++) {
					add(current, x, y);
					current = current.up;
					if(current == null)
						break;
			}			
		}		
		
		void addNextDowns(int size, int x, RoomBlock current) {
			for(int y = 0; y < size ; y++) {
					add(current, x, y);
					current = current.down;
					if(current == null)
						break;
			}			
		}		
		
		public void addFrom(RoomBlock block) {
			
			if(block == null) {
				return;
			}
			
			RoomBlock current = block;
			
			if(dir == Direction.LEFT) {
				for(int x = grid.length-1; x >=0 ; x--) {
						addNextDowns(grid[0].length/2, x, current);
						addNextTops(grid[0].length/2, x, current);
						current = current.left;
						if(current == null)
							break;
				}				
			} else if(dir == Direction.UP) {
				for(int y = grid[0].length-1; y >=0 ; y--) {
					addNextLefts(grid.length/2, y, current);
					addNextRights(grid.length/2, y, current);
					current = current.up;
					if(current == null)
						break;
				}				
			} else if(dir == Direction.DOWN) {
				for(int y = 0; y < grid[0].length; y++) {
					addNextLefts(grid.length/2, y, current);
					addNextRights(grid.length/2, y, current);
					current = current.down;
					if(current == null)
						break;
				}				
			} else if(dir == Direction.RIGHT) {
				for(int x = 0; x < grid.length; x++) {
					addNextTops(grid[0].length/2, x, current);
					addNextDowns(grid[0].length/2, x, current);
					current = current.right;
					if(current == null)
						break;
				}				
			}
			
			
		}
		
		public void add(RoomBlock block, int x, int y) {//BEM LIXO ESSE CODIGO
			if(block == null)
				return;
			
			if(!block.isWall() && !block.isDoor() && !block.isPath()) {
				block.setPath(this);
				this.grid[x][y] = new RoomLocalPathElement(block, x, y);
			} else {
				if(block.isPath()) {
					if(block.getPath() != null) {
						if(block.getPath().getDoorOrigin() != null) {
							if(this.door != null) {
								RoomBlocks.this.addJoinedDoorPath(this.door, block.getPath().getDoorOrigin());
							}
						}
					}
				}
			}
		}
		
		public RoomLocalPathElement getAt(int x, int y) {
			return this.grid[x][y];
		}
		
		public int getWidth() {
			return this.grid.length;
		}
		
		public int getHeight() {
			return this.grid[0].length;
		}		
		
		public Direction getAny(RoomLocalPathElement path) {
			Direction next = dir;//Direction.values()[random.nextInt(Direction.values().length)];
			RoomLocalPathElement ele = null;
			
			switch (next) {
			case LEFT:
				ele = this.grid[0][random.nextInt(this.getHeight())];
				break;
			case RIGHT:
				ele = this.grid[this.getWidth()-1][random.nextInt(this.getHeight())];
				break;
			case UP:
				ele = this.grid[random.nextInt(this.getWidth())][0];
				break;
			case DOWN:
				ele = this.grid[random.nextInt(this.getWidth())][this.getHeight() - 1];
				break;				
			default:
				break;
			}
			
			if(ele != null)
				ele.copyInto(path);
			
			return next;
		}
		
		public void spread() {
			//gera caminhos alterantivos que nao tem o objetivo de chegar na porta
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
		
		public boolean fit(Mold mold) {
//			RoomBlocks.this.put(mold);
//			
//			RoomBlock block = null;
////			RoomBlock block = blocks.stream().filter(b -> !b.isUsed()).findFirst();
//			
//			for(RoomBlock b : blocks) {
//				if()
//			}
//			
//			block.fit(mold);
			
			return false;
		}		
	}
	
	public class RoomDoor {
		
		String id = UUID.randomUUID().toString();
		
		private List<RoomBlock> blocks = new ArrayList<>();
		
		public void add(RoomBlock b) {
			blocks.add(b);
			b.setDoor(true);
		}
		
		public void forEachBlock(Consumer<RoomBlock> c) {
			if(blocks != null) {
				blocks.forEach(c);
			}
		}
	}
	
	public class RoomWall {
		
		private String id = UUID.randomUUID().toString();
		private List<RoomBlock> blocksList = new ArrayList<>();
		private Map<String, RoomBlock> blocks = new LinkedHashMap<>();
		private Map<String, RoomDoor> doors = new HashMap<>();
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
			
			int pseudWorldRoomX = RoomBlocks.this.dim.getX() * Configuration.getLevelGridElementWidth();
			int pseudWorldRoomY = RoomBlocks.this.dim.getY() * Configuration.getLevelGridElementWidth();
			
			lx = lx + pseudWorldRoomX;
			ly = ly + pseudWorldRoomY;			
			
			return lx+","+ly;
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
			RoomDoor door = new RoomDoor();
			this.doors.put(destiny.id, door);
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
	
