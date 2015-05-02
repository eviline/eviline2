package org.eviline.core.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.eviline.core.Command;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.XYShapes;
import org.eviline.core.ai.DefaultAIKernel.Best;

public class AIPlayer implements Player {
	protected DefaultAIKernel ai;
	protected Engine engine;
	
	protected Best best;
	protected CommandGraph graph;
	protected int dest;
	protected Deque<Command> commands = new ArrayDeque<>();
	protected Field after;
	
	protected int lookahead;
	protected boolean allowDrops = true;
	
	public AIPlayer(Engine engine) {
		this(new DefaultAIKernel(), engine, engine.getNext().length);
	}
	
	public AIPlayer(DefaultAIKernel ai, Engine engine, int lookahead) {
		this.ai = ai;
		this.engine = engine;
		this.lookahead = lookahead;
	}
	
	public Command tick() {
		if(engine.isOver()) {
			dest = -1;
			graph = null;
			best = null;
			return Command.NOP;
		}
		
		if(commands.size() == 0) {
			if(engine.getShape() == -1) {
				dest = -1;
				graph = null;
				best = null;
				return Command.NOP;
			}
			
			if(engine.isHoldable() && engine.getHold() == null) {
				after = engine.getField().clone();
				after.setHold(XYShapes.shapeFromInt(engine.getShape()).type());
				return Command.HOLD;
			}
			
			
			if(engine.isHoldable()) {
				
				Callable<Best> bestTask = new Callable<DefaultAIKernel.Best>() {
					@Override
					public Best call() throws Exception {
						return ai.bestPlacement(engine.getField(), engine.getField(), engine.getShape(), engine.getNext(), lookahead + 1, 0);
					}
				};

				Callable<Best> heldTask = new Callable<DefaultAIKernel.Best>() {
					@Override
					public Best call() throws Exception {
						Field heldField = engine.getField().clone();
						heldField.setHold(XYShapes.shapeFromInt(engine.getShape()).type());
						return ai.bestPlacement(heldField, heldField, engine.getHold().xystart(), engine.getNext(), lookahead + 1, 0);
					}
				};
				
				List<FutureTask<Best>> futs = new ArrayList<>();
				futs.add(ai.getExec().submit(bestTask));
				futs.add(ai.getExec().submit(heldTask));
				ai.getExec().await(futs);

				try {
					best = futs.get(0).get();
					Best heldBest = futs.get(1).get();
					if(heldBest.score < best.score) {
						best = heldBest;
						after = engine.getField().clone();
						after.setHold(XYShapes.shapeFromInt(engine.getShape()).type());
						return Command.HOLD;
					}
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				
			} else {
				best = ai.bestPlacement(engine.getField(), engine.getField(), engine.getShape(), engine.getNext(), lookahead + 1, 0);
			}
			
			graph = best.graph;
			int shape = graph.getSelectedShape();
			dest = shape;
			Command c = CommandGraph.commandOf(graph.getVertices(), shape);
			while(c != null) {
				if(c != Command.SOFT_DROP || allowDrops) {
					commands.offerFirst(c);
				} else { // it's a soft drop
					int originShape = CommandGraph.originOf(graph.getVertices(), shape);
					int undropping = shape;
					while(undropping != originShape) {
						commands.offerFirst(Command.SHIFT_DOWN);
						undropping = XYShapes.shiftedUp(undropping);
					}
				}
				if(CommandGraph.originOf(graph.getVertices(), shape) != CommandGraph.NULL_ORIGIN) {
					shape = CommandGraph.originOf(graph.getVertices(), shape);
					c = CommandGraph.commandOf(graph.getVertices(), shape);
				} else
					c = null;
				
			}
			commands.offerLast(Command.SHIFT_DOWN);
		}
		
		if(commands.size() == 0) {
			dest = -1;
			graph = null;
			best = null;
			return Command.NOP;
		}
		
		return commands.pollFirst();
	}
	
	public Best getBest() {
		return best;
	}
	
	public int getDest() {
		return dest;
	}
	
	public CommandGraph getGraph() {
		return graph;
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
	
	public Field getAfter() {
		return after;
	}
}
