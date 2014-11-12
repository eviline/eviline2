package org.eviline.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eviline.core.ShapeType;

public class BlockImage {
	protected BufferedImage base;
	
	protected Map<ShapeType, BufferedImage> blocks = new EnumMap<>(ShapeType.class);
	protected Map<ShapeType, ImageIcon> icons = new EnumMap<>(ShapeType.class);
	
	public BlockImage(BufferedImage base, int width, int height) {
		this.base = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = this.base.getGraphics();
		g.setColor(new Color(0,0,0,0));
		g.fillRect(0, 0, width, height);
		g.drawImage(base.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
		
		for(ShapeType type : ShapeType.values()) {
			BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Color max = new ShapeTypeColor().get(type);
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					Color pt = new Color(this.base.getRGB(x, y), true);
					pt = new Color(
							(pt.getRed() * max.getRed()) / 256,
							(pt.getGreen() * max.getGreen()) / 256,
							(pt.getBlue() * max.getBlue()) / 256,
							(pt.getAlpha() * max.getAlpha()) / 256);
					buf.setRGB(x, y, pt.getRGB());
				}
			}
			blocks.put(type, buf);
			icons.put(type, new ImageIcon(buf));
		}
	}
	
	public Image get(ShapeType type) {
		return blocks.get(type);
	}
	
	public Icon icon(ShapeType type) {
		return icons.get(type);
	}
}
