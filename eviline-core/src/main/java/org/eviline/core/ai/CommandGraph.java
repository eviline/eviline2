package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eviline.core.Command;
import org.eviline.core.Field;
import org.eviline.core.Shape;
import org.eviline.core.XYShape;

public class CommandGraph {
	public class Vertex {
		public final int pathLength;
		public final Vertex origin;
		public final Command command;
		public final XYShape shape;
		
		public Vertex(XYShape start) {
			shape = start;
			pathLength = 0;
			origin = null;
			command = null;
		}
		
		public Vertex(Vertex origin, Command command, XYShape shape) {
			this.origin = origin;
			this.command = command;
			this.shape = shape;
			pathLength = origin.pathLength + 1;
		}
		
		public List<Vertex> getOut(Field f) {
			List<Vertex> out = new ArrayList<>();
			XYShape next;
			
			next = shape.shiftedLeft();
			if(!f.intersects(next))
				out.add(new Vertex(this, Command.SHIFT_LEFT, next));
			
			while(!f.intersects(next))
				next = next.shiftedLeft();
			next = shape.shiftedRight();
			out.add(new Vertex(this, Command.AUTOSHIFT_LEFT, next));
			
			next = shape.shiftedRight();
			if(!f.intersects(next))
				out.add(new Vertex(this, Command.SHIFT_RIGHT, next));
			
			while(!f.intersects(next))
				next = next.shiftedRight();
			next = shape.shiftedLeft();
			out.add(new Vertex(this, Command.AUTOSHIFT_RIGHT, next));
			
			next = shape.shiftedDown();
			if(!f.intersects(next))
				out.add(new Vertex(this, Command.SHIFT_DOWN, next));
			
			while(!f.intersects(next))
				next = next.shiftedDown();
			next = next.shiftedUp();
			out.add(new Vertex(this, Command.SOFT_DROP, next));
			
			for(XYShape kicked : shape.rotatedLeft().kickedLeft()) {
				if(!f.intersects(kicked)) {
					out.add(new Vertex(this, Command.ROTATE_LEFT, kicked));
					break;
				}
			}
			
			for(XYShape kicked : shape.rotatedRight().kickedRight()) {
				if(!f.intersects(kicked)) {
					out.add(new Vertex(this, Command.ROTATE_RIGHT, kicked));
					break;
				}
			}
			
			return out;
		}
	}
	
	protected Map<XYShape, Vertex> vertices = new HashMap<>();
	
	public CommandGraph(Field field, XYShape start) {
		Vertex v;
		vertices.put(start, v = new Vertex(start));
		
		Deque<Vertex> pending = new ArrayDeque<>();
		
		pending.addAll(v.getOut(field));
		
		while(pending.size() > 0) {
			v = pending.poll();
			if(vertices.containsKey(v.shape) && vertices.get(v.shape).pathLength <= v.pathLength)
				continue;
			vertices.put(v.shape, v);
			pending.addAll(v.getOut(field));
		}
	}

	public Map<XYShape, Vertex> getVertices() {
		return vertices;
	}
}
