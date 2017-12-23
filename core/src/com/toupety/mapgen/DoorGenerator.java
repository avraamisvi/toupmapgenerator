package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomDoor;
import com.toupety.mapgen.RoomBlocks.RoomWall;

public class DoorGenerator {

	RandomXS128 rand = new RandomXS128();
	
	public void generate(Level level, Room newRoom) {
		//pega todas as salas adjacentes e tenta criar portas
		
		//test right
		
		newRoom.forEachLeft(room -> {
			
		});
		
		
//		int x = newRoom.getX() + newRoom.getWidth();
//		int y = newRoom.getY() + newRoom.getHeight()/2;
//		
//		Room target = level.getAt(x, y);
//		
//		int size = target.getY() - newRoom.getY();
		
//		target.createDoor(x,y, Direction.DOWN);
		
//		RoomBlocks grid = target.getGrid();
//		RoomWall wall = grid.getRightWall();
//		
//		wall.createDoor(5, 3, Direction.DOWN);
//		
//		System.out.println("x");
	}
	
	private void findBlocks(RoomWall source, RoomWall target, int xFactor, int yFactor) {
		
		List<RoomBlock> blocks = new ArrayList<>();
		
		source.forEach(bl -> {
			if(target.containsWorldPoint(bl.getX() + xFactor, bl.getY() + yFactor)) {
				blocks.add(bl);
			}
		});
		
		if(blocks.size() >= Constants.MIN_DOOR_BLOCK_LENGTH) {
			int end = rand.nextInt(blocks.size() - Constants.DOOR_LENGTH);//ints(1, blocks.size() - Constants.DOOR_LENGTH);
			target.createDoorFor(1, end, blocks);
		}
	}
	
	/**
	 * 
	 * 
Algoritmo para criacao de portas

Para cada lado da sala X verifica se tem algum sala tangente em (x,y) + 1 ou seja

se for a lateral esquerda verifica se para cada y existe uma sala em (x+1, y)
se achar uma sala lateral verifica se a porta pode ser colocada ou seja, 
se existe pelo menos 5 blocos tangenciando a lateral, se tiver então 
conta todos os blocos disponiveis, se a quantidade de blocos for maior que 5 entao 
seleciona randomicamente em quais blocos a porta estara, desde que deixe 1 bloco livre abaixo e acima.
por exemplo:


      [1][]
      [2][]
      [3][]
SALAX [4][] SALA Y
      [5][]
      [6][]
      [7][]
      [8][]
      [9][]

random entre [1 + 1] e [9 - 3] ou seja,  [2] e [6]
pq a partir de 5 vai estourar o tamanho da porta

Cda sala poderar ter N portas com a proxima sala, configuravel, 
se o limite de portas por parede for atingido para aquela sala, 
entao a nova porta nao deverar ser criada

Ao criar numa sala a outra recebe uma porta tbm
	 * 
	 * 
	 */
	
//	Direction getDirection(Room source, Room target) {
//		Direction ret;
//		
//		if(source.getWidth()) {
//			
//		}
//		
//		return ret;
//	}
}
