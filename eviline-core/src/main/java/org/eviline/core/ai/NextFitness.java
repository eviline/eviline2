package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			// first row, no lookahead
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			// subsequent rows, one row per ShapeType ordinal
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
			-0.04981645059901735, 0.3741389478656609, 4.420258842311678, 9.064838762028263, -0.17897122888422823,
	};
	
	@Override
	public double badness(Field before, Field after, ShapeType[] next) {
		int off = 0;
		if(next.length > 0 && next[0] != null) {
			off = 5 + 5 * next[0].ordinal();
		}
		
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
				c[off + 0] * (blocksAfter - blocksBefore) 
				+ c[off + 1] * ((vtxAfter * htxAfter) - (vtxBefore * htxBefore))
				+ c[off + 2] * (mhAfter - mhBefore)
				+ c[off + 3] * (holesAfter-holesBefore)
				+ c[off + 4] * (pitsAfter - pitsBefore)
				;
	}

	public double[] getC() {
		return c;
	}

	public void setC(double[] c) {
		this.c = c;
	}

}
