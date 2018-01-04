package com.toupety.mapgen.painter;

import java.util.List;
import java.util.Optional;

import com.badlogic.gdx.math.RandomXS128;

public class DecorationPalette {

	public float chance;
	public List<Chance> elements;
	RandomXS128 rand = new RandomXS128();
	private boolean sorted;
	
	public DecorationPalette() {
		rand.nextFloat();rand.nextFloat();
	}
	
	public void sort() {
		if(!sorted) {
			elements.sort((b1, b2 ) -> (int)(100*(b1.chance - b2.chance)));
		}
		
		sorted = true;
	}
	
	public String getAny() {
		rand.nextFloat();
		float ch = rand.nextFloat();
		sort();
		
		Optional<Chance> op = elements.stream().filter(b -> ch < b.chance).findFirst();
		
		if(op.isPresent()) {
			return op.get().name;
		}
		
		return null;
	}

	public static class Chance {
		public float chance;
		public String name;
	}
	
}