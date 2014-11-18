package org.eviline.core.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;
import org.eviline.core.conc.SubtaskExecutor;

public class DefaultAIKernel implements AIKernel {

	public static ThreadPoolExecutor createDefaultExecutor() {
		return createDefaultExecutor(Runtime.getRuntime().availableProcessors());
	}

	public static ThreadPoolExecutor createDefaultExecutor(int size) {
		Executors.newFixedThreadPool(1);
		return new ThreadPoolExecutor(
				size, size, 
				30, TimeUnit.SECONDS, 
				new LinkedBlockingQueue<Runnable>());
	}

	public static Comparator<Best> WORST_ORDER = new Comparator<DefaultAIKernel.Best>() {
		@Override
		public int compare(Best o1, Best o2) {
			return -Double.compare(o1.score, o2.score);
		}
	};

	public static Comparator<Best> BEST_ORDER = new Comparator<DefaultAIKernel.Best>() {
		@Override
		public int compare(Best o1, Best o2) {
			return Double.compare(o1.score, o2.score);
		}
	};

	public static class Best {
		public final CommandGraph graph;
		public final int shape;
		public final double score;
		public final Field after;
		public final ShapeType type;
		public final Best deeper;

		public Best(CommandGraph graph, int shape, double score, Field after, ShapeType type, Best deeper) {
			this.graph = graph;
			this.shape = shape;
			this.score = score;
			if(after != null) {
				this.after = after.clone();
				this.after.clearLines();
			} else
				this.after = null;
			this.type = type;
			this.deeper = deeper;
		}

		public Best deepest() {
			Best b = this;
			while(b.deeper != null)
				b = b.deeper;
			return b;
		}
	}

	protected Fitness fitness = new DefaultFitness();
	protected SubtaskExecutor exec = new SubtaskExecutor(createDefaultExecutor());

	protected boolean dropsOnly;

	protected int pruneTop = Integer.MAX_VALUE;

	public DefaultAIKernel() {}

	public DefaultAIKernel(Fitness fitness) {
		this.fitness = fitness;
	}

	@Override
	public CommandGraph bestPlacement(final Field field, int current, ShapeType[] next, final int lookahead) {
		final CommandGraph g = new CommandGraph(field, current, dropsOnly);

		Best best = new Best(null, current, Double.POSITIVE_INFINITY, field, null, null);

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

		Map<Double, Callable<Best>> tasks = new TreeMap<Double, Callable<Best>>();

		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			if(Thread.interrupted())
				throw new RuntimeException(new InterruptedException());
			final int shape;
			if(CommandGraph.originOf(vertices, shape = i) == CommandGraph.NULL_ORIGIN)
				continue;
			if(!field.intersects(XYShapes.shiftedDown(shape))) {
				continue;
			}

			if(!blitted.add(XYShapes.canonical(shape)))
				continue;

			final Field after = field.clone();
			after.blit(shape, 0);

			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					Best b = bestPlacement(field, after, nextShape, nextNext, lookahead, 1);
					Best best = new Best(g, shape, b.score, after, XYShapes.shapeFromInt(shape).type(), b);
					return best;
				}
			};

			double score = fitness.badness(field, after, nextNext);
			tasks.put(score, task);
		}

		for(Callable<Best> task : tasks.values()) {
			futs.add(exec.submit(task));
			if(futs.size() >= pruneTop )
				break;
		}

		for(Future<Best> fut : futs) {
			Best b;
			try {
				b = fut.get();
			} catch(Exception e) {
				for(Future<Best> f : futs)
					f.cancel(true);
				throw new RuntimeException(e);
			}
			if(BEST_ORDER.compare(b, best) < 0)
				best = b;
		}

		g.setSelectedShape(best.shape);
		return g;
	}

	public Best bestPlacement(final Field originalField, final Field currentField, int currentShape, ShapeType[] next, final int lookahead, final int depth) {

		if(currentShape != -1 && currentField.intersects(currentShape))
			return new Best(new CommandGraph(currentField, currentShape, dropsOnly), currentShape, Double.POSITIVE_INFINITY, currentField, null, null);

		if(currentShape == -1 || lookahead <= 0) {
			return new Best(null, currentShape, fitness.badness(originalField, currentField, next), currentField, null, null);
		}

		currentField.clearLines();

		final CommandGraph g = new CommandGraph(currentField, currentShape, dropsOnly);
		Best best = new Best(g, currentShape, Double.POSITIVE_INFINITY, currentField, null, null);

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

		Map<Double, Callable<Best>> tasks = new TreeMap<Double, Callable<Best>>();

		for(int i = 0; i < XYShapes.SHAPE_MAX; i++) {
			if(Thread.interrupted())
				throw new RuntimeException(new InterruptedException());
			if(CommandGraph.originOf(g.getVertices(), i) == CommandGraph.NULL_ORIGIN)
				continue;
			final int shape = i;
			if(!currentField.intersects(XYShapes.shiftedDown(shape)))
				continue;

			if(!blitted.add(XYShapes.canonical(shape)))
				continue;

			final Field nextField = currentField.clone();
			nextField.blit(shape, 0);

			Callable<Best> task = new Callable<Best>() {
				@Override
				public Best call() throws Exception {
					Best nextBest = bestPlacement(originalField, nextField, nextShape, nextNext, lookahead - 1, depth + 1);
					return new Best(g, shape, nextBest.score, nextField, XYShapes.shapeFromInt(shape).type(), nextBest);
				}
			};

			double score = fitness.badness(originalField, nextField, nextNext);
			tasks.put(score, task);
		}

		for(Callable<Best> task : tasks.values()) {
			futs.add(exec.submit(task));
			if(futs.size() >= pruneTop - depth || lookahead <= 1)
				break;
		}

		for(Future<Best> fut : futs) {
			Best shapeBest;
			try {
				shapeBest = fut.get();
			} catch(Exception e) {
				for(Future<Best> f : futs)
					f.cancel(true);
				throw new RuntimeException(e);
			}
			if(BEST_ORDER.compare(shapeBest, best) < 0)
				best = shapeBest;
		}

		best.graph.setSelectedShape(best.shape);

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

	public ShapeType searchNext(
			final Comparator<Best> order,
			final Field field, 
			final ShapeSource shapes, 
			ShapeType[] next, 
			final int lookahead) {
		Field bestPlayed = field;
		if(next.length > 0) {
			int nextShape = XYShapes.toXYShape(next[0].startX(), next[0].startY(), next[0].start());
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext, nextNext.length, 0).after;
		}

		final Field fbestPlayed = bestPlayed;

		bestPlayed.clearLines();

		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null, null);
		Collection<Future<Best>> futs = new ArrayList<>();

		final List<ShapeType> bag = Arrays.asList(shapes.getBag());

		for(final ShapeType type : new HashSet<>(Arrays.asList(shapes.getBag()))) {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					return searchNext(order, field, fbestPlayed, bag, lookahead, type);
				}
			};
			futs.add(exec.submit(task));
		}

		try {
			for(Future<Best> fut : futs) {
				if(order.compare(fut.get(), worst) < 0)
					worst = fut.get();
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return worst.type;
	}

	public Best searchNext(
			final Comparator<Best> order,
			final Field originalField, 
			final Field currentField, 
			List<ShapeType> bag, 
			final int lookahead, 
			ShapeType type) {
		if(lookahead == 0 || bag.size() == 0) {
			return new Best(null, -1, fitness.badness(originalField, currentField, new ShapeType[] {type}), currentField, type, null);
		}

		currentField.clearLines();

		Best worst = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null, null);

		int currentShape = XYShapes.toXYShape(type.startX(), type.startY(), type.start());
		final Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1, 0);
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
				futs.add(exec.submit(task));
			}
		} else {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					return searchNext(order, originalField, shapeBest.after, nextBag, lookahead - 1, null);
				}
			};
			futs.add(exec.submit(task));
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

	public SubtaskExecutor getExec() {
		return exec;
	}

	public void setExec(SubtaskExecutor exec) {
		this.exec = exec;
	}

	public boolean isDropsOnly() {
		return dropsOnly;
	}

	public void setDropsOnly(boolean dropsOnly) {
		this.dropsOnly = dropsOnly;
	}

	public int getPruneTop() {
		return pruneTop;
	}

	public void setPruneTop(int pruneTop) {
		this.pruneTop = pruneTop;
	}
}
