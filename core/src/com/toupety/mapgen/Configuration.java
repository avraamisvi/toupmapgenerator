package com.toupety.mapgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.Json;
import com.toupety.mapgen.mold.MoldMeta;

public class Configuration {

	public static boolean invert = false;
	public static Data properties;
	
	public static Map<String, Brush> brushes;
	public static Map<Character, Brush> brushesPerTile;
	public static List<Brush> brushesList = new ArrayList<>();
	
	public static Items items;
	public static List<ItemDefinition> itemsList;
	
	
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
		
	}
	
	public static class Data {
		public String name;
		public int rooms;
		public Key key;
		public List<Item> items;
		public List<Tag> tags;
	}
	
	public static class Item {
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
		   return limit > 0 || limit == -1;
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
		   public int[] color;
	}
	
	public static class Brushes {
		public HashMap<String, Brush> brushes;
	}
	
	public static class ItemDefinition {
		public int index = 0;
		public String name;
		public String shape;
		public int width;
		public int height;
	}
	
	public static class Items {
		Map<String, ItemDefinition> items;
	}
}
