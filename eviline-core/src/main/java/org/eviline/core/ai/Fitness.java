package org.eviline.core.ai;

import org.eviline.core.Field;

public interface Fitness {
	public double badness(Field before, Field after);
}
