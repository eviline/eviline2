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
import org.eviline.core.ai.CommandGraph.Vertex;

public class DefaultAIKernel implements AIKernel {

	protected class Best {
		public final Vertex vertex;
		public final double score;
		public final Field after;
		public final ShapeType type;
		
		public Best(Vertex vertex, double score, Field after, ShapeType type) {
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
	public Vertex bestPlacement(final Field field, int current, ShapeType[] next, final int lookahead) {
		final CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		Vertex best = null;
		
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
			
		Collection<Future<Best>> futures = new ArrayList<>();
		
//		for(final int shape : g.getVertices().keySet()) {
		for(int i = 0; i < g.getVertices().length; i++) {
			final Vertex v;
			if((v = g.getVertices()[i]) == null)
				continue;
			final int shape = i;
			if(!field.intersects(XYShapes.shiftedDown(shape)))
				continue;
			FutureTask<Best> f = new FutureTask<>(new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					Field after = field.clone();
					after.blit(shape, 0);
					Best b = bestPlacement(field, after, nextShape, nextNext, lookahead);
					return new Best(v, b.score, b.after, XYShapes.shapeFromInt(shape).type());
				}
			});
			futures.add(f);
			exec.execute(f);
		}
		
		for(Future<Best> f : futures) {
			Best b;
			try {
				b = f.get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(b.score < badness) {
				best = b.vertex;
				badness = b.score;
			}
		}
		
		return best;
	}
	
	protected Best bestPlacement(Field originalField, Field currentField, int currentShape, ShapeType[] next, int lookahead) {
		if(currentShape == -1 || lookahead == 0)
			return new Best(null, fitness.badness(originalField, currentField, next), currentField, null);
		
		currentField.clearLines();
		
		CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, Double.POSITIVE_INFINITY, null, null);

		int nextShape = -1;
		ShapeType[] nextNext = null;
		if(next.length > 0) {
			nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		}
		
//		for(int shape : g.getVertices().keySet()) {
		for(int i = 0; i < g.getVertices().length; i++) {
			if(g.getVertices()[i] == null)
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
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next, int lookahead) {
		Field bestPlayed = field;
		if(next.length > 0) {
			int nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext, nextNext.length).after;
		}

		
		return worstNext(field, bestPlayed, Arrays.asList(shapes.getBag()), lookahead).type;
	}

	protected Best worstNext(Field originalField, Field currentField, List<ShapeType> bag, int lookahead) {
		if(lookahead == 0 || bag.size() == 0) {
			return new Best(null, fitness.badness(originalField, currentField, ShapeType.NONE), currentField, null);
		}
		
		currentField.clearLines();
		
		Best worst = new Best(null, Double.NEGATIVE_INFINITY, null, null);
		
		for(ShapeType type : new HashSet<>(bag)) {
			int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
			Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1);
			List<ShapeType> nextBag = new ArrayList<>(bag);
			nextBag.remove(type);
			Best shapeWorst = worstNext(originalField, shapeBest.after, nextBag, lookahead - 1);
			if(shapeWorst.score > worst.score)
				worst = new Best(null, shapeWorst.score, shapeWorst.after, type);
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
