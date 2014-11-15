package org.eviline.lanterna;

import org.eviline.core.Engine;

import com.googlecode.lanterna.gui.layout.BorderLayout;

public class EngineWindow extends BorderLayoutWindow {

	protected EngineComponent engineComponent;
	
	public EngineWindow(Engine engine) {
		super("eviline2");
		
		addComponent(engineComponent = new EngineComponent(engine), BorderLayout.LEFT);
		
	}

	public EngineComponent getEngineComponent() {
		return engineComponent;
	}
	
	public Engine getEngine() {
		return getEngineComponent().getEngine();
	}
	
	public void setEngine(Engine engine) {
		getEngineComponent().setEngine(engine);
	}
	
}
