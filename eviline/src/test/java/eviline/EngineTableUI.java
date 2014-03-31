package eviline;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ai.AIPlayer;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;

public class EngineTableUI {

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		
		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration());
		
		final EngineTable table = new EngineTable(engine);
		frame.add(table);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		final AIPlayer ai = new AIPlayer(engine);
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				engine.tick(ai.tick());
				((EngineTableModel) table.getModel()).fireTableDataChanged();
				table.repaint();
				if(!engine.isOver())
					EventQueue.invokeLater(this);
			}
		});
	}

}
