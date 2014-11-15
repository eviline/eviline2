package org.eviline.core;

import static org.eviline.core.Shape.I_DOWN;
import static org.eviline.core.Shape.I_LEFT;
import static org.eviline.core.Shape.I_RIGHT;
import static org.eviline.core.Shape.I_UP;
import static org.eviline.core.Shape.J_DOWN;
import static org.eviline.core.Shape.J_LEFT;
import static org.eviline.core.Shape.J_RIGHT;
import static org.eviline.core.Shape.J_UP;
import static org.eviline.core.Shape.L_DOWN;
import static org.eviline.core.Shape.L_LEFT;
import static org.eviline.core.Shape.L_RIGHT;
import static org.eviline.core.Shape.L_UP;
import static org.eviline.core.Shape.O_DOWN;
import static org.eviline.core.Shape.O_LEFT;
import static org.eviline.core.Shape.O_RIGHT;
import static org.eviline.core.Shape.O_UP;
import static org.eviline.core.Shape.S_DOWN;
import static org.eviline.core.Shape.S_LEFT;
import static org.eviline.core.Shape.S_RIGHT;
import static org.eviline.core.Shape.S_UP;
import static org.eviline.core.Shape.T_DOWN;
import static org.eviline.core.Shape.T_LEFT;
import static org.eviline.core.Shape.T_RIGHT;
import static org.eviline.core.Shape.T_UP;
import static org.eviline.core.Shape.Z_DOWN;
import static org.eviline.core.Shape.Z_LEFT;
import static org.eviline.core.Shape.Z_RIGHT;
import static org.eviline.core.Shape.Z_UP;

import java.util.EnumSet;
import java.util.Set;

public enum ShapeType {
	// basic types
	S, Z, J, L, T, I, O,
	// meta types
	G, // garbage
	;
	
	public static final int S_ORD = 0;
	public static final int Z_ORD = 1;
	public static final int J_ORD = 2;
	public static final int L_ORD = 3;
	public static final int T_ORD = 4;
	public static final int I_ORD = 5;
	public static final int O_ORD = 6;
	
	public Shape start() {
		switch(this) {
		case S: return S_UP;
		case Z: return Z_UP;
		case J: return J_UP;
		case L: return L_UP;
		case T: return T_UP;
		case I: return I_UP;
		case O: return O_UP;
		case G: return null;
		}
		throw new InternalError("impossible switch fallthrough");
	}
	
	public static ShapeType[] blocks() {
		return new ShapeType[] {S, Z, J, L, T, I, O};
	}
	
	public int startX() {
		if(this == O)
			return 4;
		return 3;
	}
	
	public int startY() {
		return -2;
	}
	
	public static ShapeType[] NONE = new ShapeType[0];
	
	public static Set<ShapeType> types(ShapeType[] types) {
		Set<ShapeType> ret = EnumSet.noneOf(ShapeType.class);
		for(ShapeType type : types)
			ret.add(type);
		return ret;
	}
	
	public Shape up() {
		switch(this) {
		case S: return S_UP;
		case Z: return Z_UP;
		case J: return J_UP;
		case L: return L_UP;
		case T: return T_UP;
		case I: return I_UP;
		case O: return O_UP;
		default:
			throw new InternalError("not a block shape type:" + this);
		}
	}
	
	public Shape down() {
		switch(this) {
		case S: return S_DOWN;
		case Z: return Z_DOWN;
		case J: return J_DOWN;
		case L: return L_DOWN;
		case T: return T_DOWN;
		case I: return I_DOWN;
		case O: return O_DOWN;
		default:
			throw new InternalError("not a block shape type:" + this);
		}
		
	}
	
	public Shape left() {
		switch(this) {
		case S: return S_LEFT;
		case Z: return Z_LEFT;
		case J: return J_LEFT;
		case L: return L_LEFT;
		case T: return T_LEFT;
		case I: return I_LEFT;
		case O: return O_LEFT;
		default:
			throw new InternalError("not a block shape type:" + this);
		}
		
	}
	
	public Shape right() {
		switch(this) {
		case S: return S_RIGHT;
		case Z: return Z_RIGHT;
		case J: return J_RIGHT;
		case L: return L_RIGHT;
		case T: return T_RIGHT;
		case I: return I_RIGHT;
		case O: return O_RIGHT;
		default:
			throw new InternalError("not a block shape type:" + this);
		}
		
	}
}
