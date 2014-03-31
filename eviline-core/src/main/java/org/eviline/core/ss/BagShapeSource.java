package org.eviline.core.ss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.eviline.core.Engine;
import org.eviline.core.EngineFactory;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;

public class BagShapeSource implements ShapeSource {
	public static EngineFactory<ShapeSource> FACTORY = new EngineFactory<ShapeSource>() {
		@Override
		public ShapeSource newInstance(Engine e) {
			return new BagShapeSource();
		}
	};
	
	protected Random random = new Random();
	
	protected List<ShapeType> bag = new ArrayList<>();
	
	@Override
	public ShapeType next(Engine engine) {
		if(bag.size() == 0)
			bag.addAll(Arrays.asList(ShapeType.values()));
		return bag.remove(random.nextInt(bag.size()));
	}

}
