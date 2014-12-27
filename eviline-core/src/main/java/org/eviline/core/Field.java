package org.eviline.core;

import java.util.Arrays;

public class Field implements Cloneable {
	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 20;
	public static final int BUFFER = 3;

	protected static final long SPAWN_MASK =
			(0b1111L << 60) |
			(0b1111L << 44) ;


	protected static final Block GARBAGE_BLOCK = new Block(Block.MASK_GARBAGE);

	public final int WIDTH;
	public final int HEIGHT;

	protected final short WALL; // = (short) 0b1110000000000111;
	protected final short CLEAR;

	protected short[] mask;

	protected Block[] blocks;

	protected long lines;
	protected long score;

	protected int comboMultiplier;
	protected long comboScore;

	protected long[] typeBlitCounts;

	protected boolean noBlocks;

	public Field() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Field(int width, int height) {
		if(width < 1 || width > 10 || height < 1 || height > 20)
			throw new IllegalArgumentException();
		WIDTH = width;
		HEIGHT = height;
		WALL = (short)(0b1110000000000000 | (7 << (10 - WIDTH)));
		CLEAR = (short)(-1 << (10 - WIDTH));
		reset();
	}

	public void copyFrom(Field other) {
		this.mask = other.mask.clone();
		this.blocks = other.blocks.clone();
		this.typeBlitCounts = other.typeBlitCounts.clone();
		this.lines = other.lines;
		this.score = other.score;
		this.comboMultiplier = other.comboMultiplier;
	}

	public Field clone() {
		try {
			Field f = (Field) super.clone();
			f.mask = mask.clone();
			f.blocks = blocks.clone();
			f.typeBlitCounts = typeBlitCounts.clone();
			return f;
		} catch(CloneNotSupportedException e) {
			throw new InternalError("clone not supported?");
		}
	}

	public void reset() {
		// reset the mask
		mask = new short[32];
		int y;
		for(y = -8; y < HEIGHT; y++)
			set(y, WALL);
		for(; y < HEIGHT + BUFFER; y++)
			set(y, (short) -1);

		// reset the blocks
		blocks = new Block[(HEIGHT+12) * WIDTH];
		lines = 0;
		score = 0;
		comboMultiplier = 0;
		typeBlitCounts = new long[ShapeType.COUNT];
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

	public boolean isSpawnEndangered() {
		long imask = imask(-2);
		long smask = SPAWN_MASK >>> (BUFFER + WIDTH / 2 - 2);
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
		int s_ti = XYShapes.shapeTypeIdFromInt(xyshape);
		long smask = Shape.shapeMask(s_id, s_x);
		Shorts.setBits(mask, s_y+8, smask);
		if(!noBlocks) {
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
		typeBlitCounts[s_ti]++;
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

		if(!noBlocks) {
			// shift the blocks
			if(y+8 > 0)
				System.arraycopy(blocks, 0, blocks, WIDTH, WIDTH * (y+8));
			Arrays.fill(blocks, 0, WIDTH, null);
		}
	}

	/**
	 * Find and clear any solid lines, and return the number of lines cleared
	 * @return
	 */
	public int clearLines() {
		int cleared = 0;
		for(int y = HEIGHT - 1; y >= -8; y--) {
			if(get(y) == CLEAR) {
				clear(y);
				y++;
				cleared++;
			}
		}
		lines += cleared;
		if(cleared > 0) {
			score += cleared * cleared - 1;
			comboMultiplier += cleared;
			comboScore += cleared * comboMultiplier - 1;
		} else
			comboMultiplier = 0;
		return cleared;
	}

	public void shiftUp(short trashMask) {
		trashMask |= (short) 0b11100000000111;
		for(int i = -8+1; i < HEIGHT; i++)
			set(i-1, get(i));
		set(HEIGHT - 1, trashMask);
		if(!noBlocks) {
			System.arraycopy(blocks, WIDTH, blocks, 0, WIDTH * (HEIGHT + 8-1));
			long m = 0b1000;
			for(int x = WIDTH-1; x >= 0; x--) {
				Block b = null;
				if((trashMask & m) == m)
					b = GARBAGE_BLOCK;
				blocks[x + WIDTH * (HEIGHT + 8-1)] = b;
				m = m << 1;
			}
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
		if(noBlocks && masked(x, y))
			return GARBAGE_BLOCK;
		return blocks[x + (y+8) * WIDTH];
	}

	public void setBlock(int x, int y, Block b) {
		if(noBlocks)
			return;
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

	public long getLines() {
		return lines;
	}

	public long getScore() {
		return score;
	}

	public long getComboScore() {
		return comboScore;
	}

	public long[] getTypeBlitCounts() {
		return typeBlitCounts;
	}

	public boolean isNoBlocks() {
		return noBlocks;
	}

	public void setNoBlocks(boolean noBlocks) {
		this.noBlocks = noBlocks;
	}
}
