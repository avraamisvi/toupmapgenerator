package com.toupety.mapgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.toupety.mapgen.mold.MoldMeta;

public class Configuration {

	public static boolean invert = false;
	public static Data properties;
	
	public static int getLevelGridElementContentSize() {
		return (GeneratorConstants.LEVEL_BLOCK_HEIGHT / GeneratorConstants.ROOM_BLOCK_SIZE);
	}
	
	public static void load() throws FileNotFoundException {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		properties = json.fromJson(Data.class, new FileReader(Paths.get("./config.json").toFile()));
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
}
