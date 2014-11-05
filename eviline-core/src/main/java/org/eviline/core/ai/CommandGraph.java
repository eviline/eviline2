package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static int originOf(int[] vertex) {
		return vertex[ORIGIN];
	}
	
	private static final Command[] COMMANDS = Command.values();
	
	public static Command commandOf(int[] vertex) {
		if(vertex[COMMAND] == NULL_COMMAND)
			return null;
		return COMMANDS[vertex[COMMAND]];
	}
	
	public static int pathLengthOf(int[] vertex) {
		return vertex[PATH_LENGTH];
	}
	
	public static int shapeOf(int[] vertex) {
		return vertex[SHAPE];
	}
	
	protected int[][] vertices = new int[XYShapes.SHAPE_MAX][4];
	
	protected int selectedShape;
	
	public CommandGraph(Field field, int start) {
		for(int i = 0; i < vertices.length; i++) {
			int[] vertex = vertices[i];
			vertex[ORIGIN] = NULL_ORIGIN;
			vertex[COMMAND] = NULL_COMMAND;
			vertex[PATH_LENGTH] = Integer.MAX_VALUE;
			vertex[SHAPE] = i;
		}
		searchRoot(start, field.clone());
	}

	protected void setVertex(int shape, int origin, int command, int pathLength) {
		int[] vertex = vertices[shape];
		vertex[ORIGIN] = origin;
		vertex[COMMAND] = command;
		vertex[PATH_LENGTH] = pathLength;
	}
	
	protected void searchRoot(int shape, Field f) {
		setVertex(shape, NULL_ORIGIN, NULL_COMMAND, 0);
		search(shape, f);
	}
	
	protected void maybeUpdate(int shape, int origin, Command command, int pathLength, Field f) {
		int[] vertex = vertices[shape];
		if(pathLength >= pathLengthOf(vertex))
			return;
		setVertex(shape, origin, command.ordinal(), pathLength);
		search(shape, f);
	}
	
	protected void search(int shape, Field f) {
		int nextPathLength = vertices[shape][PATH_LENGTH] + 1;
		
		int next;
		
		for(int kicked : XYShapes.kickedLeft(XYShapes.rotatedLeft(shape))) {
			if(!f.intersects(kicked)) {
				maybeUpdate(kicked, shape, Command.ROTATE_LEFT, nextPathLength, f);
				break;
			}
		}
		
		for(int kicked : XYShapes.kickedRight(XYShapes.rotatedRight(shape))) {
			if(!f.intersects(kicked)) {
				maybeUpdate(kicked, shape, Command.ROTATE_RIGHT, nextPathLength, f);
				break;
			}
		}
		
		next = XYShapes.shiftedLeft(shape);
		if(!f.intersects(next)) {
			maybeUpdate(next, shape, Command.SHIFT_LEFT, nextPathLength, f);

			while(!f.intersects(next))
				next = XYShapes.shiftedLeft(next);
			next = XYShapes.shiftedRight(next);
			maybeUpdate(next, shape, Command.AUTOSHIFT_LEFT, nextPathLength, f);
		}
		
		next = XYShapes.shiftedRight(shape);
		if(!f.intersects(next)) {
			maybeUpdate(next, shape, Command.SHIFT_RIGHT, nextPathLength, f);
		
			while(!f.intersects(next))
				next = XYShapes.shiftedRight(next);
			next = XYShapes.shiftedLeft(next);
			maybeUpdate(next, shape, Command.AUTOSHIFT_RIGHT, nextPathLength, f);
		}
		
		next = XYShapes.shiftedDown(shape);
		if(!f.intersects(next)) {
			maybeUpdate(next, shape, Command.SHIFT_DOWN, nextPathLength, f);

			while(!f.intersects(next))
				next = XYShapes.shiftedDown(next);
			next = XYShapes.shiftedUp(next);
			maybeUpdate(next, shape, Command.SOFT_DROP, nextPathLength, f);
		}
	}

	public int[][] getVertices() {
		return vertices;
	}
	
	public int getSelectedShape() {
		return selectedShape;
	}
	
	public void setSelectedShape(int selectedShape) {
		this.selectedShape = selectedShape;
	}
}
