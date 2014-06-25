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
		ts = System.currentTimeMillis();
	}

	public EngineStats toStats() {
		EngineStats e = new EngineStats();
		e.setLines(getLines());
		e.setScore(getScore());
		e.setShapeCount(getShapeCount());
		e.setTickCount(getTickCount());
		e.setTs(getTs());
		return e;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
