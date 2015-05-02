package org.eviline.core;

import java.util.Arrays;

public class XYShapes {
	public static final int SHAPE_MAX = toXYShape(9, 19, Shape.values()[Shape.values().length - 1]);
	
	public static final int[] CANONICAL_SHAPES;
	static {
		int[] canon = new int[SHAPE_MAX];
		int count = 0;
		for(int i = 0; i < SHAPE_MAX; i++) {
			if(!isSynonym(i))	
				canon[count++] = i;
		}
		CANONICAL_SHAPES = Arrays.copyOf(canon, count);
	}
	
	public static final int MASK_X =    0b00000000001111;
	public static final int MASK_Y =    0b00000111110000;
	public static final int MASK_DIR =  0b00011000000000;
	public static final int MASK_TYPE = 0b11100000000000;
	
	public static final int MASK_TYPE_POS = MASK_X | MASK_Y | MASK_DIR;
	public static final int SIZE_TYPE_POS = 1 << 11;
	
	public static int startIntForTypeId(int type) {
		return type << 11;
	}
	
	public static int stopIntForTypeId(int type) {
		return Math.min((type + 1) << 11, SHAPE_MAX);
	}
	
	public static boolean has(int xyshape, int x, int y) {
		int this_x = xFromInt(xyshape);
		int this_y = yFromInt(xyshape);
		Shape shape = shapeFromInt(xyshape);
		if(y < this_y || y >= this_y + 4 || x < this_x || x >= this_x + 4)
			return false;
		return shape.has(x - this_x, y - this_y);
	}
	
	public static int toXYShape(int x, int y, Shape shape) {
		return toXYShape(x, y, shape.ordinal());
	}
	
	public static int toXYShape(int x, int y, int shape) {
		int i = 0;
		i |= ((x+3) & 0xf);
		i |= (((y+8) & 0x1f) << 4);
		i |= (shape << 9);
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
	
	public static int shapeTypeIdFromInt(int i) {
		return shapeIdFromInt(i) >>> 2;
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
		for(int i = 0; i < kicked.length; i++) {
			int kx = x - kt.table()[i][0];
			int ky = y - kt.table()[i][1];
			int ks = kicked[i] = toXYShape(kx, ky, shape);
			if(xFromInt(ks) != kx || yFromInt(ks) != y)
				kicked[i] = -1;
		}
		return kicked;
	}
	
	public static int[] kickedRight(int xyshape) {
		Shape shape;
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		KickTable kt = (shape = shapeFromInt(xyshape)).rightKick();
		int[] kicked = new int[kt.table().length];
		for(int i = 0; i < kicked.length; i++) {
			int kx = x - kt.table()[i][0];
			int ky = y - kt.table()[i][1];
			int ks = kicked[i] = toXYShape(kx, ky, shape);
			if(xFromInt(ks) != kx || yFromInt(ks) != y)
				kicked[i] = -1;
		}
		return kicked;
	}
	
	public static int[] inverseKickedLeft(int xyshape) {
		Shape shape;
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		KickTable kt = (shape = shapeFromInt(xyshape)).leftKick();
		int[] kicked = new int[kt.table().length];
		for(int i = 0; i < kicked.length; i++) {
			int kx = x + kt.table()[i][0];
			int ky = y + kt.table()[i][1];
			int ks = kicked[i] = toXYShape(kx, ky, shape);
			if(xFromInt(ks) != kx || yFromInt(ks) != y)
				kicked[i] = -1;
		}
		return kicked;
	}
	
	public static int[] inverseKickedRight(int xyshape) {
		Shape shape;
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		KickTable kt = (shape = shapeFromInt(xyshape)).rightKick();
		int[] kicked = new int[kt.table().length];
		for(int i = 0; i < kicked.length; i++) {
			int kx = x + kt.table()[i][0];
			int ky = y + kt.table()[i][1];
			int ks = kicked[i] = toXYShape(kx, ky, shape);
			if(xFromInt(ks) != kx || yFromInt(ks) != y)
				kicked[i] = -1;
		}
		return kicked;
	}
	
	private static final int UP = 1 << ShapeDirection.UP_ORD;
	private static final int RIGHT = 1 << ShapeDirection.RIGHT_ORD;
	private static final int DOWN = 1 << ShapeDirection.DOWN_ORD;
	private static final int LEFT = 1 << ShapeDirection.LEFT_ORD;
	
	private static final int IS_SYNONYM = (
			(DOWN | RIGHT) << (ShapeType.S_ORD << 2) |
			(DOWN | RIGHT) << (ShapeType.Z_ORD << 2) |
			(DOWN | RIGHT) << (ShapeType.I_ORD << 2) |
			(DOWN | RIGHT | LEFT) << (ShapeType.O_ORD << 2)
			);
	
	public static boolean isSynonym(int xyshape) {
		int id = shapeIdFromInt(xyshape);
		int synbit = 1 << id;
		return (IS_SYNONYM & synbit) != 0;
	}
	
	public static int canonical(int xyshape) {
		int id = shapeIdFromInt(xyshape);

		int synbit = 1 << id;
		if((IS_SYNONYM & synbit) == 0)
			return xyshape;
		
		int x = xFromInt(xyshape);
		int y = yFromInt(xyshape);
		
		int dir = id & 0x3;
		int type = id >>> 2;
		if(type == ShapeType.O_ORD)
			return toXYShape(x, type, (id & ~3) | ShapeDirection.UP_ORD);
		else if(dir == ShapeDirection.RIGHT_ORD)
			return toXYShape(x+1, y, (id & ~3) | ShapeDirection.LEFT_ORD);
		else
			return toXYShape(x, y+1, (id & ~3) | ShapeDirection.UP_ORD);
	}
	
	public static String toString(int xyshape) {
		return shapeFromInt(xyshape) + "[" + xFromInt(xyshape) + "," + yFromInt(xyshape) + "]";
	}
}
