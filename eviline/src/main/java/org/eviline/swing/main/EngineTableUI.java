package org.eviline.swing.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.swing.EngineComponent;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;

public class EngineTableUI {

	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("test");

		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration());
		engine.setNext(new ShapeType[3]);
		final DefaultAIKernel k = new DefaultAIKernel(new NextFitness());
		
		k.setExec(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		
		final AIPlayer ai = new AIPlayer(k, engine, 1);
//		final EngineTable table = new EngineTable(engine, 16);
		final EngineComponent table = new EngineComponent(engine, 32, true);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table, BorderLayout.CENTER);
		
		frame.add(panel);

		final Runnable task = new Runnable() {
			private int drawn = 0;
			@Override
			public void run() {
				ai.tick();
				engine.setShape(ai.getDest());
				engine.tick(Command.SHIFT_DOWN);
				ai.getCommands().clear();
				if(drawn == 0) {
					table.repaint();
					frame.setTitle("" + engine.getLines());
					drawn = 0;
				} else
					drawn--;
				while(engine.getShape() == -1 && !engine.isOver())
					engine.tick(Command.NOP);
				if(!engine.isOver())
					EventQueue.invokeLater(this);
				else {
					table.repaint();
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
