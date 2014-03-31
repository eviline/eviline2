package org.eviline.core;

public class XYShape {
	protected Shape shape;
	protected int x;
	protected int y;
	
	public XYShape(Shape shape, int x, int y) {
		this.shape = shape;
		this.x = x;
		this.y = y;
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
	
	
	
	
}
