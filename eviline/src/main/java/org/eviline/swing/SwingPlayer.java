package org.eviline.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.eviline.core.Command;
import org.eviline.core.ai.Player;

public class SwingPlayer implements Player {

	public static class Key {
		private int keyCode;
		private int keyMods;
		private int keyHold;
		
		public Key(int keyCode) {
			this(keyCode, 0, 0);
		}
		
		public Key(int keyCode, int keyMods) {
			this(keyCode, keyMods, 0);
		}
		
		public Key(int keyCode, int keyMods, int keyHold) {
			this.keyCode = keyCode;
			this.keyMods = keyMods;
			this.keyHold = keyHold;
		}
		
		public int getKeyCode() {
			return keyCode;
		}
		
		public int getKeyMods() {
			return keyMods;
		}
		
		public int getKeyHold() {
			return keyHold;
		}
		
		@Override
		public int hashCode() {
			return keyCode | (keyMods << 8);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			if(obj == this)
				return true;
			if(obj instanceof Key) {
				return keyCode == ((Key) obj).keyCode && keyMods == ((Key) obj).keyMods;
			}
			return false;
		}
		
		
		@Override
		public String toString() {
			String s = KeyEvent.getKeyText(keyCode);
			if(keyMods != 0)
				s = KeyEvent.getModifiersExText(keyMods) + " " + s;
			if(keyHold != 0)
				s = "HOLD/" + s;
			return s;
		}
	}
	
	protected Map<Key, Command> controls = new HashMap<>();
	protected Map<Key, Command> held = new HashMap<>();
	protected JComponent controlTarget;
	protected Deque<Command> commands = new ArrayDeque<>();
	
	protected class ControlsKeyListener extends KeyAdapter {
		protected Set<Integer> down = new HashSet<>();
		protected Map<Integer, Timer> holdTimers = new HashMap<>();
		
		public ControlsKeyListener() {
			for(final Key key : controls.keySet()) {
				final Command cmd = controls.get(key);
				if(key.getKeyHold() > 0) {
					Timer holdTimer = new Timer(key.getKeyHold(), new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							synchronized(commands) {
								commands.offerLast(cmd);
								held.put(new Key(key.getKeyCode()), cmd);
							}
							down.remove(key.getKeyCode());
							
						}
					});
					holdTimer.setRepeats(false);
					holdTimers.put(key.getKeyCode(), holdTimer);
				}
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			down.add(e.getKeyCode());
			Timer holdTimer = holdTimers.get(e.getKeyCode());
			if(holdTimer != null)
				holdTimer.restart();
			boolean fire = true;
			Key key = new Key(e.getKeyCode(), e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK);
			if(fire) {
				if(controls.containsKey(key)) {
					synchronized(commands) {
						commands.offerLast(controls.get(key));
					}
				}
				controlTarget.requestFocus();
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			Key key = new Key(e.getKeyCode(), e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK);
			Timer holdTimer = holdTimers.get(e.getKeyCode());
			if(holdTimer != null)
				holdTimer.stop();
			held.remove(key);
		}
	}
	
	protected ControlsKeyListener controlsListener;
	
	public SwingPlayer(JComponent controlTarget) {
		this.controlTarget = controlTarget;
		initKeys();
		controlsListener = new ControlsKeyListener();
		controlTarget.addKeyListener(controlsListener);
	}
	
	protected void initKeys() {
		controls.put(new Key(KeyEvent.VK_Z), Command.ROTATE_LEFT);
		controls.put(new Key(KeyEvent.VK_X), Command.ROTATE_RIGHT);
		controls.put(new Key(KeyEvent.VK_LEFT), Command.SHIFT_LEFT);
		controls.put(new Key(KeyEvent.VK_RIGHT), Command.SHIFT_RIGHT);
		controls.put(new Key(KeyEvent.VK_DOWN), Command.SHIFT_DOWN);
		controls.put(new Key(KeyEvent.VK_UP), Command.HARD_DROP);
		controls.put(new Key(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK, 150), Command.AUTOSHIFT_LEFT);
		controls.put(new Key(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK, 150), Command.AUTOSHIFT_RIGHT);
		controls.put(new Key(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK, 150), Command.SOFT_DROP);
	}

	@Override
	public Command tick() {
		Command c;
		synchronized(commands) {
			c = commands.pollFirst();
		}
		if(c == null && held.size() > 0)
			c = held.values().iterator().next();
		if(c == null)
			c = Command.NOP;
		return c;
	}

	public KeyListener getControlsListener() {
		return controlsListener;
	}
	
	@Override
	public String toString() {
		return controls.toString();
	}
	
	public Key forCommand(Command c) {
		for(Key k : controls.keySet())
			if(controls.get(k) == c)
				return k;
		return null;
	}
}
