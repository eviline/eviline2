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
		switch(key.getKind()) {
		case ArrowLeft:
			c = Command.SHIFT_LEFT;
			break;
		case ArrowRight:
			c = Command.SHIFT_RIGHT;
			break;
		case ArrowDown:
			c = Command.SHIFT_DOWN;
			break;
		case ArrowUp:
			c = Command.HARD_DROP;
			break;
		case NormalKey:
			switch(key.getCharacter()) {
			case 'z':
				c = Command.ROTATE_LEFT;
				break;
			case 'x':
				c = Command.ROTATE_RIGHT;
				break;
			case 'a':
				c = Command.AUTOSHIFT_LEFT;
				break;
			case 's':
				c = Command.SOFT_DROP;
				break;
			case 'd':
				c = Command.AUTOSHIFT_RIGHT;
				break;
			}
			break;
		}
		if(c != null) {
			synchronized(commands) {
				commands.offerLast(c);
			}
		}
	}
}
