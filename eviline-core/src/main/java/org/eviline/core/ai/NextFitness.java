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
	          0.6049109,          0.3182668,          5.5684071,          8.2859944,          0.6282172,
	          1.5450041,          2.3240437,          5.1457018,          8.5665427,          0.3048121,
	          0.8627544,          0.2287359,          4.6921528,         10.3922459,          0.8609979,
	          0.6995730,          0.3421071,          4.8913234,         14.7426963,         -0.0521081,
	          1.2897075,          1.3543857,          4.1458872,         13.1510019,          1.0573515,
	          2.5369249,          0.7836423,          5.2288425,         14.0675073,          0.2611260,
	          0.5878343,         -0.0648808,          1.1399263,          9.9495274,          0.1054610,
	          0.8000863,          0.3308860,          4.2607723,          9.9833209,          0.5758435,

	};
	
//	protected DefaultFitness df = new DefaultFitness();
	
	@Override
	public double badness(Field before, Field after, ShapeType[] next) {
		int off = 0;
		if(next != null && next.length > 0 && next[0] != null) {
			off = 5 + 5 * next[0].ordinal();
		} else {
//			return df.badness(before, after, next);
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
