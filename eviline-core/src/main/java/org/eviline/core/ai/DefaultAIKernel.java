package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class DefaultAIKernel implements AIKernel {

	protected Fitness fitness = new DefaultFitness();
	
	@Override
	public Vertex bestPlacement(Field field, XYShape current) {
		CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		Vertex best = null;
		
		for(XYShape shape : g.getVertices().keySet()) {
			if(!field.intersects(shape.shiftedDown()))
				continue;
			Field after = field.clone();
			after.blit(shape);
			double shapeBadness = fitness.badness(field, after);
			if(shapeBadness < badness) {
				best = g.getVertices().get(shape);
				badness = shapeBadness;
			}
		}
		
		return best;
	}
	
	@Override
	public ShapeType worstNext(Field field) {
		ShapeType worst = null;
		double badness = Double.NEGATIVE_INFINITY;
		
		for(ShapeType type : ShapeType.values()) {
			Field after = field.clone();
			Vertex best = bestPlacement(field, new XYShape(type.up(), type.startX(), type.startY()));
			after.blit(best.shape);
			double typeBadness = fitness.badness(field, after);
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
