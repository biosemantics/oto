package edu.arizona.biosemantics.oto.lite;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
	private static Configuration instance;

	public static Configuration getInstance() throws IOException {
		if(instance == null)
			instance = new Configuration();
		return instance;
	}

	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private String otoUrl;
	
	public Configuration() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		
		this.databaseName = properties.getProperty("databaseName");
		this.databaseUser = properties.getProperty("databaseUser");
		this.databasePassword = properties.getProperty("databasePassword");
 
		this.otoUrl = properties.getProperty("OTO_url");
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public String getOtoUrl() {
		return otoUrl;
	}
	
	
}


