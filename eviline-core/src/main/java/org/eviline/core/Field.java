package org.eviline.core;

import java.util.Arrays;

public class Field {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	public static final int BUFFER = 3;
	
	protected static final long WALL = 0b1110000000000111;
	
	protected long[] mask;
	
	protected Block[] blocks;
	
	public Field() {
		reset();
	}
	
	public void reset() {
		// reset the mask
		mask = new long[7];
		for(int y = -4; y < 20; y++)
			blit(y, WALL);
		mask[6] = -1L; // floor
		
		// reset the blocks
		blocks = new Block[(HEIGHT+8) * WIDTH];
	}
	
	/**
	 * Blits the lower 16 bits of {@code m} onto row {@code y}
	 * @param y
	 * @param m
	 */
	protected void blit(int y, long m) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		mask[i] |= (m << (o * 16));
	}
	
	/**
	 * Sets the lower 16 bits of {@code m} onto row {@code y}
	 * @param y
	 * @param m
	 */
	protected void set(int y, long m) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		mask[i] &= ~(0xffffL << (o * 16));
		mask[i] |= (m << (o * 16));
	}
	
	/**
	 * Returns row {@code y} as the lower 16 bits of the returned {@code long}
	 * @param y
	 * @return
	 */
	protected long get(int y) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		long imask = mask[i] << (o * 16);
		return imask & 0xffff;
	}
	
	/**
	 * Returns an intersection mask starting at row {@code y}
	 * @param y
	 * @return
	 */
	protected long imask(int y) {
		int i = (y + 4) >> 2;
		int o = (y + 4) & 0b11;
		long imask = mask[i] << (o * 16);
		if(o == 0)
			return imask;
		long m = ~(-1 << (o * 16));
		return imask | ((mask[i+1] & m) << ((4-o) * 16));
	}
	
	/**
	 * Returns whether {@code s} intersects with existing blocks according to the mask
	 * @param s
	 * @return
	 */
	public boolean intersects(XYShape s) {
		long imask = imask(s.y());
		long smask = s.shape().mask(s.x());
		return imask != (imask & ~smask);
	}
	
	/**
	 * Blits the shape onto the {@link #mask} and {@link #blocks}.
	 * Slow operation.
	 * @param s
	 */
	public void blit(XYShape s) {
		long smask = s.shape().mask(s.x());
		for(int i = 0; i < 4; i++) {
			int y = s.y() + i;
			long m = (smask >>> (i*16)) & 0xffff;
			blit(y, m);
			for(int x = 0; x < WIDTH; x++) {
				if((m & ((1 << 12) >>> x)) != 0)
					blocks[x + (y+4) * WIDTH] = s.block();
			}
		}
	}
	
	/**
	 * Clears row {@code y}, shifting all above rows down
	 * @param y
	 */
	public void clear(int y) {
		// shift the masks
		for(int i = y-1; i >= -4; i--)
			set(i+1, get(i));
		set(-4, WALL);
		
		// shift the blocks
		if(y+4-1 > 0)
			System.arraycopy(blocks, 0, blocks, WIDTH, WIDTH * (y+4-1));
		Arrays.fill(blocks, 0, WIDTH, null);
	}
	
	public int clearLines() {
		int cleared = 0;
		for(int y = HEIGHT - 1; y >= -4; y--) {
			if(get(y) == 0xffff) {
				clear(y);
				y++;
				cleared++;
			}
		}
		return cleared;
	}
	
	/**
	 * Returns the block at the specified row {@code y} and col {@code x}
	 * @param x 0 <= x < WIDTH
	 * @param y -4 <= y < HEIGHT + 4
	 * @return
	 */
	public Block block(int x, int y) {
		return blocks[x + (y+4) * WIDTH];
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char[] blk = new char[] {' ', '\u2584', '\u2580', '\u2588'};
		for(long m : mask) {
			for(int i = 15; i >= 0; i--) {
				boolean top = (m & (1L << i)) != 0;
				boolean bot = (m & (1L << (i+16))) != 0;
				sb.append(blk[(top?2:0) + (bot?1:0)]);
			}
			sb.append('\n');
			for(int i = 47; i >= 32; i--) {
				boolean top = (m & (1L << i)) != 0;
				boolean bot = (m & (1L << (i+16))) != 0;
				sb.append(blk[(top?2:0) + (bot?1:0)]);
			}
			sb.append('\n');
		}
		return sb.toString();
	}	
}
