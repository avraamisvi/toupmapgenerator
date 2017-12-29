package com.toupety.mapgen.cave;

import com.toupety.mapgen.Position;

public class CaracolMiner extends PredefinedMiner {
	
	HorizontalMiner h1;
	HorizontalMiner h2;
	HorizontalMiner h3;
	HorizontalMiner h4;
	
	public CaracolMiner(int x, int y, CaveGenerator generator) {
		super(x, y, generator);
		setMiners();
	}

	void setMiners() {
		h1 = new HorizontalMiner(x + 8, y, generator);
		h1.setH(3);
		h1.setW(12);
		
		h2 = new HorizontalMiner(h1.x, h1.y + 8, generator);
		h2.setH(4);
		h2.setW(6);
		
		h3 = new HorizontalMiner(h1.x - 5, h2.y + 1, generator);
		h3.setH(3);
		h3.setW(5);
		
		h4 = new HorizontalMiner(h3.x - 5, h3.y - 5, generator);
		h4.setH(8);
		h4.setW(2);		
	}
	
	@Override
	public boolean isDead() {
		return h1.isDead() && h2.isDead() && h3.isDead() && h4.isDead();
	}
	
	@Override
	boolean getChance() {
		return false;
	}
	
	@Override
	void minning() {
		this.h1.update();
		this.h2.update();
		this.h3.update();
		this.h4.update();
	}
	
	@Override
	Miner spaw(int x2, int y2, CaveGenerator generator) {
		return MinerFactory.get().getRandom(this.x, this.y, generator);
	}

	@Override
	public void setPointPosition(Position pos) {
		switch (pos) {
		case TOP:
			this.y = this.y + 1; 
			break;
		case BOTTOM:
			this.y = this.y - 12;
			break;
		case LEFT:
			this.x = this.x + 12;
			break;
		case RIGHT:
			this.x = this.x - 1;
			break;			
		default:
			break;
		}
		
		setMiners();
	}
}
