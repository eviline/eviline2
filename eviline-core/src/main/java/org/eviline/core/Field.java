package org.eviline.core;

import java.util.Arrays;

public class Field implements Cloneable {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	public static final int BUFFER = 3;
	
	protected static final long WALL = 0b1110000000000111;
	
	protected long[] mask;
	
	protected Block[] blocks;
	
	public Field() {
		reset();
	}
	
	public Field clone() {
		try {
			Field f = (Field) super.clone();
			f.mask = mask.clone();
			f.blocks = blocks.clone();
			return f;
		} catch(CloneNotSupportedException e) {
			throw new InternalError("clone not supported?");
		}
	}
	
	public void reset() {
		// reset the mask
		mask = new long[8];
		for(int y = -8; y < 20; y++)
			blit(y, WALL);
		mask[7] = -1L; // floor
		
		// reset the blocks
		blocks = new Block[(HEIGHT+12) * WIDTH];
	}
	
	/**
	 * Blits the lower 16 bits of {@code m} onto row {@code y}
	 * @param y
	 * @param m
	 */
	protected void blit(int y, long m) {
		int i = (y + 8) >> 2;
		int o = (y + 8) & 0b11;
		mask[i] |= (m << (o * 16));
	}
	
	/**
	 * Sets the lower 16 bits of {@code m} onto row {@code y}
	 * @param y
	 * @param m
	 */
	protected void set(int y, long m) {
		int i = (y + 8) >> 2;
		int o = (y + 8) & 0b11;
		mask[i] &= ~(0xffffL << (o * 16));
		mask[i] |= (m << (o * 16));
	}
	
	/**
	 * Returns row {@code y} as the lower 16 bits of the returned {@code long}
	 * @param y
	 * @return
	 */
	protected long get(int y) {
		int i = (y + 8) >> 2;
		int o = (y + 8) & 0b11;
		long imask = mask[i] >>> (o * 16);
		return imask & 0xffff;
	}
	
	/**
	 * Returns an intersection mask starting at row {@code y}
	 * @param y
	 * @return
	 */
	protected long imask(int y) {
		int i = (y + 8) >> 2;
		int o = (y + 8) & 0b11;
		long imask = mask[i] >>> (o * 16);
		if(o == 0)
			return imask;
		long jmask = mask[i+1] & ~(-1L << (o * 16));
		jmask = jmask << ((4-o) * 16);
		return imask | jmask;
	}
	
	/**
	 * Returns whether {@code s} intersects with existing blocks according to the mask
	 * @param s
	 * @return
	 */
	public boolean intersects(XYShape s) {
		if(s.y() < -8)
			return true;
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
					blocks[x + (y+8) * WIDTH] = s.block();
			}
		}
	}
	
	/**
	 * Clears row {@code y}, shifting all above rows down
	 * @param y
	 */
	public void clear(int y) {
		// shift the masks
		for(int i = y-1; i >= -8; i--)
			set(i+1, get(i));
		set(-8, WALL);
		
		// shift the blocks
		if(y+8 > 0)
			System.arraycopy(blocks, 0, blocks, WIDTH, WIDTH * (y+8));
		Arrays.fill(blocks, 0, WIDTH, null);
	}
	
	/**
	 * Find and clear any solid lines, and return the number of lines cleared
	 * @return
	 */
	public int clearLines() {
		int cleared = 0;
		for(int y = HEIGHT - 1; y >= -8; y--) {
			if(get(y) == 0xffff) {
				clear(y);
				y++;
				cleared++;
			}
		}
		return cleared;
	}
	
	public void shiftUp(long trashMask) {
		trashMask = trashMask << 3;
		trashMask = trashMask | 0b11100000000111L;
		for(int i = -8+1; i < Field.HEIGHT; i++)
			set(i-1, get(i));
		blit(Field.HEIGHT - 1, trashMask);
		System.arraycopy(blocks, WIDTH, blocks, 0, WIDTH * (Field.HEIGHT + 8-1));
		long m = 0b1000;
		for(int x = WIDTH-1; x >= 0; x--) {
			Block b = null;
			if((trashMask & m) == m)
				b = new Block(Block.MASK_GARBAGE);
			blocks[x + WIDTH * (Field.HEIGHT + 8-1)] = b;
			m = m << 1;
		}
	}
	
	/**
	 * Returns whether the block at row {@code y} col {@code x} is masked
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean masked(int x, int y) {
		return (get(y) & (1L << (12 - x))) != 0;
	}
	
	/**
	 * Returns a mask of cols 0-9 at row {@code y}
	 * @param y
	 * @return
	 */
	public long mask(int y) {
		return (get(y) >>> BUFFER) & 0b1111111111;
	}
	
	/**
	 * Returns the block at the specified row {@code y} and col {@code x}
	 * @param x 0 <= x < WIDTH
	 * @param y -4 <= y < HEIGHT + 4
	 * @return
	 */
	public Block block(int x, int y) {
		return blocks[x + (y+8) * WIDTH];
	}
	
	public void setBlock(int x, int y, Block b) {
		blocks[x + (y+8) * WIDTH] = b;
		long bm = 0b1000L;
		bm = bm << (WIDTH - (x+1));
		if(b != null) {
			blit(y, WALL | bm);
		} else {
			set(y, WALL | (get(y) & ~bm));
		}
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
