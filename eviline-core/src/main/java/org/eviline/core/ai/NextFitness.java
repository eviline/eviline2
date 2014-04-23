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
			1.3779738008898461, 1.0589794356035664, 6.501191543711048, 8.609669717140306, 1.233349397534732, 1.5687340847914713, 1.9730795457036103, 5.001088652414637, 8.208991283243618, 0.9530052448243305, 0.5730846806939912, 0.23930862970047775, 5.374890677711486, 10.192156756917743, 1.5236066251222056, 1.5595298603293593, 0.6642407739423724, 5.080520811181822, 14.81802453274184, 0.3363319859340842, 1.543634560159607, 1.2145067129577174, 4.5805039663018805, 12.689979853152922, 0.8784718806051003, 2.5554901907549152, 0.7805550769208541, 5.30974371422067, 14.164167546037284, 0.8780390043798167, 0.5091757479412435, -0.018102038339347055, 0.9028545803667805, 8.986593742529154, -0.21358769189181243, 1.2556905921447628, 0.39728050127923287, 4.559486544996188, 9.64760713435194, 0.6573559065271688

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
