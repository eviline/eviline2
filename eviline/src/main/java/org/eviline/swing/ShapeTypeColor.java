package org.eviline.swing;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import org.eviline.core.ShapeType;

public class ShapeTypeColor {
	protected Map<ShapeType, Color> colors = new EnumMap<>(ShapeType.class);
	
	public ShapeTypeColor() {
		colors.put(ShapeType.I, new Color(0, 159, 218));
		colors.put(ShapeType.J, new Color(0, 101, 189));
		colors.put(ShapeType.L, new Color(255, 121, 0));
		colors.put(ShapeType.O, new Color(254, 203, 0));
		colors.put(ShapeType.S, new Color(105, 190, 40));
		colors.put(ShapeType.T, new Color(149, 45, 152));
		colors.put(ShapeType.Z, new Color(237, 41, 57));
	}
	
	public Color get(ShapeType t) {
		return colors.get(t);
	}
}
