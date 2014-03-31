package org.eviline.swing;

import javax.swing.table.AbstractTableModel;

import org.eviline.core.Field;

public class FieldTableModel extends AbstractTableModel {
	
	protected Field field;
	
	public FieldTableModel(Field field) {
		this.field = field;
	}

	@Override
	public int getRowCount() {
		return Field.HEIGHT;
	}

	@Override
	public int getColumnCount() {
		return Field.WIDTH;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return field.block(columnIndex, rowIndex);
	}

}
