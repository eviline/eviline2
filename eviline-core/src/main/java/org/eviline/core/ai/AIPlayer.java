package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eviline.core.Command;
import org.eviline.core.Engine;
import org.eviline.core.XYShape;
import org.eviline.core.ai.CommandGraph.Vertex;

public class AIPlayer {
	protected AIKernel ai;
	protected Engine engine;
	
	protected XYShape dest;
	protected Deque<Command> commands = new ArrayDeque<>();
	
	public AIPlayer(Engine engine) {
		this(new DefaultAIKernel(), engine);
	}
	
	public AIPlayer(AIKernel ai, Engine engine) {
		this.ai = ai;
		this.engine = engine;
	}
	
	public Command tick() {
		if(engine.isOver()) {
			dest = null;
			return Command.NOP;
		}
		
		if(commands.size() == 0) {
			if(engine.getShape() == null)
				return Command.NOP;
			Vertex v = ai.bestPlacement(engine.getField(), engine.getShape());
			dest = v.shape;
			while(v.command != null) {
				commands.offerFirst(v.command);
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
}
