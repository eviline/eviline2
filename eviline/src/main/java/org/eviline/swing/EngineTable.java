package org.eviline.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.eviline.core.Engine;
import org.eviline.core.ShapeType;

public class EngineTable extends JTable {
	protected Engine engine;
	protected BlockImage blockImage;
	protected int blockSize;
	
	public EngineTable(Engine engine, int blockSize) {
		super(new EngineTableModel(engine));
		
		this.engine = engine;
		this.blockSize = blockSize;
		try {
			this.blockImage = new BlockImage(
					ImageIO.read(EngineTable.class.getResource("block.png")),
					blockSize * 8,
					blockSize * 8);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		setBackground(new Color(0,0,0,0));
		
		setCellSelectionEnabled(false);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
		setRowHeight(blockSize);
		for(TableColumn c : Collections.list(getColumnModel().getColumns()))
			c.setPreferredWidth(blockSize);
		setDefaultRenderer(Object.class, new EngineTableCellRenderer(blockSize));
		
		setOpaque(false);
	}
	
	@Override
	public EngineTableModel getModel() {
		return (EngineTableModel) super.getModel();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
//		g.setColor(Color.BLACK);;
//		g.fillRect(0, 0, getWidth(), getHeight());
		if(!engine.isPaused()) {
			if(engine.getNext().length > 0) {
				ShapeType next = engine.getNext()[0];
				if(next != null) {
					g.drawImage(blockImage.get(next), blockSize, blockSize * 4, null);
				}
			}
			for(int y = 0; y < getHeight(); y++) {
				//			for(int x = 0; x < getWidth(); x++) {
				long s = engine.getTickCount() - y*8/blockSize;
				int w = (int)(15 * (1 + Math.sin(blockSize * s / 144.)));
				g.setColor(new Color(w, w, w, 164));
				g.fillRect(0, y, getWidth(), 1);
				//			}
			}
			super.paintComponent(g);
		} else {
			g.setColor(new Color(0,0,0,192));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.setFont(Resources.getMinecrafter().deriveFont(24f));
			FontMetrics fm = g.getFontMetrics();
			int pw = fm.stringWidth("PAUSED");
			int ph = fm.getHeight();
			
			g.drawString("PAUSED", (getWidth() - pw) / 2, (getHeight() + ph) / 2);
			
		}
	}
}
