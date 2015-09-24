package org.eviline.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Engine implements Cloneable {
	private static class ImmutableBagSource implements ShapeSource {
		private final ShapeType[] bag;

		private ImmutableBagSource(ShapeType[] bag) {
			this.bag = bag;
		}

		@Override
		public ShapeType next(Engine engine) {
			return null;
		}

		@Override
		public ShapeType[] getBag() {
			return bag;
		}
		
		@Override
		public boolean remove(ShapeType type) {
			return false;
		}
	}

	protected Configuration conf;
	protected Field field;
	
	protected ShapeSource shapes;
	protected boolean over;
	protected long tickCount;
	protected long shapeCount;
	
	protected int shape = -1;
	protected boolean ghosting = true;
	protected int ghost;
	protected long shapeId;
	protected Integer downFramesRemaining;
	protected Integer respawnFramesRemaining;
	protected boolean paused;
	
	protected boolean holdEnabled;
	protected boolean holdable;
	
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
	
	public synchronized Engine clone() {
		try {
			Engine c = (Engine) super.clone();
			
			c.field = field.clone();
			c.shapes = new ImmutableBagSource(shapes.getBag());
			if(listeners != null)
				c.listeners = listeners.clone();
			
			return c;
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
	public synchronized void reset() {
		field.reset();
		shape = -1;
		shapeId = 0;
		tickCount = 0;
		shapeCount = 0;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
		respawnFramesRemaining = conf.respawnFramesRemaining(this);
		over = false;
	}
	
	public Block block(int x, int y) {
		Block b = field.block(x, y);
		if(b != null || shape == -1 || y < XYShapes.yFromInt(shape) || y >= XYShapes.yFromInt(shape) + 4)
			return b;
		if(XYShapes.has(shape, x, y))
			return new Block(XYShapes.shapeFromInt(shape), shapeId);
		return null;
	}
	
	protected ShapeType enqueue(ShapeType type) {
		ShapeType[] next = getNext();
		if(next.length == 0)
			return type;
		ShapeType ret = next[0];
		System.arraycopy(next, 1, next, 0, next.length - 1);
		next[next.length - 1] = type;
		setNext(next);
		return ret;
	}
	
	public void garbage(int lines) {
		short tm = 0b1111111111;
		short thm = (short)(1 << (int)(field.WIDTH * Math.random()));
		tm = (short)(tm & ~thm);
		tm = (short)(tm << 3);
		for(int i = 0; i < lines; i++)
			field.shiftUp(tm);
	}
	
	public synchronized boolean tick(Command c) {
		if(isPaused())
			return false;
		boolean success = false;
		boolean locked = false;
		switch(c) {
		case NOP:
			success = true;
			break;
		case HOLD:
			if(shape == -1 || !holdable)
				break;
			ShapeType held = getHold();
			setHold(XYShapes.shapeFromInt(shape).type());
			if(held == null)
				shape = -1;
			else
				shape = XYShapes.toXYShape(held.startX(), held.startY(), held.start());
			holdable = false;
			success = true;
			break;
		case SHIFT_LEFT:
			if(shape == -1)
				break;
			int moved = XYShapes.shiftedLeft(shape);
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case SHIFT_RIGHT:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedRight(shape);
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			}
			break;
		case AUTOSHIFT_LEFT:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedLeft(shape);
			while(!field.intersects(moved)) {
				shape = moved;
				moved = XYShapes.shiftedLeft(shape);
				success = true;
			}
			break;
		case AUTOSHIFT_RIGHT:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedRight(shape);
			while(!field.intersects(moved)) {
				shape = moved;
				moved = XYShapes.shiftedRight(shape);
				success = true;
			}
			break;
		case ROTATE_LEFT:
			if(shape == -1)
				break;
			moved = XYShapes.rotatedLeft(shape);
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			} else {
				for(int kicked : XYShapes.kickedLeft(moved)) {
					if(kicked == -1)
						continue;
					if(!field.intersects(kicked)) {
						shape = kicked;
						success = true;
						break;
					}
				}
			}
			break;
		case ROTATE_RIGHT:
			if(shape == -1)
				break;
			moved = XYShapes.rotatedRight(shape);
			if(!field.intersects(moved)) {
				shape = moved;
				success = true;
			} else {
				for(int kicked : XYShapes.kickedRight(moved)) {
					if(kicked == -1)
						continue;
					if(!field.intersects(kicked)) {
						shape = kicked;
						success = true;
						break;
					}
				}
			}
			break;
		case SHIFT_DOWN:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedDown(shape);
			if(!field.intersects(moved)) {
				shape = moved;
			} else {
				field.blit(shape, shapeId);
				shape = -1;
				shapeId++;
				locked = true;
			}
			downFramesRemaining = null;
			success = true;
			break;
		case SHIFT_UP:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedUp(shape);
			if(!field.intersects(moved) && XYShapes.yFromInt(shape) > -Field.BUFFER) {
				shape = moved;
			}
			downFramesRemaining = null;
			success = true;
			break;
		case SOFT_DROP:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedDown(shape);
			while(!field.intersects(moved)) {
				shape = moved;
				moved = XYShapes.shiftedDown(shape);
				success = true;
			}
			downFramesRemaining = null;
			break;
		case HARD_DROP:
			if(shape == -1)
				break;
			moved = XYShapes.shiftedDown(shape);
			while(!field.intersects(moved)) {
				shape = moved;
				moved = XYShapes.shiftedDown(shape);
			}
			field.blit(shape, shapeId);
			shape = -1;
			shapeId++;
			locked = true;
			downFramesRemaining = null;
			success = true;
			break;
		}
		
		if(shape != -1) {
			if(downFramesRemaining == null) {
				downFramesRemaining = conf.downFramesRemaining(this);
			}
			if(downFramesRemaining != null) {
				if(downFramesRemaining <= 0) {
					int moved = XYShapes.shiftedDown(shape);
					if(!field.intersects(moved))
						shape = moved;
					else {
						field.blit(shape, shapeId);
						shape = -1;
						shapeId++;
						locked = true;
						downFramesRemaining = null;
					}
				} else
					downFramesRemaining = downFramesRemaining - 1;
			}
		}
		
		if(locked) {
			field.clearLines();
			holdable = holdEnabled;
		}
		
		if(shape == -1) {
			if(respawnFramesRemaining == null) {
				respawnFramesRemaining = conf.respawnFramesRemaining(this);
			}
			if(respawnFramesRemaining != null) {
				if(respawnFramesRemaining <= 0) {
					ShapeType next = enqueue(shapes.next(this));
					while(next == null)
						next = enqueue(shapes.next(this));
					shape = XYShapes.toXYShape(next.startX(), next.startY(), next.start());
					shapeCount++;
					respawnFramesRemaining = null;
					if(field.intersects(shape)) {
						over = true;
						shape = -1;
						shapeId++;
					}
				} else
					respawnFramesRemaining = respawnFramesRemaining - 1;
			}
		}
		
		if(ghosting) {
			if(shape == -1)
				ghost = -1;
			else {
				ghost = shape;
				if(!field.intersects(ghost)) {
					while(!field.intersects(ghost))
						ghost = XYShapes.shiftedDown(ghost);
					ghost = XYShapes.shiftedUp(ghost);
				}
			}
		}
		
		tickCount++;
		
		if(listeners != null) {
			for(int i = listeners.length - 1; i >= 0; i--)
				listeners[i].ticked(this, c);
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

	public long getLines() {
		return field.getLines();
	}

	public boolean isOver() {
		return over;
	}

	public int getShape() {
		return shape;
	}
	
	public int getGhost() {
		return ghost;
	}
	
	public void setGhost(int ghost) {
		this.ghost = ghost;
	}
	
	public void setShape(int shape) {
		this.shape = shape;
	}
	
	public ShapeType[] getNext() {
		return field.getNext();
	}
	
	public long getTickCount() {
		return tickCount;
	}

	public ShapeSource getShapes() {
		return shapes;
	}

	public long getScore() {
		return field.getScore();
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public long getShapeCount() {
		return shapeCount;
	}

	public void setNext(ShapeType[] next) {
		field.setNext(next);
	}

	public long getShapeId() {
		return shapeId;
	}
	
	public void setShapes(ShapeSource shapes) {
		this.shapes = shapes;
	}
	
	public ShapeType getHold() {
		return field.getHold();
	}
	
	public void setHold(ShapeType hold) {
		field.setHold(hold);
	}

	public boolean isHoldable() {
		return holdable;
	}

	public void setHoldable(boolean holdable) {
		this.holdable = holdable;
	}

	public boolean isHoldEnabled() {
		return holdEnabled;
	}

	public void setHoldEnabled(boolean holdEnabled) {
		this.holdEnabled = holdEnabled;
	}

	public boolean isGhosting() {
		return ghosting;
	}

	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}
}
