package com.toupety.mapgen;

public class Block {
	
	private boolean closed;

	public Block(boolean closed) {
		this.closed = closed;
	}	
	
	public boolean isClosed() {
		return closed;
	}
}
