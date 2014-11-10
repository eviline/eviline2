package org.eviline.core;

import static org.eviline.core.ShapeType.*;

public enum Shape {
	S_UP(S), 
	S_RIGHT(S), 
	S_DOWN(S), 
	S_LEFT(S),
	Z_UP(Z), 
	Z_RIGHT(Z), 
	Z_DOWN(Z), 
	Z_LEFT(Z),
	J_UP(J), 
	J_RIGHT(J), 
	J_DOWN(J),
	J_LEFT(J), 
	L_UP(L),
	L_RIGHT(L), 
	L_DOWN(L), 
	L_LEFT(L), 
	T_UP(T),
	T_RIGHT(T), 
	T_DOWN(T), 
	T_LEFT(T),
	I_UP(I), 
	I_RIGHT(I), 
	I_DOWN(I), 
	I_LEFT(I),
	O_UP(O), 
	O_RIGHT(O), 
	O_DOWN(O), 
	O_LEFT(O),
	;
	
	private ShapeType type;
	private int overY;
	private ShapeDirection direction;
	
	private Shape(ShapeType type) {
		this.type = type;
		direction = ShapeDirection.valueOf(name().replaceAll(".*_", ""));
	}
	
	private static final Shape[] VALUES = values();
	
	public static Shape fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}
	
	public ShapeType type() {
		return type;
	}
	
	public long mask() {
		return shapeMask(ordinal());
	}
	
	public static long shapeMask(int ord, int x) {
		return shapeMask(ord) >>> (Field.BUFFER + x);
	}
	
	public static long shapeMask(int ord) {
		switch(ord) {
		case 0: 
			return 
					(0b0110L << 60) |
					(0b1100L << 44) ;
		case 1:
			return
					(0b0100L << 60) |
					(0b0110L << 44) |
					(0b0010L << 28) ;
		case 2:
			return
					(0b0000L << 60) |
					(0b0110L << 44) |
					(0b1100L << 28) ;
		case 3:
			return
					(0b1000L << 60) |
					(0b1100L << 44) |
					(0b0100L << 28) ;
		case 4:
			return
					(0b1100L << 60) |
					(0b0110L << 44) ;
		case 5:
			return
					(0b0010L << 60) |
					(0b0110L << 44) |
					(0b0100L << 28) ;
		case 6:
			return
					(0b0000L << 60) |
					(0b1100L << 44) |
					(0b0110L << 28) ;
		case 7:
			return
					(0b0100L << 60) |
					(0b1100L << 44) |
					(0b1000L << 28) ;
		case 8:
			return
					(0b1000L << 60) |
					(0b1110L << 44) ;
		case 9:
			return
					(0b0110L << 60) |
					(0b0100L << 44) |
					(0b0100L << 28) ;
		case 10:
			return
					(0b0000L << 60) |
					(0b1110L << 44) |
					(0b0010L << 28) ;
		case 11:
			return
					(0b0100L << 60) |
					(0b0100L << 44) |
					(0b1100L << 28) ;
		case 12:
			return
					(0b0010L << 60) |
					(0b1110L << 44) ;
		case 13:
			return
					(0b0100L << 60) |
					(0b0100L << 44) |
					(0b0110L << 28) ;
		case 14:
			return
					(0b0000L << 60) |
					(0b1110L << 44) |
					(0b1000L << 28) ;
		case 15:
			return
					(0b1100L << 60) |
					(0b0100L << 44) |
					(0b0100L << 28) ;
		case 16:
			return
					(0b0100L << 60) |
					(0b1110L << 44) ;
		case 17:
			return
					(0b0100L << 60) |
					(0b0110L << 44) |
					(0b0100L << 28) ;
		case 18:
			return
					(0b0000L << 60) |
					(0b1110L << 44) |
					(0b0100L << 28) ;
		case 19:
			return
					(0b0100L << 60) |
					(0b1100L << 44) |
					(0b0100L << 28) ;
		case 20:
			return
					(0b0000L << 60) |
					(0b1111L << 44) ;
		case 21:
			return
					(0b0010L << 60) |
					(0b0010L << 44) |
					(0b0010L << 28) |
					(0b0010L << 12) ;
		case 22:
			return
					(0b0000L << 60) |
					(0b0000L << 44) |
					(0b1111L << 28) ;
		case 23:
			return
					(0b0100L << 60) |
					(0b0100L << 44) |
					(0b0100L << 28) |
					(0b0100L << 12) ;
		case 24:
			return
					(0b1100L << 60) |
					(0b1100L << 44);
		case 25:
			return
					(0b1100L << 60) |
					(0b1100L << 44);
		case 26:
			return
					(0b1100L << 60) |
					(0b1100L << 44);
		case 27:
			return
					(0b1100L << 60) |
					(0b1100L << 44);
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public ShapeDirection direction() {
		return direction;
	}
	
	public boolean has(int x, int y) {
		return (mask() & (1L << (15 - x + (3-y) * 16))) != 0;
	}
	
	public long mask(int x) {
		return shapeMask(ordinal(), x);
	}
	
	public int overY() {
		return overY;
	}
	
	public Shape rotatedRight() {
		switch(this) {
		case S_UP: return S_RIGHT;
		case S_RIGHT: return S_DOWN;
		case S_DOWN: return S_LEFT;
		case S_LEFT: return S_UP;
		case Z_UP: return Z_RIGHT;
		case Z_RIGHT: return Z_DOWN;
		case Z_DOWN: return Z_LEFT;
		case Z_LEFT: return Z_UP;
		case J_UP: return J_RIGHT;
		case J_RIGHT: return J_DOWN;
		case J_DOWN: return J_LEFT;
		case J_LEFT: return J_UP;
		case L_UP: return L_RIGHT;
		case L_RIGHT: return L_DOWN;
		case L_DOWN: return L_LEFT;
		case L_LEFT: return L_UP;
		case T_UP: return T_RIGHT;
		case T_RIGHT: return T_DOWN;
		case T_DOWN: return T_LEFT;
		case T_LEFT: return T_UP;
		case I_UP: return I_RIGHT;
		case I_RIGHT: return I_DOWN;
		case I_DOWN: return I_LEFT;
		case I_LEFT: return I_UP;
		case O_UP: return O_RIGHT;
		case O_RIGHT: return O_DOWN;
		case O_DOWN: return O_LEFT;
		case O_LEFT: return O_UP;
		}
		throw new InternalError("impossible switch fallthrough");
	}
	
	public Shape rotatedLeft() {
		switch(this) {
		case S_UP: return S_LEFT;
		case S_LEFT: return S_DOWN;
		case S_DOWN: return S_RIGHT;
		case S_RIGHT: return S_UP;
		case Z_UP: return Z_LEFT;
		case Z_LEFT: return Z_DOWN;
		case Z_DOWN: return Z_RIGHT;
		case Z_RIGHT: return Z_UP;
		case J_UP: return J_LEFT;
		case J_LEFT: return J_DOWN;
		case J_DOWN: return J_RIGHT;
		case J_RIGHT: return J_UP;
		case L_UP: return L_LEFT;
		case L_LEFT: return L_DOWN;
		case L_DOWN: return L_RIGHT;
		case L_RIGHT: return L_UP;
		case T_UP: return T_LEFT;
		case T_LEFT: return T_DOWN;
		case T_DOWN: return T_RIGHT;
		case T_RIGHT: return T_UP;
		case I_UP: return I_LEFT;
		case I_LEFT: return I_DOWN;
		case I_DOWN: return I_RIGHT;
		case I_RIGHT: return I_UP;
		case O_UP: return O_LEFT;
		case O_LEFT: return O_DOWN;
		case O_DOWN: return O_RIGHT;
		case O_RIGHT: return O_UP;
		}
		throw new InternalError("impossible switch fallthrough");
	}
	
	public KickTable leftKick() {
		switch(this) {
		case S_UP: 
		case Z_UP:
		case J_UP:
		case L_UP:
		case T_UP:
		case O_UP:
			return KickTable.UP_RIGHT;
		case I_UP:
			return KickTable.IUP_RIGHT;
		case S_RIGHT:
		case Z_RIGHT:
		case J_RIGHT:
		case L_RIGHT:
		case T_RIGHT:
		case O_RIGHT:
			return KickTable.RIGHT_DOWN;
		case I_RIGHT:
			return KickTable.IRIGHT_DOWN;
		case S_DOWN:
		case Z_DOWN:
		case J_DOWN:
		case L_DOWN:
		case T_DOWN:
		case O_DOWN:
			return KickTable.DOWN_LEFT;
		case I_DOWN:
			return KickTable.IDOWN_LEFT;
		case S_LEFT:
		case Z_LEFT:
		case J_LEFT:
		case L_LEFT:
		case T_LEFT:
		case O_LEFT:
			return KickTable.LEFT_UP;
		case I_LEFT:
			return KickTable.ILEFT_UP;
		}
		throw new InternalError("impossible switch fallthrough");
	}
	
	public KickTable rightKick() {
		switch(this) {
		case S_UP: 
		case Z_UP:
		case J_UP:
		case L_UP:
		case T_UP:
		case O_UP:
			return KickTable.UP_LEFT;
		case I_UP:
			return KickTable.IUP_LEFT;
		case S_RIGHT:
		case Z_RIGHT:
		case J_RIGHT:
		case L_RIGHT:
		case T_RIGHT:
		case O_RIGHT:
			return KickTable.RIGHT_UP;
		case I_RIGHT:
			return KickTable.IRIGHT_UP;
		case S_DOWN:
		case Z_DOWN:
		case J_DOWN:
		case L_DOWN:
		case T_DOWN:
		case O_DOWN:
			return KickTable.DOWN_RIGHT;
		case I_DOWN:
			return KickTable.IDOWN_RIGHT;
		case S_LEFT:
		case Z_LEFT:
		case J_LEFT:
		case L_LEFT:
		case T_LEFT:
		case O_LEFT:
			return KickTable.LEFT_DOWN;
		case I_LEFT:
			return KickTable.ILEFT_DOWN;
		}
		throw new InternalError("impossible switch fallthrough");
	}
}
