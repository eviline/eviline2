package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public interface AIKernel {
	public CommandGraph bestPlacement(Field field, int shape, ShapeType[] next, int lookahead);
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next, int lookahead);
}
