package com.toupety.mapgen.algorithms;

import java.util.Optional;

import com.toupety.mapgen.Dimmensions;
import com.toupety.mapgen.Level;

public interface RoomDimmensionsAlgorithm {

	Optional<Dimmensions> next(Level level);
	
}
