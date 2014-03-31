package org.eviline.core;

import org.eviline.core.ss.BagShapeSource;

public class Engine {
	protected Configuration conf;
	protected Field field;
	
	protected ShapeSource shapes;
	protected int lines;
	
	protected XYShape shape;
	protected Integer downFramesRemaining;
	
	
	public Engine() {
		this(new Field(), new Configuration());
	}
	
	public Engine(Field field, Configuration conf) {
		this.field = field;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
	}
	
	public void reset() {
		field.reset();
		shape = null;
		lines = 0;
		shapes = conf.shapes(this);
		downFramesRemaining = conf.downFramesRemaining(this);
	}
	
	public void tick(Command c) {
		
	}
	
}
