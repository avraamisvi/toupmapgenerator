package com.toupety.mapgen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;

public class Room {
	
	List<Block> blocks;
	String id;
	int height;//bounds
	int width;
	int x;
	int y;
	List<Rectangle> rects;
	
	public Room(int height, int width, int x, int y) {
		this.rects = new ArrayList<>();
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
		this.id = UUID.randomUUID().toString();
		this.blocks = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void apply(Consumer<Block> c) {//esse algoritmo deve gerar plataformas e estruturas dentro das rooms, considerando os retangulos
		blocks.forEach(c);
	}
	
	public void normalize() {
		//deve navegar em todos os retangulos gerando os blocks nas posições e associando cada block aos adjacentes
		//talvez seja melhor usar uma formula para achar o bloco adjacente, cada bloco teria um codigo de linha e coluna, e assim ficaria facil achar o adjacente
		//como os blocos vao ficar numa lista então cada linha x coluna = a uma posicao na lista
	}
	
	public void merge(Room room) {
//		rects.stream().filter(r -> {			
//			r.contains();
//		});
		//pega cada um dos retangulos de uma room e verifica se eles estão dentro de lagum na outra room, se nao estiverem então sao incorporados a room atual.
		
	}
}
