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
	
	private Shape(ShapeType type, int... rowMasks) {
		for(int i = 0; i < rowMasks.length; i++)
			mask |= (rowMasks[i] << (12 + i * 16));
	}
	
	public ShapeType type() {
		return type;
	}
	
	public long mask() {
		return mask;
	}
	
	public long mask(int x) {
		return mask >> (Field.BUFFER + x);
	}
}
