package org.eviline.lanterna;

import java.lang.reflect.Field;

import org.eviline.core.Engine;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.listener.ContainerListener;

public class EngineWindow extends BorderLayoutWindow {

	protected Engine engine;
	
	protected EngineComponent engineComponent;
	
	public EngineWindow(Engine engine) {
		super("eviline2");
		
		this.engine = engine;
		
		addComponent(engineComponent = new EngineComponent(engine), BorderLayout.LEFT);
		
	}

	public EngineComponent getEngineComponent() {
		return engineComponent;
	}
	
}
