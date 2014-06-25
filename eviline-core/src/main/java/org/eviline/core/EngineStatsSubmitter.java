package org.eviline.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;

public class EngineStatsSubmitter {
	protected URL url;
	
	public EngineStatsSubmitter(URL url) {
		this.url = url;
	}
	
	public Map<EngineStats, String> get() throws IOException {
		ObjectInputStream in = new ObjectInputStream(url.openStream()); 
		try {
			return (Map<EngineStats, String>) in.readObject();
		} catch(ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			in.close();
		}
	}
	
	public void post(EngineStats stats, String name) throws IOException {
		URLConnection c = url.openConnection();
		c.setDoOutput(true);
		c.setDoInput(false);
		ObjectOutputStream out = new ObjectOutputStream(c.getOutputStream());
		Map<EngineStats, String> m = Collections.singletonMap(stats, name);
		out.writeObject(m);
		out.close();
	}
}
