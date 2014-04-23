package org.eviline.core.ss;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

import com.rits.cloning.Cloner;

public class Bag7NShapeSource implements ShapeSource, Cloneable {
	public static EngineFactory<ShapeSource> FACTORY = new EngineFactory<ShapeSource>() {
		@Override
		public ShapeSource newInstance(Engine e) {
			return new Bag7NShapeSource();
		}
	};
	
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
	public ShapeSource clone() {
		try {
			Bag7NShapeSource ss = (Bag7NShapeSource) super.clone();
			Cloner c = new Cloner();
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
	
}
