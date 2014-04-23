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
	          1.1585322,          0.4275769,          7.3600681,          8.3487072,          0.3117855,
	          0.4054389,          1.5922303,          5.1842151,         10.1463184,         -0.2977344,
	          1.2088110,          0.6615418,          5.4878299,          9.4209339,         -0.2116619,
	          1.1055283,          0.3928556,          4.5631081,         12.7748115,         -0.0832696,
	          0.4394532,          0.2358981,          3.5182175,         12.9627154,         -0.0307242,
	          1.4976664,          0.8784527,          5.0755744,         13.8603938,         -0.4486791,
	          0.4345860,          0.0176689,          2.4702132,          9.3127614,          0.0059929,
	          2.0906244,          1.5656553,          4.0666013,         10.8901544,          0.1289889,

	};
	
	protected DefaultFitness df = new DefaultFitness();
	
	@Override
	public double badness(Field before, Field after, ShapeType[] next) {
		int off = 0;
		if(next != null && next.length > 0 && next[0] != null) {
			off = 5 + 5 * next[0].ordinal();
		} else {
			return df.badness(before, after, next);
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
