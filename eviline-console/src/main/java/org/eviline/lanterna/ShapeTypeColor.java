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
		fg.put(ShapeType.J, Color.BLUE);
		fg.put(ShapeType.L, Color.RED);
		fg.put(ShapeType.O, Color.YELLOW);
		fg.put(ShapeType.S, Color.GREEN);
		fg.put(ShapeType.T, Color.MAGENTA);
		fg.put(ShapeType.Z, Color.RED);
		fg.put(ShapeType.G, Color.WHITE);
		
		bg.put(ShapeType.I, Color.BLUE);
		bg.put(ShapeType.J, Color.BLUE);
		bg.put(ShapeType.L, Color.YELLOW);
		bg.put(ShapeType.O, Color.YELLOW);
		bg.put(ShapeType.S, Color.GREEN);
		bg.put(ShapeType.T, Color.MAGENTA);
		bg.put(ShapeType.Z, Color.RED);
		bg.put(ShapeType.G, Color.WHITE);
	}
	
	public Color fg(ShapeType t) {
		return fg.get(t);
	}

	public Color bg(ShapeType t) {
		return bg.get(t);
	}
}
