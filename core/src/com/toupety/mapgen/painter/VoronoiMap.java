package com.toupety.mapgen.painter;

import java.util.Optional;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.cave.BlockMetaInfo;

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
		
//		System.out.println("try to paint");
		
		for(int x = 0; x < room.getW(); x++) {
			for(int y = 0; y < room.getH(); y++) {
				Optional<RoomBlock> op = room.getAt(x, y);
				if(op.isPresent()) {
					if(op.get().getOwner() != null) {
						
						rand.nextFloat();
						float island = rand.nextFloat();
						
						if(island < Configuration.properties.voronoi) {
							apply(room, palette, x, y);
						}
					}
				}
			}
		}		
	}
	
//	NAO PODE SUBSTITUIR OS TILES Q NAO SAO DO TIPO GROUND X
	
	void apply(RoomBlocks room, Palette palette, int lx, int ly) {
		
//		System.out.println("Paint");
		
		String brush = palette.getAny();
		
		if(brush == null)
			return;
		
		int w = this.rand.nextInt(Configuration.properties.voronoiSize) + 1;
		int h = this.rand.nextInt(Configuration.properties.voronoiSize) + 1;
		
		int minX = Math.abs(lx - w);
		int minY = Math.abs(ly - h);
		int maxX = lx;//lx+(w/2);
		int maxY = ly;//ly+(h/2);
		
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
		
		for(int x = maxX; x >= minX; x--) {
			for(int y = minY; y < maxY; y++) {
				room.getAt(x, y).ifPresent(b -> {
					if(b.getMetaInfo().getType().equals(BlockMetaInfo.FULL.getType()))//apenas os tipos x sao substituidos
						b.setMetaInfo(new BlockMetaInfo(Configuration.brushes.get(brush).tile));//.setType();
				});
			}
		}
		
	}
}
