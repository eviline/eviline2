package org.eviline.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.eviline.core.Block;
import org.eviline.core.Field;

public class EngineTableCellRenderer extends DefaultTableCellRenderer {
	protected ShapeTypeColor color = new ShapeTypeColor();
	
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

		if(value == null)
			c.setBackground(Color.BLACK);
		else
			c.setBackground(color.get(((Block) value).shape().type()));
			
		boolean ghost = (value != null) && ((((Block) value).getFlags() & Block.MASK_GHOST) != 0);
		
		if(row < Field.BUFFER || ghost) {
			Color cl = c.getBackground();
			if(ghost)
				cl = Color.BLACK;
			cl = new Color(Math.min(255, cl.getRed() + 128), Math.min(255, cl.getGreen() + 128), Math.min(255, cl.getBlue() + 128));
			c.setBackground(cl);
			if(ghost)
				c.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		}
		
		return c;
	}
}
