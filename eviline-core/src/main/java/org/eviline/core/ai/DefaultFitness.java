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
		
		int htxBefore = 0;
		int htxAfter = 0;
		
		for(int y = -4; y < Field.HEIGHT; y++) {
			long bm = before.mask(y);
			long am = after.mask(y);
			
			blocksBefore += Long.bitCount(bm) * (20 - y);
			blocksAfter += Long.bitCount(am) * (20 - y);
			
			htxBefore += Long.bitCount(bm ^ (bm << 1));
			htxAfter += Long.bitCount(am ^ (am << 1));
		}

		for(int y = -3; y < Field.HEIGHT; y++) {
			vtxBefore += Long.bitCount(before.mask(y-1) ^ before.mask(y));
			vtxAfter += Long.bitCount(after.mask(y-1) ^ after.mask(y));
		}
		
		return 
				(blocksAfter - blocksBefore) + 
				Math.pow(20*(vtxAfter + htxAfter), 3)
				- Math.pow(20*(vtxBefore + htxBefore), 3);
	}

}
