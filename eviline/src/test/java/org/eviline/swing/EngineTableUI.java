package org.eviline.swing;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;

public class EngineTableUI {

	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("test");

		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration());
		final DefaultAIKernel k = new DefaultAIKernel(new NextFitness());
		
		k.setFitness(new NextFitness());
		k.setExec(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		
		final AIPlayer ai = new AIPlayer(k, engine);
		final EngineTable table = new EngineTable(engine, 16);
		frame.add(table);

		final Runnable task = new Runnable() {
			private int drawn = 0;
			@Override
			public void run() {
				ai.tick();
				engine.setShape(ai.getDest());
				engine.tick(Command.SHIFT_DOWN);
				ai.getCommands().clear();
				if(drawn == 0) {
					((EngineTableModel) table.getModel()).fireTableDataChanged();
					frame.setTitle("" + engine.getLines());
					drawn = 0;
				} else
					drawn--;
				while(engine.getShape() == null && !engine.isOver())
					engine.tick(Command.NOP);
				if(!engine.isOver())
					EventQueue.invokeLater(this);
				else {
					((EngineTableModel) table.getModel()).fireTableDataChanged();
					frame.setTitle("" + engine.getLines());
				}
			}
		};

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!engine.isOver())
					return;
				engine.reset();
				EventQueue.invokeLater(task);
			}
		});

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		EventQueue.invokeLater(task);
	}

}
