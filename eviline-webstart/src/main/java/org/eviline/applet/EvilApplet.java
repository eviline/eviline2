package org.eviline.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.EngineListener;
import org.eviline.core.EngineStats;
import org.eviline.core.EngineStatsSubmitter;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.swing.EngineComponent;
import org.eviline.swing.Resources;
import org.eviline.swing.ShapeSourceComponent;
import org.eviline.swing.StatisticsTable;
import org.eviline.swing.StatisticsTableModel;
import org.eviline.swing.SwingPlayer;

public class EvilApplet extends JApplet {
	private static final SimpleDateFormat DF = new SimpleDateFormat("YYYY-MM-dd");
	private static URL url;
	static {
		try {
			url = new URL("http://www.eviline.org/eviline-webapp/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected JPanel contentPane = new JPanel(new BorderLayout()) {
		private Image spider = Resources.getFlower();
		
		@Override
		protected void paintComponent(Graphics g) {
			if(spider.getWidth(null) != getWidth() || spider.getHeight(null) != getHeight()) {
				spider = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				spider.getGraphics().drawImage(Resources.getFlower(), 0, 0, getWidth()*7/5, getHeight(), null);
			}
			g.drawImage(
					spider,
					0, 0,
					null);
		}
	};

	protected StatisticsTable stats;
	
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
		tables.add(table, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		
		final ShapeSourceComponent shapes = new ShapeSourceComponent(engine, 3);
		tables.add(shapes, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		
		stats = new StatisticsTable(engine, 16) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(64,0,0,192));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		tables.add(stats, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(10, 10, 10, 10), 0, 0));
		
		contentPane.add(tables, BorderLayout.CENTER);
		JLabel ll;
		contentPane.add(ll = new JLabel("eviline2"), BorderLayout.NORTH);
		ll.setHorizontalAlignment(SwingConstants.CENTER);
		ll.setForeground(Color.WHITE);
		ll.setFont(Resources.getMinecrafter().deriveFont(36f));
		JButton reset = new JButton(new AbstractAction("RESET") {
			@Override
			public void actionPerformed(ActionEvent e) {
				engine.reset();
				table.requestFocusInWindow();
			}
		});
		reset.setFont(Resources.getMinecrafter().deriveFont(24f));
		reset.setBackground(new Color(128,0,0));
		reset.setForeground(Color.WHITE);
		reset.setOpaque(true);
		reset.setBorder(null);
		reset.setVerticalTextPosition(SwingConstants.BOTTOM);
		contentPane.add(reset, BorderLayout.SOUTH);
		
		final SwingPlayer pl = new SwingPlayer(table, true);
		
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
			private boolean wasOver = false;
			private boolean invoked = false;
			@Override
			public void run() {
				if(!table.hasFocus())
					return;
				Command c = pl.tick();
				boolean isOver = false;
				if(!engine.isOver())
					engine.tick(c);
				else
					isOver = true;
				if(isOver && !wasOver) {
					String name = JOptionPane.showInputDialog(EvilApplet.this, "Enter your name to submit high score", "Enter High Score Name", JOptionPane.QUESTION_MESSAGE);
					if(name != null) {
						try {
							new EngineStatsSubmitter(url).post(new EngineStats(engine), name);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(EvilApplet.this, e.toString(), "Error Submitting Score", JOptionPane.ERROR_MESSAGE);
						}
					}
					showHighScores();
				}
				wasOver = isOver;
				if(invoked)
					return;
				invoked = true;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						revalidate();
						repaint();
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

		requestFocusInWindow();
		
		showHighScores();
		
//		exec.schedule(ticker, 1000000L / 60, TimeUnit.MICROSECONDS);
		exec.scheduleAtFixedRate(ticker, 0, 1000000L/60, TimeUnit.MICROSECONDS);
		
	}
	
	private void showHighScores() {
		stats.getModel().clear();

		String fmt = "%-18s%-6s%-6s%-10s\n";
		
		stats.getModel().write(String.format(fmt, "Name", "Score", "Lines", "Date"));
		
		try {
			for(Map.Entry<EngineStats, String> e : new EngineStatsSubmitter(url).get().entrySet()) {
				String label = String.format(
						fmt, 
						e.getValue(), 
						e.getKey().getScore(), 
						e.getKey().getLines(),
						DF.format(e.getKey().getTs()));
				stats.getModel().write(label);
			}
		} catch(Exception e) {
			stats.getModel().write(e.toString());
		}

		stats.getModel().write("\nClick in the field to begin.");
	}

	@Override
	public void start() {
		super.start();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				revalidate();
				repaint();
			}
		});
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
}
