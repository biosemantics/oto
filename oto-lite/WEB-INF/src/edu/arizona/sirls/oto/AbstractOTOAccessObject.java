package edu.arizona.sirls.oto;

import java.io.IOException;
import java.util.Properties;

import oto.full.OTOClient;

public abstract class AbstractOTOAccessObject {
	protected String OTO_url;

	public AbstractOTOAccessObject() {

	}

	protected OTOClient createOTOClient() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		this.OTO_url = properties.getProperty("OTO_url");
		return new OTOClient(OTO_url);
	}
}
