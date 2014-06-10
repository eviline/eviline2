package org.eviline.lanterna;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.HorisontalLayout;

public class MarkupLabel extends Panel {
	private static Pattern BOLD_TAG = Pattern.compile("(.*?)(</?b>)");
	
	public MarkupLabel(String text) {
		super();
		HorisontalLayout layout = new HorisontalLayout();
		layout.setPadding(0);
		setLayoutManager(layout);
		Matcher m = BOLD_TAG.matcher(text);
		
		boolean bold = false;
		int end;
		if(m.find()) {
			do {
				addComponent(new Label(text.substring(m.start(1), m.end(1)), bold));
				bold = "<b>".equals(m.group(2));
				end = m.end();
			} while(m.find());
			addComponent(new Label(text.substring(end), bold));
		} else
			addComponent(new Label(text));
	}
}
