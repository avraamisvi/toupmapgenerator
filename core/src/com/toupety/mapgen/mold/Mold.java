package com.toupety.mapgen.mold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.toupety.mapgen.GeneratorConstants;

public class Mold {

	private MoldMeta meta;
	private List<MoldBlock> blocks;
	int width  = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE; 
	int height = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE;
	
	public Mold(MoldMeta meta, List<String> lines) {
		blocks = new ArrayList<>();
		
		width  = meta.maxWidth; 
		height = meta.maxHeigth;
		
		String line;
		for(int y = 0; y < lines.size(); y++) {
			line = lines.get(y);
			for(int x = 0; x < line.length(); x++) {
				blocks.add(new MoldBlock(line.charAt(x), x, y));//TODO metadata for each block?
			}
		}
		
		this.meta = meta;
	}
	
	public MoldMeta getMeta() {
		return meta;
	}
	
	public Stream<MoldBlock> stream() {
		return blocks.stream();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
