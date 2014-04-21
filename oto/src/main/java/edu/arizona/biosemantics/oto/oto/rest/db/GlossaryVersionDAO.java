package edu.arizona.biosemantics.oto.oto.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GlossaryVersionDAO extends AbstractDAO {

	private GlossaryVersionDAO() throws Exception {
		super();
	}

	private static GlossaryVersionDAO instance;
	
	public static GlossaryVersionDAO getInstance() throws Exception {
		if(instance == null)
			instance = new GlossaryVersionDAO();
		return instance;
	}

	public String getLatestVersion(String glossaryType) throws Exception {
		this.openConnection();
		String sql = "SELECT * FROM glossary_versions, glossarytypes WHERE " +
				"glossarytypes.glossaryName = ? AND " +
				"glossary_versions.dataset = ? AND " +
				"glossary_versions.isForGlossaryDownload = ? AND " +
				"glossary_versions.isLatest = ? AND " +
				"glossary_versions.glossaryType = glossarytypes.glossTypeID";
		PreparedStatement preparedStatement = this.prepareStatement(sql);
		preparedStatement.setString(1, glossaryType);
		preparedStatement.setString(2, glossaryType + "_glossary");
		preparedStatement.setBoolean(3, true);
		preparedStatement.setBoolean(4, true);
		ResultSet resultSet = preparedStatement.executeQuery();
		if(resultSet.next()) {
			int primaryVersion = resultSet.getInt("primaryVersion");
			int secondaryVersion = resultSet.getInt("secondaryVersion");
			this.closeConnection();
			return primaryVersion + "." + secondaryVersion;
		} else {
			this.closeConnection();
			throw new Exception("Latest downloadable glossary of this type not found");
		}
	}
	
	public boolean existsVersion(String glossaryType, String version) throws Exception {
		this.openConnection();
		String[] versionParts = version.split("\\.");
		if(versionParts.length == 2) {
			int primaryVersion = Integer.parseInt(versionParts[0]);
			int secondaryVersion = Integer.parseInt(versionParts[1]);
			String sql = "SELECT * FROM glossary_versions, glossarytypes WHERE " +
					"glossarytypes.glossaryName = ? AND " +
					"glossary_versions.dataset = ? AND " +
					"glossary_versions.isForGlossaryDownload = ? AND " +
					"glossary_versions.primaryVersion = ? AND " + 
					"glossary_versions.secondaryVersion = ? AND " + 
					"glossary_versions.glossaryType = glossarytypes.glossTypeID";
			
			PreparedStatement preparedStatement = this.prepareStatement(sql);
			preparedStatement.setString(1, glossaryType);
			preparedStatement.setString(2, glossaryType + "_glossary");
			preparedStatement.setBoolean(3, true);
			preparedStatement.setInt(4, primaryVersion);
			preparedStatement.setInt(5, secondaryVersion);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				preparedStatement.close();
				this.closeConnection();
				return true;
			} else {
				preparedStatement.close();
				this.closeConnection();
				return false;
			}
		} else {
			this.closeConnection();
			throw new Exception("Malformed version");
		}
	}

}
