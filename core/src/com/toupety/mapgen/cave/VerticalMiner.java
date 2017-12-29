package com.toupety.mapgen.cave;

import java.util.Optional;

import com.toupety.mapgen.Position;
import com.toupety.mapgen.RoomBlocks.RoomBlock;

public class VerticalMiner extends PredefinedMiner {

	int w = 3;
	int h = 8;
	int downCount = 0;
	int downUpDirection = 0;//0 down, 1 up
	int direction = 1;
	
	public VerticalMiner(int x, int y, CaveGenerator generator) {
		super(x, y, generator);
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public void setW(int w) {
		this.w = w;
	}
	
	public void setH(int h) {
		this.h = h;
	}

	@Override
	boolean getChance() {
		return false;
	}
	
	@Override
	void minning() {
		
		if(!isDead()) {
			
			Optional<RoomBlock> op = generator.blocks.getAt(x, y);
			
			op.ifPresent(b -> {
				b.setMetaInfo(BlockMetaInfo.EMPTY);
			});
			
			if(downCount == this.w) {
				
				downCount = 0;
				this.y = this.y + direction;
				
				if(downUpDirection > 0) {
					downUpDirection = 0;
				} else {
					downUpDirection = 1;
				}
				
				this.h--;
			} else {
				if(downUpDirection == 0) {
					this.x = this.x - 1;
					downCount++;
				} else {
					this.x = this.x + 1;
					downCount++;
				}				
			}

		}
	}
	
	@Override
	public boolean isDead() {
		return h < 0;
	}
	
	@Override
	Miner spaw(int x2, int y2, CaveGenerator generator) {
		return MinerFactory.get().getAny(this.x, this.y, generator);
	}

	@Override
	public void setPointPosition(Position pos) {
		switch (pos) {
		case TOP:
			this.y = this.y + 1; 
			this.x = this.x + w;
			break;
		case BOTTOM:
			this.y = this.y - h;
			break;
		case LEFT:
			this.x = this.x + w;
			break;
		case RIGHT:
			this.x = this.x - 1;
			this.x = this.x + w;
			break;			
		default:
			break;
		}
	}
	
}
