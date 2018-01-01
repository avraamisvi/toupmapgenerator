package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.toupety.mapgen.RoomBlocks.RoomBlock;
import com.toupety.mapgen.RoomBlocks.RoomDoor;
import com.toupety.mapgen.RoomBlocks.RoomWall;

public class DoorGenerator {

	RandomXS128 rand = new RandomXS128();
	
	public void generate(Level level, Room source) {
		//pega todas as salas adjacentes e tenta criar portas
		
		//test right
		
//		System.out.println("right");
		source.forEachRight(target -> {
			if(!source.getGrid().getRightWall().containsDoorFor(target.getGrid().getLeftWall())) {
			
				int srcx = source.getX() * Configuration.getLevelGridElementContentSize() + 1;
				int srcy = source.getY() * Configuration.getLevelGridElementContentSize();
			
				List<BlockTupple> blocks = findBlocks(source.getGrid().getRightWall(), target.getGrid().getLeftWall(), srcx, srcy);
				
				if(blocks.size() >= GeneratorConstants.MIN_DOOR_BLOCK_LENGTH) {
//					int start = rand.nextInt((blocks.size() - 1) - GeneratorConstants.DOOR_BLOCKS_SIZE);//ints(1, blocks.size() - Constants.DOOR_LENGTH);
					int start = Configuration.getStartPosition();
					blocks.sort((b1, b2) -> b1.source.y - b2.source.y);
					
					//FIXME esse codigo abaixo gera bugs horriveis SOLID ZERO!
					
					RoomDoor doorTarget = target.getGrid().getLeftWall().createDoorFor(source.getGrid().getRightWall());//TODO ta um lixo esse algoritmo repensar
					RoomDoor doorSource = source.getGrid().getRightWall().createDoorFor(target.getGrid().getLeftWall());//TODO ta um lixo esse algoritmo repensar
					
					for(int i = 0; i < GeneratorConstants.DOOR_BLOCKS_SIZE; i++) {
						RoomBlock blockTarg   =   blocks.get(i+start).target;
						RoomBlock blockSource =   blocks.get(i+start).source;
						
						doorTarget.add(blockTarg);
						doorSource.add(blockSource);
					}
					
				}
			}
		});
		
//		System.out.println("left");
		source.forEachLeft(target -> {
			
			if(!source.getGrid().getLeftWall().containsDoorFor(target.getGrid().getRightWall())) {
			
				int srcx = source.getX() * Configuration.getLevelGridElementContentSize() - 1;
				int srcy = source.getY() * Configuration.getLevelGridElementContentSize();
			
				List<BlockTupple> blocks = findBlocks(source.getGrid().getLeftWall(), target.getGrid().getRightWall(), srcx, srcy);
				
				if(blocks.size() >= GeneratorConstants.MIN_DOOR_BLOCK_LENGTH) {
					//int start = rand.nextInt((blocks.size() - 1) - GeneratorConstants.DOOR_BLOCKS_SIZE);//ints(1, blocks.size() - Constants.DOOR_LENGTH);
					int start = Configuration.getStartPosition();
					blocks.sort((b1, b2) -> b1.source.y - b2.source.y);
					
					//FIXME esse codigo abaixo gera bugs horriveis SOLID ZERO!
					
					RoomDoor doorTarget = target.getGrid().getRightWall().createDoorFor(source.getGrid().getLeftWall());//TODO ta um lixo esse algoritmo repensar
					RoomDoor doorSource = source.getGrid().getLeftWall().createDoorFor(target.getGrid().getRightWall());//TODO ta um lixo esse algoritmo repensar
					
					for(int i = 0; i < GeneratorConstants.DOOR_BLOCKS_SIZE; i++) {
						RoomBlock blockTarg   =   blocks.get(i+start).target;
						RoomBlock blockSource =   blocks.get(i+start).source;
						
						doorTarget.add(blockTarg);
						doorSource.add(blockSource);
					}
					
				}
			}
		});		
		
//		System.out.println("top");
		source.forEachTop(target -> {
			
			if(!source.getGrid().getTopWall().containsDoorFor(target.getGrid().getBottomWall())) {
			
				int srcx = source.getX() * Configuration.getLevelGridElementContentSize();
				int srcy = source.getY() * Configuration.getLevelGridElementContentSize() - 1;
			
				List<BlockTupple> blocks = findBlocks(source.getGrid().getTopWall(), target.getGrid().getBottomWall(), srcx, srcy);
				
				if(blocks.size() >= GeneratorConstants.MIN_DOOR_BLOCK_LENGTH) {
//					int start = rand.nextInt((blocks.size() - 1) - GeneratorConstants.DOOR_BLOCKS_SIZE);
					int start = Configuration.getStartPosition();
					blocks.sort((b1, b2) -> b1.source.x - b2.source.x);
					
					//FIXME esse codigo abaixo gera bugs horriveis SOLID ZERO!
					
					RoomDoor doorTarget = target.getGrid().getBottomWall().createDoorFor(source.getGrid().getTopWall());//TODO ta um lixo esse algoritmo repensar
					RoomDoor doorSource = source.getGrid().getTopWall().createDoorFor(target.getGrid().getBottomWall());//TODO ta um lixo esse algoritmo repensar
					
					for(int i = 0; i < GeneratorConstants.DOOR_BLOCKS_SIZE; i++) {
						RoomBlock blockTarg   =   blocks.get(i+start).target;
						RoomBlock blockSource =   blocks.get(i+start).source;
						
						doorTarget.add(blockTarg);
						doorSource.add(blockSource);
					}
					
				}
			}
		});	
		
//		System.out.println("bottom");
		source.forEachBottom(target -> {
			
			if(!source.getGrid().getBottomWall().containsDoorFor(target.getGrid().getTopWall())) {
			
				int srcx = source.getX() * Configuration.getLevelGridElementContentSize();
				int srcy = source.getY() * Configuration.getLevelGridElementContentSize() + 1;
			
				List<BlockTupple> blocks = findBlocks(source.getGrid().getBottomWall(), target.getGrid().getTopWall(), srcx, srcy);
				
				if(blocks.size() >= GeneratorConstants.MIN_DOOR_BLOCK_LENGTH) {
//					int start = rand.nextInt((blocks.size() - 1) - GeneratorConstants.DOOR_BLOCKS_SIZE);
					int start = Configuration.getStartPosition();
					blocks.sort((b1, b2) -> b1.source.x - b2.source.x);
					
					//FIXME esse codigo abaixo gera bugs horriveis SOLID ZERO!
					
					RoomDoor doorTarget = target.getGrid().getTopWall().createDoorFor(source.getGrid().getBottomWall());//TODO ta um lixo esse algoritmo repensar
					RoomDoor doorSource = source.getGrid().getBottomWall().createDoorFor(target.getGrid().getTopWall());//TODO ta um lixo esse algoritmo repensar
					
					for(int i = 0; i < GeneratorConstants.DOOR_BLOCKS_SIZE; i++) {
						RoomBlock blockTarg   =   blocks.get(i+start).target;
						RoomBlock blockSource =   blocks.get(i+start).source;
						
						doorTarget.add(blockTarg);
						doorSource.add(blockSource);
					}
					
				}
			}
		});			

	}
	
	private List<BlockTupple> findBlocks(RoomWall source, RoomWall target, int srcx, int srcy) {//, int tgx, int tgy
		
		List<BlockTupple> lists = new ArrayList<>();
		
		source.forEach(bl -> {
			if(!bl.isCorner() && !bl.isDoor()) {
				RoomBlock tbl = target.containsWorldPoint(bl.getX() + srcx, bl.getY() + srcy);
				if(tbl != null && !tbl.isCorner() && !tbl.isDoor()) {
					lists.add(new BlockTupple(bl, tbl));
				}
			}
		});
		
//		if(lists.isEmpty()) {
//			System.out.println("empty");
//		}
		
		return lists;
	}
	
	class BlockTupple {
		RoomBlock source;
		RoomBlock target;
		public BlockTupple(RoomBlock source, RoomBlock target) {
			super();
			this.source = source;
			this.target = target;
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
}
