package com.toupety.mapgen.cave;

public class BlockMetaInfo {

	public static final BlockMetaInfo EMPTY = new BlockMetaInfo(".");
	
	private String type;
	
	public BlockMetaInfo(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
