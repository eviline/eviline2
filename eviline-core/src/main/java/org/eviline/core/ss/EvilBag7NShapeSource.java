package org.eviline.core.ss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.DefaultAIKernel;

import com.rits.cloning.Cloner;

public class EvilBag7NShapeSource implements ShapeSource, Cloneable {
	public static EngineFactory<ShapeSource> FACTORY = new EngineFactory<ShapeSource>() {
		@Override
		public ShapeSource newInstance(Engine e) {
			return new EvilBag7NShapeSource();
		}
	};
	
	protected AIKernel ai = new DefaultAIKernel();
	protected int n;
	protected Random random = new Random();
	
	protected List<ShapeType> bag = new ArrayList<>();
	
	protected int lookahead = 2;
	
	public EvilBag7NShapeSource() {
		this(4);
	}
	
	public EvilBag7NShapeSource(int n) {
		this.n = n;
		for(int i = 0; i < n; i++)
			bag.addAll(Arrays.asList(ShapeType.blocks()));
	}
	
	@Override
	public ShapeType next(Engine engine) {
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
	public ShapeSource clone() {
		try {
			EvilBag7NShapeSource ss = (EvilBag7NShapeSource) super.clone();
			Cloner c = new Cloner();
			ss.ai = c.deepClone(ai);
			ss.random = c.deepClone(random);
			ss.bag = c.deepClone(ss.bag);
			return ss;
		} catch(CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public ShapeType[] getBag() {
		return bag.toArray(new ShapeType[bag.size()]);
	}

	@Override
	public void removeFromBag(ShapeType type) {
		bag.remove(type);
		if(bag.size() == 0)
			for(int i = 0; i < n; i++)
				bag.addAll(Arrays.asList(ShapeType.blocks()));
	}

	public int getLookahead() {
		return lookahead;
	}

	public void setLookahead(int lookahead) {
		this.lookahead = lookahead;
	}

}
