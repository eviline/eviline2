package org.eviline.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Engine {
	protected Configuration conf;
	protected Field field;
	
	protected ShapeSource shapes;
	protected int lines;
	protected boolean over;
	protected long tickCount;
	
	protected XYShape shape;
	protected XYShape ghost;
	protected Integer downFramesRemaining;
	protected Integer respawnFramesRemaining;
	protected ShapeType[] next = new ShapeType[1];
	
	protected EngineListener[] listeners = null;
	
	public Engine() {
		this(new Field(), new Configuration());
	}
	
	public Engine(Field field, Configuration conf) {
		this.field = field;
		this.conf = conf;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
		respawnFramesRemaining = conf.respawnFramesRemaining(this);
	}
	
	public void reset() {
		field.reset();
		shape = null;
		lines = 0;
		tickCount = 0;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
		respawnFramesRemaining = conf.respawnFramesRemaining(this);
		over = false;
		Arrays.fill(next, null);
	}
	
	public Block block(int x, int y) {
		Block b = field.block(x, y);
		if(b != null || shape == null || y < shape.y() || y >= shape.y() + 4)
			return b;
		if(shape.has(x, y))
			return shape.block();
		return null;
	}
	
	protected ShapeType enqueue(ShapeType type) {
		ShapeType ret = next[0];
		System.arraycopy(next, 1, next, 0, next.length - 1);
		next[next.length - 1] = type;
		return ret;
	}
	
	public boolean tick(Command c) {
		boolean success = false;
		boolean locked = false;
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
			} else {
				for(XYShape kicked : moved.kickedLeft()) {
					if(!field.intersects(kicked)) {
						shape = kicked;
						success = true;
						break;
					}
				}
			}
			break;
		case ROTATE_RIGHT:
			if(shape == null)
				break;
			moved = shape.rotatedRight();
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			} else {
				for(XYShape kicked : moved.kickedRight()) {
					if(!field.intersects(kicked)) {
						shape = kicked;
						success = true;
						break;
					}
				}
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
				locked = true;
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
			locked = true;
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
						locked = true;
						downFramesRemaining = null;
					}
				} else
					downFramesRemaining = downFramesRemaining - 1;
			}
		}
		
		if(locked) {
			lines += field.clearLines();
		}
		
		if(shape == null) {
			if(respawnFramesRemaining == null) {
				respawnFramesRemaining = conf.respawnFramesRemaining(this);
			}
			if(respawnFramesRemaining != null) {
				if(respawnFramesRemaining <= 0) {
					ShapeType next = enqueue(shapes.next(this));
					while(next == null)
						next = enqueue(shapes.next(this));
					shape = new XYShape(next.start(), next.startX(), next.startY());
					respawnFramesRemaining = null;
					if(field.intersects(shape)) {
						over = true;
						shape = null;
					}
				} else
					respawnFramesRemaining = respawnFramesRemaining - 1;
			}
		}
		
		if(shape == null)
			ghost = null;
		else {
			ghost = new XYShape(shape.shape(), shape.x(), shape.y());
			if(!field.intersects(ghost)) {
				while(!field.intersects(ghost))
					ghost.setY(ghost.y() + 1);
				ghost.setY(ghost.y() - 1);
			}
		}
		
		tickCount++;
		
		if(listeners != null) {
			for(EngineListener l : listeners)
				l.ticked(this, c);
		}
		
		return success;
	}
	
	public void addEngineListener(EngineListener l) {
		if(listeners == null) {
			listeners = new EngineListener[] {l};
			return;
		}
		listeners = Arrays.copyOf(listeners, listeners.length + 1);
		listeners[listeners.length - 1] = l;
	}
	
	public void removeEngineListener(EngineListener l) {
		if(listeners == null)
			return;
		List<EngineListener> ll = new ArrayList<>(Arrays.asList(listeners));
		ll.remove(l);
		if(ll.size() == 0)
			listeners = null;
		else
			listeners = ll.toArray(new EngineListener[ll.size()]);
	}

	public Field getField() {
		return field;
	}

	public int getLines() {
		return lines;
	}

	public boolean isOver() {
		return over;
	}

	public XYShape getShape() {
		return shape;
	}
	
	public XYShape getGhost() {
		return ghost;
	}
	
	public void setShape(XYShape shape) {
		this.shape = shape;
	}
	
	public ShapeType[] getNext() {
		List<ShapeType> next = new ArrayList<>(Arrays.asList(this.next));
		while(next.contains(null))
			next.remove(null);
		return next.toArray(new ShapeType[next.size()]);
	}
	
	public long getTickCount() {
		return tickCount;
	}

	public ShapeSource getShapes() {
		return shapes;
	}
}
