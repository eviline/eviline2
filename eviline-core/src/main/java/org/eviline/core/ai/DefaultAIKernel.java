package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class DefaultAIKernel implements AIKernel {

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
		
		for(XYShape shape : g.getVertices().keySet()) {
			if(!field.intersects(shape.shiftedDown()))
				continue;
			Field after = field.clone();
			after.blit(shape);
			double shapeBadness = fitness.badness(field, after, next);
			if(shapeBadness < badness) {
				best = g.getVertices().get(shape);
				badness = shapeBadness;
			}
		}
		
		return best;
	}
	
	@Override
	public ShapeType worstNext(Field field, ShapeType[] options, ShapeType[] next) {
		ShapeType worst = null;
		double badness = Double.NEGATIVE_INFINITY;
		
		// FIXME: This totally ignores the 'next' argument
		
		for(ShapeType type : options) {
			Field after = field.clone();
			Vertex best = bestPlacement(field, new XYShape(type.start(), type.startX(), type.startY()), next);
			after.blit(best.shape);
			double typeBadness = fitness.badness(field, after, next);
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
