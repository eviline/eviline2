package org.eviline.core.ai;

import org.eviline.core.Field;

public class DefaultFitness implements Fitness {

	@Override
	public double badness(Field before, Field after) {
		after = after.clone();
		after.clearLines();
		
		int blocksBefore = 0;
		int blocksAfter = 0;
		
		for(int y = -4; y < Field.HEIGHT; y++) {
			blocksBefore += Long.bitCount(before.mask(y)) * (20 - y);
			blocksAfter += Long.bitCount(after.mask(y)) * (20 - y);
		}
		
		return blocksAfter - blocksBefore;
	}

}
