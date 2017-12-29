package com.toupety.mapgen.cave;

import java.util.Optional;

import com.toupety.mapgen.Direction;
import com.toupety.mapgen.Position;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomDoor;

public class DoorPathMiner extends Miner {

	private int blocks = 3;
	private Direction direction;
	private int count = 5;
	private RoomDoor target;
	private RoomDoor source;
	private boolean dead = false;
	
	public DoorPathMiner(int x, int y, CaveGenerator generator) {
		super(x, y, generator);
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setTarget(RoomDoor target) {
		this.target = target;
	}
	
	public void setSource(RoomDoor source) {
		this.source = source;
	}
	
	@Override
	void minning() {//CRIAR LOCK REGION para evitar que ele entre numa regiao que ele ja passou e forcar o progresso
		
		Optional<RoomBlock> op = generator.blocks.getAt(x, y);
		
		op.ifPresent(b -> {
			
			if(b.isWall()) {
				
				if(direction == Direction.LEFT) {
					
					if(target.getBlocks().get(1).getY() < this.y) {
						setDirection(Direction.UP);
					} else {
						setDirection(Direction.DOWN);
					}
					
				} else if(direction == Direction.RIGHT) {
					if(target.getBlocks().get(1).getY() < this.y) {
						setDirection(Direction.UP);
					} else {
						setDirection(Direction.DOWN);
					}
				} else if(direction == Direction.DOWN) {
					if(target.getBlocks().get(1).getX() < this.x) {
						setDirection(Direction.LEFT);
					} else {
						setDirection(Direction.RIGHT);
					}
				} else if(direction == Direction.UP) {
					if(target.getBlocks().get(1).getX() < this.x) {
						setDirection(Direction.LEFT);
					} else {
						setDirection(Direction.RIGHT);
					}
				}
			} else {
				b.setMetaInfo(BlockMetaInfo.EMPTY);
			}
		});
		
		if(direction == Direction.LEFT) {
			this.x = this.x - 1;
		} else if(direction == Direction.RIGHT) {
			this.x = this.x + 1;
		} else if(direction == Direction.DOWN) {
			this.y = this.y + 1;
		} else if(direction == Direction.UP) {
			this.y = this.y - 1;
		}		
		
//		isConnected();
		
//		if(!isDead()) {
//			
//			Optional<RoomBlock> op = generator.blocks.getAt(x, y);
//			
//			op.ifPresent(b -> {
//				b.setMetaInfo(BlockMetaInfo.EMPTY);
//			});
//			
//			if(downCount == this.h) {
//				
//				downCount = 0;
//				this.x = this.x + direction;
//				
//				if(downUpDirection > 0) {
//					downUpDirection = 0;
//				} else {
//					downUpDirection = 1;
//				}
//				
//				this.w--;
//			} else {
//				if(downUpDirection == 0) {
//					this.y = this.y - 1;
//					downCount++;
//				} else {
//					this.y = this.y + 1;
//					downCount++;
//				}				
//			}
//
//		}
	}
	
	public void setDead(RoomBlock block) {
		boolean targ = block.getDoor().equals(target);
		
		if(!this.dead)
			this.dead = targ;
	}
	
	void found() {
		
		if(direction == Direction.LEFT) {
			
			generator.blocks.getAt(x, y-2).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
			
			generator.blocks.getAt(x, y+2).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
			
		} else if(direction == Direction.RIGHT) {
			generator.blocks.getAt(x, y-2).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
			
			generator.blocks.getAt(x, y+2).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
		} else if(direction == Direction.DOWN) {
			generator.blocks.getAt(x-2, y).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
			
			generator.blocks.getAt(x+2, y).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
		} else if(direction == Direction.UP) {
			generator.blocks.getAt(x-2, y).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
			
			generator.blocks.getAt(x+2, y).ifPresent(b -> {
				if(b.isDoor()) {
					setDead(b);
				}
			});
		}		
		
	}
	
	@Override
	public boolean isDead() {
		return this.dead;
	}

	@Override
	Miner spaw(int x2, int y2, CaveGenerator generator) {
		return MinerFactory.get().getAny(this.x, this.y, generator);
	}
	
	@Override
	boolean getChance() {
		return false;
	}

}
