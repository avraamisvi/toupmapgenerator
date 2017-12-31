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

	public VirtualPathLevelBlock(int x, int y) {
		super();
		this.x = x;
		this.y = y;
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
	
	public boolean isAcceptable(MoldMeta meta) {
		
		if( openTop ) {
			if(!meta.open.contains(Position.TOP.name())) {
				return false;
			}
		}
		
		if( openBottom ) {
			if(!meta.open.contains(Position.BOTTOM.name())) {
				return false;
			}
		}

		if( openLeft ) {
			if(!meta.open.contains(Position.LEFT.name())) {
				return false;
			}
		}
		
		if( openRight ) {
			if(!meta.open.contains(Position.RIGHT.name())) {
				return false;
			}
		}
		
		return (openTop || openBottom || openLeft || openRight);
//		return ret && (openTop || openBottom || openLeft || openRight);
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
