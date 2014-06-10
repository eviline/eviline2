package org.eviline.lanterna;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.GUIScreenBackgroundRenderer;
import com.googlecode.lanterna.screen.Screen;

public class EngineScreen extends GUIScreen {

	public EngineScreen(Screen screen) {
		super(screen);
	}

	public EngineScreen(Screen screen, String title) {
		super(screen, title);
	}

	public EngineScreen(Screen screen,
			GUIScreenBackgroundRenderer backgroundRenderer) {
		super(screen, backgroundRenderer);
	}

	@Override
	public boolean update() {
		return super.update();
	}
	
}
