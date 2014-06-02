package org.eviline.applet;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JPanel;

import org.eviline.swing.Resources;

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
		if(!EventQueue.isDispatchThread()) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					init();
				}
			});
			return;
		}
		setContentPane(contentPane);
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
