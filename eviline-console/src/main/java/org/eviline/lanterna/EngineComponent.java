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
		g.setForegroundColor(Color.WHITE);
		g.setBackgroundColor(Color.WHITE);
		g.fillArea(' ');
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
				g.drawString(x, y, "\u2592");
			}
		}
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		return new TerminalSize(Field.WIDTH + 2, Field.BUFFER + Field.HEIGHT + 2);
	}

}
