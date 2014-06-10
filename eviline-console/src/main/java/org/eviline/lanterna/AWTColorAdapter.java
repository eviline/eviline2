package org.eviline.lanterna;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.swing.TerminalPalette;

public class AWTColorAdapter {
	public static class ColorAdaption {
		private char c;
		private Color fg;
		private Color bg;
		private java.awt.Color awt;
		
		public ColorAdaption(char c, Color fg, Color bg, java.awt.Color awt) {
			this.c = c;
			this.fg = fg;
			this.bg = bg;
			this.awt = awt;
		}

		public char getC() {
			return c;
		}

		public Color getFg() {
			return fg;
		}

		public Color getBg() {
			return bg;
		}

		public java.awt.Color getAwt() {
			return awt;
		}
	}
	
	protected static Collection<ColorAdaption> colors = new ArrayList<ColorAdaption>();
	
	static {
		TerminalPalette p = TerminalPalette.XTERM;
		BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) i.getGraphics();
		char[] cs = new char[] {' ', '\u2591', '\u2592', '\u2593', '\u2588'};
		for(Color fg : Color.values()) {
			for(Color bg : Color.values()) {
				for(int ci = 0; ci < cs.length; ci++) {
					char c = cs[ci];
					double fgAlpha = ci / ((double) cs.length);
					g.setColor(getAWTBackground(bg, p));
					g.fillRect(0, 0, 1, 1);
					java.awt.Color afg = getAWTForeground(fg, p);
					g.setColor(new java.awt.Color(afg.getRed(), afg.getGreen(), afg.getBlue(), (int)(255 * fgAlpha)));
					g.fillRect(0, 0, 1, 1);
					
					colors.add(new ColorAdaption(c, fg, bg, new java.awt.Color(i.getRGB(0, 0))));
				}
			}
		}
	}
	
	public static ColorAdaption get(java.awt.Color c) {
		float[] chsb = java.awt.Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		double dist = Double.POSITIVE_INFINITY;
		ColorAdaption entry = null;
		for(ColorAdaption e : colors) {
			float[] ehsb = java.awt.Color.RGBtoHSB(e.getAwt().getRed(), e.getAwt().getGreen(), e.getAwt().getBlue(), null);
			double edist = Math.sqrt(
					Math.pow(100 * Math.abs(chsb[0] - ehsb[0]), 2) +
					Math.pow(100 * Math.abs(chsb[1] - ehsb[1]), 2) +
					Math.pow(100 * Math.abs(chsb[2] - ehsb[2]), 2));
			if(edist < dist) {
				entry = e;
				dist = edist;
			}
		}
		return entry;
	}
	
	public static java.awt.Color getAWTForeground(Color c, TerminalPalette p) {
		switch(c) {
		case BLACK: return p.getBrightBlack();
		case BLUE: return p.getBrightBlue();
		case CYAN: return p.getBrightCyan();
		case DEFAULT: return p.getDefaultBrightColor();
		case GREEN: return p.getBrightGreen();
		case MAGENTA: return p.getBrightMagenta();
		case RED: return p.getBrightRed();
		case WHITE: return p.getBrightWhite();
		case YELLOW: return p.getBrightYellow();
		}
		throw new IllegalArgumentException();
	}

	public static java.awt.Color getAWTBackground(Color c, TerminalPalette p) {
		switch(c) {
		case BLACK: return p.getNormalBlack();
		case BLUE: return p.getNormalBlue();
		case CYAN: return p.getNormalCyan();
		case DEFAULT: return p.getDefaultColor();
		case GREEN: return p.getNormalGreen();
		case MAGENTA: return p.getNormalMagenta();
		case RED: return p.getNormalRed();
		case WHITE: return p.getNormalWhite();
		case YELLOW: return p.getNormalYellow();
		}
		throw new IllegalArgumentException();
	}

	
}
