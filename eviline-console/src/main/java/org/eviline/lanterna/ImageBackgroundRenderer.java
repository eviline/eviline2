package org.eviline.lanterna;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.eviline.lanterna.AWTColorAdapter.ColorAdaption;

import com.googlecode.lanterna.gui.GUIScreenBackgroundRenderer;
import com.googlecode.lanterna.gui.TextGraphics;

public class ImageBackgroundRenderer implements GUIScreenBackgroundRenderer {

	private BufferedImage source;
	private BufferedImage scaled;
	
	public ImageBackgroundRenderer(BufferedImage source) {
		this.source = source;
		this.scaled = source;
	}
	
	@Override
	public void drawBackground(TextGraphics g) {
		if(scaled.getWidth() != g.getWidth() || scaled.getHeight() != g.getHeight()) {
			scaled = new BufferedImage(g.getWidth(), g.getHeight(), BufferedImage.TYPE_INT_RGB);
			scaled.getGraphics().drawImage(source, 0, 0, g.getWidth(), g.getHeight(), null);
		}
		for(int y = 0; y < g.getHeight(); y++) {
			for(int x = 0; x < g.getWidth(); x++) {
				ColorAdaption ca = AWTColorAdapter.get(new Color(scaled.getRGB(x, y)));
				g.setBackgroundColor(ca.getBg());
				g.setForegroundColor(ca.getFg());
				g.drawString(x, y, Character.toString(ca.getC()));
			}
		}
	}

}
