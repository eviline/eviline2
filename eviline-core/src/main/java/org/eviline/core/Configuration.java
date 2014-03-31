package org.eviline.core;

import org.eviline.core.ss.BagShapeSource;

public class Configuration {
	protected EngineFactory<Integer> downFramesRemaining = new EngineFactory<Integer>() {
		@Override
		public Integer newInstance(Engine e) {
			return 60;
		}
	};
	protected EngineFactory<ShapeSource> shapes = BagShapeSource.FACTORY;
	
	
	public Integer downFramesRemaining(Engine e) {
		return downFramesRemaining.newInstance(e);
	}
	
	public ShapeSource shapes(Engine e) {
		return shapes.newInstance(e);
	}
	
}
