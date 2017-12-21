package com.toupety.mapgen.mold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.toupety.mapgen.Constants;

public class Mold {

	private List<MoldBlock> blocks;
	int width  = Constants.LEVEL_BLOCK_WIDTH / Constants.ROOM_BLOCK_SIZE; 
	int height = Constants.LEVEL_BLOCK_WIDTH / Constants.ROOM_BLOCK_SIZE;
	
	public Mold(List<String> data) {
		blocks = new ArrayList<>();
		
		String line;
		for(int y = 0; y < data.size(); y++) {
			line = data.get(y);
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
