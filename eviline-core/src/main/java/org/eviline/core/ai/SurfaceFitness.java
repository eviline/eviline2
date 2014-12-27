package org.eviline.core.ai;

import java.util.Arrays;

import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class SurfaceFitness implements CoefficientFitness {
	protected static final double DF_SCALE_FACTOR = 0.5;
	
	protected static final int DOMAIN = 9; // [-4 ... 4]
	protected static final int LEN_DF = new DefaultFitness().getC().length;
	protected static final int LEN_I = 1;
	protected static final int LEN_SINGLE = DOMAIN;
	protected static final int LEN_PAIR = DOMAIN * DOMAIN;
	
	protected static final int OFF_DF = 0;
	protected static final int OFF_I = OFF_DF + LEN_DF;
	protected static final int OFF_SINGLE = OFF_I + LEN_I;
	protected static final int OFF_PAIR = OFF_SINGLE + LEN_SINGLE;
	
	protected double[] c;
	protected final DefaultFitness df;
	
	public SurfaceFitness() {
		df = new DefaultFitness();
		c = Arrays.copyOf(df.getC(), LEN_DF + LEN_I + LEN_SINGLE + LEN_PAIR);
	}

	@Override
	public double badness(Field before, Field after, ShapeType[] next) {
		return (fs(after) - fs(before)) + (df.badness(before, after, next) * DF_SCALE_FACTOR);
	}
	
	protected double fs(Field field) {
		int[] heights = new int[10];
		short doneMask = 0;
		for(int y = 0; y < field.HEIGHT; y++) {
			short mask = field.mask(y);
			int h = field.HEIGHT - y;
			heights[0] = Math.max(heights[0], ((mask >>> 9) & 0x1) * h);
			heights[1] = Math.max(heights[1], ((mask >>> 8) & 0x1) * h);
			heights[2] = Math.max(heights[2], ((mask >>> 7) & 0x1) * h);
			heights[3] = Math.max(heights[3], ((mask >>> 6) & 0x1) * h);
			heights[4] = Math.max(heights[4], ((mask >>> 5) & 0x1) * h);
			heights[5] = Math.max(heights[5], ((mask >>> 4) & 0x1) * h);
			heights[6] = Math.max(heights[6], ((mask >>> 3) & 0x1) * h);
			heights[7] = Math.max(heights[7], ((mask >>> 2) & 0x1) * h);
			heights[8] = Math.max(heights[8], ((mask >>> 1) & 0x1) * h);
			heights[9] = Math.max(heights[9], ((mask >>> 0) & 0x1) * h);
			doneMask |= mask;
			if(doneMask  == (short) 0b1111111111)
				break;
		}
		int[] deltas = new int[8];
		deltas[0] = domain(heights[1] - heights[0]);
		deltas[1] = domain(heights[2] - heights[1]);
		deltas[2] = domain(heights[3] - heights[2]);
		deltas[3] = domain(heights[4] - heights[3]);
		deltas[4] = domain(heights[5] - heights[4]);
		deltas[5] = domain(heights[6] - heights[5]);
		deltas[6] = domain(heights[7] - heights[6]);
		deltas[7] = domain(heights[8] - heights[7]);
		
		double score = c[OFF_I] * heights[9];
		
		for(int i = 0; i < 8; i++) {
			int d = deltas[i] + 4;
			score += c[OFF_SINGLE + d];
		}
		
		for(int i = 0; i < 7; i++) {
			int d0 = deltas[i] + 4;
			int d1 = deltas[i+1] + 4;
			int off = OFF_PAIR + DOMAIN * d0 + d1;
			score += c[off];
		}
		
		return score;
	}
	
	protected int domain(int delta) {
		return Math.min(4, Math.max(-4, delta));
	}

	@Override
	public double[] getC() {
		return c;
	}

	@Override
	public void setC(double[] c) {
		this.c = c;
		df.setC(Arrays.copyOf(c, LEN_DF));
	}

}
