package org.eviline.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.eviline.core.Block;

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
		
		if(value != null)
			c.setText(((Block) value).shape().type().name());
		else
			c.setText(" ");
		c.setBorder(null);

		if(value == null)
			c.setBackground(Color.WHITE);
		else
			c.setBackground(color.get(((Block) value).shape().type()));
			
		
		return c;
	}
}
