package org.eviline.swing;

import java.awt.Dimension;

import javax.swing.JTable;

import org.eviline.core.Field;

public class FieldTable extends JTable {
	protected Field field;
	
	public FieldTable(Field field) {
		super(new FieldTableModel(field));
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setDefaultRenderer(Object.class, new FieldTableCellRenderer());
	}
}
