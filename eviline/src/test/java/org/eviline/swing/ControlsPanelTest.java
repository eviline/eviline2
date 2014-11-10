package org.eviline.swing;

import javax.swing.JFrame;

public class ControlsPanelTest {

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("controls");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ControlsPanel());
		frame.pack();
		frame.setVisible(true);
	}

}
