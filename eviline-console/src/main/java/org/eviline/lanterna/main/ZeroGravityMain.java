package org.eviline.lanterna.main;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.eviline.core.Command;
import org.eviline.core.Configuration;
import org.eviline.core.Engine;
import org.eviline.core.EngineFactories;
import org.eviline.core.Field;
import org.eviline.core.ShapeType;
import org.eviline.core.ai.DefaultAIKernel;
import org.eviline.core.ai.NextFitness;
import org.eviline.core.ss.EvilBag7NShapeSource;
import org.eviline.lanterna.EngineScreen;
import org.eviline.lanterna.EngineWindow;
import org.eviline.lanterna.HighScoreWindow;
import org.eviline.lanterna.ImageBackgroundRenderer;
import org.eviline.lanterna.LanternaPlayer;
import org.eviline.lanterna.MarkupLabel;
import org.eviline.lanterna.ShapeTypeColor;
import org.eviline.lanterna.ShapeTypeTextIcon;
import org.eviline.lanterna.SubmitScoreWindow;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
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

public class ZeroGravityMain {
	private static URL url;
	static {
		try {
			url = new URL("http://www.eviline.org/eviline-webapp/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Engine engine;
	private static EngineScreen gui;
	private static EngineWindow w;
	private static DefaultAIKernel ai;
	private static LanternaPlayer player;

	private static Label[] bag;

	public static void main(String... args) throws Exception {
		Field field = new Field();

		Configuration conf = new Configuration(
				EngineFactories.createIntegerFactory(null),
				EngineFactories.createIntegerFactory(1),
				EvilBag7NShapeSource.createFactory(EvilBag7NShapeSource.DEFAULT_N, 3));
		
		engine = new Engine(field, conf);

		engine.setNext(new ShapeType[0]);

		Terminal term;
		try {
			term = TerminalFacade.createUnixTerminal();
			Screen screen = TerminalFacade.createScreen(term);
			gui = new EngineScreen(screen);
			gui.getScreen().startScreen();
			gui.getScreen().stopScreen();
		} catch(Exception e) {
			System.out.println("\nThe above garbage was an attempt to recognize a unix console.  It can safely be ignored.");
			TerminalAppearance appearance = new TerminalAppearance(
					TerminalAppearance.DEFAULT_NORMAL_FONT,
					TerminalAppearance.DEFAULT_BOLD_FONT,
					TerminalPalette.DEFAULT,
					true);
			term = TerminalFacade.createSwingTerminal(appearance, 160, 50);
			Screen screen = TerminalFacade.createScreen(term);
			gui = new EngineScreen(screen);
		}

		BufferedImage flower = ImageIO.read(ZeroGravityMain.class.getResource("flower.jpg"));
		gui.setBackgroundRenderer(new ImageBackgroundRenderer(flower));

		final Window okw = new Window("eviline2");
		okw.addComponent(new Label("falling blocks that are totally not out to get you"));
		okw.addComponent(new Label(""));
		okw.addComponent(new Label("Press any key to begin"));
		okw.addWindowListener(new WindowAdapter() {
			@Override
			public void onUnhandledKeyboardInteraction(Window window, Key key) {
				okw.close();
				gui.invalidate();
				gui.update();
			}
		});

		gui.getScreen().startScreen();

		gui.showWindow(okw, Position.CENTER);

		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(3);

		w = new EngineWindow(engine);

		w.getEngineComponent().setGhosting(true);

		Panel p = new Panel(Orientation.VERTICAL);
		p.addComponent(new Label("normal play"));
		p.addComponent(new Label(""));
		p.addComponent(new MarkupLabel("Press <b>Q</b> to quit"));
		p.addComponent(new MarkupLabel("Press <b>R</b> to reset"));
		p.addComponent(new Label(""));
		p.addComponent(new MarkupLabel("Press <b>Z</b> to rotate left"));
		p.addComponent(new MarkupLabel("Press <b>X</b> to rotate right"));
		p.addComponent(new MarkupLabel("Press <b>j</b> to shift left"));
		p.addComponent(new MarkupLabel("Press <b>J</b> to autoshift shift left"));
		p.addComponent(new MarkupLabel("Press <b>l</b> to shift right"));
		p.addComponent(new MarkupLabel("Press <b>L</b> to autoshift right"));
		p.addComponent(new MarkupLabel("Press <b>k</b> to shift down"));
		p.addComponent(new MarkupLabel("Press <b>K</b> to soft drop"));
		p.addComponent(new MarkupLabel("Press <b>SPACE</b> to hard drop"));
		p.addComponent(new Label(""));
		p.addComponent(new Label("Available shapes:"));
		Panel bp = new Panel("bag remaining", Orientation.VERTICAL);
		bag = new Label[ShapeType.values().length - 1];
		for(ShapeType t : ShapeType.values()) {
			if(t == ShapeType.G || t == ShapeType.GH)
				continue;
			Label ll = bag[t.ordinal()] = new Label("", new ShapeTypeColor().fg(t), true);
			ll.setStyle(Category.SHADOW);
			bp.addComponent(ll);
		}
		p.addComponent(bp);
		w.addComponent(p, BorderLayout.RIGHT);

		w.addWindowListener(new WindowAdapter() {
			@Override
			public void onUnhandledKeyboardInteraction(Window window, Key key) {
				switch(key.getCharacter()) {
				case 'q':
					if(key.isCtrlPressed())
						return;
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
			}
		});
		player = new LanternaPlayer();
		w.addWindowListener(player);

		ai = new DefaultAIKernel();
		ai.setFitness(new NextFitness());

		Runnable ticker = new Runnable() {
			private boolean wasOver = false;
			@Override
			public void run() {
				Command c = player.tick();
				boolean isOver = false;
				synchronized(engine) {
					if(!engine.isOver())
						engine.tick(c);
					else
						isOver = true;
				}
				if(isOver && !wasOver) {
					final Window lines = new Window("Game Over");
					lines.addComponent(new Label("Total Lines: " + engine.getLines()));
					Button ok = new Button("OK", new Action() {
						@Override
						public void doAction() {
							lines.close();
						}
					});
					lines.addComponent(ok);
					gui.showWindow(lines, Position.CENTER);
				}
				wasOver = isOver;
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
							ShapeTypeTextIcon icons = new ShapeTypeTextIcon();
							w.getContentPane().setTitle("eviline2: lines:" + engine.getLines());
							ShapeType[] types = engine.getShapes().getBag();
							Collections.sort(Arrays.asList(types));
							for(ShapeType pt : ShapeType.values()) {
								if(pt == ShapeType.G)
									break;
								StringBuilder sb = new StringBuilder(" ");
								boolean found = false;
								for(ShapeType t : types) {
									if(pt == t) {
										found = true;
										sb.append(icons.get(t) + " ");
									} else if(found) {
										break;
									}
								}
								for(int i = 0; i < 21; i++)
									sb.append(" ");
								String s = sb.toString().substring(0, 21);
								bag[pt.ordinal()].setText(s);
							}
							gui.invalidate();
							lock.release();
						}
					}
				});
				lock.acquireUninterruptibly();
			}
		};

		exec.scheduleWithFixedDelay(ticker, 0, 10, TimeUnit.MILLISECONDS);
		exec.scheduleWithFixedDelay(drawer, 0, 10, TimeUnit.MILLISECONDS);

		gui.showWindow(w, Position.CENTER);
	}

}
