package org.eviline.core;

import static org.eviline.core.Shape.*;

import java.util.EnumSet;
import java.util.Set;

public enum ShapeType {
	S, Z, J, L, T, I, O
	;
	
	public Shape start() {
		switch(this) {
		case S: return S_UP;
		case Z: return Z_UP;
		case J: return J_UP;
		case L: return L_UP;
		case T: return T_UP;
		case I: return I_UP;
		case O: return O_UP;
		}
		throw new InternalError("impossible switch fallthrough");
	}
	
	public int startX() {
		if(this == O)
			return 4;
		return 3;
	}
	
	public int startY() {
		return -2;
	}
	
	public static Set<ShapeType> types(ShapeType[] types) {
		Set<ShapeType> ret = EnumSet.noneOf(ShapeType.class);
		for(ShapeType type : types)
			ret.add(type);
		return ret;
	}
}
