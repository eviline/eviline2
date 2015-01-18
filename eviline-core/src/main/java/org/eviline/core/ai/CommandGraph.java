package org.eviline.core.ai;

import java.util.Set;

import org.eviline.core.Command;
import org.eviline.core.Field;
import org.eviline.core.XYShapes;

public class CommandGraph {
	public static final int ORIGIN = 0;
	public static final int COMMAND = 1;
	public static final int PATH_LENGTH = 2;
	public static final int SHAPE = 3;

	public static final int NULL_ORIGIN = -1;
	public static final int NULL_COMMAND = -1;

	public static int originOf(int[] vertices, int shape) {
		return vertices[shape * 3 + ORIGIN];
	}

	private static final Command[] COMMANDS = Command.values();

	public static Command commandOf(int[] vertices, int shape) {
		if(vertices[shape * 3 + COMMAND] == NULL_COMMAND)
			return null;
		return COMMANDS[vertices[shape * 3 + COMMAND]];
	}

	public static int pathLengthOf(int[] vertices, int shape) {
		return vertices[shape * 3 + PATH_LENGTH];
	}

	private static ThreadLocal<int[]> pending = new ThreadLocal<int[]>() {
		@Override
		protected int[] initialValue() {
			return new int[XYShapes.SHAPE_MAX];
		}
	};

	private static ThreadLocal<boolean[]> enqueued = new ThreadLocal<boolean[]>() {
		protected boolean[] initialValue() {
			return new boolean[XYShapes.SHAPE_MAX];
		}
	};

	protected int[] vertices = new int[XYShapes.SHAPE_MAX * 3];

	protected int pendingHead = 0;
	protected int pendingTail = 0;

	protected int selectedShape;

	protected boolean dropsOnly;
	protected int start;
	protected Field field;

	public CommandGraph(Field field, int start, boolean dropsOnly) {
		this.field = field;
		this.dropsOnly = dropsOnly;
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			vertices[i * 3 + ORIGIN] = NULL_ORIGIN;
			vertices[i * 3 + COMMAND] = NULL_COMMAND;
			vertices[i * 3 + PATH_LENGTH] = Integer.MAX_VALUE;
		}
		if(start != -1 && !field.intersects(start))
			searchRoot(start, field);
	}

	protected void setVertex(int shape, int origin, int command, int pathLength) {
		vertices[shape * 3 + ORIGIN] = origin;
		vertices[shape * 3 + COMMAND] = command;
		vertices[shape * 3 + PATH_LENGTH] = pathLength;
	}

	protected void searchRoot(int shape, Field f) {
		setVertex(shape, NULL_ORIGIN, NULL_COMMAND, 0);
		search(shape, f);
		while(pendingHead != pendingTail) {
			shape = pending.get()[pendingHead++];
			enqueued.get()[shape] = false;
			pendingHead %= XYShapes.SHAPE_MAX;
			search(shape, f);
		}
	}

	protected void maybeUpdate(int shape, int origin, Command command, int pathLength, Field f) {
		if(pathLength >= pathLengthOf(vertices, shape))
			return;
		setVertex(shape, origin, command.ordinal(), pathLength);
		if(enqueued.get()[shape])
			return;
		enqueued.get()[shape] = true;
		pending.get()[pendingTail++] = shape;
		pendingTail %= XYShapes.SHAPE_MAX;
	}

	protected void search(int shape, Field f) {
		searchRotateLeft(shape, f);
		searchRotateRight(shape, f);
		searchShiftLeft(shape, f);
		searchShiftRight(shape, f);
		searchShiftDown(shape, f);
	}

	protected void searchRotateLeft(int shape, Field f) {
		int nextPathLength = pathLengthOf(vertices, shape) + 1;

		for(int kicked : XYShapes.kickedLeft(XYShapes.rotatedLeft(shape))) {
			if(kicked == -1)
				continue;
			if(!f.intersects(kicked)) {
				maybeUpdate(kicked, shape, Command.ROTATE_LEFT, nextPathLength, f);
				break;
			}
		}
	}

	protected void searchRotateRight(int shape, Field f) {
		int nextPathLength = pathLengthOf(vertices, shape) + 1;

		for(int kicked : XYShapes.kickedRight(XYShapes.rotatedRight(shape))) {
			if(kicked == -1)
				continue;
			if(!f.intersects(kicked)) {
				maybeUpdate(kicked, shape, Command.ROTATE_RIGHT, nextPathLength, f);
				break;
			}
		}
	}
	
	protected void searchShiftLeft(int shape, Field f) {
		int nextPathLength = pathLengthOf(vertices, shape) + 1;

		int next;
		
		next = XYShapes.shiftedLeft(shape);
		if(!f.intersects(next)) {
			maybeUpdate(next, shape, Command.SHIFT_LEFT, nextPathLength, f);

			while(!f.intersects(next))
				next = XYShapes.shiftedLeft(next);
			next = XYShapes.shiftedRight(next);
			maybeUpdate(next, shape, Command.AUTOSHIFT_LEFT, nextPathLength, f);
		}
	}
	
	protected void searchShiftRight(int shape, Field f) {
		int nextPathLength = pathLengthOf(vertices, shape) + 1;

		int next;
		
		next = XYShapes.shiftedRight(shape);
		if(!f.intersects(next)) {
			maybeUpdate(next, shape, Command.SHIFT_RIGHT, nextPathLength, f);

			while(!f.intersects(next))
				next = XYShapes.shiftedRight(next);
			next = XYShapes.shiftedLeft(next);
			maybeUpdate(next, shape, Command.AUTOSHIFT_RIGHT, nextPathLength, f);
		}
	}

	protected void searchShiftDown(int shape, Field f) {
		int nextPathLength = pathLengthOf(vertices, shape) + 1;

		int next;
		
		next = XYShapes.shiftedDown(shape);
		if(!f.intersects(next)) {
			if(!dropsOnly)
				maybeUpdate(next, shape, Command.SHIFT_DOWN, nextPathLength, f);

			while(!f.intersects(next))
				next = XYShapes.shiftedDown(next);
			next = XYShapes.shiftedUp(next);
			maybeUpdate(next, shape, Command.SOFT_DROP, nextPathLength, f);
		}
	}

	public int[] getVertices() {
		return vertices;
	}

	public int getSelectedShape() {
		return selectedShape;
	}

	public void setSelectedShape(int selectedShape) {
		this.selectedShape = selectedShape;
	}

	public boolean isDropsOnly() {
		return dropsOnly;
	}

	public int getStart() {
		return start;
	}

	public Field getField() {
		return field;
	}
}
