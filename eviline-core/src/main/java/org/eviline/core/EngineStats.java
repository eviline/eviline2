package org.eviline.core;

import java.io.Serializable;

public class EngineStats implements Comparable<EngineStats>, Serializable {
	private static final long serialVersionUID = 0;
	
	protected long lines;
	protected long score;
	protected long tickCount;
	protected long shapeCount;
	protected long ts;
	
	public EngineStats() {}
	
	public EngineStats(Engine e) {
		lines = e.getLines();
		score = e.getScore();
		tickCount = e.getTickCount();
		shapeCount = e.getShapeCount();
		ts = System.currentTimeMillis();
	}
	
	public long getLines() {
		return lines;
	}
	public void setLines(long lines) {
		this.lines = lines;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public long getTickCount() {
		return tickCount;
	}
	public void setTickCount(long tickCount) {
		this.tickCount = tickCount;
	}
	public long getShapeCount() {
		return shapeCount;
	}
	public void setShapeCount(long shapeCount) {
		this.shapeCount = shapeCount;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}

	@Override
	public int compareTo(EngineStats o) {
		int c;
		if((c = Long.compare(score, o.score)) != 0)
			return -c;
		if((c = Long.compare(lines, o.lines)) != 0)
			return -c;
		if((c = Long.compare(shapeCount, o.shapeCount)) != 0)
			return -c;
		return -Long.compare(tickCount, o.tickCount);
	}
}
