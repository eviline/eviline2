package org.eviline.core.ai;

import org.eviline.core.Field;

public class DefaultFitness implements Fitness {

	@Override
	public double badness(Field before, Field after) {
		after = after.clone();
		after.clearLines();
		
		int blocksBefore = 0;
		int blocksAfter = 0;
		
		int vtxBefore = 0;
		int vtxAfter = 0;
		
		for(int y = -4; y < Field.HEIGHT; y++) {
			blocksBefore += Long.bitCount(before.mask(y)) * (20 - y);
			blocksAfter += Long.bitCount(after.mask(y)) * (20 - y);
		}

		for(int y = -3; y < Field.HEIGHT; y++) {
			vtxBefore += Long.bitCount(before.mask(y-1) ^ before.mask(y));
			vtxAfter += Long.bitCount(after.mask(y-1) ^ after.mask(y));
		}
		
		return (blocksAfter - blocksBefore) + (vtxAfter - vtxBefore) * 30;
	}

}
