package org.eviline.core;

import java.util.concurrent.atomic.AtomicLong;

public class XYShapes {
	public static final int SHAPE_MAX = toXYShape(15, 19, Shape.values()[Shape.values().length - 1]);
	
	public static boolean has(int xyshape, int x, int y) {
		int this_x = xFromInt(xyshape);
		int this_y = yFromInt(xyshape);
		Shape shape = shapeFromInt(xyshape);
		if(y < this_y || y >= this_y + 4 || x < this_x || x >= this_x + 4)
			return false;
		return shape.has(x - this_x, y - this_y);
	}
	
	public static int toXYShape(int x, int y, Shape shape) {
		int i = 0;
		i |= ((x+3) & 0xf);
		i |= (((y+8) & 0x1f) << 4);
		i |= (shape.ordinal() << 9);
		return i;
	}
	
	public static int xFromInt(int i) {
		return (i & 0xf) - 3;
	}
	
	public static int yFromInt(int i) {
		return ((i >>> 4) & 0x1f) - 8;
	}
	
	public static int shapeIdFromInt(int i) {
		return (i >>> 9);
	}
	
	public static Shape shapeFromInt(int i) {
		return Shape.fromOrdinal(i >>> 9);
	}
	
	public static int rotatedRight(int xyshape) {
		int i = xyshape & 0b11100111111111;
		int r = (xyshape >>> 9) & 0x3;
		r = (r+1) & 0x3;
		i |= (r << 9);
		return i;
	}
	
	public static int rotatedLeft(int xyshape) {
		int i = xyshape & 0b11100111111111;
		int r = (xyshape >>> 9) & 0x3;
		r = (r-1) & 0x3;
		i |= (r << 9);
		return i;
	}
	
	public static int shiftedLeft(int xyshape) {
		int i = xyshape & 0b11111111110000;
		int x = xyshape & 0xf;
		x = (x-1) & 0xf;
		return i | x;
	}
	
	public static int shiftedRight(int xyshape) {
		int i = xyshape & 0b11111111110000;
		int x = xyshape & 0xf;
		x = (x+1) & 0xf;
		return i | x;
	}
	
	public static int shiftedDown(int xyshape) {
		int i = xyshape & 0b11111000001111;
		int y = (xyshape >>> 4) & 0x1f;
		y = (y+1) & 0x1f;
		return i | (y << 4);
	}

	public static int shiftedUp(int xyshape) {
		int i = xyshape & 0b11111000001111;
		int y = (xyshape >>> 4) & 0x1f;
		y = (y-1) & 0x1f;
		return i | (y << 4);
	}

	public static int[] kickedLeft(int xyshape) {
		Shape shape;
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		KickTable kt = (shape = shapeFromInt(xyshape)).leftKick();
		int[] kicked = new int[kt.table().length];
		for(int i = 0; i < kicked.length; i++)
			kicked[i] = toXYShape(x + kt.table()[i][0], y + kt.table()[i][1], shape);
		return kicked;
	}
	
	public static int[] kickedRight(int xyshape) {
		Shape shape;
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		KickTable kt = (shape = shapeFromInt(xyshape)).rightKick();
		int[] kicked = new int[kt.table().length];
		for(int i = 0; i < kicked.length; i++)
			kicked[i] = toXYShape(x + kt.table()[i][0], y + kt.table()[i][1], shape);
		return kicked;
	}
}
