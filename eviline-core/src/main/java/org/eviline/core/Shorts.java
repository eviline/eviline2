package org.eviline.core;

public class Shorts {
	public static short[] split(long l) {
		short[] ret = new short[4];
		ret[0] = (short)((l & 0xffff000000000000L) >>> 48);
		ret[1] = (short)((l & 0xffff00000000L) >>> 32);
		ret[2] = (short)((l & 0xffff0000L) >>> 16);
		ret[3] = (short)(l & 0xffffL);
		return ret;

	}

	public static long pack(short[] s, int off) {
		long v = 0xffffl & s[off + 3];
		v |= (0xffffl & s[off + 2]) << 16;
		v |= (0xffffl & s[off + 1]) << 32;
		v |= (0xffffl & s[off]) << 48;
		return v;
	}

	public static void setBits(short[] dest, int doff, long src) {
		doff +=3;
		dest[doff--] |= (short) src;
		src = src >>> 16;
		dest[doff--] |= (short) src;
		src = src >>> 16;
		dest[doff--] |= (short) src;
		src = src >>> 16;
		dest[doff] |= (short) src;
	}

	public static void setBits(short[] dest, int doff, short[] src, int soff, int len) {
		for(int i = 0; i < len; i++)
			dest[doff + i] |= src[soff + i];
	}

	public static void clearBits(short[] dest, int doff, short[] src, int soff, int len) {
		for(int i = 0; i < len; i++)
			dest[doff + i] &= ~src[soff + i];
	}

	public static int bitCount(int x) {
		return bitCount((short) x);
	}

	/** Count the number of set bits in a short;
	 *  @param x the short to have its bits counted
	 *  @returns the number of bits set in x
	 *  @author Tim Tyler tt@iname.com
	 */
	public static int bitCount(short x) {
		int temp;
		int y = (int)x;

		temp = 0x5555;
		y = (y & temp) + (y >>> 1 & temp);
		temp = 0x3333;
		y = (y & temp) + (y >>> 2 & temp);
		temp = 0x0707;
		y = (y & temp) + (y >>> 4 & temp);

		return (y & 0x000f) + (y >>> 8);
	}

	private Shorts() {}
}
