package com.toupety.mapgen.algorithms;

import com.toupety.mapgen.Dimensions;
import com.toupety.mapgen.Room;

public class RoomAlgorithmResult {

	private Room target;
	private Dimensions dim;
	
	public RoomAlgorithmResult(Room target, Dimensions dim) {
		super();
		this.target = target;
		this.dim = dim;
	}
	
	public Room getTarget() {
		return target;
	}
	public Dimensions getDim() {
		return dim;
	}
}
