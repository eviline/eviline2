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
	
	public boolean has(int x, int y) {
		if(y < this.y || y >= this.y + 4 || x < this.x || x >= this.x + 4)
			return false;
		return shape.has(x - this.x, y - this.y);
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
	
	@Override
	public int hashCode() {
		return shape.hashCode() + 29 * x + 31 * y * Field.WIDTH;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(obj == this)
			return true;
		if(obj instanceof XYShape) {
			XYShape o = (XYShape) obj;
			return shape == o.shape && x == o.x && y == o.y;
		}
		return false;
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
	
	public XYShape shiftedUp() {
		return new XYShape(shape, x, y-1, id);
	}
	
	public XYShape[] kickedLeft() {
		KickTable kt = shape.leftKick();
		XYShape[] kicked = new XYShape[kt.table().length];
		for(int i = 0; i < kicked.length; i++)
			kicked[i] = new XYShape(shape, x + kt.table()[i][0], y + kt.table()[i][1], id);
		return kicked;
	}
	
	public XYShape[] kickedRight() {
		KickTable kt = shape.rightKick();
		XYShape[] kicked = new XYShape[kt.table().length];
		for(int i = 0; i < kicked.length; i++)
			kicked[i] = new XYShape(shape, x + kt.table()[i][0], y + kt.table()[i][1], id);
		return kicked;
	}
}
