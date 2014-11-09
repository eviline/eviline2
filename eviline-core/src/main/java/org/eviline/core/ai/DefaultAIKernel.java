package org.eviline.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

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

	protected class Best {
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
		
		int best = -1;
		
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
		
		final List<Callable<Best>> tasks = new ArrayList<Callable<Best>>();
		final ExecutorCompletionService<Best> cs = new ExecutorCompletionService<Best>(exec);
		final AtomicInteger incomplete = new AtomicInteger(0);
		
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			final int shape;
			if(CommandGraph.originOf(vertices, shape = i) == CommandGraph.NULL_ORIGIN)
				continue;
			if(!field.intersects(XYShapes.shiftedDown(shape))) {
				continue;
			}

			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					try {
						Field after = field.clone();
						after.blit(shape, 0);
						Best b = bestPlacement(field, after, nextShape, nextNext, lookahead);
						Best best = new Best(g, shape, b.score, b.after, XYShapes.shapeFromInt(shape).type());
						return best;
					} finally {
						synchronized(incomplete) {
							if(incomplete.decrementAndGet() == 0) {
								incomplete.notify();
							}
						}
					}
				}
			};
			
			tasks.add(task);
		}
		
		int totalTasks = tasks.size();
		
		synchronized(incomplete) {
			incomplete.addAndGet(tasks.size());
			for(Callable<Best> task : tasks)
				cs.submit(task);
			while(incomplete.get() > 0) {
				try {
					incomplete.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		for(int i = 0; i < totalTasks; i++) {
			Future<Best> fut;
			Best b;
			try {
				fut = cs.take();
				b = fut.get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			if(b.score < badness) {
				best = b.shape;
				badness = b.score;
			}
		}
		
		g.setSelectedShape(best);
		return g;
	}
	
	protected Best bestPlacement(final Field originalField, final Field currentField, int currentShape, ShapeType[] next, final int lookahead) {
		if(currentShape != -1 && currentField.intersects(currentShape))
			return new Best(null, currentShape, Double.POSITIVE_INFINITY, currentField, null);
		
		if(currentShape == -1 || lookahead == 0) {
			return new Best(null, currentShape, fitness.badness(originalField, currentField, next), currentField, null);
		}
		
		currentField.clearLines();
		
		CommandGraph g = new CommandGraph(currentField, currentShape);
		
		Best best = new Best(null, currentShape, Double.POSITIVE_INFINITY, null, null);

		final int nextShape;
		final ShapeType[] nextNext;
		if(next.length > 0) {
			nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextShape = -1;
			nextNext = null;
		}
		
		final ExecutorCompletionService<Best> cs = new ExecutorCompletionService<Best>(exec);
		final List<Callable<Best>> tasks = new ArrayList<Callable<Best>>();
		final AtomicInteger incomplete = new AtomicInteger(0);
		
		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			if(CommandGraph.originOf(g.getVertices(), i) == CommandGraph.NULL_ORIGIN)
				continue;
			final int shape = i;
			if(!currentField.intersects(XYShapes.shiftedDown(shape)))
				continue;
			
			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					try {
						Field nextField = currentField.clone();
						nextField.blit(shape, 0);
						return bestPlacement(originalField, nextField, nextShape, nextNext, lookahead - 1);
					} finally {
						synchronized(incomplete) {
							if(incomplete.decrementAndGet() == 0) {
								incomplete.notify();
							}
						}
					}
				}
			};

			tasks.add(task);
		}
		
		int totalTasks = tasks.size();
		
		synchronized(incomplete) {
			incomplete.addAndGet(tasks.size());
			for(Callable<Best> task : tasks)
				cs.submit(task);
			while(incomplete.get() > 0) {
				try {
					incomplete.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		for(int i = 0; i < totalTasks; i++) {
			Best shapeBest;
			try {
				shapeBest = cs.take().get();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
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
		
		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null);
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
					return new Best(null, shapeWorst.shape, shapeWorst.score, shapeWorst.after, type);
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
		if(lookahead == 0 || bag.size() == 0) {
			return new Best(null, -1, fitness.badness(originalField, currentField, ShapeType.NONE), currentField, null);
		}
		
		currentField.clearLines();
		
		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null);
		
		for(ShapeType type : new HashSet<>(bag)) {
			int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
			Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1);
			List<ShapeType> nextBag = new ArrayList<>(bag);
			nextBag.remove(type);
			Best shapeWorst = worstNext(originalField, shapeBest.after, nextBag, lookahead - 1);
			if(shapeWorst.score > worst.score)
				worst = new Best(null, shapeWorst.shape, shapeWorst.score, shapeWorst.after, type);
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
