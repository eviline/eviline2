package org.eviline.webapp.db;

import java.util.List;

import org.eviline.webapp.dbo.NamedEngineStats;

public interface StatsMapper {
	public List<NamedEngineStats> get();
	public void post(NamedEngineStats stats);
}
