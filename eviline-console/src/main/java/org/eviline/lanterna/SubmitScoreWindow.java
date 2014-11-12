package org.eviline.lanterna;

import java.net.URL;

import org.eviline.core.Engine;
import org.eviline.core.EngineStats;
import org.eviline.core.EngineStatsSubmitter;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen.Position;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import com.googlecode.lanterna.gui.component.TextBox;

public class SubmitScoreWindow extends Window {
	protected URL url;
	protected Engine e;
	
	public SubmitScoreWindow(URL url, Engine e) {
		super("Submit High Score");

		this.url = url;
		this.e = e;
		
		final EngineStats stats = new EngineStats(e);
		
		Panel p = new Panel(Orientation.VERTICAL);

		String fmt = "%-20s%-10s%-10s%-30s";
		
		p.addComponent(new Label(String.format(fmt, "Name", "Score", "Lines", "Date")));
		p.addComponent(new Label(String.format(fmt, "YOUR_NAME", stats.getScore(), stats.getLines(), HighScoreWindow.DF.format(stats.getTs()))));
		p.addComponent(new Label(""));
		p.addComponent(new Label("Enter your name below:"));
		
		final TextBox name = new TextBox("anonymous", 70);
		p.addComponent(name);
		
		p.addComponent(new Label(""));
		
		Button b = new Button("Submit", new Action() {
			@Override
			public void doAction() {
				try {
					new EngineStatsSubmitter(SubmitScoreWindow.this.url).post(stats, name.getText());
				} catch (Exception e) {
					final Window w = new Window("Error Submitting Score");
					Panel p = new Panel(Orientation.VERTICAL);
					p.addComponent(new Label(e.toString()));
					p.addComponent(new Label(""));
					Button b = new Button("OK", new Action() {
						@Override
						public void doAction() {
							w.close();
						}
					});
					p.addComponent(b);
					w.addComponent(p);
					SubmitScoreWindow.this.getOwner().showWindow(w, Position.CENTER);
				}
				SubmitScoreWindow.this.close();
			}
		});
		p.addComponent(b);
		
		addComponent(p);
	}

}
