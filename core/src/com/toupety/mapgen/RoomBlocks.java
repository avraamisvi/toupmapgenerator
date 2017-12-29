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
import com.toupety.mapgen.cave.BlockMetaInfo;
import com.toupety.mapgen.cave.CaveGenerator;
import com.toupety.mapgen.mold.Mold;
import com.toupety.mapgen.mold.MoldBlock;
import com.toupety.mapgen.mold.MoldFactory;
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
	
	public RoomBlocks(Dimensions dim) {
				
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
		
		this.roomDim = dim;//FIXME rever a necessidade desse dim
		
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
	
	public Optional<RoomBlock> getAt(int x, int y) {
		if((x < this.w && y < this.h) && (x >= 0 && y >= 0)) {
			return Optional.of(this.grid[x][y]);
		}
		return Optional.empty();
	}
	
//	public void createPath() {
//		this.createPathFromTopDoor();
//		this.createPathFromLeftDoor();
//		this.createPathFromRightDoor();
//		this.createPathFromBottomDoor();
		
//		VirtualPathGenerator vph = new VirtualPathGenerator(roomDim.getW(), roomDim.getH());
//		vph.process(this);
//		System.out.println("teste");
//
//		vph.forEach(path -> {
//			Mold mod = MoldFactory.get().getAny(mo -> {
//				return path.isAcceptable(mo.getMeta());
//			});
//			
//			if(mod != null) {
//				int lx = path.x * Configuration.getLevelGridElementContentSize();
//				int ly = path.y * Configuration.getLevelGridElementContentSize();
//				
//				RoomLocalPath newPath = new RoomLocalPath(Direction.DOWN, mod);
//				newPath.addFrom(this.grid[lx][ly]);
//			}
//			
//		});
		
//		Mold mod = MoldFactory.get().getAny(m -> {
//		return this.needOpen(m.getMeta().open) && m.getMeta().open.contains(Position.TOP.name());
//	});		
		
		//TODO spread
//	}
	
//	private boolean needOpen(List<String> opens) {//TODO refazer usando a logica do virtual path
//		return this.doors.stream().filter(door -> {
//			return opens.contains(door.getPosition().name());			
//		}).count() > 0;
//	}
	
//	private int countLevelBlocksLeft(int x) {
//		int ret = 0;
//		
//		ret = Math.abs(Configuration.getLevelGridElementContentSize() - x)/Configuration.getLevelGridElementContentSize();
//		
//		return ret;
//	}
//	
//	private int countLevelBlocksRight(int x) {
//		int ret = 0;
//		
//		ret = roomDim.getW() - (Math.abs(Configuration.getLevelGridElementContentSize() + x)/Configuration.getLevelGridElementContentSize());
//		
//		return ret;
//	}
//	
//	private int countLevelBlocksUp(int y) {
//		int ret = 0;
//		
//		ret = Math.abs(Configuration.getLevelGridElementContentSize() - y)/Configuration.getLevelGridElementContentSize();
//		
//		return ret;
//	}	
//	
//	private int countLevelBlocksBottom(int y) {
//		int ret = 0;
//		
//		ret = roomDim.getH() - (Math.abs(Configuration.getLevelGridElementContentSize() + y)/Configuration.getLevelGridElementContentSize());
//		
//		return ret;
//	}
//	
//	private boolean found;//USADO NO VIRTUAL PATH
////	private VirtualPath lastVirtual;
//	
//	private void setFoundDoor(boolean found) {
//		this.found = found;
//	}
////	private void setLastVirtualPath(VirtualPath virtual) {
////		this.lastVirtual = virtual;
////	}	
//	
//	public VirtualRoute createVirtualPathDoorTop() {//TODO isso pode ser simplificado
//		VirtualRoute route = new VirtualRoute();
//		route.root.mustTop = true;
//		
//		RoomDoor from = this.topWall.getAnyDoor();
//		
//		List<RoomDoor> targetDoors = doors.stream().filter(door -> {
//			return door != from;
//		}).collect(Collectors.toList());
//		
//		Point doorPos = new Point(from.blocks.get(0).x, from.blocks.get(0).y);
//		this.found = false;
//		
//		targetDoors.forEach(target -> {
//			
//			Point currentPos = doorPos.copy();
//			
//			if(target.getPosition() == Position.RIGHT) {
//				int count = countLevelBlocksRight(from.blocks.get(0).x);					
//				
//				RoomBlock targetFirstBlock = getFirstRoomBlockInsideLevelBlock(target.blocks.get(0).x, target.blocks.get(0).y);
//				VirtualPath current = route.root;
//				
//				//RIGHT
//				for(int x = 0; x < count; x++) {
//					RoomBlock doorblock = getFirstRoomBlockInsideLevelBlock(currentPos.x, currentPos.y);
//					currentPos.x = currentPos.x + Configuration.getLevelGridElementContentSize();
//					current.mustRight = true;
//					
//					if(targetFirstBlock == doorblock) {					
//						setFoundDoor(true);
//						break;
//					}
//					
//					current = current.createRight();
//				}
//				
//				//DOWN
//				if(!this.found) {
//					currentPos = doorPos.copy();
//					for(int x = 0; x < count; x++) {
//						RoomBlock doorblock = getFirstRoomBlockInsideLevelBlock(currentPos.x, currentPos.y);
//						currentPos.y = currentPos.y + Configuration.getLevelGridElementContentSize();
//						current.mustBottom = true;
//						
//						if(targetFirstBlock == doorblock) {
//							setFoundDoor(true);
//							break;
//						}
//						
//						current = current.createDown();
//					}
//					
//				}
//				
//				//LEFT
//				if(!this.found) {
//					currentPos = doorPos.copy();
//					for(int x = 0; x < count; x++) {
//						RoomBlock doorblock = getFirstRoomBlockInsideLevelBlock(currentPos.x, currentPos.y);
//						currentPos.x = currentPos.x - Configuration.getLevelGridElementContentSize();
//						current.mustLeft = true;
//						
//						if(targetFirstBlock == doorblock) {
//							setFoundDoor(true);
//							break;
//						}
//						
//						current = current.createLeft();
//					}					
//				}
//				
//				//UP
//				if(!this.found) {
//					currentPos = doorPos.copy();
//					for(int x = 0; x < count; x++) {
//						RoomBlock doorblock = getFirstRoomBlockInsideLevelBlock(currentPos.x, currentPos.y);
//						currentPos.y = currentPos.y - Configuration.getLevelGridElementContentSize();
//						current.mustTop = true;
//						
//						if(targetFirstBlock == doorblock) {
//							setFoundDoor(true);
//							break;
//						}
//						
//						current = current.createUp();
//					}					
//				}				
//			}
//		
//			
////			RoomBlock currentDoorblock = getFirstRoomBlockInsideLevelBlock(doorPos.x, doorPos.y);
//			//|| (target.getPosition() == Position.TOP && target.blocks.get(0).x > from.blocks.get(0).x)
//		});
//		
//		return route;
//	}
//	
//	
//	public void createPathFromTopDoor() {
//
//		this.topWall.forEachDoor(door -> {
//			Mold mod = MoldFactory.get().getAny(m -> {
//				return this.needOpen(m.getMeta().open) && m.getMeta().open.contains(Position.TOP.name());
//			});
//			
//			RoomLocalPath newPath = new RoomLocalPath(Direction.DOWN, mod);
//			door.sort();
//			
//			RoomBlock block = door.blocks.get(0);
//			block = getFirstRoomBlockInsideLevelBlock(block.x, block.y);
//			newPath.addFrom(block);
//			
//			this.topPaths.add(newPath);
//		});
//	}
//	
//	public void createPathFromBottomDoor() {
//
//		this.bottomWall.forEachDoor(door -> {
//			Mold mod = MoldFactory.get().getAny(m -> {
//				return this.needOpen(m.getMeta().open) && m.getMeta().open.contains(Position.BOTTOM.name());
//			});
//			
//			RoomLocalPath newPath = new RoomLocalPath(Direction.UP, mod);
//			door.sort();
//			
//			RoomBlock block = door.blocks.get(0);
//			block = getFirstRoomBlockInsideLevelBlock(block.x, block.y);
//			newPath.addFrom(block);
//			
//			this.bottomPaths.add(newPath);
//		});
//	}
//	
//	public void createPathFromLeftDoor() {
//
//		this.leftWall.forEachDoor(door -> {
//			Mold mod = MoldFactory.get().getAny(m -> {
//				return this.needOpen(m.getMeta().open) && m.getMeta().open.contains(Position.LEFT.name());
//			});
//			
//			RoomLocalPath newPath = new RoomLocalPath(Direction.RIGHT, mod);
//			door.sort();
//			
//			RoomBlock block = door.blocks.get(0);
//			block = getFirstRoomBlockInsideLevelBlock(block.x, block.y);
//			newPath.addFrom(block);
//			
//			this.leftPaths.add(newPath);
//		});
//	}	
//	
//	public void createPathFromRightDoor() {
//
//		this.rightWall.forEachDoor(door -> {
//			Mold mod = MoldFactory.get().getAny(m -> {
//				return this.needOpen(m.getMeta().open) && m.getMeta().open.contains(Position.RIGHT.name());
//			});
//			
//			RoomLocalPath newPath = new RoomLocalPath(Direction.LEFT, mod);
//			door.sort();
//			
//			RoomBlock block = door.blocks.get(0);
//			block = getFirstRoomBlockInsideLevelBlock(block.x, block.y);
//			newPath.addFrom(block);
//			
//			this.rightPaths.add(newPath);
//		});
//	}	
	
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
	
//	public void createPath() {
//		if(paths.size() == 0) {
//			this.getBottomWall().forEachDoor(door -> {
//				createPathBottom(door);
//			});
//			this.getTopWall().forEachDoor(door -> {
//				createPathTop(door);
//			});
//			this.getRightWall().forEachDoor(door -> {
//				createPathRight(door);
//			});
//			this.getLeftWall().forEachDoor(door -> {
//				createPathLeft(door);
//			});			
//		}
//		
//		int maxPaths = 1000;
//		while(!isDoorsPathJoined()) {
//			RoomLocalPath path = this.paths.get(random.nextInt(this.paths.size()));
//			RoomLocalPathElement any = new RoomLocalPathElement(null, -1, -1);
//			Direction dir = path.getAny(any);
//			
//			int lw = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
//			int lh = random.nextInt(GeneratorConstants.MAX_ROOM_PATH_DIMENSIONS) + GeneratorConstants.MIN_ROOM_PATH_DIMENSIONS;
//			
//			if(any.block == null)
//				continue;
//			
//			RoomLocalPath newPath = new RoomLocalPath(dir, lw, lh);
//			this.paths.add(newPath);
//			newPath.addFrom(any.block);
//			
//			if(maxPaths <= 0)
//				break;
//			
//			maxPaths--;
//		}
//	}
	
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
	
//	public boolean isUsed(int x, int y) {
//		return this.grid[x][y].isUsed();
//	}
	
//	public synchronized boolean put(Mold room) {
//		return this.grid[0][0].fit(room);//TODO algoritmo precisa analisar e procurar regiões com path e ai aplica nessas regioes
//	}
	
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
	
	public class RoomBlock {
		
		int x, y;
		
		RoomBlock up;
		RoomBlock down;
		RoomBlock left;
		RoomBlock right;
		
//		RoomLocalPath path;
		RoomDoor door;
		List<RoomWall> walls = new ArrayList<>();
		BlockMetaInfo metaInfo = new BlockMetaInfo("x");//TODO metainfo
		
//		private MoldBlock owner;
		
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
		
//		public void setMold(MoldBlock mold) {
//			this.owner = mold;
//		}
//		
//		public MoldBlock getOwner() {
//			return owner;
//		}
		
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
		
//		public boolean isPath() {
//			return this.path != null;
//		}
		
		public boolean isDoor() {
			return door != null;
		}
		
		public RoomDoor getDoor() {
			return door;
		}
		
		public boolean isCorner() {
			return this.walls.size() > 1;
		}
		
//		void setPath(RoomLocalPath path) {
//			this.path = path;
//		}
//		
//		public RoomLocalPath getPath() {
//			return path;
//		}

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
	}
	
	
//	public class RoomLocalPathElement {
//		
//		public RoomBlock block;
//		public int x, y;
//		
//		public RoomLocalPathElement(RoomBlock block, int x, int y) {
//			super();
//			this.block = block;
//			this.x = x;
//			this.y = y;
//		}
//		
//		void copyInto(RoomLocalPathElement path) {
//			path.block = block;
//			path.x = x;
//			path.y = y;
//		}
//	}
	
//	public class RoomLocalPath {
////		private List<RoomBlock> blocks = new ArrayList<>();
//		private RoomLocalPathElement[][] grid;
//		private RoomDoor door;
//		private Direction dir;
//		private Mold mold;
//		
//		public RoomLocalPath(Direction dir, Mold mold) {
//			this.dir = dir;
//			this.grid = new RoomLocalPathElement[mold.getWidth()][mold.getHeight()];
//			this.mold = mold;
//		}
//		
//		public int getWidth() {
//			return this.grid.length;
//		}
//		
//		public int getHeight() {
//			return this.grid[0].length;
//		}		
//		
//		public void setDoorOrigin(RoomDoor door) {
//			this.door = door; 
//		}
//		
//		public RoomDoor getDoorOrigin() {
//			return this.door;
//		}
//		
//		public boolean isOwneredBy(RoomDoor door) {
//			return this.door == door;
//		}
//		
//		public Direction getDirection() {
//			return dir;
//		}
//		
//		public void addFrom(RoomBlock block) {
//			
//			if(block == null) {
//				return;
//			}
//			
//			RoomBlock column = block;
//			RoomBlock line   = block;
//			
//			for(int x = 0; x < this.mold.getWidth(); x++) {
//				for(int y = 0; y < this.mold.getHeight(); y++) {
//					
//					this.grid[x][y] = new RoomLocalPathElement(column, x, y);
//					column.setMold(this.mold.getGrid()[x][y]);
//					
//					column = column.down;
//					if(column == null)
//						break;
//				}
//				
//				line = column = line.right;
//				if(line == null)
//					break;				
//			}
//		}
//		
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
//			
//	}
	
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
	
