package org.eviline.swing;

import java.util.Map;

import javax.swing.JComponent;

import org.eviline.core.Command;

public class CustomSwingPlayer extends SwingPlayer {
	
	protected Map<Command, Key> keys;

	public CustomSwingPlayer(JComponent controlTarget, Map<Command, Key> keys) {
		super(controlTarget);
		this.keys = keys;
	}

	@Override
	public void initKeys(boolean appletMode) {
		for(Map.Entry<Command, Key> k : keys.entrySet()) {
			controls.put(k.getValue(), k.getKey());
		}
	}
	
}
