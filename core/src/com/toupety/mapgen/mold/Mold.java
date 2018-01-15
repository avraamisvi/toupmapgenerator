package com.toupety.mapgen.mold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.toupety.mapgen.GeneratorConstants;

public class Mold {

	private MoldMeta meta;
	private MoldBlock[][] grid;
	private List<MoldBlock> blocks;
	int width  = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE; 
	int height = GeneratorConstants.LEVEL_BLOCK_WIDTH / GeneratorConstants.ROOM_BLOCK_SIZE;
	
	public Mold(MoldMeta meta, List<String> lines) {
		blocks = new ArrayList<>();
		
		width  = meta.maxWidth; 
		height = meta.maxHeigth;
		
		grid = new MoldBlock[width][height];
		
		String line;
		for(int y = 0; y < height; y++) {
			line = lines.get(y);
			for(int x = 0; x < width; x++) {
				MoldBlock b = new MoldBlock(line.charAt(x), x, y);
				b.setMeta(meta);
				blocks.add(b);//TODO metadata for each block?
				grid[x][y] = b;
			}
		}
		
		if(meta.items != null && !meta.items.isEmpty()) {
			meta.items.forEach(itm -> {
				grid[(meta.maxWidth - 1) - itm.x][itm.y].addItem(itm);//(meta.maxWidth - 1) inverte o X
			});
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
	
	public MoldBlock[][] getGrid() {
		return grid;
	}
}
