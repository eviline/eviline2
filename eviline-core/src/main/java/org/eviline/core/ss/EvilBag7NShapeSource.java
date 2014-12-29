package org.eviline.core.ss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactories;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;

public class EvilBag7NShapeSource implements ShapeSource, Cloneable {
	public static final EngineFactory<ShapeSource> FACTORY = EngineFactories.createSourceFactory(EvilBag7NShapeSource.class);
	
	public static EngineFactory<ShapeSource> createFactory(int n, int lookahead) {
		return new EngineFactories.ShapeSourceFactory(
				EvilBag7NShapeSource.class, 
				new Class<?>[]{int.class, int.class}, 
				new Object[]{n, lookahead});
	}
	
	public static final int DEFAULT_N = 4;
	public static final int DEFAULT_LOOKAHEAD = 2;
	
	protected AIKernel ai = new DefaultAIKernel(new NextFitness());
	protected int n;
	protected Random random = new Random();
	
	protected ShapeType forcedNext;
	
	protected List<ShapeType> bag = new ArrayList<>();
	
	protected int lookahead = 2;
	
	public EvilBag7NShapeSource() {
		this(DEFAULT_N, DEFAULT_LOOKAHEAD);
	}
	
	public EvilBag7NShapeSource(int n, int lookahead) {
		this.n = n;
		for(int i = 0; i < n; i++)
			bag.addAll(Arrays.asList(ShapeType.blocks()));
		forcedNext = bag.remove(random.nextInt(bag.size()));
		this.lookahead = lookahead;
	}
	
	@Override
	public ShapeType next(Engine engine) {
		if(forcedNext != null) {
			ShapeType ret = forcedNext;
			forcedNext = null;
			return ret;
		}
		try {
			Set<ShapeType> types = new HashSet<>();
			types.addAll(bag);
			ShapeType chosen = ai.worstNext(engine.getField(), this, engine.getNext(), lookahead);
			return bag.remove(bag.indexOf(chosen));
		} finally {
			if(bag.size() == 0)
				for(int i = 0; i < n; i++)
					bag.addAll(Arrays.asList(ShapeType.blocks()));
		}
	}
	
	@Override
	public ShapeType[] getBag() {
		return bag.toArray(new ShapeType[bag.size()]);
	}

	public List<ShapeType> getRawBag() {
		return bag;
	}
	
	public boolean remove(ShapeType type) {
		try {
			return bag.remove(type);
		} finally {
			if(bag.size() == 0)
				for(int i = 0; i < n; i++)
					bag.addAll(Arrays.asList(ShapeType.blocks()));
		}
	}
	
	public AIKernel getAi() {
		return ai;
	}
	
	public void setAi(AIKernel ai) {
		this.ai = ai;
	}

	public int getLookahead() {
		return lookahead;
	}

	public void setLookahead(int lookahead) {
		this.lookahead = lookahead;
	}

}
