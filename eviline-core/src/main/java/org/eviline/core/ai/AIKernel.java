package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public interface AIKernel {
	public Vertex bestPlacement(Field field, XYShape shape, ShapeType[] next, int lookahead);
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next, int lookahead);
}
