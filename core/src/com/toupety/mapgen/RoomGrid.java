package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.mold.Mold;

public class RoomGrid {
	
	private List<RoomBlocks> grid = new ArrayList<>();
	private int maxSize = 0;
	private Dimensions dim;
	
	public RoomGrid(Dimensions dim) {
//		IntStream.range(0, w*h).forEach(i -> grid.add(new RoomBlocks()));
//		rand = new RandomXS128();
		this.dim = dim;
		this.maxSize = (dim.getW()*dim.getH());
	}
	
	public Dimensions getDimensions() {
		return dim;
	}
	
	public boolean putNext(Mold mold) {
		
		if(grid.size() < maxSize) {
			RoomBlocks blocks = new RoomBlocks();
			blocks.put(mold);
			grid.add(blocks);
		}
		
		return true;
	}
	
	public void forEach(Consumer<RoomBlocks> c) {
		grid.forEach(c);
	}
}
