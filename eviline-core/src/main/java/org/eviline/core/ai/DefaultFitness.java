package org.eviline.core.ai;

import org.eviline.core.Field;

public class DefaultFitness implements Fitness {

	protected double[] c = new double[] {
			0.3891297691277476, 0.36143725872140636, 6.592529654860779, 10.033920006980495, 0.15180835858364783
	};
	
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
		
		int holesBefore = 0;
		int holesAfter = 0;
		
		int pitsBefore = 0;
		int pitsAfter = 0;
		
		for(int y = -4; y < Field.HEIGHT; y++) {
			long bm = before.mask(y);
			long am = after.mask(y);
			
			if(bm != 0 && mhBefore == 0)
				mhBefore = Field.HEIGHT - y;
			if(am != 0 && mhAfter == 0)
				mhAfter = Field.HEIGHT - y;
			
			blocksBefore += Long.bitCount(bm);
			blocksAfter += Long.bitCount(am);
			
			htxBefore += Long.bitCount(0b1111111110 & (bm ^ (bm << 1)));
			htxAfter += Long.bitCount(0b1111111110 & (am ^ (am << 1)));
			
			pitsBefore += Long.bitCount((bm ^ (bm << 1)) & (bm ^ (bm >>> 1)));
			pitsAfter += Long.bitCount((am ^ (am << 1)) & (am ^ (am >>> 1)));
		}

		long bhm = 0;
		long ahm = 0;
		for(int y = -3; y < Field.HEIGHT; y++) {
			bhm |= before.mask(y-1);
			ahm |= after.mask(y-1);
			
			vtxBefore += Long.bitCount(before.mask(y-1) ^ before.mask(y));
			vtxAfter += Long.bitCount(after.mask(y-1) ^ after.mask(y));
			
			holesBefore += Long.bitCount(bhm & (bhm ^ before.mask(y)));
			holesAfter += Long.bitCount(ahm & (ahm ^ after.mask(y)));
		}
		
		return 
				c[0] * (blocksAfter - blocksBefore) 
				+ c[1] * ((vtxAfter * htxAfter) - (vtxBefore * htxBefore))
				+ c[2] * (mhAfter - mhBefore)
				+ c[3] * (holesAfter-holesBefore)
				+ c[4] * (pitsAfter - pitsBefore)
				;
	}

	public double[] getC() {
		return c;
	}

	public void setC(double[] c) {
		this.c = c;
	}

}
