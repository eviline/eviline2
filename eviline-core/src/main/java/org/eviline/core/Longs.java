package org.eviline.core;

public class Longs {
	public static short[] split(long l) {
		short[] ret = new short[4];
		ret[3] = (short)(l & 0xFFFF);
		ret[2] = (short)((l >>> 16) & 0xFFFF);
		ret[1] = (short)((l >>> 32) & 0xFFFF);
		ret[0] = (short)((l >>> 48) & 0xFFFF);
		return ret;
	}
	
	public static long join(short[] s) {
		return (long) s[0] << 48 | (long) s[1] << 32| (long) s[2] << 16 | (long) s[3];
	}
	
	public static void set(short[] dest, int doff, short[] src, int soff) {
		for(int i = 0; i < src.length; i++)
			dest[doff + i] |= src[soff + i];
	}
	
	public static void unset(short[] dest, int doff, short[] src, int soff) {
		for(int i = 0; i < src.length; i++)
			dest[doff + i] &= ~src[soff + i];
	}
}
