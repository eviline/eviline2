package org.eviline.core;

public class Block {
	protected Shape shape;
	protected long id;
	
	public Block(Shape shape, long id) {
		this.shape = shape;
		this.id = id;
	}
	
	public Shape shape() {
		return shape;
	}
	
	public long id() {
		return id;
	}
}
