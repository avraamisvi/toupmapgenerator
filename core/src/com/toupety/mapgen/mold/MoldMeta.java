package com.toupety.mapgen.mold;

import java.util.HashMap;
import java.util.List;

import com.toupety.mapgen.Configuration.AreaDefinition;
import com.toupety.mapgen.Configuration.ElementDefinition;
import com.toupety.mapgen.Configuration.ItemDefinition;

public class MoldMeta {

	public String name;
	public List<ElementDefinition> elements;
	public List<ItemDefinition> items;
	public List<AreaDefinition> areas;
	public List<String> tags;
	public int maxWidth;
	public int maxHeigth;
	public Platform platform;
	public List<String> open;
	
	List<HashMap<String, Object>> collision;
	
	public static class Platform {
		public int height;
		public int width;
	}	
}
