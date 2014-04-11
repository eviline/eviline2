package org.eviline.core.ss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public class Bag7NShapeSource implements ShapeSource {
	public static EngineFactory<ShapeSource> FACTORY = new EngineFactory<ShapeSource>() {
		@Override
		public ShapeSource newInstance(Engine e) {
			return new Bag7NShapeSource();
		}
	};
	
	protected int n;
	protected Random random = new Random();
	
	protected List<ShapeType> bag = new ArrayList<>();
	
	public Bag7NShapeSource() {
		this(1);
	}
	
	public Bag7NShapeSource(int n) {
		this.n = n;
	}
	
	@Override
	public ShapeType next(Engine engine) {
		if(bag.size() == 0)
			for(int i = 0; i < n; i++)
				bag.addAll(Arrays.asList(ShapeType.values()));
		return bag.remove(random.nextInt(bag.size()));
	}

}
