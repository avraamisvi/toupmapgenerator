package com.toupety.mapgen.cave;

import com.toupety.mapgen.Position;

public abstract class PredefinedMiner extends Miner {

	public PredefinedMiner(int x, int y, CaveGenerator generator) {
		super(x, y, generator);
	}

	public abstract void setPointPosition(Position pos);
}
