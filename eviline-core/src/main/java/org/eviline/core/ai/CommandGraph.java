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
	public class Vertex {
		public final int pathLength;
		public final Vertex origin;
		public final Command command;
		public final int shape;
		
		public Vertex(int start) {
			shape = start;
			pathLength = 0;
			origin = null;
			command = null;
		}
		
		public Vertex(Vertex origin, Command command, int shape) {
			this.origin = origin;
			this.command = command;
			this.shape = shape;
			pathLength = origin.pathLength + 1;
		}
		
		public List<Vertex> getOut(Field f) {
			List<Vertex> out = new ArrayList<>();
			int next;
			
			for(int kicked : XYShapes.kickedLeft(XYShapes.rotatedLeft(shape))) {
				if(!f.intersects(kicked)) {
					out.add(new Vertex(this, Command.ROTATE_LEFT, kicked));
					break;
				}
			}
			
			for(int kicked : XYShapes.kickedRight(XYShapes.rotatedRight(shape))) {
				if(!f.intersects(kicked)) {
					out.add(new Vertex(this, Command.ROTATE_RIGHT, kicked));
					break;
				}
			}
			
			next = XYShapes.shiftedLeft(shape);
			if(!f.intersects(next)) {
				out.add(new Vertex(this, Command.SHIFT_LEFT, next));

				while(!f.intersects(next))
					next = XYShapes.shiftedLeft(next);
				next = XYShapes.shiftedRight(next);
				out.add(new Vertex(this, Command.AUTOSHIFT_LEFT, next));
			}
			
			next = XYShapes.shiftedRight(shape);
			if(!f.intersects(next)) {
				out.add(new Vertex(this, Command.SHIFT_RIGHT, next));
			
				while(!f.intersects(next))
					next = XYShapes.shiftedRight(next);
				next = XYShapes.shiftedLeft(next);
				out.add(new Vertex(this, Command.AUTOSHIFT_RIGHT, next));
			}
			
			next = XYShapes.shiftedDown(shape);
			if(!f.intersects(next)) {
				out.add(new Vertex(this, Command.SHIFT_DOWN, next));

				while(!f.intersects(next))
					next = XYShapes.shiftedDown(next);
				next = XYShapes.shiftedUp(next);
				out.add(new Vertex(this, Command.SOFT_DROP, next));
			}
			
			return out;
		}
	}
	
//	protected Map<XYShape, Vertex> vertices = new HashMap<>();
	protected Vertex[] vertices = new Vertex[XYShapes.SHAPE_MAX];
	
	public CommandGraph(Field field, int start) {
		Vertex v;
//		vertices.put(start, v = new Vertex(start));
		v = new Vertex(start);
		vertices[start] = v;
		
		Deque<Vertex> pending = new ArrayDeque<>();
		
		pending.addAll(v.getOut(field));
		
		while(pending.size() > 0) {
			v = pending.poll();
			int si = v.shape;
			if(vertices[si] != null && vertices[si].pathLength <= v.pathLength)
				continue;
			vertices[si] = v;
			pending.addAll(v.getOut(field));
		}
	}

	public Vertex[] getVertices() {
		return vertices;
	}
}
