package org.eviline.swing;

import java.util.Map;

import org.eviline.core.Command;

public class CustomSwingPlayer extends SwingPlayer {
	
	protected Map<Command, Key> keys;

	public CustomSwingPlayer(Map<Command, Key> keys) {
		super();
		this.keys = keys;
	}

	@Override
	public void initKeys(boolean appletMode) {
		for(Map.Entry<Command, Key> k : keys.entrySet()) {
			if(k.getKey() != null && k.getValue() != null)
				controls.put(k.getValue(), k.getKey());
		}
		controlsListener = new ControlsKeyListener();
	}
	
}
