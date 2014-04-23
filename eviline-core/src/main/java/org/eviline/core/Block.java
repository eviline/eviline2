package org.eviline.core;

public class Block {
	public static final long MASK_GHOST = 0b1;
	public static final long MASK_GARBAGE = 0b10;
	
	protected Shape shape;
	protected long id;
	protected long flags;
	
	public Block(ShapeType type) {
		if(type == ShapeType.G)
			flags = MASK_GARBAGE;
		else
			shape = type.start();
	}
	
	public Block(Shape shape, long id) {
		this(shape, id, 0);
	}
	
	public Block(Shape shape, long id, long flags) {
		this.shape = shape;
		this.id = id;
		this.flags = flags;
	}
	
	public Block(long flags) {
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
