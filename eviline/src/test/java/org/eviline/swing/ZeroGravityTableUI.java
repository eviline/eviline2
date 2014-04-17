package org.eviline.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ai.Player;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.swing.EngineTable;
import org.eviline.swing.EngineTableModel;
import org.eviline.swing.SwingPlayer;

public class ZeroGravityTableUI {

	public static void main(String[] args) throws Exception {
		final JFrame frame = new JFrame("test");
		
		JPanel contentPane = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(
						Resources.getStork(),
						0, 0,
						getWidth(), getHeight(),
						null);
			}
		};
		
		frame.setContentPane(contentPane);
		
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
			@Override
			public ShapeSource shapes(Engine e) {
				return EvilBag7NShapeSource.FACTORY.newInstance(e);
			}
		});
		JPanel tables = new JPanel(new GridBagLayout());
		tables.setOpaque(false);
		
		final EngineTable table = new EngineTable(engine, 16);
		table.getModel().setGhosting(true);
		tables.add(table, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

		StatisticsTable stats = new StatisticsTable(engine, 16);
		tables.add(stats, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		
		frame.add(tables, BorderLayout.CENTER);
		JLabel ll;
		frame.add(ll = new JLabel("eviline2"), BorderLayout.NORTH);
		ll.setHorizontalAlignment(SwingConstants.CENTER);
		ll.setForeground(Color.WHITE);
		ll.setFont(Resources.getMinecrafter().deriveFont(36f));
		
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
