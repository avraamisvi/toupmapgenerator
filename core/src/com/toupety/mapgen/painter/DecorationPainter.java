package com.toupety.mapgen.painter;

import java.util.Optional;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Configuration;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;

public class DecorationPainter {
	
	RandomXS128 rand = new RandomXS128();
	
	public DecorationPainter() {
		rand.nextInt();rand.nextInt();
	}	
	
	public void process(RoomBlocks room) {
		DecorationPalette palette = Configuration.properties.decorations;
		palette.sort();
		
		for(int x = 0; x < room.getW(); x++) {
			for(int y = 0; y < room.getH(); y++) {
				
				Optional<RoomBlock> op = room.getAt(x, y);
				if(op.isPresent()) {
					if(op.get().getOwner() != null) {
						
						rand.nextFloat();
						float chance = rand.nextFloat();
						
						if(chance < palette.chance) {
							apply(room, palette, x, y);
						}
					}
				}
				
			}
		}		
	}
	
	void apply(RoomBlocks room, DecorationPalette palette, int lx, int ly) {
		
//		System.out.println("Paint");
		
		String brush = palette.getAny();
		
		if(brush == null)
			return;
		
		Decoration dec = Configuration.decorations.getDecoration(brush);
		Optional<RoomBlock> op = room.getAt(lx, ly);
		
		if(op.isPresent()) {
			if(dec.canBePlaced(op.get().getPlace())) {
				dec.x = lx;
				dec.y = ly;
				room.getOwner().getDecorations().add(dec);
			}
		}
	}
}
