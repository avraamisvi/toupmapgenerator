package com.toupety.mapgen.mold;

import java.util.List;
import java.util.Map;

public class MoldMeta {

	private List<String> data;
	private int width;
	private int heigth;
	
	public MoldMeta() {
		super();
//		this.data = data;
//		this.width = Integer.parseInt(fields.get("width").toString());
//		this.heigth = Integer.parseInt(fields.get("heigth").toString());
	}
	
	public void setData(List<String> data) {
		this.data = data;
	}
	
	public List<String> getData() {
		return data;
	}
	public int getWidth() {
		return width;
	}
	public int getHeigth() {
		return heigth;
	}
	
	
}
