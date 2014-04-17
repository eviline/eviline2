package org.eviline.swing;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.eviline.core.Field;

public class StatisticsTableModel extends AbstractTableModel {
	private static final int DIM = Field.HEIGHT + Field.BUFFER;

	protected Character[][] chars = new Character[DIM][DIM];
	
	public void write(int x, int y, String s) {
		for(int i = 0; i < s.length(); i++) {
			chars[y][x+i] = s.charAt(i);
		}
	}
	
	public void clear() {
		for(Character[] c : chars)
			Arrays.fill(c, null);
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
		return chars[rowIndex][columnIndex];
	}

}
