package com.toupety.mapgen;

import java.util.Optional;
import java.util.Random;

import com.toupety.mapgen.algorithms.RoomDimmenstionsAlgorithm;

public class RoomGenerator {
	
	private RoomDimmenstionsAlgorithm algorithm;
	
	public RoomGenerator(RoomDimmenstionsAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void generate(Level level) {
		
		while(true) {
			
			Optional<Dimmensions> op = algorithm.next(level);
			
			if(op.isPresent()) {
				Dimmensions dim = op.get();
				if(level.size() > 0) {				

					level.addRoom(new Room(dim.getW(), dim.getH(), dim.getX(), dim.getY()));
					
				} else {
					level.addRoom(new Room(dim.getW(), dim.getH(), 0, 0));
				}

			} else {
				break;
			}
//			
//			if(maxIterations == 0) {
//				break;
//			}
//			
//			//getRoomMaxMinDimessions no caso criar uma classe que ira pegar randomicamente os tamanhos para as salas, considerando o tipo de sala, 
//			//por exemplo corredores etc, acho q a proxima posicao da sala pode ser calculada por nodulos adjacendes das salas anteriores
//			//por exmeplo ao inves de x e y serem completamente randomicos, seria ineressante q as salas novas comecassem a partir das preexistentes 
//			//ou para cada sala criada criar corredores com certos comprimentos e altura
//			
//			int h = ran.nextInt(maxRoomHeight);
//			int w = ran.nextInt(maxRoomWidth);
//			
//			if(w < this.minRoomWidth) {
//				w = this.minRoomWidth;
//			}
//			
//			if(h < this.minRoomHeight) {
//				h = this.minRoomHeight;
//			}			
//			
//
//			
//			maxIterations--;
		}
	}
}
