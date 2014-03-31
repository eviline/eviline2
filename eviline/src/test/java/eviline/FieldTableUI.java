package eviline;

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.Shape;
import org.eviline.core.XYShape;
import org.eviline.swing.FieldTable;
import org.eviline.swing.FieldTableModel;

public class FieldTableUI {

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		
		Field f = new Field();
		
		final FieldTable table = new FieldTable(f);
		frame.add(table);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		final Engine engine = new Engine(f, new Configuration());
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				engine.tick(Command.NOP);
				((FieldTableModel) table.getModel()).fireTableDataChanged();
				table.repaint();
				if(!engine.isOver())
					EventQueue.invokeLater(this);
			}
		});
	}

}
