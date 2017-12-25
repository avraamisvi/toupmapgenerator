package com.toupety.mapgen.mold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.toupety.mapgen.GeneratorConstants;

public class Mold {

	private List<MoldBlock> blocks;
	int width  = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE; 
	int height = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE;
	
	public Mold(MoldMeta meta) {
		blocks = new ArrayList<>();
		
		width  = meta.getWidth(); 
		height = meta.getHeigth();
		
		String line;
		for(int y = 0; y < meta.getData().size(); y++) {
			line = meta.getData().get(y);
			for(int x = 0; x < line.length(); x++) {
				blocks.add(new MoldBlock(line.charAt(x), x, y));//TODO metadata for each block?
			}
		}
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
