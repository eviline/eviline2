package org.eviline.webapp.dbo;

import org.eviline.core.EngineStats;

public class NamedEngineStats extends EngineStats {
	private static final long serialVersionUID = 0;
	
	protected String name;
	
	public NamedEngineStats() {}
	
	public NamedEngineStats(String name, EngineStats stats) {
		this.name = name;
		lines = stats.getLines();
		score = stats.getScore();
		shapeCount = stats.getShapeCount();
		tickCount = stats.getTickCount();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
