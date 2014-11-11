package org.eviline.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;

public class DefaultAIKernel implements AIKernel {
	
	public static ThreadPoolExecutor createDefaultExecutor() {
		return new ThreadPoolExecutor(
				0, 32, 
				30, TimeUnit.SECONDS, 
				new SynchronousQueue<Runnable>(), 
				new CallerRunsPolicy());
	}

	protected static Comparator<Best> WORST_ORDER = new Comparator<DefaultAIKernel.Best>() {
		@Override
		public int compare(Best o1, Best o2) {
			return -Double.compare(o1.score, o2.score);
		}
	};
	
	protected static Comparator<Best> BEST_ORDER = new Comparator<DefaultAIKernel.Best>() {
		@Override
		public int compare(Best o1, Best o2) {
			return Double.compare(o1.score, o2.score);
		}
	};
	
	protected static class Best {
		public final CommandGraph graph;
		public final int shape;
		public final double score;
		public final Field after;
		public final ShapeType type;
		
		public Best(CommandGraph graph, int shape, double score, Field after, ShapeType type) {
			this.graph = graph;
			this.shape = shape;
			this.score = score;
			this.after = after;
			this.type = type;
		}
	}
	
	protected Fitness fitness = new DefaultFitness();
	protected Executor exec = createDefaultExecutor();
	
	public DefaultAIKernel() {}
	
	public DefaultAIKernel(Fitness fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public CommandGraph bestPlacement(final Field field, int current, ShapeType[] next, final int lookahead) {
		final CommandGraph g = new CommandGraph(field, current);
		double badness = Double.POSITIVE_INFINITY;
		
		Best best = new Best(null, current, Double.POSITIVE_INFINITY, field, null);
		
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
			
		int[] vertices = g.getVertices();
		
		Collection<Future<Best>> futs = new ArrayList<Future<Best>>();
		
		Set<Integer> blitted = new HashSet<>();
		
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			final int shape;
			if(CommandGraph.originOf(vertices, shape = i) == CommandGraph.NULL_ORIGIN)
				continue;
			if(!field.intersects(XYShapes.shiftedDown(shape))) {
				continue;
			}
			
			if(!blitted.add(XYShapes.canonical(shape)))
				continue;
			
			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
						Field after = field.clone();
						after.blit(shape, 0);
						Best b = bestPlacement(field, after, nextShape, nextNext, lookahead - 1);
						Best best = new Best(g, shape, b.score, b.after, XYShapes.shapeFromInt(shape).type());
						return best;
				}
			};
			
			FutureTask<Best> fut = new FutureTask<Best>(task);
			exec.execute(fut);
			futs.add(fut);
		}
		
		for(Future<Best> fut : futs) {
			Best b;
			try {
				b = fut.get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(BEST_ORDER.compare(b, best) < 0)
				best = b;
		}
		
		g.setSelectedShape(best.shape);
		return g;
	}
	
	protected Best bestPlacement(final Field originalField, final Field currentField, int currentShape, ShapeType[] next, final int lookahead) {
		if(currentShape != -1 && currentField.intersects(currentShape))
			return new Best(null, currentShape, Double.POSITIVE_INFINITY, currentField, null);
		
		if(currentShape == -1 || lookahead == 0) {
			return new Best(null, currentShape, fitness.badness(originalField, currentField, next), currentField, null);
		}
		
		currentField.clearLines();
		
		final CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, currentShape, Double.POSITIVE_INFINITY, currentField, null);

		final int nextShape;
		final ShapeType[] nextNext;
		if(next.length > 0) {
			nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextShape = -1;
			nextNext = null;
		}
		
		Collection<Future<Best>> futs = new ArrayList<Future<Best>>();
		
		Set<Integer> blitted = new HashSet<>();
		
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			if(CommandGraph.originOf(g.getVertices(), i) == CommandGraph.NULL_ORIGIN)
				continue;
			final int shape = i;
			if(!currentField.intersects(XYShapes.shiftedDown(shape)))
				continue;
			
			if(!blitted.add(XYShapes.canonical(shape)))
				continue;
			
			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					Field nextField = currentField.clone();
					nextField.blit(shape, 0);
					Best nextBest = bestPlacement(originalField, nextField, nextShape, nextNext, lookahead - 1);
					return new Best(g, shape, nextBest.score, nextBest.after, XYShapes.shapeFromInt(shape).type());
				}
			};
			
			FutureTask<Best> fut = new FutureTask<Best>(task);
			exec.execute(fut);
			futs.add(fut);
		}
		
		for(Future<Best> fut : futs) {
			Best shapeBest;
			try {
				shapeBest = fut.get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(BEST_ORDER.compare(shapeBest, best) < 0)
				best = shapeBest;
		}

		return best;
	}
	
	@Override
	public ShapeType bestNext(Field field, ShapeSource shapes,
			ShapeType[] next, int lookahead) {
		return searchNext(BEST_ORDER, field, shapes, next, lookahead);
	}
	
	@Override
	public ShapeType worstNext(Field field, ShapeSource shapes, ShapeType[] next, int lookahead) {
		return searchNext(WORST_ORDER, field, shapes, next, lookahead);
	}
	
	protected ShapeType searchNext(
			final Comparator<Best> order,
			final Field field, 
			final ShapeSource shapes, 
			ShapeType[] next, 
			final int lookahead) {
		Field bestPlayed = field;
		if(next.length > 0) {
			int nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext, nextNext.length).after;
		}
		
		final Field fbestPlayed = bestPlayed;

		bestPlayed.clearLines();
		
		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null);
		Collection<Future<Best>> futs = new ArrayList<>();
		
		final List<ShapeType> bag = Arrays.asList(shapes.getBag());
		
		for(final ShapeType type : new HashSet<>(Arrays.asList(shapes.getBag()))) {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					return searchNext(order, field, fbestPlayed, bag, lookahead, type);
				}
			};
			FutureTask<Best> f = new FutureTask<>(task);
			exec.execute(f);
			futs.add(f);
		}
		
		try {
			for(Future<Best> f : futs) {
				if(order.compare(f.get(), worst) < 0)
					worst = f.get();
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return worst.type;
	}

	protected Best searchNext(
			final Comparator<Best> order,
			final Field originalField, 
			final Field currentField, 
			List<ShapeType> bag, 
			final int lookahead, 
			ShapeType type) {
		if(lookahead == 0 || bag.size() == 0) {
			return new Best(null, -1, fitness.badness(originalField, currentField, new ShapeType[] {type}), currentField, type);
		}
		
		currentField.clearLines();
		
		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null);
		
		int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
		final Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1);
		final List<ShapeType> nextBag = new ArrayList<>(bag);
		nextBag.remove(type);
		
		Collection<Future<Best>> futs = new ArrayList<Future<Best>>();
		
		if(nextBag.size() > 0) {
			for(final ShapeType next : EnumSet.copyOf(nextBag)) {
				Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
					@Override
					public Best call() throws Exception {
						return searchNext(order, originalField, shapeBest.after, nextBag, lookahead - 1, next);
					}
				};
				FutureTask<Best> fut = new FutureTask<Best>(task);
				if(lookahead - 1 > 0)
					exec.execute(fut);
				else
					fut.run();
				futs.add(fut);
			}
		} else {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					return searchNext(order, originalField, shapeBest.after, nextBag, lookahead - 1, null);
				}
			};
			FutureTask<Best> fut = new FutureTask<Best>(task);
			fut.run();
			futs.add(fut);
		}
		
		for(Future<Best> fut : futs) {
			Best shapeWorst;
			try {
				shapeWorst = fut.get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(order.compare(shapeWorst, worst) < 0)
				worst = shapeWorst;
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
