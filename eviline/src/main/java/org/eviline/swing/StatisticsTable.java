package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.eviline.core.Command;
import org.eviline.core.Engine;
import org.eviline.core.EngineListener;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;

public class StatisticsTable extends JTable implements EngineListener {
	
	public StatisticsTable(Engine engine, int blockSize) {
		super(new StatisticsTableModel());
		
		engine.addEngineListener(this);
		
		setBackground(Color.BLACK);
		
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setRowHeight(blockSize);
		for(TableColumn c : Collections.list(getColumnModel().getColumns()))
			c.setPreferredWidth(blockSize);
		setDefaultRenderer(Object.class, new StatisticsTableCellRenderer());
		
		ticked(engine, Command.NOP);
	}
	
	@Override
	public StatisticsTableModel getModel() {
		return (StatisticsTableModel) super.getModel();
	}

	@Override
	public void ticked(Engine e, Command c) {
		StatisticsTableModel m = getModel();
		m.clear();
		m.write(0, 0, "lines");
		m.write(0, 1, "" + e.getLines());
		m.fireTableDataChanged();
	}
}
