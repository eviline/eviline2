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
		long ret = 0;
		ret |= (0xFFFFL & s[0 + off]) << 48;
		ret |= (0xFFFFL & s[1 + off]) << 32;
		ret |= (0xFFFFL & s[2 + off]) << 16;
		ret |= (0xFFFFL & s[3 + off]) << 0;
		return ret;
	}
	
	public static void set(short[] dest, int doff, short[] src, int soff, int len) {
		for(int i = 0; i < len; i++)
			dest[doff + i] |= src[soff + i];
	}
	
	public static void unset(short[] dest, int doff, short[] src, int soff, int len) {
		for(int i = 0; i < len; i++)
			dest[doff + i] &= ~src[soff + i];
	}
}
