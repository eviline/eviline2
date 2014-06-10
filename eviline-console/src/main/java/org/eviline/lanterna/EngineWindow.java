package org.eviline.lanterna;

import org.eviline.core.Engine;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.listener.ContainerListener;

public class EngineWindow extends Window {

	protected Engine engine;
	protected Panel panel;
	
	public EngineWindow(Engine engine) {
		super("eviline2");
		
		this.engine = engine;
		
		panel = new Panel();
		panel.setLayoutManager(new BorderLayout());
		super.addComponent(panel);
		
		addComponent(new EngineComponent(engine), BorderLayout.LEFT);
	}

	@Override
	public void addComponent(Component component, LayoutParameter... layoutParameters) {
		panel.addComponent(component, layoutParameters);
	}

	@Override
	public void addContainerListener(ContainerListener cl) {
		panel.addContainerListener(cl);
	}

	@Override
	public void removeContainerListener(ContainerListener cl) {
		panel.removeContainerListener(cl);
	}

	@Override
	public void removeComponent(Component component) {
		panel.removeComponent(component);
	}

	@Override
	public void removeAllComponents() {
		panel.removeAllComponents();
	}

	
}
