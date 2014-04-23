package org.eviline.core;

public class ShapeEquate {
	protected Shape from;
	protected Shape to;
	protected int dx;
	protected int dy;
	
	public ShapeEquate(Shape identity) {
		this(identity, identity, 0, 0);
	}
	
	public ShapeEquate(Shape from, Shape to, int dx, int dy) {
		this.from = from;
		this.to = to;
		this.dx = dx;
		this.dy = dy;
	}

	public Shape from() {
		return from;
	}

	public Shape to() {
		return to;
	}

	public int dx() {
		return dx;
	}

	public int dy() {
		return dy;
	}
	
}
