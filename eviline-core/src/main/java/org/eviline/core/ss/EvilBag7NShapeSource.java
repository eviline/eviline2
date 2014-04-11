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

public class EvilBag7NShapeSource implements ShapeSource {
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
	
	public EvilBag7NShapeSource() {
		this(4);
	}
	
	public EvilBag7NShapeSource(int n) {
		this.n = n;
	}
	
	@Override
	public ShapeType next(Engine engine) {
		if(bag.size() == 0)
			for(int i = 0; i < n; i++)
				bag.addAll(Arrays.asList(ShapeType.values()));
		Set<ShapeType> types = new HashSet<>();
		types.addAll(bag);
		ShapeType chosen = ai.worstNext(engine.getField(), types.toArray(new ShapeType[types.size()]), new ShapeType[0]);
		return bag.remove(bag.indexOf(chosen));
	}

}
