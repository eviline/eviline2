package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.Shorts;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			
			1.1169431379682038, 0.2853736324178939, 7.993277143455884, 10.02325856094052, 1.6382782012459867, 0.7076144192518014, 0.27780226112174305, 6.3301921643404535, 10.63784219396793, 0.05429545765631915, 0.6300740984403954, 0.4692135968257779, 6.744358872493625, 10.512873409897782, 0.6607633458336862, 0.26649481141299425, 0.33537927721419764, 7.232297542199828, 10.679539123285505, 0.5075741751506724, 0.22865958804356093, 0.2747650085052654, 6.903095558323753, 11.011739973348005, 0.18185834605609047, 0.27173341400885836, 0.2752152898119479, 6.717706783364804, 11.545196123927964, 0.08321512311085244, 0.12130108716110792, 0.3671639906879805, 6.47281608749846, 10.140057640141256, 0.023112928860503902, 0.40935838027939503, 0.6486884609083349, 6.514164728963695, 8.880368643998185, 0.14143004464961487

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
