package org.eviline.core.ai;

import org.eviline.core.Field;

public class DefaultFitness implements Fitness {

	@Override
	public double badness(Field before, Field after) {
		after = after.clone();
		after.clearLines();
		
		int mhBefore = 0;
		int mhAfter = 0;
		
		int blocksBefore = 0;
		int blocksAfter = 0;
		
		int vtxBefore = 0;
		int vtxAfter = 0;
		
		int htxBefore = 0;
		int htxAfter = 0;
		
		for(int y = -4; y < Field.HEIGHT; y++) {
			long bm = before.mask(y);
			long am = after.mask(y);
			
			if(bm != 0 && mhBefore == 0)
				mhBefore = Field.HEIGHT - y;
			if(am != 0 && mhAfter == 0)
				mhAfter = Field.HEIGHT - y;
			
			blocksBefore += Long.bitCount(bm) * (Field.HEIGHT - y);
			blocksAfter += Long.bitCount(am) * (Field.HEIGHT - y);
			
			htxBefore += Long.bitCount(0b1111111110 & (bm ^ (bm << 1)));
			htxAfter += Long.bitCount(0b1111111110 & (am ^ (am << 1)));
		}

		for(int y = -3; y < Field.HEIGHT; y++) {
			vtxBefore += Long.bitCount(before.mask(y-1) ^ before.mask(y));
			vtxAfter += Long.bitCount(after.mask(y-1) ^ after.mask(y));
		}
		
		return 
				(blocksAfter - blocksBefore) 
				+ Math.pow(10*(vtxAfter + htxAfter), 3.2)
				- Math.pow(10*(vtxBefore + htxBefore), 3.2)
				+ Math.pow(10*Math.abs(mhAfter - mhBefore), 1.5) * Math.signum(mhAfter - mhBefore)
				;
	}

}
