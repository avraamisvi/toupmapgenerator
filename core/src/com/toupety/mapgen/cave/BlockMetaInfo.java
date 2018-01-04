package com.toupety.mapgen.cave;

import com.toupety.mapgen.mold.MoldBlock;

public class BlockMetaInfo {

	public static final BlockMetaInfo EMPTY = new BlockMetaInfo(".");
	public static final BlockMetaInfo FULL =  new BlockMetaInfo("x");
	
	private String type;
	
	public BlockMetaInfo(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
//	public void setType(String type) {
//		this.type = type;
//	}
	
	public static BlockMetaInfo parse(MoldBlock block) {//TODO temporary?
		if(block.getValue() == '.') {
			return EMPTY;
		} else {
			return FULL;
		}
	}
}
