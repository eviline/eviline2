package org.eviline.core.ai;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.Shorts;

public class NextFitness implements CoefficientFitness {
	protected double[] c = new double[] {
			
			1.1838583959291287, 0.8376435075916467, 7.51711982712575, 9.534311931688546, 1.6556494178442462, 
			0.4313450625381037, 0.24684806894903175, 6.437483124788034, 10.291868566984187, 0.2642965187858961, 
			0.5029631144421812, 0.45675686452632874, 6.863635850792088, 11.183800391934403, 0.17746933791000624, 
			0.3703978399218468, 0.5688282602201166, 7.779853213091811, 10.694989061119914, 0.7172766334821792, 
			0.10515057754159697, 0.21467691742027453, 6.732925345950574, 11.370842709266064, 0.7603819757327897, 
			0.31801135082541315, 0.26943366115385836, 6.974579311326651, 11.297777046143889, 0.17961566972735177, 
			0.30493461824446644, 0.29473788930567135, 6.482201723582194, 9.842059222841481, 0.10792793099782672, 
			0.36090358731732997, 0.3911557591041263, 6.705652904466988, 9.456349892788255, 0.40756668169684607

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
