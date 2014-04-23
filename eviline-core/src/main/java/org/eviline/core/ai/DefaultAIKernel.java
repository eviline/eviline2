package org.eviline.core.ai;

import java.util.Arrays;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class DefaultAIKernel implements AIKernel {

	protected class Best {
		public final Vertex vertex;
		public final double score;
		public final Field after;
		
		public Best(Vertex vertex, double score, Field after) {
			this.vertex = vertex;
			this.score = score;
			this.after = after;
		}
	}
	
	protected Fitness fitness = new DefaultFitness();
	
	public DefaultAIKernel() {}
	
	public DefaultAIKernel(Fitness fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public Vertex bestPlacement(Field field, XYShape current, ShapeType[] next) {
		CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		Vertex best = null;
		
		XYShape nextShape = null;
		ShapeType[] nextNext = null;
		if(next.length > 0) {
			nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		}
		
		for(XYShape shape : g.getVertices().keySet()) {
			if(!field.intersects(shape.shiftedDown()))
				continue;
			Field after = field.clone();
			after.blit(shape);
			double shapeBadness = bestPlacement(field, after, nextShape, nextNext).score;
			if(shapeBadness < badness) {
				best = g.getVertices().get(shape);
				badness = shapeBadness;
			}
		}
		
		return best;
	}
	
	protected Best bestPlacement(Field originalField, Field currentField, XYShape currentShape, ShapeType[] next) {
		if(next == null)
			return new Best(null, fitness.badness(originalField, currentField, next), currentField);
		
		currentField.clearLines();
		
		CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, Double.POSITIVE_INFINITY, null);

		XYShape nextShape = null;
		ShapeType[] nextNext = null;
		if(next.length > 0) {
			nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		}
		
		for(XYShape shape : g.getVertices().keySet()) {
			if(!currentField.intersects(shape.shiftedDown()))
				continue;
			Field nextField = currentField.clone();
			nextField.blit(shape);
			Best shapeBest = bestPlacement(originalField, nextField, nextShape, nextNext);
			if(shapeBest.score < best.score)
				best = shapeBest;
		}
		
		return best;
	}
	
	@Override
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next) {
		Field bestPlayed = field;
		if(next.length > 0) {
			XYShape nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext).after;
		}

		
		ShapeType worst = null;
		double badness = Double.NEGATIVE_INFINITY;
		
		next = new ShapeType[0];
		
		for(ShapeType type : ShapeType.types(shapes.getBag())) {
			Field after = bestPlayed.clone();
			Vertex best = bestPlacement(field, new XYShape(type.start(), type.startX(), type.startY()), next);
			after.blit(best.shape);
			double typeBadness = fitness.badness(bestPlayed, after, next);
			if(typeBadness > badness) {
				worst = type;
				badness = typeBadness;
			}
		}
		
		return worst;
	}

	public Fitness getFitness() {
		return fitness;
	}

	public void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}

}
