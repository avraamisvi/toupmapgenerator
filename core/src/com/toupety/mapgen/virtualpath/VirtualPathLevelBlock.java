package com.toupety.mapgen.virtualpath;

import java.util.ArrayList;
import java.util.List;

import com.toupety.mapgen.Position;
import com.toupety.mapgen.RoomBlocks.RoomDoor;
import com.toupety.mapgen.mold.MoldMeta;

public class VirtualPathLevelBlock {

	private RoomDoor door;
	public boolean openRight;
	public boolean openLeft;
	public boolean openTop;
	public boolean openBottom;
	public int x, y;
	public List<VirtualPathLevelBlock> conected = new ArrayList<>();
	private String item;

	public VirtualPathLevelBlock right;
	public VirtualPathLevelBlock left;
	public VirtualPathLevelBlock top;
	public VirtualPathLevelBlock bottom;
	
	public VirtualPathLevelBlock(int x, int y) {
		super();
		this.x = x;
		this.y = y;
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
		return true;
	}
	
	public boolean isAcceptable(MoldMeta meta) {
		
		if(this.item != null && meta.items.stream().filter(it -> it.name.equals(this.item)).count() == 0)
			return false;
		
		if(this.item == null && meta.items.size() > 0)
			return false;
		
		if(!checkTags(meta))
			return false;
		
		boolean top = meta.open.contains(Position.TOP.name());
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
}
