package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.Shorts;

public class DefaultFitness implements CoefficientFitness {

	protected double[] c = new double[] {
			0.4336283987124731, 0.36005330744432673, 6.98229343443467, 9.862352551247495, -0.02320808076007677
	};
	
	@Override
	public double badness(Field before, Field after, ShapeType[] next) {
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
			short bm = before.mask(y);
			short am = after.mask(y);
			
			if(bm != 0 && mhBefore == 0)
				mhBefore = Field.HEIGHT - y;
			if(am != 0 && mhAfter == 0)
				mhAfter = Field.HEIGHT - y;
			
			blocksBefore += Shorts.bitCount(bm);
			blocksAfter += Shorts.bitCount(am);
			
			htxBefore += Shorts.bitCount(0b1111111110 & (bm ^ (bm << 1)));
			htxAfter += Shorts.bitCount(0b1111111110 & (am ^ (am << 1)));
			
			pitsBefore += Shorts.bitCount((bm ^ (bm << 1)) & (bm ^ (bm >>> 1)));
			pitsAfter += Shorts.bitCount((am ^ (am << 1)) & (am ^ (am >>> 1)));
		}

		short bhm = 0;
		short ahm = 0;
		for(int y = -3; y < Field.HEIGHT; y++) {
			bhm |= before.mask(y-1);
			ahm |= after.mask(y-1);
			
			vtxBefore += Shorts.bitCount(before.mask(y-1) ^ before.mask(y));
			vtxAfter += Shorts.bitCount(after.mask(y-1) ^ after.mask(y));
			
			holesBefore += Shorts.bitCount(bhm & (bhm ^ before.mask(y)));
			holesAfter += Shorts.bitCount(ahm & (ahm ^ after.mask(y)));
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
