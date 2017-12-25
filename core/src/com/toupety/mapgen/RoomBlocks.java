package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;

public class RoomBlocks {
	
	private RoomBlock[][] grid;
	private int w, h;
	private Dimensions dim;
	
	private List<RoomLocalPath> paths;
	private List<RoomBlock> path;
	private List<RoomBlock> doors;
	
	private RoomWall topWall;
	private RoomWall leftWall;
	private RoomWall rightWall;
	private RoomWall bottomWall;
	
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
	
	public RoomLocalPath createPath(Direction dir) {
		return new RoomLocalPath(dir);
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
		
		@Deprecated
		int countNextDown() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.isWall()) {
				count++;
				b = b.down;
			}
			
			return count;
		}
		@Deprecated
		int countNextUp() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.isWall()) {
				count++;
				b = b.up;
			}
			
			return count;
		}		
		@Deprecated
		int countNextRight() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.isWall()) {
				count++;
				b = b.right;
			}
			
			return count;
		}
		@Deprecated
		int countNextLeft() {
			int count = 0;
			
			RoomBlock b = this;
			while(b != null && b.isWall()) {
				count++;
				b = b.left;
			}
			
			return count;
		}		
		
		@Deprecated
		RoomDoor makeDoor(Direction dir) {
			RoomDoor door = new RoomDoor();			
			RoomBlock b = this;
			for(int i = 0; i < GeneratorConstants.DOOR_BLOCKS_SIZE; i++) {
				
				if(b == null)
					break;
				door.add(b);
				switch (dir) {
				case DOWN:
					b = b.down;
					break;
				case LEFT:
					b = b.left;
					break;
				case RIGHT:
					b = b.right;
					break;
				case UP:
					b = b.up;
					break;
				}
			}
			
			return door;
		}
	}
	
	public class RoomLocalPath {
		private List<RoomBlock> blocks = new ArrayList<>();
		private RoomDoor door;
		private Direction dir;
		
		public RoomLocalPath(Direction dir) {
			this.dir = dir;
		}
		
		public void add(RoomBlock block) {
			block.setPath(this);
			this.blocks.add(block);
		}
		
		public void spread() {
			//gera caminhos alterantivos que nao tem o objetivo de chegar na porta
		}
		
		public void setDoorOrigin(RoomDoor door) {
			this.door = door; 
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
		private Map<String, RoomBlock> blocks = new HashMap<>();
		private Map<String, RoomDoor> doors = new HashMap<>();
		private Direction dir;
		private Position pos;
		
		private int x = -1;
		private int y = -1;
		
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
	
