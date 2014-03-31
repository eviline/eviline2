package eviline;

import java.awt.EventQueue;
import javax.swing.JFrame;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ai.AIPlayer;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;

public class EngineTableUI {

	public static void main(String[] args) {
		final JFrame frame = new JFrame("test");
		
		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration());
		
		final EngineTable table = new EngineTable(engine);
		frame.add(table);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		final AIPlayer ai = new AIPlayer(engine);
		
		EventQueue.invokeLater(new Runnable() {
			private int drawn = 0;
			@Override
			public void run() {
				engine.tick(ai.tick());
				if(engine.getShape() == null) {
					if(drawn == 0) {
						((EngineTableModel) table.getModel()).fireTableDataChanged();
						frame.setTitle("" + engine.getLines());
						drawn = 10;
					} else
						drawn--;
				}
				if(!engine.isOver())
					EventQueue.invokeLater(this);
				else {
					((EngineTableModel) table.getModel()).fireTableDataChanged();
					frame.setTitle("" + engine.getLines());
				}
			}
		});
	}

}
