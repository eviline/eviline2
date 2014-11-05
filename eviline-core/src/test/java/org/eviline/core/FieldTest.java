package org.eviline.core;

import org.junit.Assert;
import org.junit.Test;

public class FieldTest {
	
	@Test
	public void testImask() {
		Field f = new Field();
		Assert.assertEquals(0b1110000000000111111000000000011111100000000001111110000000000111L, f.imask(0));
	}
	
	@Test
	public void testIntersects() {
		Field f = new Field();
		Assert.assertFalse(f.intersects(XYShapes.toXYShape(0, 0, Shape.O_UP)));
		Assert.assertTrue(f.intersects(XYShapes.toXYShape(-1, 0, Shape.O_UP)));
	}
	
	@Test
	public void testMasked() {
		Field f = new Field();
		f.blit(XYShapes.toXYShape(0, 0, Shape.O_DOWN), 0);
		System.out.println(f.toString());
		Assert.assertTrue(f.masked(0, 0));
		Assert.assertFalse(f.masked(2, 0));
	}
}
