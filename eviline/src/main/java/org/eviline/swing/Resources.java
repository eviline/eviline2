package org.eviline.swing;

import java.awt.Font;
import java.awt.Image;
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
	
	private static Image stork;
	public static Image getStork() {
		if(stork == null) {
			try {
				stork = ImageIO.read(Resources.class.getResource("stork.jpg"));
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return stork;
	}
	
	private Resources() {}
}
