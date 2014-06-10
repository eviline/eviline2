package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import org.eviline.core.Command;
import org.eviline.core.Engine;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class AIPlayer implements Player {
	protected AIKernel ai;
	protected Engine engine;
	
	protected XYShape dest;
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
			dest = null;
			return Command.NOP;
		}
		
		if(commands.size() == 0) {
			if(engine.getShape() == null)
				return Command.NOP;
			Vertex v = ai.bestPlacement(engine.getField(), engine.getShape(), engine.getNext(), lookahead);
			dest = v.shape;
			while(v.command != null) {
				if(v.command != Command.SOFT_DROP || allowDrops) {
					commands.offerFirst(v.command);
				} else { // it's a soft drop
					XYShape dropping = v.origin.shape;
					dropping = dropping.shiftedDown();
					while(!engine.getField().intersects(dropping)) {
						commands.offerFirst(Command.SHIFT_DOWN);
						dropping = dropping.shiftedDown();
					}
				}
				v = v.origin;
			}
			commands.offerLast(Command.SHIFT_DOWN);
		}
		
		if(commands.size() == 0) {
			dest = null;
			return Command.NOP;
		}
		
		return commands.pollFirst();
	}
	
	public XYShape getDest() {
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
