package org.eviline.core;

import org.eviline.core.ss.Bag7NShapeSource;

public class Configuration {
	protected EngineFactory<Integer> downFramesRemaining = new EngineFactory<Integer>() {
		@Override
		public Integer newInstance(Engine e) {
			return 60;
		}
	};
	protected EngineFactory<Integer> respawnFramesRemaining = new EngineFactory<Integer>() {
		@Override
		public Integer newInstance(Engine e) {
			return 10;
		}
	};
	protected EngineFactory<ShapeSource> shapes = Bag7NShapeSource.FACTORY;
	
	public Integer respawnFramesRemaining(Engine e) {
		return respawnFramesRemaining.newInstance(e);
	}
	
	public Integer downFramesRemaining(Engine e) {
		return downFramesRemaining.newInstance(e);
	}
	
	public ShapeSource shapes(Engine e) {
		return shapes.newInstance(e);
	}
	
}
