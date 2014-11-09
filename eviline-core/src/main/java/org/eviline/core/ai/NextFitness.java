package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.Shorts;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			
			1.2679804572064999, 1.0272292414104676, 7.655827848467139, 10.088232811812162, 1.4267745527163131, 0.39861934969609003, 0.24361882869666313, 6.453569497353521, 10.016604664812673, 0.07926182456021921, 0.7791633967804836, 0.7694635299746428, 6.870637915792503, 10.80755214008806, 0.8167210672214846, 0.12377837285019885, 0.6429246277988085, 7.556908307880067, 9.934255098506545, 0.4639757448678689, 0.016568478430921352, 0.10555340388492203, 7.241333008965312, 10.6812229565033, 0.1439745896373311, 0.11723810154521, 0.14855102076666332, 6.918430322592078, 11.403059882769503, 0.18970498483145265, 0.10142969642424475, 0.13732731066467305, 6.456079166529966, 9.732660283354484, 0.4739252562195801, 0.40268790838764773, 0.6398284028570906, 6.966444592184877, 9.693477035388883, 0.42874147210642705

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
			short bm = beforeMasks[y+4] = before.mask(y);
			short am = afterMasks[y+4] = after.mask(y);
			
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
			bhm |= beforeMasks[y+3];
			ahm |= afterMasks[y+3];
			
			vtxBefore += Shorts.bitCount(beforeMasks[y+3] ^ beforeMasks[y+4]);
			vtxAfter += Shorts.bitCount(afterMasks[y+3] ^ afterMasks[y+4]);
			
			holesBefore += Shorts.bitCount(bhm & (bhm ^ beforeMasks[y+4]));
			holesAfter += Shorts.bitCount(ahm & (ahm ^ afterMasks[y+4]));
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
