package com.toupety.mapgen.virtualpath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.toupety.mapgen.GeneratorConstants;
import com.toupety.mapgen.Position;
import com.toupety.mapgen.RoomBlocks.RoomDoor;
import com.toupety.mapgen.mold.MoldMeta;

public class VirtualPathLevelBlock {

	private Set<String> opens;
	private RoomDoor door;
	
	private boolean openRight;
	private boolean openLeft;
	private boolean openTop;
	private boolean openBottom;
	
	public int x, y;
	public List<VirtualPathLevelBlock> conected = new ArrayList<>();
	private String item;

	public VirtualPathLevelBlock right;
	public VirtualPathLevelBlock left;
	public VirtualPathLevelBlock top;
	public VirtualPathLevelBlock bottom;
	private VirtualPathGenerator owner;
	
	public VirtualPathLevelBlock(VirtualPathGenerator owner,  int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.opens = new HashSet<>();
		this.owner = owner;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public String getItem() {
		return item;
	}
	
	public void setDoor(RoomDoor door) {
		switch (door.getPosition()) {
		case RIGHT:
			openRight = true;
			break;
		case LEFT:
			openLeft = true;
			break;
		case TOP:
			openTop = true;
			break;
		case BOTTOM:
			openBottom = true;
			break;			
		default:
			break;
		}
		this.door = door;
	}
	
	public boolean checkTags(MoldMeta meta) {
		return this.owner.blocks.getOwner().getTagString().equals(meta.getTagString());
	}
	
	public boolean isAcceptable(MoldMeta meta) {
		
		if(this.item != GeneratorConstants.ITEM_PHANTOM) {//se phatom entao ignora
			if(this.item != null && meta.items.stream().filter(it -> it.name.equals(this.item)).count() == 0)
				return false;
			
			if(this.item == null && meta.items.size() > 0)
				return false;
		}
		
		if(!checkTags(meta))
			return false;
		
		boolean top = meta.open.contains(Position.TOP.name());//TODO precisa verificar isso?
		boolean left = meta.open.contains(Position.LEFT.name());
		boolean right = meta.open.contains(Position.RIGHT.name());
		boolean bottom = meta.open.contains(Position.BOTTOM.name());
		
		if( (openTop && !top) || (!openTop && top) ) {
			return false;
		}
		
		if( (openBottom && !bottom) || (!openBottom && bottom) ) {
			return false;
		}
		
		if( (openLeft && !left) || (!openLeft && left) ) {
			return false;
		}		
		
		if( (openRight && !right) || (!openRight && right) ) {
			return false;
		}				
		
		return (openTop || openBottom || openLeft || openRight);
	}
	
	public boolean isPath() {
		return openTop || openBottom || openLeft || openRight;
	}
	
	public boolean isConnected(VirtualPathLevelBlock targ) {
		return conected.size() > 0 && conected.contains(targ);
	}
	
	public void connect(VirtualPathLevelBlock targ) {
		if(targ != this && !targ.isConnected(this)) {
			conected.add(targ);
			targ.connect(this);
		}
	}

	public String getOpens() {
		if(this.opens.isEmpty()) {
			return "";
		} else {
			return this.opens.stream().sorted().collect(Collectors.joining("_"));
		}
	}

	public boolean isOpenRight() {
		return openRight;
	}

	public void setOpenRight(boolean openRight) {
		
		if(openRight)
			this.opens.add(Position.RIGHT.name());
		
		this.openRight = openRight;
	}

	public boolean isOpenLeft() {
		return openLeft;
	}

	public void setOpenLeft(boolean openLeft) {
		
		if(openLeft)
			this.opens.add(Position.LEFT.name());
		
		this.openLeft = openLeft;
	}

	public boolean isOpenTop() {
		return openTop;
	}

	public void setOpenTop(boolean openTop) {
		
		if(openTop)
			this.opens.add(Position.TOP.name());
		
		this.openTop = openTop;
	}

	public boolean isOpenBottom() {
		return openBottom;
	}

	public void setOpenBottom(boolean openBottom) {
		
		if(openBottom)
			this.opens.add(Position.BOTTOM.name());
		
		this.openBottom = openBottom;
	}
}
