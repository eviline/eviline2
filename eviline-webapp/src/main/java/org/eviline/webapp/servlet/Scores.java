package org.eviline.webapp.servlet;

import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.eviline.webapp.dbo.Score;

@Path("/scores")
public class Scores {
	private ObjectMapper mapper = new ObjectMapper();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String foo(Score score) throws Exception {
		return mapper.writeValueAsString(score);
	}
}
