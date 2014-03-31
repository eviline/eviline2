package org.eviline.core.ai;

import java.util.Map;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class DefaultAIKernel implements AIKernel {

	protected Fitness fitness = new DefaultFitness();
	
	@Override
	public Vertex bestPlacement(Field field, ShapeType type) {
		CommandGraph g = new CommandGraph(field, new XYShape(type.up(), type.startX(), type.startY()));
		double badness = Double.POSITIVE_INFINITY;
		
		Vertex best = null;
		
		for(XYShape shape : g.getVertices().keySet()) {
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
			Vertex best = bestPlacement(field, type);
			after.blit(best.shape);
			double typeBadness = fitness.badness(field, after);
			if(typeBadness > badness) {
				worst = type;
				badness = typeBadness;
			}
		}
		
		return worst;
	}

}
