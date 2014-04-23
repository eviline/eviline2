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
import org.eviline.core.ai.DefaultFitness;
import org.eviline.core.ai.Fitness;

public class StatisticsTable extends JTable implements EngineListener {
	
	protected Fitness fit = new DefaultFitness();
	
	public StatisticsTable(Engine engine, int blockSize) {
		super(new StatisticsTableModel());
		
		engine.addEngineListener(this);
		
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setRowHeight(blockSize);
		for(TableColumn c : Collections.list(getColumnModel().getColumns()))
			c.setPreferredWidth(blockSize);
		setDefaultRenderer(Object.class, new StatisticsTableCellRenderer());
		
		setOpaque(false);
		
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
		m.write("lines\n");
		m.write(e.getLines() + "\n", Color.CYAN);
		m.write("score\n");
		m.write(e.getScore() + "\n", Color.CYAN);
		
		m.write("badness\n");
		if(e.getGhost() != null) {
			Field after = e.getField().clone();
			after.blit(e.getGhost());
			m.write(((int) fit.badness(e.getField(), after, e.getNext())) + "\n", Color.CYAN);
		} else
			m.write("N/A\n");
		
		m.fireTableDataChanged();
		
	}
}
