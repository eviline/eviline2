package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			
			0.5578489540028045, 0.47464424779222536, 8.535932228022919, 10.10454381500064, 0.009250565143829381, 0.21885957127886058, 0.28695053535292453, 7.399066546215874, 9.640219232896007, 0.88168984155149, 0.6640611892951541, 0.034710517452437584, 6.163641647257337, 11.387583262393921, 0.4633424505594539, 0.4654207178156424, 0.3978239897578474, 6.8029331977807965, 10.62089899114332, 0.4684744708283621, 0.09226548833712461, 0.2927858218533569, 6.991306160672326, 10.120539989700559, 0.5778388728934409, 0.4326111296055031, 0.49569559689914444, 7.375624972276603, 10.63740843759408, 0.05595802203554946, 0.3382631020184715, 0.3084262625100882, 6.863524684173186, 10.34537352593971, 0.08766714016846235, 0.38035517451376816, 0.7832740013656653, 6.124499173489741, 9.56630440674067, 0.5181973138625239

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
