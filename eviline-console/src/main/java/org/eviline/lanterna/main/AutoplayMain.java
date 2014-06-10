package org.eviline.lanterna.main;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.XYShape;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ai.Player;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.lanterna.EngineComponent;
import org.eviline.lanterna.EngineScreen;
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
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;
import com.googlecode.lanterna.terminal.swing.TerminalPalette;

public class AutoplayMain {
	private static Engine engine;
	private static EngineScreen gui;
	private static EngineWindow w;
	private static DefaultAIKernel ai;
	private static AIPlayer player;

	public static void main(String... args) throws Exception {
		Field field = new Field();

		engine = new Engine(field, new Configuration() {
			@Override
			public Integer downFramesRemaining(Engine e) {
				return null;
			}
			@Override
			public Integer respawnFramesRemaining(Engine e) {
				return 1;
			}
		});

		engine.setNext(new ShapeType[7]);

		Terminal term;
		try {
			term = TerminalFacade.createUnixTerminal();
			Screen screen = TerminalFacade.createScreen(term);
			gui = new EngineScreen(screen);
			gui.getScreen().startScreen();
			gui.getScreen().stopScreen();
		} catch(Exception e) {
			System.out.println("\nThe above garbage was an attempt to recognize a unix console.  It can safely be ignored.");
			term = TerminalFacade.createSwingTerminal(
					new TerminalAppearance(
							TerminalAppearance.DEFAULT_NORMAL_FONT,
							TerminalAppearance.DEFAULT_BOLD_FONT,
							TerminalPalette.XTERM,
							true)
					);
			Screen screen = TerminalFacade.createScreen(term);
			gui = new EngineScreen(screen);
		}


		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(3);

		w = new EngineWindow(engine);
		Panel p = new Panel(Orientation.VERTICAL);
		p.addComponent(new Label("autoplay"));
		p.addComponent(new Label(""));
		p.addComponent(new Label("Press Q to quit"));
		p.addComponent(new Label("Press R to reset"));
		p.addComponent(new Label("Press UP to increase lookahead"));
		p.addComponent(new Label("Press DOWN to decrease lookahead"));
		w.addComponent(p, BorderLayout.RIGHT);

		w.addWindowListener(new WindowAdapter() {
			@Override
			public void onUnhandledKeyboardInteraction(Window window, Key key) {
				switch(key.getCharacter()) {
				case 'q':
					w.close();
					gui.getScreen().stopScreen();
					exec.shutdown();
					System.exit(0);
					break;
				case 'r':
					synchronized(engine) {
						engine.reset();
					}
					break;
				}
				switch(key.getKind()) {
				case ArrowUp:
					if(player.getLookahead() < engine.getNext().length)
						player.setLookahead(player.getLookahead() + 1);
					break;
				case ArrowDown:
					if(player.getLookahead() > 0)
						player.setLookahead(player.getLookahead() - 1);
				}
			}
		});

		ai = new DefaultAIKernel();
		ai.setFitness(new NextFitness());
		ai.setExec(Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable arg0) {
				Thread t = new Thread(arg0);
				t.setPriority(Thread.MIN_PRIORITY);
				t.setDaemon(true);
				return t;
			}
		}));
		player = new AIPlayer(ai, engine, 1);

		Runnable ticker = new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				Command c = player.tick();
				synchronized(engine) {
					if(!engine.isOver())
						engine.tick(c);
				}
			}
		};
		Runnable drawer = new Runnable() {
			private Semaphore lock = new Semaphore(0);
			@Override
			public void run() {
				gui.runInEventThread(new Action() {
					@Override
					public void doAction() {
						synchronized(engine) {
							while(engine.getShape() == null)
								engine.tick(Command.NOP);
							w.getContentPane().setTitle("eviline2: lookahead:" + player.getLookahead() + " lines:" + engine.getLines());
							gui.invalidate();
							lock.release();
						}
					}
				});
				lock.acquireUninterruptibly();
			}
		};
		exec.scheduleWithFixedDelay(ticker, 0, 1, TimeUnit.NANOSECONDS);
		exec.scheduleWithFixedDelay(drawer, 0, 1, TimeUnit.NANOSECONDS);

		gui.getScreen().startScreen();

		gui.showWindow(w);
	}
}
