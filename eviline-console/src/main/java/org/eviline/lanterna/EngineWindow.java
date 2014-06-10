package org.eviline.lanterna;

import org.eviline.core.Engine;

import com.googlecode.lanterna.gui.Window;

public class EngineWindow extends Window {

	protected Engine engine;
	
	public EngineWindow(Engine engine) {
		super("eviline2");
		
		this.engine = engine;
		
		addComponent(new EngineComponent(engine));
	}

	
}
