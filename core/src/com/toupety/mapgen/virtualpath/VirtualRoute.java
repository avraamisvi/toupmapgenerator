package com.toupety.mapgen.virtualpath;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Deprecated
public class VirtualRoute {

	VirtualPath root = new VirtualPath();
	
	public static class VirtualPath {
		private VirtualPath right;
		private VirtualPath left;
		private VirtualPath up;
		private VirtualPath down;
		
		public boolean mustBottom;//molde deve ter o down open
		public boolean mustTop;//molde deve ter o down open
		public boolean mustLeft;//molde deve ter o down open
		public boolean mustRight;//molde deve ter o down open
		
		public VirtualPath createRight() {
			if(this.right == null)
				this.right = new VirtualPath();
			
			return this.right;
		}
		
		public VirtualPath createLeft() {
			if(this.left == null)
				this.left = new VirtualPath();
			
			return this.left;
		}		
		
		public VirtualPath createUp() {
			if(this.up == null)
				this.up = new VirtualPath();
			
			return this.up;
		}
		
		public VirtualPath createDown() {
			if(this.down == null)
				this.down = new VirtualPath();
			
			return this.down;
		}		
	}
	
	public void createRight(int count, Function<Optional<VirtualPath>, Boolean> consumer) {
		VirtualPath current = root;
		
		for(int i = 0; i < count; i++) {
			if(consumer.apply(Optional.of(current))) {
				return;
			}
			current = current.createRight();
		}
		
		consumer.apply(Optional.of(null));
	}
	
	public void createLeft(int count, Function<Optional<VirtualPath>, Boolean> consumer) {
		VirtualPath current = root;
		
		for(int i = 0; i < count; i++) {
			if(consumer.apply(Optional.of(current))) {
				return;
			}
			current = current.createLeft();
		}
		
		consumer.apply(Optional.of(null));
	}
	
	public void createUp(int count, Function<Optional<VirtualPath>, Boolean> consumer) {
		VirtualPath current = root;
		
		for(int i = 0; i < count; i++) {
			if(consumer.apply(Optional.of(current))) {
				return;
			}
			current = current.createUp();
		}
		
		consumer.apply(Optional.of(null));
	}	
	
	public void createDown(int count, Function<Optional<VirtualPath>, Boolean> consumer) {
		VirtualPath current = root;
		
		for(int i = 0; i < count; i++) {
			if(consumer.apply(Optional.of(current))) {
				return;
			}
			current = current.createDown();
		}
		
		consumer.apply(Optional.of(null));
	}	
}
