package org.eviline.core;

public enum Command {
	NOP,
	ROTATE_RIGHT,
	ROTATE_LEFT,
	AUTOSHIFT_LEFT,
	AUTOSHIFT_RIGHT,
	SHIFT_LEFT,
	SHIFT_RIGHT,
	SHIFT_DOWN,
	SOFT_DROP,
	HARD_DROP,
	
	HOLD,
	
	
	;
	
	private static final Command[] VALUES = values();
	
	public static Command fromOrdinal(int ordinal) {
		if(ordinal < 0)
			return null;
		return VALUES[ordinal];
	}
}
