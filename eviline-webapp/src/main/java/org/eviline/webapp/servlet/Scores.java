package org.eviline.webapp.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eviline.core.EngineStats;

public class Scores extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<EngineStats, String> stats;
		try {
			ObjectInputStream in = new ObjectInputStream(req.getInputStream());
			stats = (Map<EngineStats, String>) in.readObject();
		} catch(ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
	
}
