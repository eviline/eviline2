package org.eviline.core;

import org.junit.Assert;
import org.junit.Test;

public class FieldTest {
	
	@Test
	public void testImask() {
		Field f = new Field();
		long i0 = f.imask(0);
		Assert.assertEquals(0b1110000000000111111000000000011111100000000001111110000000000111L, f.imask(0));
	}
	
	@Test
	public void testIntersects() {
		Field f = new Field();
		Assert.assertFalse(f.intersects(new XYShape(Shape.O_UP, 0, 0)));
		Assert.assertTrue(f.intersects(new XYShape(Shape.O_UP, -1, 0)));
	}
}
