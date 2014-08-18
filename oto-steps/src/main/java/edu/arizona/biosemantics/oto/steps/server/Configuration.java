/**
 * 
 */
package edu.arizona.biosemantics.oto.steps.server;
import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * @author Hong Cui
 *
 */
public class Configuration {
	public static String otolite_databaseName;
	public static String otolite_databaseUser;
	public static String otolite_databasePassword;
	public static String bioportalUrl;
	public static String OTO_url; 

	public static String ontology_dir;
	public static String dict_dir;
	public static String fileBase;
	public static String src_file_dir;
	public static String etc_ontology_baseIRI;

	static {
		//Logger.getLogger(Configuration.class).debug("Init Configuration");

		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Properties properties = new Properties(); 
			properties.load(loader.getResourceAsStream("config.properties"));
			
			otolite_databaseName = properties.getProperty("otolite_databaseName");
			otolite_databaseUser = properties.getProperty("otolite_databaseUser");
			otolite_databasePassword = properties.getProperty("otolite_databasePassword");
			bioportalUrl = properties.getProperty("bioportalUrl");
			OTO_url = properties.getProperty("OTO_url");
			etc_ontology_baseIRI = properties.getProperty("etc_ontology_baseIRI");
			//dirs don't end with /
			ontology_dir = properties.getProperty("ontology_dir").replaceFirst("[/\\\\]+$", "").replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
			dict_dir = properties.getProperty("dict_dir").replaceFirst("[/\\\\]+$", "").replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
			fileBase = properties.getProperty("fileBase").replaceFirst("[/\\\\]+$", "").replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
			src_file_dir = properties.getProperty("src_file_dir").replaceFirst("[/\\\\]+$", "").replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
