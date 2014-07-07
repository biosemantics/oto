package edu.arizona.biosemantics.oto.oto.rest.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.arizona.biosemantics.oto.common.model.TermCategory;
import edu.arizona.biosemantics.oto.oto.Configuration;

public class TermCategoryDAO extends AbstractDAO {

	private static TermCategoryDAO instance;

	private TermCategoryDAO() throws Exception {
		super();
	}

	public static TermCategoryDAO getInstance() throws Exception {
		if (instance == null)
			instance = new TermCategoryDAO();
		return instance;
	}

	public List<TermCategory> getTermCategories(String glossaryType,
			String version) throws Exception {
		List<TermCategory> result = new ArrayList<TermCategory>();
		this.openConnection();

		String downloadFilePath = Configuration.getInstance().getGlossaryFilePath();

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
				if (filename.contains("_term_category_")) {
					CSVReader reader = new CSVReader(new InputStreamReader(
							new FileInputStream(downloadFilePath
									+ File.separator + filename)), ',', '"', 1);
					String[] nextLine;
					while ((nextLine = reader.readNext()) != null) {
						boolean hasSyn = nextLine[2].equals("1");
						result.add(new TermCategory(nextLine[0], nextLine[1],
								hasSyn, nextLine[3], nextLine[4]));
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
