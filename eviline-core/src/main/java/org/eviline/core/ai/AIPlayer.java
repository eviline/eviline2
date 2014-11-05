package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import org.eviline.core.Command;
import org.eviline.core.Engine;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;

public class AIPlayer implements Player {
	protected AIKernel ai;
	protected Engine engine;
	
	protected int dest;
	protected Deque<Command> commands = new ArrayDeque<>();
	
	protected int lookahead;
	protected boolean allowDrops = true;
	
	public AIPlayer(Engine engine) {
		this(new DefaultAIKernel(), engine, engine.getNext().length);
	}
	
	public AIPlayer(AIKernel ai, Engine engine, int lookahead) {
		this.ai = ai;
		this.engine = engine;
		this.lookahead = lookahead;
	}
	
	public Command tick() {
		if(engine.isOver()) {
			dest = -1;
			return Command.NOP;
		}
		
		if(commands.size() == 0) {
			if(engine.getShape() == -1)
				return Command.NOP;
			CommandGraph g = ai.bestPlacement(engine.getField(), engine.getShape(), engine.getNext(), lookahead);
			int[] v = g.getVertices()[g.getSelectedShape()];
			dest = CommandGraph.shapeOf(v);
			Command c = CommandGraph.commandOf(v);
			while(c != null) {
				if(c != Command.SOFT_DROP || allowDrops) {
					commands.offerFirst(c);
				} else { // it's a soft drop
					int[] originVertex = g.getVertices()[CommandGraph.originOf(v)];
					int dropping = CommandGraph.shapeOf(originVertex);
					dropping = XYShapes.shiftedDown(dropping);
					while(!engine.getField().intersects(dropping)) {
						commands.offerFirst(Command.SHIFT_DOWN);
						dropping = XYShapes.shiftedDown(dropping);
					}
				}
				if(CommandGraph.originOf(v) != CommandGraph.NULL_ORIGIN) {
					v = g.getVertices()[CommandGraph.originOf(v)];
					c = CommandGraph.commandOf(v);
				} else
					c = null;
				
			}
			commands.offerLast(Command.SHIFT_DOWN);
		}
		
		if(commands.size() == 0) {
			dest = -1;
			return Command.NOP;
		}
		
		return commands.pollFirst();
	}
	
	public int getDest() {
		return dest;
	}
	
	public Deque<Command> getCommands() {
		return commands;
	}

	public int getLookahead() {
		return lookahead;
	}

	public void setLookahead(int lookahead) {
		this.lookahead = lookahead;
	}

	public boolean isAllowDrops() {
		return allowDrops;
	}

	public void setAllowDrops(boolean allowDrops) {
		this.allowDrops = allowDrops;
	}
}
