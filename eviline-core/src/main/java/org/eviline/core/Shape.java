package org.eviline.core;

import static org.eviline.core.ShapeType.*;

public enum Shape {
	S_UP(S,
			0b0110,
			0b1100
			), 
	S_RIGHT(S,
			0b0100,
			0b0110,
			0b0010
			), 
	S_DOWN(S,
			0b0000,
			0b0110,
			0b1100
			), 
	S_LEFT(S,
			0b1000,
			0b1100,
			0b0100
			),
	Z_UP(Z,
			0b1100,
			0b0110
			), 
	Z_RIGHT(Z,
			0b0010,
			0b0110,
			0b0100
			), 
	Z_DOWN(Z,
			0b0000,
			0b1100,
			0b0110
			), 
	Z_LEFT(Z,
			0b0100,
			0b1100,
			0b1000
			),
	J_UP(J,
			0b0010,
			0b0010,
			0b0110
			), 
	J_RIGHT(J,
			0b0000,
			0b1000,
			0b1110
			), 
	J_DOWN(J,
			0b1100,
			0b1000,
			0b1000
			), 
	J_LEFT(J,
			0b1110,
			0b0010
			),
	L_UP(L,
			0b1000,
			0b1000,
			0b1100
			), 
	L_RIGHT(L,
			0b1110,
			0b1000
			), 
	L_DOWN(L,
			0b0110,
			0b0010,
			0b0010
			), 
	L_LEFT(L,
			0b0000,
			0b0010,
			0b1110
			),
	T_UP(T,
			0b0100,
			0b1110
			),
	T_RIGHT(T,
			0b0100,
			0b0110,
			0b0100
			), 
	T_DOWN(T,
			0b0000,
			0b1110,
			0b0100
			), 
	T_LEFT(T,
			0b0100,
			0b1100,
			0b0100
			),
	I_UP(I,
			0b0000,
			0b1111
			), 
	I_RIGHT(I,
			0b0010,
			0b0010,
			0b0010,
			0b0010
			), 
	I_DOWN(I,
			0b0000,
			0b0000,
			0b1111
			), 
	I_LEFT(I,
			0b0100,
			0b0100,
			0b0100,
			0b0100
			),
	O_UP(O,
			0b1100,
			0b1100
			), 
	O_RIGHT(O,
			0b1100,
			0b1100
			), 
	O_DOWN(O,
			0b1100,
			0b1100
			), 
	O_LEFT(O,
			0b1100,
			0b1100
			),
	;
	
	private ShapeType type;
	private long mask;
	
	private Shape(ShapeType type, long... rowMasks) {
		this.type = type;
		for(int i = 0; i < rowMasks.length; i++)
			mask |= (rowMasks[i] << (12 + i * 16));
	}
	
	public ShapeType type() {
		return type;
	}
	
	public long mask() {
		return mask;
	}
	
	public boolean has(int x, int y) {
		return (mask & (1L << (15 - x + y * 16))) != 0;
	}
	
	public long mask(int x) {
		return mask >> (Field.BUFFER + x);
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
	
	public KickTable rightKick() {
		switch(this) {
		case S_UP: 
		case Z_UP:
		case J_UP:
		case L_UP:
		case T_UP:
		case I_UP:
		case O_UP:
			return KickTable.UP_RIGHT;
		case S_RIGHT:
		case Z_RIGHT:
		case J_RIGHT:
		case L_RIGHT:
		case T_RIGHT:
		case I_RIGHT:
		case O_RIGHT:
			return KickTable.RIGHT_DOWN;
		case S_DOWN:
		case Z_DOWN:
		case J_DOWN:
		case L_DOWN:
		case T_DOWN:
		case I_DOWN:
		case O_DOWN:
			return KickTable.DOWN_LEFT;
		case S_LEFT:
		case Z_LEFT:
		case J_LEFT:
		case L_LEFT:
		case T_LEFT:
		case I_LEFT:
		case O_LEFT:
			return KickTable.LEFT_UP;
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
		case I_UP:
		case O_UP:
			return KickTable.UP_LEFT;
		case S_RIGHT:
		case Z_RIGHT:
		case J_RIGHT:
		case L_RIGHT:
		case T_RIGHT:
		case I_RIGHT:
		case O_RIGHT:
			return KickTable.RIGHT_UP;
		case S_DOWN:
		case Z_DOWN:
		case J_DOWN:
		case L_DOWN:
		case T_DOWN:
		case I_DOWN:
		case O_DOWN:
			return KickTable.DOWN_RIGHT;
		case S_LEFT:
		case Z_LEFT:
		case J_LEFT:
		case L_LEFT:
		case T_LEFT:
		case I_LEFT:
		case O_LEFT:
			return KickTable.LEFT_DOWN;
		}
		throw new InternalError("impossible switch fallthrough");
	}
}
