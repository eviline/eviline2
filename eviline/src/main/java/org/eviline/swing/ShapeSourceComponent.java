package org.eviline.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eviline.core.Engine;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public class ShapeSourceComponent extends JComponent {
	protected int blocksize;
	protected Engine engine;
	protected ShapeTypeIcon icons;
	
	public ShapeSourceComponent(Engine engine, int blocksize) {
		this.engine = engine;
		this.blocksize = blocksize*4;
		icons = new ShapeTypeIcon(blocksize);
	}
	
	public void draw(Graphics g) {
		List<ShapeType> bag = Arrays.asList(engine.getShapes().getBag());
		bag = new ArrayList<>(bag);
		Collections.sort(bag);
		for(int i = 0; i < bag.size(); i++) {
			Image icon = icons.getImage(bag.get(i));
			g.drawImage(icon, 1, 1 + i * (blocksize + 1), null);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		draw(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		if(isPreferredSizeSet())
			return super.getPreferredSize();
		return getMinimumSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(blocksize + 2, 1 + engine.getShapes().getBag().length * (blocksize + 1));
	}
}
