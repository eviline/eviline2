package org.eviline.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;

public class DefaultAIKernel implements AIKernel {

	protected class Best {
		public final CommandGraph graph;
		public final int[] vertex;
		public final double score;
		public final Field after;
		public final ShapeType type;
		
		public Best(CommandGraph graph, int[] vertex, double score, Field after, ShapeType type) {
			this.graph = graph;
			this.vertex = vertex;
			this.score = score;
			this.after = after;
			this.type = type;
		}
	}
	
	protected Fitness fitness = new DefaultFitness();
	protected Executor exec = new Executor() {
		@Override
		public void execute(Runnable command) {
			command.run();
		}
	};
	
	public DefaultAIKernel() {}
	
	public DefaultAIKernel(Fitness fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public CommandGraph bestPlacement(final Field field, int current, ShapeType[] next, final int lookahead) {
		final CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		int[] best = null;
		
		final int nextShape;
		final ShapeType[] nextNext;
		if(lookahead == 0) {
			nextShape = -1;
			nextNext = Arrays.copyOf(next, next.length);
		} else if(next.length > 0) {
			nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextShape = -1;
			nextNext = null;
		}
			
		int[][] vertices = g.getVertices();
		final Best[] bests = new Best[vertices.length];
		
//		for(final int shape : g.getVertices().keySet()) {
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			final int[] v;
			if(CommandGraph.originOf(v = vertices[i]) == CommandGraph.NULL_ORIGIN)
				continue;
			final int shape = i;
			if(!field.intersects(XYShapes.shiftedDown(shape))) {
//				v[CommandGraph.ORIGIN] = CommandGraph.NULL_ORIGIN;
				continue;
			}
			
			Runnable task = new Runnable() {
				@Override
				public void run() {
					Field after = field.clone();
					after.blit(shape, 0);
					Best b = bestPlacement(field, after, nextShape, nextNext, lookahead);
					Best best = new Best(g, v, b.score, b.after, XYShapes.shapeFromInt(shape).type());
					synchronized(v) {
						bests[shape] = best;
						v.notify();
					}
				}
			};
			
			exec.execute(task);
		}
		
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			int[] v = vertices[i];
			if(CommandGraph.originOf(v) == CommandGraph.NULL_ORIGIN)
				continue;
			if(!field.intersects(XYShapes.shiftedDown(i)))
				continue;
			Best b;
			synchronized(v) {
				while((b = bests[i]) == null) {
					try {
						v.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
			if(b.score < badness) {
				best = b.vertex;
				badness = b.score;
			}
		}
		
		g.setSelectedShape(CommandGraph.shapeOf(best));
		return g;
	}
	
	protected Best bestPlacement(Field originalField, Field currentField, int currentShape, ShapeType[] next, int lookahead) {
		int[] v = new int[4];
		v[CommandGraph.ORIGIN] = CommandGraph.NULL_ORIGIN;
		v[CommandGraph.SHAPE] = currentShape;
		v[CommandGraph.COMMAND] = CommandGraph.NULL_COMMAND;
		v[CommandGraph.PATH_LENGTH] = Integer.MAX_VALUE;
		if(currentShape == -1 || lookahead == 0) {
			return new Best(null, v, fitness.badness(originalField, currentField, next), currentField, null);
		}
		
		currentField.clearLines();
		
		CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, v, Double.POSITIVE_INFINITY, null, null);

		int nextShape = -1;
		ShapeType[] nextNext = null;
		if(next.length > 0) {
			nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		}
		
//		for(int shape : g.getVertices().keySet()) {
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			if(CommandGraph.originOf(g.getVertices()[i]) == CommandGraph.NULL_ORIGIN)
				continue;
			int shape = i;
			if(!currentField.intersects(XYShapes.shiftedDown(shape)))
				continue;
			Field nextField = currentField.clone();
			nextField.blit(shape, 0);
			Best shapeBest = bestPlacement(originalField, nextField, nextShape, nextNext, lookahead - 1);
			if(shapeBest.score < best.score)
				best = shapeBest;
		}
		
		return best;
	}
	
	@Override
	public ShapeType worstNext(final Field field, final ShapeSource shapes, ShapeType[] next, final int lookahead) {
		Field bestPlayed = field;
		if(next.length > 0) {
			int nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext, nextNext.length).after;
		}
		
		final Field fbestPlayed = bestPlayed;

		bestPlayed.clearLines();
		
		int[] v = new int[4];
		v[CommandGraph.ORIGIN] = CommandGraph.NULL_ORIGIN;
		v[CommandGraph.SHAPE] = -1;
		v[CommandGraph.COMMAND] = CommandGraph.NULL_COMMAND;
		v[CommandGraph.PATH_LENGTH] = Integer.MAX_VALUE;
		
		Best worst = new Best(null, v, Double.NEGATIVE_INFINITY, null, null);
		Collection<Future<Best>> futs = new ArrayList<>();
		
		for(final ShapeType type : new HashSet<>(Arrays.asList(shapes.getBag()))) {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
					Best shapeBest = bestPlacement(field, fbestPlayed, currentShape, ShapeType.NONE, 1);
					List<ShapeType> nextBag = new ArrayList<>(Arrays.asList(shapes.getBag()));
					nextBag.remove(type);
					Best shapeWorst = worstNext(field, shapeBest.after, nextBag, lookahead - 1);
					return new Best(null, shapeWorst.vertex, shapeWorst.score, shapeWorst.after, type);
				}
			};
			FutureTask<Best> f = new FutureTask<>(task);
			exec.execute(f);
			futs.add(f);
		}
		
		try {
			for(Future<Best> f : futs) {
				if(f.get().score > worst.score)
					worst = f.get();
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return worst.type;
	}

	protected Best worstNext(Field originalField, Field currentField, List<ShapeType> bag, int lookahead) {
		int[] v = new int[4];
		v[CommandGraph.ORIGIN] = CommandGraph.NULL_ORIGIN;
		v[CommandGraph.SHAPE] = -1;
		v[CommandGraph.COMMAND] = CommandGraph.NULL_COMMAND;
		v[CommandGraph.PATH_LENGTH] = Integer.MAX_VALUE;
		if(lookahead == 0 || bag.size() == 0) {
			return new Best(null, v, fitness.badness(originalField, currentField, ShapeType.NONE), currentField, null);
		}
		
		currentField.clearLines();
		
		Best worst = new Best(null, v, Double.NEGATIVE_INFINITY, null, null);
		
		for(ShapeType type : new HashSet<>(bag)) {
			int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
			Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1);
			List<ShapeType> nextBag = new ArrayList<>(bag);
			nextBag.remove(type);
			Best shapeWorst = worstNext(originalField, shapeBest.after, nextBag, lookahead - 1);
			if(shapeWorst.score > worst.score)
				worst = new Best(null, shapeWorst.vertex, shapeWorst.score, shapeWorst.after, type);
		}
		
		return worst;
	}
	
	public Fitness getFitness() {
		return fitness;
	}

	public void setFitness(Fitness fitness) {
		this.fitness = fitness;
	}

	public Executor getExec() {
		return exec;
	}

	public void setExec(Executor exec) {
		this.exec = exec;
	}

}
