package edu.arizona.sirls.rest.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.sirls.rest.beans.TermSynonym;

public class TermSynonymDAO extends AbstractDAO {

	private static TermSynonymDAO instance;

	private TermSynonymDAO() throws Exception {
		super();
	}

	public static TermSynonymDAO getInstance() throws Exception {
		if (instance == null)
			instance = new TermSynonymDAO();
		return instance;
	}

	public List<TermSynonym> getTermSynonyms(String glossaryType, String version)
			throws Exception {
		List<TermSynonym> result = new ArrayList<TermSynonym>();
		this.openConnection();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String downloadFilePath = properties.getProperty("GLOSSARY_FILE_PATH");

		String[] versionParts = version.split("\\.");
		if (versionParts.length == 2) {
			int primaryVersion = Integer.parseInt(versionParts[0]);
			int secondaryVersion = Integer.parseInt(versionParts[1]);
			String sql = "SELECT * FROM glossary_versions, glossarytypes WHERE "
					+ "glossarytypes.glossaryName = ? AND "
					+ "glossary_versions.dataset = ? AND "
					+ "glossary_versions.isForGlossaryDownload = ? AND "
					+ "glossary_versions.primaryVersion = ? AND "
					+ "glossary_versions.secondaryVersion = ? AND "
					+ "glossary_versions.glossaryType = glossarytypes.glossTypeID";

			PreparedStatement preparedStatement = this.prepareStatement(sql);
			preparedStatement.setString(1, glossaryType);
			preparedStatement.setString(2, glossaryType + "_glossary");
			preparedStatement.setBoolean(3, true);
			preparedStatement.setInt(4, primaryVersion);
			preparedStatement.setInt(5, secondaryVersion);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String filename = resultSet.getString("filename");
				if (filename.contains("_syns_")) {
					CSVReader reader = new CSVReader(new InputStreamReader(
							new FileInputStream(downloadFilePath
									+ File.separator + filename)), ',', '"', 1);
					String[] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						result.add(new TermSynonym(nextLine[0], nextLine[1],
								nextLine[2], nextLine[3]));
					}
					reader.close();
					break;
				} else {
					continue;
				}
			}
			preparedStatement.close();
		}
		this.closeConnection();
		return result;
	}
}
