package com.toupety.mapgen.painter;

import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.GeneratorConstants;

public class Palette {

	public String base;
	public List<BrushChance> brushes;
	RandomXS128 rand = new RandomXS128();
	private boolean sorted;
	
	public Palette() {
		rand.nextFloat();rand.nextFloat();
	}
	
	public void sort() {
		if(!sorted) {
			brushes.sort((b1, b2 ) -> (int)(100*(b1.chance - b2.chance)));
		}
		
		sorted = true;
	}
	
	public String getAny() {
		rand.nextFloat();
		float ch = rand.nextFloat();
		sort();
		
		Optional<BrushChance> op = brushes.stream().filter(b -> ch < b.chance).findFirst();
		
		if(op.isPresent()) {
			return op.get().name;
		}
		
		return GeneratorConstants.DEFAULT_GROUND_BRUSH;//GeneratorConstants.DEFAULT_GROUND_BRUSH
	}

	public static class BrushChance {
		public float chance;
		public String name;
	}
	
}