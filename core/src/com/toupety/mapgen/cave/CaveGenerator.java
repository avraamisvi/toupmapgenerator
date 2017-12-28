package com.toupety.mapgen.cave;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.RoomBlocks;
import com.toupety.mapgen.RoomBlocks.RoomBlock;

public class CaveGenerator {

	private static final int MAX_MINERS = 400;
	int directions[] = new int[]{-1, 1};
	RandomXS128 rand = new RandomXS128();
	List<Miner> miners = new ArrayList<>();
	
	public void generate(RoomBlocks blocks) {
		rand.nextInt(); rand.nextInt();
		
//		miners.add(new Miner(
//				rand.nextInt(blocks.getW()-1) + 1, 
//				rand.nextInt(blocks.getH()-1) + 1,
//				blocks));
		miners.add(new Miner(
				blocks.getW()/2, 
				blocks.getH()/2,
				blocks));
//		miners.add(new Miner(
//				rand.nextInt(blocks.getW()-1) + 1, 
//				rand.nextInt(blocks.getH()-1) + 1,
//				blocks));		
		
		
//		synchronized (blocks) {
			while(miners.size() < MAX_MINERS) {//miners.size() > 0 && 
				System.out.println(miners.size());
				Miner[] arr = miners.toArray(new Miner[0]);
				for(Miner miner : arr){
					if(miner != null) {
						if(!miner.isDead()) {
							miner.update();
						} else {
							miners.remove(miner);
						}
					}
				}
			}
//		}
	}
	
	public void addMiner(Miner miner) {
//		if(miners.size() < MAX_MINERS) {
			this.miners.add(miner);
//		}
	}
	
	//miners types algorithm types get next x;
	
	class Miner {
		
		private static final double CREATE_PROB = 0.06;
		
		int x, y;
		RoomBlocks blocks;
		int empty;
		int maxEmpty = 6;
		
		public Miner(int x, int y, RoomBlocks blocks) {
			super();
			this.x = x;
			this.y = y;
			this.blocks = blocks;			
		}
		
		public void createNew() {
			if(rand.nextDouble() < CREATE_PROB) {
				CaveGenerator.this.addMiner(new Miner(this.x, this.y, blocks));
			}
		}
		
		public boolean isDead() {
			return empty >= maxEmpty;
		}

		public void update() {
			countEmpty();		
			if(empty < maxEmpty) {
				this.blocks.getAt(x, y).ifPresent(block -> {
					if(!block.isWall() && !block.getMetaInfo().getType().equals(".")) {
						block.setMetaInfo(BlockMetaInfo.EMPTY);
					}
				});
			} else {
				if(CaveGenerator.this.miners.size() <= 1) {
					empty = 0;
				}
			}
			
			if(this.x <= 0) {
				this.x = 1;
			}
			else if(this.x >= blocks.getW()) {
				this.x = blocks.getW()-1;
			} else {				
				this.x = this.x + directions[rand.nextInt(2)];
			}
			
			if(this.y <= 0) {
				this.y = 1;
			} 
			else if(this.y >= blocks.getH()) {
				this.y = blocks.getH()-1;
			} else {
				this.y = this.y + directions[rand.nextInt(2)];
			}
			
			this.createNew();
		}
		
		public void addEmpty() {
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
					
//					if(!this.blocks.getAt(lx + x, ly + y).isPresent()) {
//						addEmpty();
//					}
				}				
			}
			
		}
	}
}
