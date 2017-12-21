package com.toupety.mapgen;

import com.toupety.mapgen.mold.MoldBlock;

public class Block {
	
	private MoldBlock value;
	
	private Block up;
	private Block down;
	private Block left;
	private Block right;

	public Block(MoldBlock val) {
		this.value = val;
	}

	public MoldBlock getValue() {
		return value;
	}

	public void setValue(MoldBlock value) {
		this.value = value;
	}

	public Block getUp() {
		return up;
	}

	public void setUp(Block up) {
		this.up = up;
	}

	public Block getDown() {
		return down;
	}

	public void setDown(Block down) {
		this.down = down;
	}

	public Block getLeft() {
		return left;
	}

	public void setLeft(Block left) {
		this.left = left;
	}

	public Block getRight() {
		return right;
	}

	public void setRight(Block right) {
		this.right = right;
	}
}
