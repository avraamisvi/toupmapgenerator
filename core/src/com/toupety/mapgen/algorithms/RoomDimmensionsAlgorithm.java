package com.toupety.mapgen.algorithms;

import java.util.Optional;

import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;

public interface RoomDimmensionsAlgorithm {

	Optional<Dimensions> next(Level level);
	
}
