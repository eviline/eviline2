package org.eviline.core;

import org.eviline.core.ss.Bag7NShapeSource;

public class Configuration {
	protected EngineFactory<Integer> downFramesRemaining = EngineFactories.createIntegerFactory(60);
	protected EngineFactory<Integer> respawnFramesRemaining = EngineFactories.createIntegerFactory(10);
	protected EngineFactory<ShapeSource> shapes = EngineFactories.createSourceFactory(Bag7NShapeSource.class);
	
	public Configuration() {}
	
	public Configuration(Integer downFrames, Integer respawnFrames) {
		downFramesRemaining = EngineFactories.createIntegerFactory(downFrames);
		respawnFramesRemaining = EngineFactories.createIntegerFactory(respawnFrames);
	}
	
	public Configuration(Integer downFrames, Integer respawnFrames, Class<? extends ShapeSource> shapesType) {
		downFramesRemaining = EngineFactories.createIntegerFactory(downFrames);
		respawnFramesRemaining = EngineFactories.createIntegerFactory(respawnFrames);
		shapes = EngineFactories.createSourceFactory(shapesType);
	}

	public Configuration(EngineFactory<Integer> downFrames, EngineFactory<Integer> respawnFrames, EngineFactory<ShapeSource> shapes) {
		downFramesRemaining = downFrames;
		respawnFramesRemaining = respawnFrames;
		this.shapes = shapes;
	}

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
