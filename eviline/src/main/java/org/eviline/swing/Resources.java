package org.eviline.swing;

import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Resources {
	
	private static Font minecrafter;
	public static Font getMinecrafter() {
		if(minecrafter == null) {
			try {
				minecrafter = Font.createFont(
						Font.TRUETYPE_FONT, 
						Resources.class.getResourceAsStream("Minecrafter_3.ttf"));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return minecrafter;
	}
	
	private static Image spider;
	public static Image getSpider() {
		if(spider == null) {
			try {
				spider = ImageIO.read(Resources.class.getResource("spider.jpg"));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return spider;
	}

	private static BufferedImage block;
	public static BufferedImage getBlock() {
		if(block == null) {
			try {
				block = ImageIO.read(Resources.class.getResource("block.png"));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return block;
	}

	private Resources() {}
}
