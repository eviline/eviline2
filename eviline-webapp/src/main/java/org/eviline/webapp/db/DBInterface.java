package org.eviline.webapp.db;

import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBInterface {
	private static SqlSessionFactory sql;
	
	public SqlSessionFactory get() {
		if(sql != null)
			return sql;
		
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		
		try {
			Properties props = new Properties();
			props.load(DBInterface.class.getResourceAsStream("config.properties"));
			return sql = builder.build(DBInterface.class.getResourceAsStream("config.xml"), props);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
