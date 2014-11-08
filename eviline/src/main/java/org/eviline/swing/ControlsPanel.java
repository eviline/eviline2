package org.eviline.swing;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.eviline.core.Command;
import org.eviline.swing.SwingPlayer.Key;

public class ControlsPanel extends JPanel {

	public static final Map<Command, Key> DEFAULT_CONTROLS;
	static {
		Map<Command, Key> ctrl = new EnumMap<Command, Key>(Command.class);
		SwingPlayer p = new SwingPlayer(null);
		p.initKeys(false);
		for(Command c : Command.values()) {
			ctrl.put(c, p.forCommand(c));
		}
		DEFAULT_CONTROLS = Collections.unmodifiableMap(ctrl);
	}
	
	protected Map<Command, Key> ctrl;
	
	public ControlsPanel() {
		this(DEFAULT_CONTROLS);
	}
	
	public ControlsPanel(Map<Command, Key> ctrl) {
		super(new GridLayout(0, 3));
		this.ctrl = new EnumMap<Command, Key>(ctrl);
		
		JLabel l;
		add(l = new JLabel("Action"));
		l.setHorizontalAlignment(SwingConstants.CENTER);
		add(l = new JLabel("Key"));
		l.setHorizontalAlignment(SwingConstants.CENTER);
		add(l = new JLabel("Delay"));
		l.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		for(Map.Entry<Command, Key> k : ctrl.entrySet()) {
			final Command c = k.getKey();
			final Key key = k.getValue();
			if(key == null)
				continue;
			final JToggleButton edit = new JToggleButton(KeyEvent.getKeyText(key.getKeyCode()));
			final JTextField delay = new JTextField("");
			delay.setDocument(new IntegerDocument());
			delay.setText("" + key.getKeyHold());
			
			edit.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int hold = 0;
					if(!delay.getText().isEmpty())
						hold = Integer.parseInt(delay.getText());
					ControlsPanel.this.ctrl.put(c, new Key(e.getKeyCode(), 0, hold));
					edit.setText(KeyEvent.getKeyText(e.getKeyCode()));
					edit.setSelected(false);
				}
			});
			
			add(new JLabel(c.toString()));
			add(edit);
			add(delay);
		}
	}

	public SwingPlayer getPlayer(JComponent target) {
		return new CustomSwingPlayer(target, ctrl);
	}
	
	public Map<Command, Key> getCtrl() {
		return ctrl;
	}
}
