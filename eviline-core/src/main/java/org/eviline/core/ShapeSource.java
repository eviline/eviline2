package org.eviline.core;

public interface ShapeSource {
	public ShapeType next(Engine engine);
	public ShapeType[] getBag();
	public boolean remove(ShapeType type);
}
