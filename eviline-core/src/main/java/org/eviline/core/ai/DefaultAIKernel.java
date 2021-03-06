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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShapes;
import org.eviline.core.conc.ActiveCompletor;
import org.eviline.core.conc.ConstantFuture;
import org.eviline.core.conc.QuietCallable;
import org.eviline.core.conc.SubtaskExecutor;

public class DefaultAIKernel implements AIKernel {

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
			if(after != null)
				this.after = after.clone();
			else
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

	public static interface BestAdjuster {
		public Best adjust(Best best);
	}
	
	public static class IdentityBestAdjuster implements BestAdjuster {
		private static final IdentityBestAdjuster instance = new IdentityBestAdjuster();
		
		public static IdentityBestAdjuster get() {
			return instance;
		}
		
		private IdentityBestAdjuster() {}
		
		@Override
		public Best adjust(Best best) {
			return best;
		}
		
	}
	
	protected Fitness fitness;
	protected ActiveCompletor exec;

	protected boolean dropsOnly;

	protected int pruneTop = Integer.MAX_VALUE;
	
	protected BestAdjuster adjuster = IdentityBestAdjuster.get();

	public DefaultAIKernel() {
		this(new DefaultFitness());
	}
	
	public DefaultAIKernel(Fitness fitness) {
		this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), fitness);
	}
	
	public DefaultAIKernel(Executor exec, Fitness fitness) {
		this(new ActiveCompletor(exec), fitness);
	}
	
	public DefaultAIKernel(ActiveCompletor exec, Fitness fitness) {
		this.exec = exec;
		this.fitness = fitness;
	}

	protected int starter(Field field, ShapeType type) {
		return XYShapes.toXYShape(type.startX(), type.startY(), type.start());
	}

	protected int starter(Field field, ShapeType[] next, int lookahead) {
		if(lookahead <= 0 || next.length == 0)
			return -1;
		return starter(field, next[0]);
	}
	
	protected CommandGraph graph(Field field, int start, boolean dropsOnly) {
		return new CommandGraph(field, start, dropsOnly);
	}
	
	@Override
	public CommandGraph bestPlacement(final Field field, int current, final ShapeType[] next, final int lookahead) {
		final CommandGraph g = graph(field, current, dropsOnly);

		Best best = new Best(null, current, Double.POSITIVE_INFINITY, field, null, null);

		final ShapeType[] nextNext;
		if(lookahead == 0) {
			nextNext = Arrays.copyOf(next, next.length);
		} else if(next.length > 0) {
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextNext = null;
		}

		int[] vertices = g.getVertices();

		Collection<FutureTask<Best>> futs = new ArrayList<>();

		Set<Integer> blitted = new HashSet<>();

		Map<Double, QuietCallable<Best>> tasks = new TreeMap<Double, QuietCallable<Best>>();

		int start = XYShapes.startIntForTypeId(XYShapes.shapeTypeIdFromInt(current));
		int stop = XYShapes.stopIntForTypeId(XYShapes.shapeTypeIdFromInt(current));
		
		for(int i = start; i < stop; i++) {
			if(Thread.interrupted())
				throw new RuntimeException(new InterruptedException());
			final int shape;
			if(CommandGraph.originOf(vertices, shape = i) == CommandGraph.NULL_ORIGIN)
				continue;
			if(!field.intersects(XYShapes.shiftedDown(shape))) {
				continue;
			}
			if(XYShapes.yFromInt(shape) <= XYShapes.yFromInt(current))
				continue;

			if(!blitted.add(XYShapes.canonical(shape)))
				continue;

			final Field after = field.clone();
			after.blit(shape, 0);
			after.clearLines();

			QuietCallable<Best> task = new QuietCallable<Best>() {
				@Override
				public Best call() {
					int nextShape = starter(after, next, lookahead);
					Best b = bestPlacement(field, after, nextShape, nextNext, lookahead, 1);
					Best best = new Best(g, shape, b.score, after, XYShapes.shapeFromInt(shape).type(), b);
					return best;
				}
			};

			double score = fitness.badness(field, after, nextNext);
			if(after.isSpawnEndangered())
				score = Double.POSITIVE_INFINITY;
			tasks.put(score, task);
		}

		for(QuietCallable<Best> task : tasks.values()) {
			if(lookahead > 1)
				futs.add(exec.submit(task));
			else
				futs.add(new FutureTask<>(task));
			if(futs.size() >= pruneTop)
				break;
		}
		
		exec.await(futs);

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

	public Best bestPlacement(final Field originalField, final Field currentField, int currentShape, final ShapeType[] next, final int lookahead, final int depth) {
		if(Thread.interrupted())
			throw new RuntimeException(new InterruptedException());
		
		if(currentShape != -1 && currentField.intersects(currentShape))
			return adjuster.adjust(new Best(graph(currentField, currentShape, dropsOnly), currentShape, Double.POSITIVE_INFINITY, currentField, null, null));

		if(currentShape == -1 || lookahead <= 0) {
			if(currentField.isSpawnEndangered())
				return adjuster.adjust(new Best(graph(currentField, currentShape, dropsOnly), currentShape, Double.POSITIVE_INFINITY, currentField, null, null));
			return adjuster.adjust(new Best(null, currentShape, fitness.badness(originalField, currentField, next), currentField, null, null));
		}

		final CommandGraph g = graph(currentField, currentShape, dropsOnly);
		Best best = new Best(g, currentShape, Double.POSITIVE_INFINITY, currentField, null, null);

		final ShapeType[] nextNext;
		if(next.length > 0) {
			nextNext = Arrays.copyOfRange(next, 1, next.length);
		} else {
			nextNext = null;
		}

		Collection<FutureTask<Best>> futs = new ArrayList<>();

		Set<Integer> blitted = new HashSet<>();

		Map<Double, QuietCallable<Best>> tasks = new TreeMap<Double, QuietCallable<Best>>();

		int start = XYShapes.startIntForTypeId(XYShapes.shapeTypeIdFromInt(currentShape));
		int stop = XYShapes.stopIntForTypeId(XYShapes.shapeTypeIdFromInt(currentShape));

		for(int i = start; i < stop; i++) {
			if(Thread.interrupted())
				throw new RuntimeException(new InterruptedException());
			if(CommandGraph.originOf(g.getVertices(), i) == CommandGraph.NULL_ORIGIN)
				continue;
			
			final int shape = i;
			if(!currentField.intersects(XYShapes.shiftedDown(shape)))
				continue;

			if(!blitted.add(XYShapes.canonical(shape)))
				continue;

			if(XYShapes.yFromInt(shape) <= XYShapes.yFromInt(currentShape))
				continue;

			final Field nextField = currentField.clone();
			nextField.blit(shape, 0);
			nextField.clearLines();

			QuietCallable<Best> task = new QuietCallable<Best>() {
				@Override
				public Best call() {
					int nextShape = starter(nextField, next, lookahead);
					Best nextBest = bestPlacement(originalField, nextField, nextShape, nextNext, lookahead - 1, depth + 1);
					return new Best(g, shape, nextBest.score, nextField, XYShapes.shapeFromInt(shape).type(), nextBest);
				}
			};

			double score = fitness.badness(originalField, nextField, nextNext);
			if(nextField.isSpawnEndangered())
				score = Double.POSITIVE_INFINITY;
			tasks.put(score, task);
		}

		for(QuietCallable<Best> task : tasks.values()) {
			if(lookahead > 1)
				futs.add(exec.submit(task));
			else
				futs.add(new FutureTask<>(task));
			if(futs.size() >= pruneTop - depth)
				break;
		}

		exec.await(futs);
		
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
			int nextShape = starter(field, next[0]);
			ShapeType[] nextNext = Arrays.copyOfRange(next, 1, next.length);
			bestPlayed = bestPlacement(field, field, nextShape, nextNext, nextNext.length, 0).after;
		}

		final Field fbestPlayed = bestPlayed;

		bestPlayed.clearLines();

		Best worst1 = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null, null);
		Best worst2 = new Best(null, -1, Double.POSITIVE_INFINITY, null, null, null);
		
		Best worst;
		if(order.compare(worst1, worst2) > 0)
			worst = worst1;
		else
			worst = worst2;
		
		Collection<FutureTask<Best>> futs = new ArrayList<>();

		List<ShapeType> bag = Arrays.asList(shapes.getBag());

		for(final ShapeType type : new HashSet<>(Arrays.asList(shapes.getBag()))) {
			final List<ShapeType> typeBag = new ArrayList<>(bag);
			typeBag.remove(type);
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					Best worst = searchNext(order, field, fbestPlayed, typeBag, lookahead, type);
					return new Best(null, starter(fbestPlayed, type), worst.score, worst.after, type, worst);
				}
			};
			futs.add(exec.submit(task));
		}

		exec.await(futs);
		
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
		if(lookahead <= 0 || bag.size() == 0) {
			return adjuster.adjust(new Best(null, -1, fitness.badness(originalField, currentField, new ShapeType[] {type}), currentField, type, null));
		}

		currentField.clearLines();

		Best worst1 = new Best(null, -1, Double.NEGATIVE_INFINITY, null, null, null);
		Best worst2 = new Best(null, -1, Double.POSITIVE_INFINITY, null, null, null);
		
		Best worst;
		if(order.compare(worst1, worst2) > 0)
			worst = worst1;
		else
			worst = worst2;
		
		int currentShape = starter(currentField, type);
		final Best shapeBest = bestPlacement(originalField, currentField, currentShape, ShapeType.NONE, 1, 1);
		final List<ShapeType> nextBag = new ArrayList<>(bag);
		nextBag.remove(type);

		Collection<FutureTask<Best>> futs = new ArrayList<>();

		if(nextBag.size() > 0) {
			for(final ShapeType next : EnumSet.copyOf(nextBag)) {
				Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
					@Override
					public Best call() throws Exception {
						Best worst = searchNext(order, originalField, shapeBest.after, nextBag, lookahead - 1, next);
						return new Best(null, starter(shapeBest.after, next), worst.score, worst.after, next, worst);
					}
				};
				futs.add(exec.submit(task));
			}
		} else {
			Callable<Best> task = new Callable<DefaultAIKernel.Best>() {
				@Override
				public Best call() throws Exception {
					Best worst = searchNext(order, originalField, shapeBest.after, nextBag, lookahead - 1, null);
					return new Best(null, -1, worst.score, worst.after, null, worst);
				}
			};
			futs.add(exec.submit(task));
		}
		
		exec.await(futs);

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

//	public ExecutorService getExec() {
//		return exec;
//	}
//
//	public void setExec(ExecutorService exec) {
//		this.exec = exec;
//	}

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
	
	public BestAdjuster getAdjuster() {
		return adjuster;
	}
	
	public void setAdjuster(BestAdjuster adjuster) {
		this.adjuster = adjuster;
	}

	public ActiveCompletor getExec() {
		return exec;
	}

	public void setExec(ActiveCompletor exec) {
		this.exec = exec;
	}
}
