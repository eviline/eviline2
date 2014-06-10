package org.eviline.lanterna;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

public class EngineComponent extends AbstractComponent {
	
	protected ShapeTypeColor color;
	protected Engine engine;
	
	public EngineComponent(Engine engine) {
		this.engine = engine;
		color = new ShapeTypeColor();
	}

	@Override
	public void repaint(TextGraphics g) {
		g.setForegroundColor(Color.BLACK);
		g.setBackgroundColor(Color.WHITE);
		g.drawString(0, 0, "\u2554");
		g.drawString(0, Field.BUFFER + Field.HEIGHT + 1, "\u255a");
		g.drawString(Field.WIDTH + 1, 0, "\u2557");
		g.drawString(Field.WIDTH + 1, Field.BUFFER + Field.HEIGHT + 1, "\u255d");
		g.fillRectangle('\u2550', new TerminalPosition(1, 0), new TerminalSize(Field.WIDTH, 1));
		g.fillRectangle('\u2550', new TerminalPosition(1, Field.BUFFER + Field.HEIGHT + 1), new TerminalSize(Field.WIDTH, 1));
		g.fillRectangle('\u2551', new TerminalPosition(0, 1), new TerminalSize(1, Field.BUFFER + Field.HEIGHT));
		g.fillRectangle('\u2551', new TerminalPosition(Field.WIDTH + 1, 1), new TerminalSize(1, Field.BUFFER + Field.HEIGHT));
		g = g.subAreaGraphics(new TerminalPosition(1, 1), new TerminalSize(Field.WIDTH, Field.BUFFER + Field.HEIGHT));
		g.setBackgroundColor(Color.BLACK);
		g.setForegroundColor(Color.BLACK);
		g.fillArea(' ');
		for(int y = -Field.BUFFER; y < Field.HEIGHT; y++) {
			for(int x = 0; x < Field.WIDTH; x++) {
				Block b = engine.block(x, y);
				if(b == null)
					continue;
				ShapeType t = b.shape().type();
				g.setBackgroundColor(color.bg(t));
				g.setForegroundColor(color.fg(t));
				g.drawString(x, y + Field.BUFFER, "\u2592");
			}
		}
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		return new TerminalSize(Field.WIDTH + 2, Field.BUFFER + Field.HEIGHT + 2);
	}

}
