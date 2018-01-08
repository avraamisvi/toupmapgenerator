package com.toupety.mapgen.chunkeditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.toupety.mapgen.CameraHolder;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.Configuration.AreaDefinition;
import com.toupety.mapgen.Configuration.Brush;
import com.toupety.mapgen.Configuration.ElementDefinition;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.Position;
import com.toupety.mapgen.Room.RoomLevelBlockElement;
import com.toupety.mapgen.chunkeditor.ProjectSaver.Info;

public class Chunk implements InputProcessor{

	static final String EMPTY_TILE = "."; 
	
	enum DrawnMode {
		COLUMN,
		LINE,
		NODE,
		ITEM,
		AREA,
		ElEMENT
	}
	
	enum Mode {
		REMOVE,
		FILL
	}
	
	enum Option {
		BRUSH,
		ITEMS
	}
	
	DrawnMode drawnMode = DrawnMode.NODE;
	Mode mode = Mode.FILL;
	
	ShapeRenderer renderer = new ShapeRenderer();
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	Block grid[][] = new Block[Configuration.getLevelGridElementContentSize()][Configuration.getLevelGridElementContentSize()];
	private int mousex;
	private int mousey;
	
	private Brush brush = Configuration.brushes.get("ground");
	
	private ItemDefinition selectedItem;
	private List<ItemDefinition> items = new ArrayList<>();
	
	private AreaSelected selectedArea = new AreaSelected();
	private List<AreaDefinition> areas = new ArrayList<>();

	private ElementDefinition selectedElement;
	private List<ElementDefinition> elements = new ArrayList<>();
	
	public Chunk() {
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				grid[x][y] = new Block();
				grid[x][y].x = x;
				grid[x][y].y = y;
				grid[x][y].bounds = new Rectangle(x*GeneratorConstants.ROOM_BLOCK_SIZE, y*GeneratorConstants.ROOM_BLOCK_SIZE, GeneratorConstants.ROOM_BLOCK_SIZE, GeneratorConstants.ROOM_BLOCK_SIZE);
				grid[x][y].filled = brush;
			}
		}
		
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		info = new Info();
	}
	
	public void modeColumn() {
		drawnMode = DrawnMode.COLUMN;
	}
	
	public void modeLine() {
		drawnMode = DrawnMode.LINE;
	}
	
	public void modeNode() {
		drawnMode = DrawnMode.NODE;
	}	
	
	public void modeErase() {
		mode = Mode.REMOVE;
	}
	
	public void modeFill() {
		mode = Mode.FILL;
	}	
	
//	public void click(int x, int y) {
//		
//	}
//	
//	public void mousePointer(int x, int y) {
//		this.mousex = x;
//		this.mousey = y;
//		
//		System.out.println("y:" + y);
//		System.out.println("x:" + x);
//	}
	
	public boolean middle(int x, int y) {
		return x == Configuration.getStartPosition() 
			   || y == Configuration.getStartPosition()
			   || x == Configuration.getStartPosition()+1
			   || x == Configuration.getStartPosition()+2
			   || y == Configuration.getStartPosition()+1
			   || y == Configuration.getStartPosition()+2;
	}
	
	public void drawn() {
		
		CameraHolder.instance().getOrtho().position.x = (grid.length * GeneratorConstants.ROOM_BLOCK_SIZE) / 2;
		CameraHolder.instance().getOrtho().position.y = (grid[0].length * GeneratorConstants.ROOM_BLOCK_SIZE) / 2;
		CameraHolder.instance().getOrtho().zoom = -100;
		CameraHolder.instance().getOrtho().update();
		renderer.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				
				if(grid[x][y].filled != null) {
					renderer.begin(ShapeType.Filled);
					float col [] = grid[x][y].filled.color;
					renderer.setColor(col[0], col[1], col[2], col[3]);
					
					renderer.rect(x * GeneratorConstants.ROOM_BLOCK_SIZE, 
							y * GeneratorConstants.ROOM_BLOCK_SIZE, 
							GeneratorConstants.ROOM_BLOCK_SIZE, 
							GeneratorConstants.ROOM_BLOCK_SIZE);
					renderer.end();						
				}
				
				//Drawn selection
				if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
					renderer.begin(ShapeType.Filled);
					renderer.setColor(Color.RED);
					
					renderer.rect(x * GeneratorConstants.ROOM_BLOCK_SIZE, 
							y * GeneratorConstants.ROOM_BLOCK_SIZE, 
							GeneratorConstants.ROOM_BLOCK_SIZE, 
							GeneratorConstants.ROOM_BLOCK_SIZE);
					renderer.end();
				}
				
				//drawn line
				renderer.begin(ShapeType.Line);
				if(middle(x, y))
					renderer.setColor(Color.YELLOW);
				else
					renderer.setColor(Color.RED);
				renderer.rect(x * GeneratorConstants.ROOM_BLOCK_SIZE, 
						y * GeneratorConstants.ROOM_BLOCK_SIZE, 
						GeneratorConstants.ROOM_BLOCK_SIZE, 
						GeneratorConstants.ROOM_BLOCK_SIZE);
				renderer.end();					
				
			}
		}
		
		drawnMenu();
		drawnMode();
		drawnItems();
		drawnAreas();
		drawnElements();
		drawnItemSelected();
		drawnAreaSelected();
		drawnElementSelected();
	}

	public void fillOpen() {
		
		int left = 0;
		int right = 0;
		int bottom = 0;
		int top = 0;
		
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				if(x == 0 && middle(x, y)) {//RIGHT
					if(grid[x][y].filled.tile.equals(EMPTY_TILE))
						right++;
				} else if(x == grid.length-1 && middle(x, y)) {//LEFT
					if(grid[x][y].filled.tile.equals(EMPTY_TILE))
						left++;
				} else if(y == grid.length-1 && middle(x, y)) {//BOTTOM
					if(grid[x][y].filled.tile.equals(EMPTY_TILE))
						bottom++;
				} else if(y == 0 && middle(x, y)) {//TOP
					if(grid[x][y].filled.tile.equals(EMPTY_TILE))
						top++;
				}	
			}
		}
		
		info.open = new ArrayList<>(); 
		if(top >= 3) {
			info.open.add(Position.TOP.name());
		}
		
		if(bottom >= 3) {
			info.open.add(Position.BOTTOM.name());
		}
		
		if(left >= 3) {
			info.open.add(Position.LEFT.name());
		}
		
		if(right >= 3) {
			info.open.add(Position.RIGHT.name());
		}	
	}

	void drawnMenu() {
		
		font.getData().setScale(-4, -4);
		
		spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		spriteBatch.begin();
		font.draw(spriteBatch, "Brush:" + brush.name, -100, -60);
		spriteBatch.end();
		
		renderer.begin(ShapeType.Filled);
		renderer.setColor(brush.color[0], brush.color[1], brush.color[2], brush.color[3]);
		renderer.rect(-256, 0, 128, 128);
		renderer.end();
		
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.RED);
		renderer.rect(-256, 0, 128, 128);
		renderer.end();				
	}
	
	void drawnMode() {
		
		font.getData().setScale(-4, -4);
		
		spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		spriteBatch.begin();
		
		font.draw(spriteBatch, "MODE: " + mode.name(), 1900, -60);
		font.draw(spriteBatch, "DMODE: " + drawnMode.name(), 500, -60);
		
		spriteBatch.end();
	}	
	
	void drawnItemSelected() {
		
		if(selectedItem == null || drawnMode != DrawnMode.ITEM)
			return;
		
		font.getData().setScale(-4, -4);
		
		spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		spriteBatch.begin();
		
		font.draw(spriteBatch, "ITEM: " + selectedItem.name, 1900, 1950);
		
		spriteBatch.end();
		
		selectedItem.draw(renderer, this.mousex, this.mousey);
	}
	
	void drawnElementSelected() {
		
		if(selectedElement == null || drawnMode != DrawnMode.ElEMENT)
			return;
		
		font.getData().setScale(-4, -4);
		
		spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		spriteBatch.begin();
		
		font.draw(spriteBatch, "ELEMENT: " + selectedElement.name, 1900, 1950);
		
		spriteBatch.end();
		
		selectedElement.draw(renderer, this.mousex, this.mousey);
	}	
	
	
	void drawnAreaSelected() {
		selectedArea.drawSelectedAreaLabel();
		selectedArea.draw();
	}
	
	private void drawnItems() {
		items.forEach(it -> {
			it.draw(renderer);
		});
	}
	
	private void drawnAreas() {
		areas.forEach(it -> {
			it.draw(renderer);
		});
	}
	
	private void drawnElements() {
		elements.forEach(it -> {
			it.draw(renderer);
		});
	}	
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	boolean pressed = false;
	private Info info;
	@Override
	public boolean keyUp(int keycode) {
		
		if(Input.Keys.SPACE == keycode) {
			pressed = !pressed;
		}
		
		if(Input.Keys.S == keycode) {
			save();
		}
		
		if(Input.Keys.O == keycode) {
			load();
		}
		
		if(Input.Keys.UP == keycode) {
			previousBrush();
		}
		
		if(Input.Keys.DOWN == keycode) {
			nextBrush();
		}
		
		if(Input.Keys.LEFT == keycode) {
			
			if(this.drawnMode == DrawnMode.AREA)
				previousArea();
			else if(this.drawnMode == DrawnMode.ITEM)
				previousItem();
			else if(this.drawnMode == DrawnMode.ElEMENT)
				previousElement();
		}
		
		if(Input.Keys.RIGHT == keycode) {
			if(this.drawnMode == DrawnMode.AREA)
				nextArea();
			else if(this.drawnMode == DrawnMode.ITEM)
				nextItem();
			else if(this.drawnMode == DrawnMode.ElEMENT)
				nextElement();
		}		

		if(Input.Keys.I == keycode) {
			this.drawnMode = DrawnMode.ITEM;
			selectedItem = Configuration.itemsList.get(0);
		}
		
		if(Input.Keys.E == keycode) {
			this.drawnMode = DrawnMode.ElEMENT;
			selectedElement = Configuration.elements.list.get(0);
		}		
		
		if(Input.Keys.A == keycode) {
			this.drawnMode = DrawnMode.AREA;
			selectedArea.reset();
		}		
		
		if(Input.Keys.R == keycode) {
			this.mode = Mode.REMOVE;
		}
		
		if(Input.Keys.F == keycode) {
			this.mode = Mode.FILL;
		}
		
		if(Input.Keys.C == keycode) {
			this.drawnMode = DrawnMode.COLUMN;
		}		
		
		if(Input.Keys.L == keycode) {
			this.drawnMode = DrawnMode.LINE;
		}
		
		if(Input.Keys.N == keycode) {
			this.drawnMode = DrawnMode.NODE;
		}
		
		return false;
	}

	private void load() {
		try {
			info = ProjectSaver.load(grid);
			
			if(info.areas != null)
				areas = info.areas;//.stream().map(a -> Configuration.areas.areas.get(a)).collect(Collectors.toList());
			else
				areas = new ArrayList<>();
			
			if(info.items != null)
				items = info.items;
			else
				items = new ArrayList<>();
			
			if(info.elements != null)
				elements = info.elements;
			else
				elements = new ArrayList<>();
			
//			areas = info.areas;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		info.items = items;//.stream().map(it -> it.name).collect(Collectors.toList());
		info.areas = areas;//.stream().map(it -> it.name).collect(Collectors.toList());
		info.elements = elements;
		fillOpen();
		ProjectSaver.save(info, grid);
	}

	private void nextBrush() {
		if(brush.index < Configuration.brushesList.size()-1) {
			brush = Configuration.brushesList.get(brush.index+1);
		} else {
			brush = Configuration.brushesList.get(0);
		}
	}

	private void previousBrush() {
		if(brush.index > 0) {
			brush = Configuration.brushesList.get(brush.index-1);
		} else {
			brush = Configuration.brushesList.get(Configuration.brushesList.size()-1);
		}
	}
	
	private void nextItem() {
		
		if(selectedItem == null) {
			selectedItem = Configuration.itemsList.get(0);
		}
		
		if(selectedItem.index < Configuration.itemsList.size()-1) {
			selectedItem = Configuration.itemsList.get(selectedItem.index+1);
		} else {
			selectedItem = Configuration.itemsList.get(0);
		}
	}

	private void previousItem() {
		
		if(selectedItem == null) {
			selectedItem = Configuration.itemsList.get(0);
		}		
		
		if(selectedItem.index > 0) {
			selectedItem = Configuration.itemsList.get(selectedItem.index-1);
		} else {
			selectedItem = Configuration.itemsList.get(Configuration.itemsList.size()-1);
		}
	}
	
	private void nextElement() {
		
		if(selectedElement == null) {
			selectedElement = Configuration.elements.list.get(0);
		}
		
		if(selectedElement.index < Configuration.elements.list.size()-1) {
			selectedElement = Configuration.elements.list.get(selectedElement.index+1);
		} else {
			selectedElement = Configuration.elements.list.get(0);
		}
	}

	private void previousElement() {
		
		if(selectedElement == null) {
			selectedElement = Configuration.elements.list.get(0);
		}		
		
		if(selectedElement.index > 0) {
			selectedElement = Configuration.elements.list.get(selectedElement.index-1);
		} else {
			selectedElement = Configuration.elements.list.get(Configuration.elements.list.size()-1);
		}
	}	
	
	private void nextArea() {
		
		if(!selectedArea.canSelect())
			return;		
		
		if(!selectedArea.isSelected()) {
			selectedArea.area = Configuration.areas.list.get(0);
		}
		
		if(selectedArea.area.index < Configuration.areas.list.size()-1) {
			selectedArea.area = Configuration.areas.list.get(selectedArea.area.index+1);
		} else {
			selectedArea.area = Configuration.areas.list.get(0);
		}
	}

	private void previousArea() {
		
		if(!selectedArea.canSelect())
			return;
		
		if(!selectedArea.isSelected()) {
			selectedArea.area = Configuration.areas.list.get(0);
		}
		
		if(selectedArea.area.index > 0) {
			selectedArea.area = Configuration.areas.list.get(selectedArea.area.index-1);
		} else {
			selectedArea.area = Configuration.areas.list.get(Configuration.areas.list.size()-1);
		}
	}		

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.apply();
		return false;
	}

	private void apply() {
		if(drawnMode == DrawnMode.AREA) {
			applyArea();
		} else
		if(drawnMode == DrawnMode.ITEM) {
			applyItem();
		} else
		if(drawnMode == DrawnMode.ElEMENT) {
			applyElement();
		} else			
		if(drawnMode == DrawnMode.COLUMN) {
			applyColumn();
		} else if(drawnMode == DrawnMode.NODE) {
			applyNode();
		} else if(drawnMode == DrawnMode.LINE) {
			applyLine();
		}		
	}
	
	private void applyElement() {
		if(mode == Mode.REMOVE) {
			Optional<ElementDefinition> element = elements.stream().filter(it -> it.contains(this.mousex, this.mousey)).findFirst();
			element.ifPresent(it -> {
				elements.remove(it);
			});
		} else {
			if(selectedElement != null) {
				ElementDefinition item = selectedElement.copy();
//				item.setPosition(this.mousex, this.mousey);
				
				for(int x = 0; x < grid.length; x++) {
					for(int y = 0; y < grid[0].length; y++) {
						if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
							item.setPosition(x, y);
							break;
						}
					}
				}				
				
				elements.add(item);
			}
		}
	}

	private void applyItem() {
		if(mode == Mode.REMOVE) {
			Optional<ItemDefinition> item = items.stream().filter(it -> it.contains(this.mousex, this.mousey)).findFirst();
			item.ifPresent(it -> {
				items.remove(it);
			});
		} else {
			if(selectedItem != null) {
				
				ItemDefinition item = selectedItem.copy();
				
				for(int x = 0; x < grid.length; x++) {
					for(int y = 0; y < grid[0].length; y++) {
						if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
							item.setPosition(x, y);
							break;
						}
					}
				}
				
				items.add(item);
			}
		}
	}
	
	private void applyArea() {
		if(mode == Mode.REMOVE) {
			Optional<AreaDefinition> area = areas.stream().filter(it -> it.contains(this.mousex, this.mousey)).findFirst();
			area.ifPresent(it -> {
				areas.remove(it);
			});
		} else {
			if(selectedArea.isSelected()) {
				
				for(int x = 0; x < grid.length; x++) {
					for(int y = 0; y < grid[0].length; y++) {
						if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
							selectedArea.setPosition(x, y);
							break;
						}
					}
				}				
				
			}
		}
	}	

	private void applyLine() {
		
		Brush fill = brush;
		if(mode == Mode.REMOVE) {
			fill = Configuration.brushes.get("space");
		}
		
		Block first = null;
		
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
					first = grid[x][y];
					break;
				}
			}
		}
		
		if(first != null)
			for(int x = 0; x < grid.length; x++) {
				grid[x][first.y].filled = fill;
			}
	}

	private void applyNode() {
		Brush fill = brush;
		if(mode == Mode.REMOVE) {
			fill = Configuration.brushes.get("space");
		}
		
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
					grid[x][y].filled = fill;
					break;
				}
			}
		}
	}

	private void applyColumn() {
		Brush fill = brush;
		if(mode == Mode.REMOVE) {
			fill = Configuration.brushes.get("space");
		}
		
		Block first = null;
		
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				if(grid[x][y].bounds.contains(this.mousex, this.mousey)) {
					first = grid[x][y];
					break;
				}
			}
		}
		
		if(first != null)
			for(int y = 0; y < grid.length; y++) {
				grid[first.x][y].filled = fill;
			}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		this.apply();
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		this.mousex = screenX;
		this.mousey = screenY;
		
		Vector3 point = CameraHolder.instance().getOrtho().unproject(new Vector3(this.mousex, this.mousey, 0));
		
		this.mousex = (int) point.x;
		this.mousey = (int) point.y;
		
//		this.mousex = (this.mousex % GeneratorConstants.ROOM_BLOCK_SIZE) + GeneratorConstants.ROOM_BLOCK_SIZE;
//		this.mousey = (this.mousey % GeneratorConstants.ROOM_BLOCK_SIZE) + GeneratorConstants.ROOM_BLOCK_SIZE;
		
		if(pressed) {
			apply();
		}
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	class Block {
		int x, y;
		Brush filled;
		Rectangle bounds;
	}
	
	class AreaSelected {
		
		AreaDefinition area;
		
		int x = -1, y = -1;
		int nx = -1, ny = -1;
		
		public boolean canSelect() {
			return x == -1;
		}
		
		public boolean isSelected() {
			return area != null;
		}
		
		public void drawSelectedAreaLabel() {
			
			if(!selectedArea.isSelected() || drawnMode != DrawnMode.AREA)
				return;
			
			font.getData().setScale(-4, -4);
			
			spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
			spriteBatch.begin();
			
			font.draw(spriteBatch, "AREA: " + area.name, 1900, 1950);
			
			spriteBatch.end();
		}
		
		public void setPosition(int x, int y) {
			if(this.x == -1) {
				this.x = x;
				this.y = y;
				area.setPosition(x, y);
			} else {
				area.setSize(x, y);
				areas.add(area);
				reset();
			}
		}
		
		void reset() {
			x = y = nx = ny = -1;
			area = null;
		}
		
		public void draw() {
			
			if(area == null || drawnMode != DrawnMode.AREA)
				return;
			
			if(x != -1) {
//				drawCircle();
//			} else {
				drawArea();
			}
		}

//		private void drawCircle() {
//			renderer.begin(ShapeType.Filled);
//			renderer.setColor(area.color[0], area.color[1], area.color[2], area.color[3]);
//			renderer.circle(mousex, mousey, 2);
//			renderer.end();
//		}
		
		private void drawArea() {
			
			int nextX = this.x;
			int nextY = this.y;
			
			OUT: for(int lx = this.x; lx < grid.length; lx++) {
				for(int ly = this.y; ly < grid[0].length; ly++) {
					if(grid[lx][ly].bounds.contains(mousex, mousey)) {
						System.out.println(lx);
						System.out.println(ly);
						nextX = lx;
						nextY = ly;
						break OUT;
					}
				}
			}
		
			renderer.begin(ShapeType.Filled);
			renderer.setColor(area.color[0], area.color[1], area.color[2], 1);
			renderer.rect(x * GeneratorConstants.ROOM_BLOCK_SIZE, y * GeneratorConstants.ROOM_BLOCK_SIZE,
					((nextX - x)+1) * GeneratorConstants.ROOM_BLOCK_SIZE, ((nextY - y)+1) * GeneratorConstants.ROOM_BLOCK_SIZE);
			renderer.end();
			
			area.setSize(nextX, nextY);
//			System.out.println("AREA X: " + ((mousex/GeneratorConstants.ROOM_BLOCK_SIZE) - x));
//			System.out.println("AREA Y: " + ((mousex/GeneratorConstants.ROOM_BLOCK_SIZE) - y));
//			renderer.begin(ShapeType.Filled);
//			renderer.setColor(area.color[0], area.color[1], area.color[2], area.color[3]);
//			renderer.circle(mousex, mousey, 2);
//			renderer.end();
//			area.draw(renderer);
		}
	}
}
