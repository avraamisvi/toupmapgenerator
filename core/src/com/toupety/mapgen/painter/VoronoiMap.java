package com.toupety.mapgen.painter;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.RoomBlocks;

public class VoronoiMap {

//	private RoomBlocks owner;
//	private int w;
//	private int h;
	RandomXS128 rand = new RandomXS128();
	
	public VoronoiMap() {
		
//		Dimensions worldDim = owner.getDim().toRoomWorldDimmensions();
//		this.w = worldDim.getW() / GeneratorConstants.ROOM_BLOCK_SIZE;
//		this.h = worldDim.getH() / GeneratorConstants.ROOM_BLOCK_SIZE;
		
		rand.nextInt();rand.nextInt();
	}	
	
	public void process(RoomBlocks room) {
		Palette palette = Configuration.properties.palette;
		palette.sort();
		
		for(int x = 0; x < room.getW(); x++) {
			for(int y = 0; y < room.getH(); y++) {
				apply(room, palette, x, y);
			}
		}		
	}
	
	void apply(RoomBlocks room, Palette palette, int lx, int ly) {
		
		String brush = palette.getAny();
		
		if(brush == null)
			return;
		
		int w = this.rand.nextInt(GeneratorConstants.MAX_VORONOI_ISLAND_SIZE) + GeneratorConstants.MAX_VORONOI_ISLAND_SIZE/2;
		int h = this.rand.nextInt(GeneratorConstants.MAX_VORONOI_ISLAND_SIZE) + GeneratorConstants.MAX_VORONOI_ISLAND_SIZE/2;
		
		int minX = Math.abs(lx-(w/2));
		int minY = Math.abs(ly-(h/2));
		int maxX = lx+(w/2);
		int maxY = ly+(h/2);
		
		if(minX < 0) {
			minX = 0;
		}
		
		if(minY < 0) {
			minY = 0;
		}			
		
		if(maxX > room.getW()) {
			maxX = room.getW();
		}
		
		if(maxY > room.getH()) {
			maxY = room.getH();
		}		
		
		for(int x = minX; x < maxX; x++) {
			for(int y = minY; y < maxY; y++) {
				
				room.getAt(x, y).ifPresent(b -> {
					if(!b.getMetaInfo().getType().equals("."))
						b.getMetaInfo().setType(Configuration.brushes.get(brush).tile);
				});
			}
		}	
		
	}
 	
//	class VoronoiMapBlock {
//		int x; 
//		int y;
//		String brush;
//		
//		public VoronoiMapBlock(int x, int y, String brush) {
//			super();
//			this.x = x;
//			this.y = y;
//			this.brush = brush;
//		}
//	}
}
