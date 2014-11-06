package org.eviline.lanterna.main;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeSource;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.AIKernel;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ai.Player;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.lanterna.EngineComponent;
import org.eviline.lanterna.EngineScreen;
import org.eviline.lanterna.EngineWindow;
import org.eviline.lanterna.ImageBackgroundRenderer;
import org.eviline.lanterna.LanternaPlayer;
import org.eviline.lanterna.MarkupLabel;

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
	private static final int MAX_LOOKAHEAD = 3;
	
	private static Engine engine;
	private static EngineScreen gui;
	private static EngineWindow w;
	private static DefaultAIKernel ai;
	private static AIPlayer player;
	private static boolean syncDisplay = false;

	private static ScheduledExecutorService exec;

	private static Runnable blockingDraw = new Runnable() {
		private AtomicBoolean done = new AtomicBoolean(false);
		@Override
		public synchronized void run() {
			done.set(false);
			gui.runInEventThread(new Action() {
				@Override
				public void doAction() {
					Engine e = AutoplayMain.engine.clone();
					w.setEngine(e);
					w.getContentPane().setTitle("eviline2: lookahead:" + player.getLookahead() + "/" + MAX_LOOKAHEAD + " lines:" + e.getLines());
					gui.invalidate();
					gui.update();
					synchronized(done) {
						done.set(true);
						done.notifyAll();
					}
				}
			});
			synchronized(done) {
				while(!done.get()) {
					try {
						done.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	};

	private static Runnable ticker = new Runnable() {
		@Override
		public void run() {
			Command c = player.tick();
			if(!engine.isOver())
				engine.tick(c);
			if(engine.getShape() == -1)
				player.setAllowDrops(!syncDisplay);
			if(syncDisplay)
				blockingDraw.run();
		}
	};

	public static void main(String... args) throws Exception {
		Field field = new Field();

		engine = new Engine(field, new Configuration(null, 1));

		engine.setNext(new ShapeType[5]);

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

		exec = Executors.newScheduledThreadPool(3);

		w = new EngineWindow(engine);
		Panel p = new Panel(Orientation.VERTICAL);
		p.addComponent(new Label("autoplay"));
		p.addComponent(new Label(""));
		p.addComponent(new MarkupLabel("Press <b>Q</b> to quit"));
		p.addComponent(new MarkupLabel("Press <b>R</b> to reset"));
		p.addComponent(new MarkupLabel("Press <b>UP</b> to increase lookahead"));
		p.addComponent(new MarkupLabel("Press <b>DOWN</b> to decrease lookahead"));
		p.addComponent(new MarkupLabel("Press <b>S</b> to toggle synchronous display"));
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
					engine.reset();
					break;
				case 's':
					syncDisplay = !syncDisplay;
					break;
				}
				switch(key.getKind()) {
				case ArrowUp:
					if(player.getLookahead() < MAX_LOOKAHEAD)
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
		player = new AIPlayer(ai, engine, 1);
		player.setAllowDrops(true);

		exec.scheduleWithFixedDelay(ticker, 1, 1, TimeUnit.NANOSECONDS);
		exec.scheduleWithFixedDelay(blockingDraw, 10, 10, TimeUnit.MILLISECONDS);

		gui.getScreen().startScreen();

		gui.showWindow(w);
	}
}
