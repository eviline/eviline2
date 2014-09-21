package org.eviline.core;

import org.eviline.core.ss.Bag7NShapeSource;

public class Configuration {
	protected EngineFactory<Integer> downFramesRemaining = EngineFactories.createIntegerFactory(60);
	protected EngineFactory<Integer> respawnFramesRemaining = EngineFactories.createIntegerFactory(10);
	protected EngineFactory<ShapeSource> shapes = EngineFactories.createSourceFactory(Bag7NShapeSource.class);
	
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
