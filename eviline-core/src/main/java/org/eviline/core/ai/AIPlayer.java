package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eviline.core.Command;
import org.eviline.core.Engine;
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
			int shape = g.getSelectedShape();
			dest = shape;
			Command c = CommandGraph.commandOf(g.getVertices(), shape);
			while(c != null) {
				if(c != Command.SOFT_DROP || allowDrops) {
					commands.offerFirst(c);
				} else { // it's a soft drop
					int originShape = CommandGraph.originOf(g.getVertices(), shape);
					int undropping = shape;
					while(undropping != originShape) {
						commands.offerFirst(Command.SHIFT_DOWN);
						undropping = XYShapes.shiftedUp(undropping);
					}
				}
				if(CommandGraph.originOf(g.getVertices(), shape) != CommandGraph.NULL_ORIGIN) {
					shape = CommandGraph.originOf(g.getVertices(), shape);
					c = CommandGraph.commandOf(g.getVertices(), shape);
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
