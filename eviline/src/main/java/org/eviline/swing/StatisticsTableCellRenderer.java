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

public class StatisticsTableCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(
			JTable table, 
			Object value,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) {
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		c.setBorder(null);
		c.setIcon(null);

		c.setBackground(new Color(0,0,0,0));
		c.setForeground(Color.WHITE);
		c.setFont(Fonts.getMinecrafter().deriveFont(10f));
		
		c.setText(value != null ? value.toString() : null);

		return c;
	}
}
