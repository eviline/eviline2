package org.eviline.swing;

import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

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
		setRowHeight(10);
		for(TableColumn c : Collections.list(getColumnModel().getColumns()))
			c.setPreferredWidth(10);
		setDefaultRenderer(Object.class, new EngineTableCellRenderer());
	}
	
	@Override
	public EngineTableModel getModel() {
		return (EngineTableModel) super.getModel();
	}
}
