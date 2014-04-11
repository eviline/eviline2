package eviline;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ai.Player;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;
import org.eviline.swing.SwingPlayer;

public class ZeroGravityTableUI {

	public static void main(String[] args) {
		final JFrame frame = new JFrame("test");

		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration() {
			@Override
			public Integer downFramesRemaining(Engine e) {
				return null;
			}
			@Override
			public Integer respawnFramesRemaining(Engine e) {
				return 0;
			}
		});
		final EngineTable table = new EngineTable(engine);
		frame.add(table);

		final Player pl = new SwingPlayer(table);

		Timer ticker = new Timer(1000 / 60, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Command c = pl.tick();
				if(!engine.isOver())
					engine.tick(c);
				((EngineTableModel) table.getModel()).fireTableDataChanged();
				frame.setTitle("" + engine.getLines());
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!engine.isOver())
					return;
				engine.reset();
			}
		});

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		ticker.start();
	}

}
