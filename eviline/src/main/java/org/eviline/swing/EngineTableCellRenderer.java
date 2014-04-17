package org.eviline.swing;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.eviline.core.Block;
import org.eviline.core.Field;

public class EngineTableCellRenderer extends DefaultTableCellRenderer {
	private static Color TXP = new Color(0, 0, 0, 0);
	
	protected ShapeTypeColor color = new ShapeTypeColor();
	
	protected BlockImage blockImages;
	
	public EngineTableCellRenderer(int size) {
		try {
			blockImages = new BlockImage(ImageIO.read(EngineTableCellRenderer.class.getResource("block.png")), size, size);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Component getTableCellRendererComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) {
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		c.setText(null);
		c.setBorder(null);

		c.setBackground(TXP);
		c.setIcon(null);
		if(value != null)
			c.setIcon(blockImages.icon(((Block) value).shape().type()));
			
		boolean ghost = (value != null) && ((((Block) value).getFlags() & Block.MASK_GHOST) != 0);
		
		if(row < Field.BUFFER) {
			c.setBackground(new Color(255, 255, 255, 128));
		}
		
		if(ghost) {
			c.setBackground(Color.WHITE);
			c.setIcon(null);
		}
		
		return c;
	}
}
