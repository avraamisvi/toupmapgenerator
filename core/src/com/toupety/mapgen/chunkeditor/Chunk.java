package com.toupety.mapgen.chunkeditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.toupety.mapgen.Configuration.Brush;
import com.toupety.mapgen.Configuration.ItemDefinition;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.Room.RoomLevelBlockElement;
import com.toupety.mapgen.chunkeditor.ProjectSaver.Info;

public class Chunk implements InputProcessor{

	enum DrawnMode {
		COLUMN,
		LINE,
		NODE		
	}
	
	enum Mode {
		ERASE,
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
		mode = Mode.ERASE;
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
					int col [] = grid[x][y].filled.color;
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
		
//		drawnCursor();
		drawnMenu();
		drawnMode();
	}

	private void drawnCursor() {
		renderer.begin(ShapeType.Filled);
		int w = GeneratorConstants.ROOM_BLOCK_SIZE;
		int h = GeneratorConstants.ROOM_BLOCK_SIZE;
		
		if(drawnMode == DrawnMode.LINE) {
			h = h /2;
		}
		if(drawnMode == DrawnMode.COLUMN) {
			w = w /2;
		}		
		if(mode == Mode.ERASE) {
			renderer.setColor(Color.BLACK);
		}
		if(mode == Mode.FILL) {
			renderer.setColor(brush.color[0], brush.color[1], brush.color[2], brush.color[3]);
		}
		
		renderer.rect(this.mousex,  this.mousey, w, h);
		renderer.end();	
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
		
		font.getData().setScale(-4, -4);
		
		spriteBatch.setProjectionMatrix(CameraHolder.instance().getOrtho().combined);
		spriteBatch.begin();
		
		font.draw(spriteBatch, "MODE: " + mode.name(), 1900, -60);
		font.draw(spriteBatch, "DMODE: " + drawnMode.name(), 500, -60);
		
		spriteBatch.end();
	}		
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	boolean pressed = false;
	private Info info;
	@Override
	public boolean keyUp(int keycode) {
		
		if(Input.Keys.A == keycode) {
			pressed = !pressed;
		}
		
		if(Input.Keys.S == keycode) {
			ProjectSaver.save(info, grid);
		}
		
		if(Input.Keys.O == keycode) {
			try {
				info = ProjectSaver.load(grid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(Input.Keys.UP == keycode) {
			previousBrush();
		}
		
		if(Input.Keys.DOWN == keycode) {
			nextBrush();
		}
		
		if(Input.Keys.LEFT == keycode) {
			previousItem();
		}
		
		if(Input.Keys.RIGHT == keycode) {
			nextItem();
		}		
		
		if(Input.Keys.E == keycode) {
			this.mode = Mode.ERASE;
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
		if(selectedItem == null || selectedItem.index < Configuration.itemsList.size()-1) {
			selectedItem = Configuration.itemsList.get(selectedItem.index+1);
		} else {
			selectedItem = Configuration.itemsList.get(0);
		}
	}

	private void previousItem() {
		if(selectedItem == null || selectedItem.index > 0) {
			selectedItem = Configuration.itemsList.get(selectedItem.index-1);
		} else {
			selectedItem = Configuration.itemsList.get(Configuration.itemsList.size()-1);
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
		if(drawnMode == DrawnMode.COLUMN) {
			applyColumn();
		} else if(drawnMode == DrawnMode.NODE) {
			applyNode();
		} else if(drawnMode == DrawnMode.LINE) {
			applyLine();
		}		
	}
	
	private void applyLine() {
		
		Brush fill = brush;
		if(mode == Mode.ERASE) {
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
		if(mode == Mode.ERASE) {
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
		if(mode == Mode.ERASE) {
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
}
