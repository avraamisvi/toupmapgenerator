package com.toupety.mapgen.algorithms;

import java.util.Optional;

import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Level;

public interface RoomAlgorithm {

	Optional<RoomAlgorithmResult> next(Level level);
	
}
