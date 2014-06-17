package edu.arizona.biosemantics.oto.steps.server.oto;

import java.io.IOException;
import java.util.Properties;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;

public abstract class AbstractOTOAccessObject {
	protected String OTO_url;

	public AbstractOTOAccessObject() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		this.OTO_url = properties.getProperty("OTO_url");
	}

	protected OTOClient createOTOClient() {
		return new OTOClient(OTO_url);
	}
}
