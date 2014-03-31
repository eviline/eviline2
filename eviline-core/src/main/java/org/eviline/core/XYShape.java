package org.eviline.core;

import java.util.concurrent.atomic.AtomicLong;

public class XYShape {
	private static final AtomicLong nextId = new AtomicLong();
	
	protected Shape shape;
	protected int x;
	protected int y;
	protected long id;
	protected Block block;
	
	public XYShape(Shape shape, int x, int y) {
		this.shape = shape;
		this.x = x;
		this.y = y;
		id = nextId.incrementAndGet();
	}
	
	public Shape shape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	public int x() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int y() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public long id() {
		return id;
	}
	
	public Block block() {
		if(block == null)
			block = new Block(shape, id);
		return block;
	}
}
