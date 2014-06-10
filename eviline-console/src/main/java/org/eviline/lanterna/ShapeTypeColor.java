package org.eviline.lanterna;

import java.util.EnumMap;
import java.util.Map;

import org.eviline.core.ShapeType;

import com.googlecode.lanterna.terminal.Terminal.Color;

public class ShapeTypeColor {
	protected Map<ShapeType, Color> fg = new EnumMap<>(ShapeType.class);
	protected Map<ShapeType, Color> bg = new EnumMap<>(ShapeType.class);
	
	public ShapeTypeColor() {
		fg.put(ShapeType.I, Color.WHITE);
		fg.put(ShapeType.J, Color.WHITE);
		fg.put(ShapeType.L, Color.WHITE);
		fg.put(ShapeType.O, Color.WHITE);
		fg.put(ShapeType.S, Color.WHITE);
		fg.put(ShapeType.T, Color.WHITE);
		fg.put(ShapeType.Z, Color.WHITE);
		fg.put(ShapeType.G, Color.WHITE);
		
		bg.put(ShapeType.I, Color.WHITE);
		bg.put(ShapeType.J, Color.WHITE);
		bg.put(ShapeType.L, Color.WHITE);
		bg.put(ShapeType.O, Color.WHITE);
		bg.put(ShapeType.S, Color.WHITE);
		bg.put(ShapeType.T, Color.WHITE);
		bg.put(ShapeType.Z, Color.WHITE);
		bg.put(ShapeType.G, Color.WHITE);
	}
	
	public Color fg(ShapeType t) {
		return fg.get(t);
	}

	public Color bg(ShapeType t) {
		return bg.get(t);
	}
}
