package org.eviline.swing;

import java.awt.Font;
import java.io.IOException;

public abstract class Fonts {
	
	private static Font minecrafter;
	public static Font getMinecrafter() {
		if(minecrafter == null) {
			try {
				minecrafter = Font.createFont(
						Font.TRUETYPE_FONT, 
						Fonts.class.getResourceAsStream("Minecrafter_3.ttf"));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		return minecrafter;
	}
	
	private Fonts() {}
}
