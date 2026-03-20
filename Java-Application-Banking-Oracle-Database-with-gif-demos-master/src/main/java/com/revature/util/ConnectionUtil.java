package com.revature.util;

	import java.io.InputStream;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.util.Properties;

	public class ConnectionUtil {
		private static ConnectionUtil cu = null;
		private static Properties prop;
		
		private ConnectionUtil() {
			prop = new Properties();
			try {
				// using the class loader to reduce reliance on file system
				InputStream dbProps = ConnectionUtil.class.getClassLoader()
						.getResourceAsStream("database.properties");
				prop.load(dbProps);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public static synchronized ConnectionUtil getConnectionUtil() {
			if(cu == null)
				cu = new ConnectionUtil();
			return cu;
		}
		
		public Connection getConnection() {
			Connection conn = null;
			try {
				// We have to register our driver class
				Class.forName(prop.getProperty("drv"));
				conn = DriverManager.getConnection(prop.getProperty("url"), 
						prop.getProperty("usr"), 
						prop.getProperty("pswd"));
						
			} catch(Exception e) {
				e.printStackTrace();
			}
			return conn;
		}

	}

