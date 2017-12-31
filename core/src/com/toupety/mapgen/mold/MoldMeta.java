package com.toupety.mapgen.mold;

import java.util.HashMap;
import java.util.List;

public class MoldMeta {

	public String name;
	public List<String> elements;
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
