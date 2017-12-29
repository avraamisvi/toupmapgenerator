package com.toupety.mapgen.cave;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;

public final class MinerFactory {

	private static final MinerFactory inst = new MinerFactory(); 
	RandomXS128 rand = new RandomXS128();
	
	private List<Class<? extends Miner>> miners;
	private List<Class<? extends Miner>> rects;
	
	private MinerFactory() {
		rand.nextInt();rand.nextInt();
		this.miners = new ArrayList<>();
		rects = new ArrayList<>();
		
		miners.add(HorizontalMiner.class);
		miners.add(VerticalMiner.class);
		miners.add(RandomMiner.class);
		
		rects.add(HorizontalMiner.class);
		rects.add(VerticalMiner.class);
		rects.add(CaracolMiner.class);
	}
	
	public static MinerFactory get() {
		return inst;
	}
	
	public Miner getAny(int x, int y, CaveGenerator cave) {
		
		Class<? extends Miner> any = this.miners.get(rand.nextInt(this.miners.size()));
		
		try {
//			getConstructor(Integer.class, Integer.class, CaveGenerator.class).newInstance(x, y, cave);
			return (Miner) any.getConstructors()[0].newInstance(x, y, cave);
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public PredefinedMiner getAnyPredefined(int x, int y, CaveGenerator cave) {
		
		Class<? extends Miner> any = this.rects.get(rand.nextInt(this.miners.size()));
		
		try {
			return (PredefinedMiner) any.getConstructors()[0].newInstance(x, y, cave);
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}	
	
	public Miner getRandom(int x, int y, CaveGenerator cave) {
		return new RandomMiner(x, y, cave);
	}
}
