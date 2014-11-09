package org.eviline.swing.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.EngineFactories;
import org.eviline.core.EngineFactory;
import org.eviline.core.EngineListener;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.Player;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.swing.ControlsPanel;
import org.eviline.swing.EngineComponent;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;
import org.eviline.swing.Resources;
import org.eviline.swing.ShapeSourceComponent;
import org.eviline.swing.StatisticsTable;
import org.eviline.swing.StatisticsTableModel;
import org.eviline.swing.SwingPlayer;
import org.eviline.swing.SwingPlayer.Key;

public class ZeroGravityTableUI {

	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("test");
		
		JPanel contentPane = new JPanel(new BorderLayout()) {
			private Image stork = Resources.getFlower();
			
			@Override
			protected void paintComponent(Graphics g) {
				if(stork.getWidth(null) != getWidth() || stork.getHeight(null) != getHeight()) {
					stork = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
					stork.getGraphics().drawImage(Resources.getFlower(), 0, 0, getWidth(), getHeight(), null);
				}
				g.drawImage(
						stork,
						0, 0,
						null);
			}
		};
		
		frame.setContentPane(contentPane);
		contentPane.setDoubleBuffered(true);
		
		Field f = new Field();
		
		Configuration conf = new Configuration(
				EngineFactories.createIntegerFactory(null),
				EngineFactories.createIntegerFactory(1),
				EvilBag7NShapeSource.createFactory(EvilBag7NShapeSource.DEFAULT_N, 3));
		
		final Engine engine = new Engine(f, conf);
		engine.setNext(new ShapeType[0]);
		JPanel tables = new JPanel(new GridBagLayout());
		tables.setOpaque(false);
		
//		final EngineTable table = new EngineTable(engine, 24);
//		table.getModel().setGhosting(true);
		final EngineComponent table = new EngineComponent(engine, 24, false);
		table.setGhosting(true);
		table.setBackground(new Color(192,192,255,96));
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
		
		final ShapeSourceComponent shapes = new ShapeSourceComponent(engine, 4);
		tables.add(shapes, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		
		final StatisticsTable stats = new StatisticsTable(engine, 24);
		tables.add(stats, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		
		frame.add(tables, BorderLayout.CENTER);
		JLabel ll;
		frame.add(ll = new JLabel("eviline2"), BorderLayout.NORTH);
		ll.setHorizontalAlignment(SwingConstants.CENTER);
		ll.setForeground(new Color(255,64,96));
		ll.setFont(Resources.getMinecrafter().deriveFont(36f));
		
		final AtomicReference<SwingPlayer> pl = new AtomicReference<SwingPlayer>(new SwingPlayer());
		pl.get().initKeys(false);
		table.addKeyListener(pl.get().getControlsListener());
		
		JButton controls = new JButton("Controls");
		frame.add(controls, BorderLayout.SOUTH);
		controls.addActionListener(new ActionListener() {
			private Map<Command, Key> ctrl = ControlsPanel.DEFAULT_CONTROLS;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog(frame, "Edit Controls");
				dialog.setLayout(new BorderLayout());
				final ControlsPanel cp = new ControlsPanel(ctrl);
				JButton apply = new JButton("Apply");
				apply.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						table.removeKeyListener(pl.get().getControlsListener());
						pl.set(cp.getPlayer());
						pl.get().initKeys(false);
						table.addKeyListener(pl.get().getControlsListener());
						ctrl = cp.getCtrl();
						dialog.dispose();
					}
				});
				JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();
					}
				});
				dialog.add(cp, BorderLayout.CENTER);
				JPanel ac = new JPanel(new GridLayout(1, 0));
				ac.add(apply);
				ac.add(cancel);
				dialog.add(ac, BorderLayout.SOUTH);
				dialog.pack();
				dialog.setModal(true);
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
		});
		
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
							SwingPlayer.Key key = pl.get().forCommand(cmd);
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
				Command c = pl.get().tick();
				if(!engine.isOver())
					engine.tick(c);
				if(invoked)
					return;
				invoked = true;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						shapes.repaint();
						table.repaint();
						frame.setTitle("" + engine.getLines());
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

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		table.requestFocusInWindow();
//		exec.schedule(ticker, 1000000L / 60, TimeUnit.MICROSECONDS);
		exec.scheduleAtFixedRate(ticker, 0, 1000000L/60, TimeUnit.MICROSECONDS);
	}

}
