package org.eviline.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eviline.core.Shape;
import org.eviline.core.ShapeType;

public class ShapeTypeIcon {
	protected Map<ShapeType, Image> images;
	protected Map<ShapeType, Icon> icons;
	
	public ShapeTypeIcon(int blocksize) {
		images = new EnumMap<>(ShapeType.class);
		icons = new EnumMap<>(ShapeType.class);
		
		BlockImage blocks = new BlockImage(Resources.getBlock(), blocksize, blocksize);
		for(ShapeType type : ShapeType.values()) {
			Shape start = type.start();
			if(start == null)
				continue;
			BufferedImage img = new BufferedImage(blocksize*4, blocksize*4, BufferedImage.TYPE_INT_ARGB_PRE);
//			for(int y = 0; y < img.getHeight(); y++)
//				for(int x = 0; x < img.getWidth(); x++)
//					img.setRGB(x, y, 0);
			Graphics g = img.getGraphics();
			for(int y = 0; y < 4; y++) {
				for(int x = 0; x < 4; x++) {
					if(start.has(x, y))
						g.drawImage(blocks.get(type), x*blocksize, y*blocksize, null);
				}
			}
			images.put(type, img);
			icons.put(type, new ImageIcon(img));
		}
	}
	
	public Image getImage(ShapeType type) {
		return images.get(type);
	}
	
	public Icon get(ShapeType type) {
		return icons.get(type);
	}
}
