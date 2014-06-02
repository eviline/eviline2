package org.eviline.swing;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eviline.core.Engine;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public class ShapeSourceComponent extends JPanel {
	protected Engine engine;
	protected ShapeTypeIcon icons;
	
	public ShapeSourceComponent(Engine engine, int blocksize) {
		super(new GridLayout(0, 1));
		this.engine = engine;
		icons = new ShapeTypeIcon(blocksize);
		setOpaque(false);
	}
	
	public void update() {
		removeAll();
		List<ShapeType> bag = Arrays.asList(engine.getShapes().getBag());
		Collections.sort(bag);
		for(ShapeType type : bag) {
			JLabel l = new JLabel(icons.get(type));
			l.setOpaque(false);
			add(l);
		}
		revalidate();
	}
}
