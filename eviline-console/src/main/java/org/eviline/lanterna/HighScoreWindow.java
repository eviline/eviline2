package org.eviline.lanterna;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.eviline.core.EngineStats;
import org.eviline.core.EngineStatsSubmitter;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;

public class HighScoreWindow extends Window {
	public static final SimpleDateFormat DF = new SimpleDateFormat("YYYY-MM-dd");
	
	protected URL url;
	
	public HighScoreWindow(URL url) throws IOException {
		super("High Scores");
		
		Panel p = new Panel(Orientation.VERTICAL);
		
		this.url = url;
		
		String fmt = "%-20s%-10s%-10s%-30s";
		
		p.addComponent(new Label(String.format(fmt, "Name", "Score", "Lines", "Date")));
		
		for(Map.Entry<EngineStats, String> e : new EngineStatsSubmitter(url).get().entrySet()) {
			String label = String.format(
					fmt, 
					e.getValue(), 
					e.getKey().getScore(), 
					e.getKey().getLines(),
					DF.format(e.getKey().getTs()));
			p.addComponent(new Label(label));
		}
		
		p.addComponent(new Label(""));
		
		Button ok = new Button("OK", new Action() {
			@Override
			public void doAction() {
				HighScoreWindow.this.close();
			}
		});
		p.addComponent(ok);
		
		addComponent(p);
	}

}
