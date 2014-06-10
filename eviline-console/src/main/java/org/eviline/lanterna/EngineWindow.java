package org.eviline.lanterna;

import java.lang.reflect.Field;

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
	
	protected EngineComponent engineComponent;
	
	private Field contentPane;
	
	public EngineWindow(Engine engine) {
		super("eviline2");
		
		this.engine = engine;
		
		panel = new Panel();
		panel.setLayoutManager(new BorderLayout());
		super.addComponent(panel);
		
		addComponent(engineComponent = new EngineComponent(engine), BorderLayout.LEFT);
		
		try {
			contentPane = Window.class.getDeclaredField("contentPane");
			contentPane.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
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

	public Panel getContentPane() {
		try {
			return (Panel) contentPane.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public EngineComponent getEngineComponent() {
		return engineComponent;
	}
	
}
