package org.eviline.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
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
	public Vertex bestPlacement(final Field field, XYShape current, ShapeType[] next) {
		final CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		Vertex best = null;
		
		final XYShape nextShape;
		final ShapeType[] nextNext;
		if(next.length > 0) {
			nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextShape = null;
			nextNext = null;
		}
			
		Collection<Future<Best>> futures = new ArrayList<>();
		
		for(final XYShape shape : g.getVertices().keySet()) {
			if(!field.intersects(shape.shiftedDown()))
				continue;
			FutureTask<Best> f = new FutureTask<>(new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					Field after = field.clone();
					after.blit(shape);
					Best b = bestPlacement(field, after, nextShape, nextNext);
					return new Best(g.getVertices().get(shape), b.score, b.after, shape.shape().type());
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
	
	protected Best bestPlacement(Field originalField, Field currentField, XYShape currentShape, ShapeType[] next) {
		if(next == null)
			return new Best(null, fitness.badness(originalField, currentField, next), currentField, null);
		
		currentField.clearLines();
		
		CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, Double.POSITIVE_INFINITY, null, null);

		XYShape nextShape = null;
		ShapeType[] nextNext = null;
		if(next.length > 0) {
			nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		}
		
		for(XYShape shape : g.getVertices().keySet()) {
			if(!currentField.intersects(shape.shiftedDown()))
				continue;
			Field nextField = currentField.clone();
			nextField.blit(shape);
			Best shapeBest = bestPlacement(originalField, nextField, nextShape, nextNext);
			if(shapeBest.score < best.score)
				best = shapeBest;
		}
		
		return best;
	}
	
	@Override
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next, int lookahead) {
		Field bestPlayed = field;
		if(next.length > 0) {
			XYShape nextShape = new XYShape(next[0].start(), next[0].startX(), next[0].startY());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext).after;
		}

		
		return worstNext(field, bestPlayed, shapes, lookahead).type;
	}

	protected Best worstNext(Field originalField, Field currentField, ShapeSource shapes, int lookahead) {
		if(lookahead == 0) {
			return new Best(null, fitness.badness(originalField, currentField, ShapeType.NONE), currentField, null);
		}
		
		currentField.clearLines();
		
		Best worst = new Best(null, Double.NEGATIVE_INFINITY, null, null);
		
		for(ShapeType type : ShapeType.types(shapes.getBag())) {
			XYShape currentShape = new XYShape(type.start(), type.startX(), type.startY());
			Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE);
			ShapeSource nextShapes = shapes.clone();
			nextShapes.removeFromBag(type);
			Best shapeWorst = worstNext(originalField, shapeBest.after, nextShapes, lookahead - 1);
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
