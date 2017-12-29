package com.toupety.mapgen.cave;

public class RandomMiner extends Miner {

	private double chance;

	public RandomMiner(int x, int y, CaveGenerator generator) {
		super(x, y, generator);
		chance = 1;//this.generator.getRand().nextDouble()
	}

	@Override
	void minning() {
		
		if(this.x <= 0) {
			this.x = 1;
		}
		else if(this.x >= blocks.getW()) {
			this.x = blocks.getW()-1;
		} else {				
			if(generator.getRand().nextDouble() < chance)
				this.x = this.x + directions[this.generator.getRand().nextInt(2)];
		}
		
		if(this.y <= 0) {
			this.y = 1;
		} 
		else if(this.y >= blocks.getH()) {
			this.y = blocks.getH()-1;
		} else {
			if(generator.getRand().nextDouble() < chance)
				this.y = this.y + directions[this.generator.getRand().nextInt(2)];
		}
		
		this.blocks.getAt(x, y).ifPresent(block -> {
			if(!block.isWall() && !block.getMetaInfo().getType().equals(".")) {
				block.setMetaInfo(BlockMetaInfo.EMPTY);
			}
		});
	}

	@Override
	Miner spaw(int x2, int y2, CaveGenerator generator) {
		return MinerFactory.get().getRandom(this.x, this.y, generator);
	}
}
