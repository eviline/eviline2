package org.eviline.core.ss;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactories;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public class Bag7NShapeSource implements ShapeSource, Cloneable {
	public static final EngineFactory<ShapeSource> FACTORY = EngineFactories.createSourceFactory(Bag7NShapeSource.class);
	
	protected int n;
	protected SecureRandom random = new SecureRandom();
	
	protected List<ShapeType> bag = new ArrayList<>();
	
	public Bag7NShapeSource() {
		this(1);
	}
	
	public Bag7NShapeSource(int n) {
		this.n = n;
		for(int i = 0; i < n; i++)
			bag.addAll(Arrays.asList(ShapeType.blocks()));
	}
	
	@Override
	public ShapeType next(Engine engine) {
		try {
			return bag.remove(random.nextInt(bag.size()));
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
	
	public boolean remove(ShapeType type) {
		try {
			return bag.remove(type);
		} finally {
			if(bag.size() == 0)
				for(int i = 0; i < n; i++)
					bag.addAll(Arrays.asList(ShapeType.blocks()));
		}
	}
}
