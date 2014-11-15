package org.eviline.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.eviline.swing.StatisticsTableModel.ColoredCharacter;

public class StatisticsTableCellRenderer extends DefaultTableCellRenderer {
	
	private OutlineLabel ll = new OutlineLabel();
	
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
		
		c.setText(cc.c != null ? cc.c.toString() : null);
		c.setForeground(cc.color != null ? cc.color : Color.BLACK);

		ll.set(c);
		
		return ll;
	}
	
	private class OutlineLabel extends JLabel {
		public OutlineLabel() {
			setOpaque(false);
			setBorder(null);
//			setFont(Resources.getMinecrafter().deriveFont(16f));
			setFont(getFont().deriveFont(12f).deriveFont(Font.BOLD));
		}
		
		public void set(JLabel c) {
			setText(c.getText());
			setForeground(c.getForeground());
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Color fg = getForeground();
			setForeground(Color.WHITE);
			for(int x = 0; x <= 2; x++) {
				for(int y = 0; y <= 2; y++) {
					Graphics gg = g.create();
					gg.translate(x-1, y-1);
					super.paintComponent(gg);
				}
			}
			setForeground(fg);
			Graphics gg = g.create();
			super.paintComponent(gg);
		}
	}
}
