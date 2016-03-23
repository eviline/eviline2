package org.eviline.lanterna;

import org.eviline.core.Block;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

public class EngineComponent extends AbstractComponent {

	protected ShapeTypeColor color;
	protected Engine engine;
	protected boolean ghosting;

	public EngineComponent(Engine engine) {
		this.engine = engine;
		color = new ShapeTypeColor();
	}

	@Override
	public void repaint(TextGraphics g) {
		Field field = engine.getField();
		g.setForegroundColor(Color.BLACK);
		g.setBackgroundColor(Color.WHITE);
		g.drawString(0, 0, "\u2554");
		g.drawString(0, field.BUFFER + field.HEIGHT + 1, "\u255a");
		g.drawString(field.WIDTH*2 + 1, 0, "\u2557");
		g.drawString(field.WIDTH*2 + 1, field.BUFFER + field.HEIGHT + 1, "\u255d");
		g.fillRectangle('\u2550', new TerminalPosition(1, 0), new TerminalSize(field.WIDTH*2, 1));
		g.fillRectangle('\u2550', new TerminalPosition(1, field.BUFFER + field.HEIGHT + 1), new TerminalSize(field.WIDTH*2, 1));
		g.fillRectangle('\u2551', new TerminalPosition(0, 1), new TerminalSize(1, field.BUFFER + field.HEIGHT));
		g.fillRectangle('\u2551', new TerminalPosition(field.WIDTH*2 + 1, 1), new TerminalSize(1, field.BUFFER + field.HEIGHT));
		g = g.subAreaGraphics(new TerminalPosition(1, 1), new TerminalSize(field.WIDTH*2, field.BUFFER + field.HEIGHT));
		g.setBackgroundColor(Color.BLACK);
		g.setForegroundColor(Color.BLACK);
		g.fillArea(' ');

		for(int y = -field.BUFFER; y < field.HEIGHT; y++) {
			for(int x = 0; x < field.WIDTH; x++) {
				Block b = engine.block(x, y);
				ShapeType t = null;
				if(ghosting && b == null && engine.getGhost() != -1 && XYShapes.has(engine.getGhost(), x, y))
					t = ShapeType.G;
				else if(b != null && (b.getFlags() & Block.MASK_GARBAGE) != 0)
					t = ShapeType.G;
				else if(b != null && b.shape() != null)
					t = b.shape().type();
				if(t != null) {
					g.setBackgroundColor(color.bg(t));
					g.setForegroundColor(color.fg(t));
					String s = "\u2592\u2592";
					if(t == ShapeType.G)
						s = "\u2591\u2591";
					g.drawString(x*2, y + field.BUFFER, s, ScreenCharacterStyle.Bold);
				} else if(y == -1) {
					g.setBackgroundColor(Color.BLACK);
					g.setForegroundColor(Color.WHITE);
					g.drawString(x*2, y + field.BUFFER, "\u2581\u2581");
				}
			}
		}
	}

	@Override
	protected TerminalSize calculatePreferredSize() {
		Field field = engine.getField();
		return new TerminalSize(field.WIDTH*2 + 2, field.BUFFER + field.HEIGHT + 2);
	}

	public boolean isGhosting() {
		return ghosting;
	}

	public void setGhosting(boolean ghosting) {
		this.ghosting = ghosting;
	}

	public Engine getEngine() {
		return engine;
	}
	
	public void setEngine(Engine engine) {
		this.engine = engine;
	}
}
