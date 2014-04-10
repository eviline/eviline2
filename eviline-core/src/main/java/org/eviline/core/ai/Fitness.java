package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public interface Fitness {
	public double badness(Field before, Field after, ShapeType[] next);
}
