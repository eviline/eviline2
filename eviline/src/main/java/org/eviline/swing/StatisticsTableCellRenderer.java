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
import org.eviline.swing.StatisticsTableModel.ColoredCharacter;

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
		
		ColoredCharacter cc = (ColoredCharacter) value;
		
		c.setBorder(null);
		c.setIcon(null);

		c.setBackground(new Color(0,0,0,0));
		c.setFont(Fonts.getMinecrafter().deriveFont(10f));
		
		c.setText(cc.c != null ? cc.c.toString() : null);
		c.setForeground(cc.color != null ? cc.color : Color.WHITE);

		return c;
	}
}
