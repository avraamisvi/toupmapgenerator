package com.toupety.mapgen.mold;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.toupety.mapgen.Configuration.ElementDefinition;
import com.toupety.mapgen.Configuration.ItemDefinition;

public class MoldBlock {

	private MoldMeta meta;
	private char value;
	private int x, y;
	
	private List<ElementDefinition> elements;
	private List<ItemDefinition> items;//TODO talvez nao precise vamos ver
	
	public MoldBlock(char value, int x, int y) {
		super();
		this.value = value;
		this.x = x;
		this.y = y;
	}
	
	public void setMeta(MoldMeta meta) {
		this.meta = meta;
	}
	
	public MoldMeta getMeta() {
		return meta;
	}

	public char getValue() {
		return value;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void addItem(ItemDefinition item) {
		if(items == null)
			items = new ArrayList<>();
		
		items.add(item);
	}
	
	public void addElement(ElementDefinition el) {
		if(elements == null)
			elements = new ArrayList<>();
		
		elements.add(el);
	}
	
	public void forEachItems(Consumer<ItemDefinition> it) {
		if(items != null) {
			items.forEach(it);
		}
	}
}
