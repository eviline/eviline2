package org.eviline.swing;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.eviline.core.Field;

public class StatisticsTableModel extends AbstractTableModel {
	private static final int DIM = Field.HEIGHT + Field.BUFFER;

	public static class ColoredCharacter {
		public final Character c;
		public final Color color;
		
		public ColoredCharacter(Character c, Color color) {
			this.c = c;
			this.color = color;
		}
	}
	
	protected Character[][] chars = new Character[DIM][DIM];
	protected Color[][] colors = new Color[DIM][DIM];
	protected int x, y;
	protected Color color;
	
	public void write(String s) {
		write(s, color);
	}
	
	public void write(String s, Color color) {
		for(char c : s.toCharArray()) {
			if(c != '\n')
				write(x++, y, String.valueOf(c), color);
			else {
				x = 0;
				y++;
			}
		}
	}
	
	public void write(int x, int y, String s) {
		write(x, y, s, color);
	}
	
	public void write(int x, int y, String s, Color c) {
		for(int i = 0; i < s.length(); i++) {
			chars[y][x+i] = s.charAt(i);
			colors[y][x+i] = c;
		}
	}
	
	public void clear() {
		for(Character[] c : chars)
			Arrays.fill(c, null);
		for(Color[] c : colors)
			Arrays.fill(c, null);
		x = y = 0;
		color = null;
	}
	
	@Override
	public int getRowCount() {
		return DIM;
	}

	@Override
	public int getColumnCount() {
		return DIM;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return new ColoredCharacter(chars[rowIndex][columnIndex], colors[rowIndex][columnIndex]);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
