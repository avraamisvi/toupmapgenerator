package com.toupety.mapgen.cave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.Direction;
import com.toupety.mapgen.Position;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomDoor;

public class CaveGenerator {

	private static final int MAX_MINERS = 400;
	
	RandomXS128 rand = new RandomXS128();
	List<Miner> miners = new ArrayList<>();
	
	public RoomBlocks blocks;
	
	public RoomBlocks getBlocks() {
		return blocks;
	}
	
	public RandomXS128 getRand() {
		return rand;
	}
	
	public void generate(RoomBlocks blocks) {
		rand.nextInt(); rand.nextInt();
		this.blocks = blocks;
		
//		Miner mi = MinerFactory.get().getRandom(blocks.getW()/2, blocks.getH()/2, this);
//		miners.add(mi);
		
//		int rx = rand.nextInt(blocks.getW() - 1);
//		int ry = rand.nextInt(blocks.getH() - 1);
		
//		miners.add(MinerFactory.get().getAny(rx, ry, this));
		
//		rx = rand.nextInt(blocks.getW() - 1);
//		ry = rand.nextInt(blocks.getH() - 1);
		
//		miners.add(MinerFactory.get().getAny(rx, ry, this));
		
//		miners.add(new VerticalMiner(blocks.getW()/2, blocks.getH()/2, this));
//		miners.add(new CaracolMiner(blocks.getW()/2, blocks.getH()/2, this));
		
		mineFromEachDoor();
		
//		synchronized (blocks) {
			int iterations = 0;
			while(miners.size() < MAX_MINERS && iterations < 100) {//miners.size() > 0 && && iterations < 10000
				iterations++;
				System.out.println(miners.size());
				Miner[] arr = miners.toArray(new Miner[0]);
				
//				if(miners.size() == 0) {
//					int rx = rand.nextInt(blocks.getW() - 1) + 1;
//					int ry = rand.nextInt(blocks.getH() - 1) + 1;
//					miners.add(MinerFactory.get().getAny(rx, ry, this));					
//				}
				
				for(Miner miner : arr){
					if(miner != null) {
						if(!miner.isDead()) {
							miner.update();
						} else {
							miner.createNew();
							miners.remove(miner);
						}
					}
				}
			}
//		}
	}
	
	void mineFromEachDoor() {
		RoomDoor target = blocks.getDoors().get(0);
		blocks.forEachDoor(source -> {
			if(target != source) {
				
				source.sort();
				RoomBlock bl = source.getBlocks().get(1);
				DoorPathMiner m = new DoorPathMiner(bl.getX(), bl.getY(), this);
				
				m.setTarget(target);
				m.setSource(source);
				
				if(source.getPosition() == Position.TOP) {
					m.setDirection(Direction.DOWN);
				} else if(source.getPosition() == Position.LEFT) {
					m.setDirection(Direction.RIGHT);
				} else if(source.getPosition() == Position.RIGHT) {
					m.setDirection(Direction.LEFT);
				} else if(source.getPosition() == Position.BOTTOM) {
					m.setDirection(Direction.UP);
				}
				
				miners.add(m);
			}
		});
	}
	
	public void addMiner(Miner miner) {
		if(miners.size() < MAX_MINERS) {
			this.miners.add(miner);
		}
	}
	
	//miners types algorithm types get next x;

}
