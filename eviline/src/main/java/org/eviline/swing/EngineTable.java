package org.eviline.swing;

import java.awt.Dimension;

import javax.swing.JTable;

import org.eviline.core.Engine;
import org.eviline.core.Field;

public class EngineTable extends JTable {
	protected Field field;
	
	public EngineTable(Engine engine) {
		super(new EngineTableModel(engine));
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setDefaultRenderer(Object.class, new EngineTableCellRenderer());
	}
}
