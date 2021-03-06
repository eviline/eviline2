package org.eviline.lanterna.main;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.AIPlayer;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ai.ScoreFitness;
import org.eviline.lanterna.EngineScreen;
import org.eviline.lanterna.EngineWindow;
import org.eviline.lanterna.MarkupLabel;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;
import com.googlecode.lanterna.terminal.swing.TerminalPalette;

public class AutoplayMain {
	private static final int MAX_LOOKAHEAD = 3;
	
	private static class ManualScreen extends Screen {
		
		public ManualScreen(Terminal terminal) {
			super(terminal);
		}
		
		public void manualRefresh() {
			super.refresh();
		}

		@Override
		public void refresh() {
		}
		
	}

	private static Engine engine;
	private static EngineScreen gui;
	private static ManualScreen mscreen;
	private static EngineWindow w;
	private static DefaultAIKernel ai;
	private static AIPlayer player;
	private static boolean syncDisplay = false;

	private static ScheduledExecutorService exec;

	private static ReentrantLock drawLock = new ReentrantLock();

	private static Runnable blockingDraw = new Runnable() {
		private Semaphore sync = new Semaphore(0);
		@Override
		public void run() {
			final Engine e = AutoplayMain.engine.clone();
			gui.runInEventThread(new Action() {
				@Override
				public void doAction() {
					drawLock.lock();
					w.setEngine(e);
					w.getContentPane().setTitle("eviline2: lookahead:" + player.getLookahead() + "/" + MAX_LOOKAHEAD + " lines:" + e.getLines());
					mscreen.manualRefresh();
					if(syncDisplay && (gui.getScreen().getTerminal() instanceof SwingTerminal)) {
						final SwingTerminal st = (SwingTerminal) gui.getScreen().getTerminal();
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								JPanel p = (JPanel) st.getJFrame().getContentPane();
								p.paintImmediately(p.getBounds());
								sync.release();
							}
						});
					} else {
						sync.release();
					}
					drawLock.unlock();
				}
			});
			sync.acquireUninterruptibly();
		}
	};

	private static Runnable nonblockingDraw = new Runnable() {

		@Override
		public void run() {
			final Engine e = AutoplayMain.engine.clone();
			gui.runInEventThread(new Action() {
				@Override
				public void doAction() {
					if(!drawLock.tryLock())
						return;
					w.setEngine(e);
					w.getContentPane().setTitle("eviline2: lookahead:" + player.getLookahead() + "/" + MAX_LOOKAHEAD + " score:" + e.getScore());
					mscreen.manualRefresh();
					drawLock.unlock();
				}
			});
		}
	};

	private static Runnable ticker = new Runnable() {
		@Override
		public void run() {
			while(!engine.isOver() && !Thread.interrupted()) {
				for(Command c = player.tick(); c == Command.NOP && !engine.isOver(); c = player.tick())
					engine.tick(c);
				if(engine.isOver())
					break;
				if(syncDisplay)
					blockingDraw.run();
				else
					nonblockingDraw.run();
				engine.setShape(player.getDest());
				engine.tick(Command.SHIFT_DOWN);
				player.getCommands().clear();
				player.setAllowDrops(!syncDisplay);
			}
			blockingDraw.run();
		}
	};

	private static Future<?> tickerFuture;
	
	public static void main(String... args) throws Exception {
		Field field = new Field();
		
		engine = new Engine(field, new Configuration(null, 1));

		engine.setNext(new ShapeType[4]);

		Terminal term;
		try {
			term = TerminalFacade.createUnixTerminal();
			mscreen = new ManualScreen(term);
			gui = new EngineScreen(mscreen);
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
			mscreen = new ManualScreen(term);
			gui = new EngineScreen(mscreen);
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
					tickerFuture.cancel(true);
					engine.reset();
					tickerFuture = exec.submit(ticker);
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
		ai.setFitness(new ScoreFitness());
		player = new AIPlayer(ai, engine, 1);
		player.setAllowDrops(true);

		tickerFuture = exec.submit(ticker);
//		exec.scheduleAtFixedRate(nonblockingDraw, 100, 100, TimeUnit.MILLISECONDS);
		
		gui.getScreen().startScreen();

		gui.showWindow(w);
	}
}
