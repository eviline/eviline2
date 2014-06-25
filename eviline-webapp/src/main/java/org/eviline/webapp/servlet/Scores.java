package org.eviline.webapp.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.eviline.core.EngineStats;
import org.eviline.webapp.db.DBInterface;
import org.eviline.webapp.db.StatsMapper;
import org.eviline.webapp.dbo.NamedEngineStats;

public class Scores extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		SqlSession s = DBInterface.get().openSession();
		try {
			StatsMapper m = s.getMapper(StatsMapper.class);
			List<NamedEngineStats> nes = m.get();
			Map<EngineStats, String> top = new TreeMap<EngineStats, String>();
			for(NamedEngineStats es : nes)
				top.put(es.toStats(), es.getName());
			ObjectOutputStream out = new ObjectOutputStream(resp.getOutputStream());
			out.writeObject(top);
			out.close();
		} finally {
			s.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<EngineStats, String> stats;
		SqlSession s = DBInterface.get().openSession();
		try {
			ObjectInputStream in = new ObjectInputStream(req.getInputStream());
			stats = (Map<EngineStats, String>) in.readObject();
			StatsMapper m = s.getMapper(StatsMapper.class);
			for(EngineStats es : stats.keySet())
				m.post(new NamedEngineStats(stats.get(es), es));
		} catch(ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			s.close();
		}
	}
	
}
