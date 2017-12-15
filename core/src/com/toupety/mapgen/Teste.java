package com.toupety.mapgen;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Teste {
	
	public static void main(String[] args) throws IOException {
		
//		ExecutorService exec = Executors.newFixedThreadPool(2);
		
//		exec.execute(()-> {
//			try {
//				while(true) {
//					TimeUnit.SECONDS.sleep(1);
//					System.out.println("t1");
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		});
//		
//		exec.execute(()-> {
//			try {
//				TimeUnit.SECONDS.sleep(5);
//				IntStream.range(1, 100000000).forEach(i -> {});
//				System.out.println("t2");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		
//		exec.execute(()-> {
//			try {
//				while(true) {
//					TimeUnit.SECONDS.sleep(1);
//					System.out.println("t3");
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		});		
		
//		Level level = new Level(1000, 1000);
//		RoomGenerator gen = new RoomGenerator(500, 500, 5, 100, 100);
//		
//		gen.generate(level);
//		new LevelDrawner(level).draw();
		
	}
}
