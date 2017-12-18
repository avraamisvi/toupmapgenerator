package com.toupety.mapgen.drawner;

@FunctionalInterface
public interface ElementDrawner<T> {
	void draw(T element);
}
