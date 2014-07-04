package org.eviline.core;

import org.junit.Test;

public class ShapeTest {
	@Test
	public void testMask() {
		System.out.println(Long.toBinaryString(Shape.O_DOWN.mask()));
		for(short s : Shorts.split(Shape.O_UP.mask()))
			System.out.println(Long.toBinaryString(s & 0xffff));
	}
}
