package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
/*			
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
*/
	          0.1556682,          2.0951980,          7.6433412,          8.1946781,          1.1210992,
	          1.5332807,          0.8666180,          5.1502283,          8.0735581,         -0.1117730,
	          0.0978346,          0.2226518,          5.9841487,          9.1601265,         -0.2579902,
	          1.8137632,          0.8759923,          4.2245475,         11.2520540,          0.1769108,
	          0.5504363,          0.8258910,          4.2423165,         11.6128805,          0.3075744,
	          1.7622339,          0.3519774,          4.0907373,         13.2814185,          1.2253584,
	          0.3383231,          0.1289832,          3.7040210,         10.2629033,          0.9502953,
	          0.8881288,          1.2865813,          3.8518674,         10.4412015,          0.4084755,

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
