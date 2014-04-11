package org.eviline.core;

public class Block {
	public static final long MASK_GHOST = 0b1;
	
	protected Shape shape;
	protected long id;
	protected long flags;
	
	public Block(Shape shape, long id) {
		this(shape, id, 0);
	}
	
	public Block(Shape shape, long id, long flags) {
		this.shape = shape;
		this.id = id;
		this.flags = flags;
	}
	
	public Shape shape() {
		return shape;
	}
	
	public long id() {
		return id;
	}
	
	public long getFlags() {
		return flags;
	}
}
