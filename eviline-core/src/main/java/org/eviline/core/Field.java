package org.eviline.core;

import java.util.Arrays;

public class Field implements Cloneable {
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	public static final int BUFFER = 3;
	
	protected static final short WALL = (short) 0b1110000000000111;
	
	protected short[] mask;
	
	protected Block[] blocks;
	
	public Field() {
		reset();
	}
	
	public void copyFrom(Field other) {
		this.mask = other.mask.clone();
		this.blocks = other.blocks.clone();
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
		mask = new short[32];
		int y;
		for(y = -8; y < Field.HEIGHT; y++)
			set(y, WALL);
		for(; y < Field.HEIGHT + BUFFER; y++)
			set(y, (short) -1);
		
		// reset the blocks
		blocks = new Block[(HEIGHT+12) * WIDTH];
	}
	
	protected short get(int y) {
		return mask[y + 8];
	}
	
	protected void set(int y, short v) {
		mask[y+8] = v;
	}
	
	/**
	 * Returns an intersection mask starting at row {@code y}
	 * @param y
	 * @return
	 */
	protected long imask(int y) {
		return Shorts.pack(mask, y+8);
	}
	
	/**
	 * Returns whether {@code s} intersects with existing blocks according to the mask
	 * @param s
	 * @return
	 */
	public boolean intersects(int xyshape) {
		int s_x = XYShapes.xFromInt(xyshape);
		int s_y = XYShapes.yFromInt(xyshape);
		int s_id = XYShapes.shapeIdFromInt(xyshape);
		long imask = imask(s_y);
		long smask = Shape.shapeMask(s_id, s_x);
		return (imask & smask) != 0;
	}
	
	/**
	 * Blits the shape onto the {@link #mask} and {@link #blocks}.
	 * Slow operation.
	 * @param s
	 */
	public void blit(int xyshape, long id) {
		int s_x = XYShapes.xFromInt(xyshape);
		int s_y = XYShapes.yFromInt(xyshape);
		int s_id = XYShapes.shapeIdFromInt(xyshape);
		long smask = Shape.shapeMask(s_id, s_x);
		Shorts.setBits(mask, s_y+8, smask);
		Block block = new Block(Shape.fromOrdinal(s_id), id);
		for(int i = 3; i >= 0; i--) {
			smask = smask >>> 3;
			int y = s_y + i;
			for(int x = WIDTH-1; x >= 0; x--) {
				if((smask & 1) != 0)
					blocks[x + (y+8) * WIDTH] = block;
				smask = smask >>> 1;
			}
			smask = smask >>> 3;
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
			if(get(y) == (short) 0xffff) {
				clear(y);
				y++;
				cleared++;
			}
		}
		return cleared;
	}
	
	public void shiftUp(short trashMask) {
		trashMask |= (short) 0b11100000000111;
		for(int i = -8+1; i < Field.HEIGHT; i++)
			set(i-1, get(i));
		set(Field.HEIGHT - 1, trashMask);
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
		return (get(y) & (1 << (12 - x))) != 0;
	}
	
	/**
	 * Returns a mask of cols 0-9 at row {@code y}
	 * @param y
	 * @return
	 */
	public short mask(int y) {
		return (short)(0x3ff & (get(y) >>> BUFFER));
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
		short bm = 0b1000;
		bm = (short) (bm << (WIDTH - (x+1)));
		if(b != null) {
			set(y, (short)(get(y) |  bm));
		} else {
			set(y, (short)(get(y) & ~bm));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char blk = '\u2588';
		for(int y = -8; y < HEIGHT + BUFFER; y++) {
			short m = mask[y+8];
			for(int x = 15; x >= 0; x--) {
				short xm = (short)(1 << x);
				sb.append((m & xm) != 0 ? blk : ' ');
			}
			sb.append("\n");
		}
		return sb.toString();
	}	
}
