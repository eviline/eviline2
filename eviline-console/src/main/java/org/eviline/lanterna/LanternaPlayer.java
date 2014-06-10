package org.eviline.lanterna;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eviline.core.Command;
import org.eviline.core.ai.Player;

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

public class LanternaPlayer extends WindowAdapter implements Player {
	protected Deque<Command> commands = new ArrayDeque<Command>();

	@Override
	public Command tick() {
		Command c;
		synchronized(commands) {
			c = commands.pollFirst();
		}
		if(c == null)
			c = Command.NOP;
		return c;
	}

	@Override
	public void onUnhandledKeyboardInteraction(Window window, Key key) {
		Command c = null;
		switch(key.getCharacter()) {
		case 'k':
			c = Command.ROTATE_LEFT;
			break;
		case 'l':
			c = Command.ROTATE_RIGHT;
			break;
		case 'a':
			if(key.isAltPressed())
				c = Command.AUTOSHIFT_LEFT;
			else
				c = Command.SHIFT_LEFT;
			break;
		case 'w':
			c = Command.HARD_DROP;
			break;
		case 's':
			if(key.isAltPressed())
				c = Command.SOFT_DROP;
			else
				c = Command.SHIFT_DOWN;
			break;
		case 'd':
			if(key.isAltPressed())
				c = Command.AUTOSHIFT_RIGHT;
			else
				c = Command.SHIFT_RIGHT;
			break;
		}
		if(c != null) {
			synchronized(commands) {
				commands.offerLast(c);
			}
		}
	}
}
