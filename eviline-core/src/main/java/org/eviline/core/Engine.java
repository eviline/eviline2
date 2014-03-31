package org.eviline.core;

import org.eviline.core.ss.BagShapeSource;

public class Engine {
	protected Configuration conf;
	protected Field field;
	
	protected ShapeSource shapes;
	protected int lines;
	
	protected XYShape shape;
	protected Integer downFramesRemaining;
	protected Integer respawnFramesRemaining;
	
	
	public Engine() {
		this(new Field(), new Configuration());
	}
	
	public Engine(Field field, Configuration conf) {
		this.field = field;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
		respawnFramesRemaining = conf.respawnFramesRemaining(this);
	}
	
	public void reset() {
		field.reset();
		shape = null;
		lines = 0;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
		respawnFramesRemaining = conf.respawnFramesRemaining(this);
	}
	
	public boolean tick(Command c) {
		boolean success = false;
		switch(c) {
		case NOP:
			success = true;
			break;
		case SHIFT_LEFT:
			if(shape == null)
				break;
			XYShape moved = shape.shiftedLeft();
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case SHIFT_RIGHT:
			if(shape == null)
				break;
			moved = shape.shiftedRight();
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case AUTOSHIFT_LEFT:
			if(shape == null)
				break;
			moved = shape.shiftedLeft();
			while(!field.intersects(moved)) {
				shape = moved;
				moved = shape.shiftedLeft();
				success = true;
			}
			break;
		case AUTOSHIFT_RIGHT:
			if(shape == null)
				break;
			moved = shape.shiftedRight();
			while(!field.intersects(moved)) {
				shape = moved;
				moved = shape.shiftedRight();
				success = true;
			}
			break;
		case ROTATE_LEFT:
			if(shape == null)
				break;
			moved = shape.rotatedLeft();
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case ROTATE_RIGHT:
			if(shape == null)
				break;
			moved = shape.rotatedRight();
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case SHIFT_DOWN:
			if(shape == null)
				break;
			moved = shape.shiftedDown();
			if(!field.intersects(moved)) {
				shape = moved;
			} else {
				field.blit(shape);
				shape = null;
			}
			downFramesRemaining = null;
			success = true;
			break;
		case SOFT_DROP:
			if(shape == null)
				break;
			moved = shape.shiftedDown();
			while(!field.intersects(moved)) {
				shape = moved;
				moved = shape.shiftedDown();
				success = true;
			}
			downFramesRemaining = null;
			break;
		case HARD_DROP:
			if(shape == null)
				break;
			moved = shape.shiftedDown();
			while(!field.intersects(moved)) {
				shape = moved;
				moved = shape.shiftedDown();
			}
			field.blit(shape);
			shape = null;
			downFramesRemaining = null;
			success = true;
			break;
		}
		
		if(shape != null) {
			if(downFramesRemaining == null) {
				downFramesRemaining = conf.downFramesRemaining(this);
			}
			if(downFramesRemaining != null) {
				if(downFramesRemaining <= 0) {
					XYShape moved = shape.shiftedDown();
					if(!field.intersects(moved))
						shape = moved;
					else {
						field.blit(shape);
						shape = null;
						downFramesRemaining = null;
					}
				} else
					downFramesRemaining = downFramesRemaining - 1;
			}
		}
		
		if(shape == null) {
			if(respawnFramesRemaining == null) {
				respawnFramesRemaining = conf.respawnFramesRemaining(this);
			}
			if(respawnFramesRemaining != null) {
				if(respawnFramesRemaining <= 0) {
					shape = new XYShape(shapes.next(this).up(), 3, 0);
					respawnFramesRemaining = null;
				} else
					respawnFramesRemaining = respawnFramesRemaining - 1;
			}
		}
		
		return success;
	}
	
}
