package com.toupety.mapgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toupety.mapgen.mold.MoldMeta;
import com.toupety.mapgen.painter.Decoration;
import com.toupety.mapgen.painter.DecorationPalette;
import com.toupety.mapgen.painter.Decorations;
import com.toupety.mapgen.painter.Palette;

public class Configuration {

	public static int roomToDraw = -1;
	public static boolean invert = false;
	public static Data properties;
	
	public static Map<String, Brush> brushes;
	public static Map<Character, Brush> brushesPerTile;
	public static List<Brush> brushesList = new ArrayList<>();
	
	public static Items items;
	public static List<ItemDefinition> itemsList;
	
	public static Areas areas;
	public static Elements elements;
	
	public static Decorations decorations;
	
	public static int getLevelGridElementContentSize() {
		return (GeneratorConstants.LEVEL_BLOCK_HEIGHT / GeneratorConstants.ROOM_BLOCK_SIZE);
	}

	public static int getStartPosition() {
		return ((GeneratorConstants.LEVEL_BLOCK_HEIGHT / GeneratorConstants.ROOM_BLOCK_SIZE)/2)-2;
	}
	
	public static void load() throws FileNotFoundException {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		properties = json.fromJson(Data.class, new FileReader(Paths.get("./config.json").toFile()));
		
		json.setIgnoreUnknownFields(true);
		Brushes bs = json.fromJson(Brushes.class, new FileReader(Paths.get("./brushes.json").toFile()));
		brushes = bs.brushes;
		brushesPerTile = new HashMap<>();
		brushes.values().forEach(b -> {
			b.index = brushesList.size();
			brushesList.add(b);
			brushesPerTile.put(b.tile.charAt(0), b);
		});
		
		items = json.fromJson(Items.class, new FileReader(Paths.get("./items.json").toFile()));
		itemsList = new ArrayList<>();
		items.items.values().forEach(b -> {
			b.index = itemsList.size();
			itemsList.add(b);
		});
		
		
		areas = json.fromJson(Areas.class, new FileReader(Paths.get("./areas.json").toFile()));
		areas.process();
		
		elements = json.fromJson(Elements.class, new FileReader(Paths.get("./elements.json").toFile()));
		elements.process();
		
		decorations = json.fromJson(Decorations.class, new FileReader(Paths.get("./decorations.json").toFile()));
	}
	
	public static class Data {
		public String name;
		public int rooms;
		public Key key;
		public List<ItemConfiguration> items;
		public List<Tag> tags;
		public Palette palette;
		public float voronoi;
		public int voronoiSize;
		public DecorationPalette decorations;
	}
	
	public static class ItemConfiguration {
	   public String id;
	   public int from;
	   public int limit;
	   public double chance;
	   
	   public void consume() {
		   if(limit > 0) {
			   limit--;
		   }
	   }
	   
	   public boolean isAvaiable() {
		   return limit != 0;
	   }	   
	}
	
	public static class Tag {
		   public String id;
		   public int room;
		   public int limit;
		   
		   public void consume() {
			   if(limit > 0) {
				   limit--;
			   }
		   }
		   
		   public boolean isAvaiable() {
			   return limit > 0 || limit == -1;
		   }
	}
	
	public static class Key {
		   public String color;
		   public int room;
		   public int limit;
		   
		   public void consume() {
			   if(limit > 0) {
				   limit--;
			   }
		   }
		   
		   public boolean isAvaiable() {
			   return limit > 0 || limit == -1;
		   }		   
	}
	
	public static class Brush {
		   public int index = 0;
		   public String name;
		   public String tile;
		   public float[] color;
	}
	
	public static class Brushes {
		public HashMap<String, Brush> brushes;
	}
	
	//used for moldes editor
	public static class ItemDefinition {
		public int index = 0;
		public String name;
		public String shape;
		public int width;
		public int height;
		public float[] color;
		
		public int x;
		public int y;
		public int dx;
		public int dy;
		
		@JsonIgnore
		private Rectangle bounds;

		
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			this.bounds = new Rectangle(x, y, width, height);
		}
		
		public void setDelta(int x, int y) {
			this.dx = x;
			this.dy = y;
		}	
		
		public boolean contains(int x, int y) {
			
			if(bounds == null) {
				this.bounds = new Rectangle(x, y, width, height);
			}			
			
			return bounds.contains(x, y);
		}
		
		public void draw(ShapeRenderer renderer) {
			int fx = this.x * GeneratorConstants.ROOM_BLOCK_SIZE;
			int fy = this.y * GeneratorConstants.ROOM_BLOCK_SIZE;
			
			fx = dx + fx;
			fy = dy + fy;
			
			draw(renderer, fx, fy);			
//			draw(renderer, this.x * GeneratorConstants.ROOM_BLOCK_SIZE, this.y * GeneratorConstants.ROOM_BLOCK_SIZE);
		}
		
		public void draw(ShapeRenderer renderer, int x, int y) {
			
			renderer.begin(ShapeType.Filled);
			renderer.setColor(color[0], color[1], color[2], 1);
			renderer.rect(x, y, width, height);
			renderer.end();
			
			renderer.begin(ShapeType.Line);
			renderer.setColor(Color.RED);
			renderer.rect(x, y, width, height);
			renderer.end();				
		}		
		
		public ItemDefinition copy() {
			ItemDefinition ret = new ItemDefinition();
			ret.index = this.index;
			ret.name = this.name;
			ret.shape = this.shape;
			ret.width = this.width;
			ret.height = this.height;
			ret.color = this.color;
			ret.x = this.x;
			ret.y = this.y;
			ret.dx = this.dx;
			ret.dy = this.dy;
			
			return ret;
		}
	}
	
	public static class Items {
		HashMap<String, ItemDefinition> items;
	}
	
	public static class AreaDefinition {
		public int index = 0;
		public String name;
		public int width = 5;
		public int height = 5;
		public float[] color;
		
		public int x;
		public int y;
		
		@JsonIgnore
		private Rectangle bounds;
		
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			this.bounds = new Rectangle(x, y, width, height);
		}
		
		public void setSize(int nx, int ny) {
			
			int w = nx - x;
			int h = ny - y;
			
			if(w < 0)
				w = 1;
			
			if(h < 0)
				h = 1;
			
			width = w + 1;
			height = h + 1;
			
			this.bounds.height = h;
			this.bounds.width = w;			
		}		
		
		public boolean contains(int x, int y) {
			
			if(bounds == null) {
				this.bounds = new Rectangle(x, y, width, height);
			}
			
			return bounds.contains(x, y);
		}
		
		public void draw(ShapeRenderer renderer) {
			draw(renderer, this.x * GeneratorConstants.ROOM_BLOCK_SIZE, this.y * GeneratorConstants.ROOM_BLOCK_SIZE);
		}
		
		public void draw(ShapeRenderer renderer, int x, int y) {
			
			renderer.begin(ShapeType.Filled);
			renderer.setColor(color[0], color[1], color[2], 1);
			renderer.rect(x, y, width * GeneratorConstants.ROOM_BLOCK_SIZE, height * GeneratorConstants.ROOM_BLOCK_SIZE);
			renderer.end();
			
			renderer.begin(ShapeType.Line);
			renderer.setColor(Color.WHITE);
			renderer.rect(x, y, width * GeneratorConstants.ROOM_BLOCK_SIZE, height * GeneratorConstants.ROOM_BLOCK_SIZE);
			renderer.end();				
		}		
		
		public AreaDefinition copy() {
			AreaDefinition ret = new AreaDefinition();
			ret.index = this.index;
			ret.name = this.name;
			ret.width = this.width;
			ret.height = this.height;
			ret.color = this.color;
			ret.x = this.x;
			ret.y = this.y;
			
			return ret;
		}
	}
	
	public static class Areas {
		public HashMap<String, AreaDefinition> areas;
		public List<AreaDefinition> list = new ArrayList<>();
		
		public void process() {
			areas.values().forEach(b -> {
				b.index = list.size();
				list.add(b);
			});
		}
	}	
	
	public static class ElementDefinition {
		public int index = 0;
		public String name;
		public String shape;
		public int width;
		public int height;
		public float[] color;
		
		public int x;
		public int y;
		
		@JsonIgnore
		private Rectangle bounds;
		public int dx;
		public int dy;
		
		public void setDelta(int x, int y) {
			this.dx = x;
			this.dy = y;
		}		
		
		//o bloco o qual o elemento esta associado
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			this.bounds = new Rectangle(x, y, width, height);
		}
		
		public boolean contains(int x, int y) {
			
			if(bounds == null) {
				this.bounds = new Rectangle(x, y, width, height);
			}			
			
			return bounds.contains(x, y);
		}
		
		public void draw(ShapeRenderer renderer) {
			
			int fx = this.x * GeneratorConstants.ROOM_BLOCK_SIZE;
			int fy = this.y * GeneratorConstants.ROOM_BLOCK_SIZE;
			
			fx = dx + fx;
			fy = dy + fy;
			
			draw(renderer, fx, fy);
		}
		
		public void draw(ShapeRenderer renderer, int x, int y) {
			
//			int realW = width  * GeneratorConstants.ROOM_BLOCK_SIZE;
//			int realH = height * GeneratorConstants.ROOM_BLOCK_SIZE;
			
			renderer.begin(ShapeType.Filled);
			renderer.setColor(color[0], color[1], color[2], 1);
//			renderer.rect(x - width/4, y - height/4, width, height);
			renderer.rect(x, y, width, height);
			renderer.end();
			
			renderer.begin(ShapeType.Line);
			renderer.setColor(Color.RED);
//			renderer.rect(x - width/4, y - height/4, width, height);
			renderer.rect(x, y, width, height);
			renderer.end();				
		}		
		
		public ElementDefinition copy() {
			ElementDefinition ret = new ElementDefinition();
			ret.index = this.index;
			ret.name = this.name;
			ret.shape = this.shape;
			ret.width = this.width;
			ret.height = this.height;
			ret.color = this.color;
			ret.x = this.x;
			ret.y = this.y;
			
			return ret;
		}
	}
	
	public static class Elements {
		public HashMap<String, ElementDefinition> elements;
		public List<ElementDefinition> list = new ArrayList<>();
		
		public void process() {
			elements.values().forEach(b -> {
				b.index = list.size();
				list.add(b);
			});
		}
	}
	
}
