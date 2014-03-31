package org.eviline.core;

public class Field {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	public static final int BUFFER = 3;
	
	private static final int WALL = 0b1110000000000111;
	
	protected long[] mask = new long[7];
	
	public Field() {
		reset();
	}
	
	protected void reset() {
		mask = new long[7];
		for(int y = -4; y < 20; y++)
			blit(y, WALL);
		mask[6] = -1L; // floor
	}
	
	protected void blit(int y, long m) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		mask[i] |= (m << (o * 16));
	}
	
	protected long imask(int y) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		long imask = mask[i] << (o * 16);
		if(o == 0)
			return imask;
		long m = ~(-1 << (o * 16));
		return imask | ((mask[i+1] & m) << ((4-o) * 16));
	}
	
	public boolean intersects(XYShape s) {
		long imask = imask(s.y());
		long smask = s.shape().mask(s.x());
		return imask != (imask & ~smask);
	}
}
