package org.eviline.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.EngineListener;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.swing.EngineComponent;
import org.eviline.swing.Resources;
import org.eviline.swing.StatisticsTable;
import org.eviline.swing.StatisticsTableModel;
import org.eviline.swing.SwingPlayer;

public class EvilApplet extends JApplet {
	protected JPanel contentPane = new JPanel(new BorderLayout()) {
		private Image stork = Resources.getSpider();
		
		@Override
		protected void paintComponent(Graphics g) {
			if(stork.getWidth(null) != getWidth() || stork.getHeight(null) != getHeight()) {
				stork = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				stork.getGraphics().drawImage(Resources.getSpider(), 0, 0, getWidth(), getHeight(), null);
			}
			g.drawImage(
					stork,
					0, 0,
					null);
		}
		
		@Override
		public boolean isDoubleBuffered() {
			return true;
		}
	};

	@Override
	public void init() {
		super.init();
//		if(!EventQueue.isDispatchThread()) {
//			EventQueue.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					init();
//				}
//			});
//			return;
//		}
		setContentPane(contentPane);
		
		Field f = new Field();
		final Engine engine = new Engine(f, new Configuration() {
			@Override
			public Integer downFramesRemaining(Engine e) {
				return null;
			}
			@Override
			public Integer respawnFramesRemaining(Engine e) {
				return 1;
			}
			@Override
			public ShapeSource shapes(Engine e) {
				EvilBag7NShapeSource shapes = (EvilBag7NShapeSource) EvilBag7NShapeSource.FACTORY.newInstance(e);
				shapes.setLookahead(3);
				return shapes;
			}
		});
		engine.setNext(new ShapeType[0]);
		JPanel tables = new JPanel(new GridBagLayout());
		tables.setOpaque(false);
		
//		final EngineTable table = new EngineTable(engine, 24);
//		table.getModel().setGhosting(true);
		final EngineComponent table = new EngineComponent(engine, 24, false);
		table.setGhosting(true);
		table.setBackground(new Color(128,0,0,96));
		tables.add(table, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		table.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				engine.setPaused(true);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				engine.setPaused(false);
			}
		});
		
		final StatisticsTable stats = new StatisticsTable(engine, 24);
		tables.add(stats, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		
		add(tables, BorderLayout.CENTER);
		JLabel ll;
		add(ll = new JLabel("eviline2"), BorderLayout.NORTH);
		ll.setHorizontalAlignment(SwingConstants.CENTER);
		ll.setForeground(Color.WHITE);
		ll.setFont(Resources.getMinecrafter().deriveFont(36f));
		
		final SwingPlayer pl = new SwingPlayer(table);
		
		engine.addEngineListener(new EngineListener() {
			private boolean invoked = false;
			@Override
			public void ticked(Engine e, final Command c) {
				if(invoked)
					return;
				invoked = true;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						stats.ticked(engine, c);
						StatisticsTableModel m = stats.getModel();
						for(Command cmd : Command.values()) {
							SwingPlayer.Key key = pl.forCommand(cmd);
							if(key == null)
								continue;
							m.write(cmd + ": " + key + "\n");
						}
						m.fireTableDataChanged();
						invoked = false;
					}
				});
			}
		});
		
		engine.removeEngineListener(stats);
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		Runnable ticker = new Runnable() {
			private boolean invoked = false;
			@Override
			public void run() {
				Command c = pl.tick();
				if(!engine.isOver())
					engine.tick(c);
				if(invoked)
					return;
				invoked = true;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						table.repaint();
						invoked = false;
					}
				});
			}
		};
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!engine.isOver())
					return;
				engine.reset();
			}
		});

		table.requestFocusInWindow();
//		exec.schedule(ticker, 1000000L / 60, TimeUnit.MICROSECONDS);
		exec.scheduleAtFixedRate(ticker, 0, 1000000L/60, TimeUnit.MICROSECONDS);
		
		revalidate();
		repaint();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
}
