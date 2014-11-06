package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			
			1.237927804539232, 0.8372468711750208, 7.519580036189341, 10.033990328817746, 1.6673921025605043,
			0.5178969800542423, 0.24288633752976954, 6.438465550239929, 9.871760760944694, 0.05425830048215226, 
			0.6790464784623796, 0.38176680864140417, 6.869751319530422, 10.79022022563639, 0.4525433663884086, 
			0.2664348042411383, 0.722177388620118, 7.249679564781709, 10.680110523075603, 0.5189993680009173, 
			0.11361240918625945, 0.1906553839776781, 7.005125364622191, 11.060907553982656, 0.5485864620427946, 
			0.1415756319975379, 0.26959094150436874, 6.929180876582973, 11.297784112284274, 0.10830436007345118, 
			0.3075125387781086, 0.3283471962333028, 6.437827502218317, 9.839017715267227, 0.08885843452382965, 
			0.3596909282666165, 0.39077370583109533, 6.967421549234407, 9.327408215527983, 0.3969266275225346

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
		
		short[] beforeMasks = new short[Field.HEIGHT + 4];
		short[] afterMasks = new short[Field.HEIGHT + 4];

		for(int y = -4; y < Field.HEIGHT; y++) {
			long bm = beforeMasks[y+4] = before.mask(y);
			long am = afterMasks[y+4] = after.mask(y);
			
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
			bhm |= beforeMasks[y+3];
			ahm |= afterMasks[y+3];
			
			vtxBefore += Long.bitCount(beforeMasks[y+3] ^ beforeMasks[y+4]);
			vtxAfter += Long.bitCount(afterMasks[y+3] ^ afterMasks[y+4]);
			
			holesBefore += Long.bitCount(bhm & (bhm ^ beforeMasks[y+4]));
			holesAfter += Long.bitCount(ahm & (ahm ^ afterMasks[y+4]));
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
