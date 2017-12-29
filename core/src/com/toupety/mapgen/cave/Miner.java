package com.toupety.mapgen.cave;

import com.toupety.mapgen.RoomBlocks;

public abstract class Miner {

	static final double CREATE_PROB = 0.06;
	static final int directions[] = new int[]{-1, 1};
	
	int x, y;
	CaveGenerator generator;
	int empty;
	int maxEmpty = 6;

	RoomBlocks blocks;
	
	public Miner(int x, int y, CaveGenerator generator) {
		super();
		this.x = x;
		this.y = y;
		this.generator = generator;			
		this.blocks = generator.getBlocks();
	}
	
	public final void createNew() {
		this.createNew(false);
	}
	
	public final void createNew(boolean force) {
		if(getChance() || force) {
			generator.addMiner(spaw(this.x, this.y, generator));
		}
	}
	
	abstract Miner spaw(int x2, int y2, CaveGenerator generator);

//	private Object spaw() {
//		MinerFactory.get().getAny(this.x, this.y, generator)
//		return null;
//	}

	boolean getChance() {
		return generator.getRand().nextDouble() < CREATE_PROB;
	}

	public boolean isDead() {
		return (empty >= maxEmpty)  && generator.miners.size() > 1;
	}

	public final void update() {
		countEmpty();		
		
		this.minning();
		
		this.createNew();
	}
	
	abstract void minning();
	
	public final void addEmpty() {
		this.empty++;
	}
	
	void countEmpty() {
		empty = 0;
		for(int lx = -1; lx <= 1; lx++) {
			for(int ly = -1; ly <= 1; ly++) {
				this.blocks.getAt(lx + x, ly + y).ifPresent(block -> {
					if(block.getMetaInfo().getType().equals(".")) {//block.isWall() || 
						addEmpty();
					}
				});
			}				
		}
		
	}
}
