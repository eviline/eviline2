package org.eviline.lanterna;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eviline.core.Engine;
import org.junit.Assume;
import org.junit.Test;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.terminal.Terminal;

public class EngineComponentTest {
	private static Engine engine;
	private static GUIScreen gui;
	private static Window w;
	
	public static void main(String... args) throws Exception {
		engine = new Engine();
		
		gui = TerminalFacade.createGUIScreen();
		
		w = new Window(EngineComponent.class.getName());
		w.addComponent(new EngineComponent(engine));

		try {
			gui.getScreen().startScreen();
		} catch(Exception e) {
			Assume.assumeNoException(e);
		}

		gui.showWindow(w);
	}
	
	@Test
	public void testComponent() throws Exception {
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(); 
		exec.schedule(new Runnable() {
			@Override
			public void run() {
				w.close();
				gui.getScreen().stopScreen();
			}
		}, 2, TimeUnit.SECONDS);
		main();
		;
	}
}
