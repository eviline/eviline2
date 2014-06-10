package org.eviline.lanterna;

import java.util.EnumMap;
import java.util.Map;

import org.eviline.core.ShapeType;

public class ShapeTypeTextIcon {
	private Map<ShapeType, String> icons = new EnumMap<ShapeType, String>(ShapeType.class);
	
	public ShapeTypeTextIcon() {
		icons.put(ShapeType.I, "\u2584\u2584\u2584\u2584");
		icons.put(ShapeType.J, "\u2588\u2584\u2584");
		icons.put(ShapeType.L, "\u2584\u2584\u2588");
		icons.put(ShapeType.S, "\u2584\u2588\u2580");
		icons.put(ShapeType.Z, "\u2580\u2588\u2584");
		icons.put(ShapeType.T, "\u2584\u2588\u2584");
		icons.put(ShapeType.O, "\u2588\u2588");
	}
	
	public String get(ShapeType t) {
		return icons.get(t);
	}
}
