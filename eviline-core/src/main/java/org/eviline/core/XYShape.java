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
	
	public XYShape(Shape shape, int x, int y, long id) {
		this.shape = shape;
		this.x = x;
		this.y = y;
		this.id = id;
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
	
	public XYShape rotatedRight() {
		return new XYShape(shape.rotatedRight(), x, y, id);
	}
	
	public XYShape rotatedLeft() {
		return new XYShape(shape.rotatedLeft(), x, y, id);
	}
	
	public XYShape shiftedLeft() {
		return new XYShape(shape, x-1, y, id);
	}
	
	public XYShape shiftedRight() {
		return new XYShape(shape, x+1, y, id);
	}
	
	public XYShape shiftedDown() {
		return new XYShape(shape, x, y+1, id);
	}
}
