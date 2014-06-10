package org.eviline.lanterna.main;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ai.Player;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.lanterna.EngineComponent;
import org.eviline.lanterna.EngineWindow;
import org.eviline.lanterna.LanternaPlayer;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

public class AutoplayMain {
	private static Engine engine;
	private static GUIScreen gui;
	private static EngineWindow w;
	private static Player player;

	public static void main(String... args) throws Exception {
		Field field = new Field();
		
		engine = new Engine(field, new Configuration() {
			@Override
			public Integer downFramesRemaining(Engine e) {
				return null;
			}
			@Override
			public Integer respawnFramesRemaining(Engine e) {
				return 0;
			}
		});

		gui = TerminalFacade.createGUIScreen();

		final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

		w = new EngineWindow(engine);
		Panel p = new Panel(Orientation.VERTICAL);
		p.addComponent(new Label("Press Q to quit"));
		p.addComponent(new Label("Press R to reset"));
		w.addComponent(p, BorderLayout.RIGHT);

		w.addWindowListener(new WindowAdapter() {
			@Override
			public void onUnhandledKeyboardInteraction(Window window, Key key) {
				switch(key.getCharacter()) {
				case 'q':
					w.close();
					gui.getScreen().stopScreen();
					exec.shutdown();
					break;
				case 'r':
					engine.reset();
					break;
				}
					
			}
		});
		
		DefaultAIKernel ai = new DefaultAIKernel();
		ai.setFitness(new NextFitness());
		ai.setExec(Executors.newCachedThreadPool());
		player = new AIPlayer(ai, engine, 1);
		
		Runnable ticker = new Runnable() {
			@Override
			public void run() {
				final Command c = player.tick();
				gui.runInEventThread(new Action() {
					@Override
					public void doAction() {
						if(!engine.isOver())
							engine.tick(c);
						if(c != Command.NOP)
							gui.invalidate();
					}
				});
			}
		};
		exec.scheduleAtFixedRate(ticker, 0, 1000000L/60, TimeUnit.MICROSECONDS);
		
		gui.getScreen().startScreen();

		gui.showWindow(w);
	}
}
