package edu.arizona.biosemantics.oto.oto.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.GlossaryDictionaryEntry;

public class GlossaryDictionaryDAO extends AbstractDAO {

	private GlossaryDictionaryDAO() throws Exception {
		super();
	}

	private static GlossaryDictionaryDAO instance;

	public static GlossaryDictionaryDAO getInstance() throws Exception {
		if(instance == null)
			instance = new GlossaryDictionaryDAO();
		return instance;
	}

	public GlossaryDictionaryEntry getGlossaryDictionaryEntry(String glossaryType, String term, String category) throws SQLException {
		this.openConnection();
		String[] termIDAndDefinition = getTermIDAndDefinition(term, category, getGlossaryTypeID(glossaryType));
		this.closeConnection();
		return new GlossaryDictionaryEntry(termIDAndDefinition[0], term, category, glossaryType, termIDAndDefinition[1]);
	}
	
	private int getGlossaryTypeID(String glossaryType) throws SQLException {
		PreparedStatement statement = this.prepareStatement("SELECT glossTypeID FROM glossarytypes WHERE glossaryName = ?");
		statement.setString(1, glossaryType);
		ResultSet resultSet = statement.executeQuery();
		int glossaryTypeID = -1;
		if(resultSet.next()) {
			glossaryTypeID = resultSet.getInt(1);
		} else {
			resultSet.close();
			statement.close();
			throw new IllegalArgumentException();
		}
		resultSet.close();
		statement.close();
		return glossaryTypeID;
	}
	
	private String[] getTermIDAndDefinition(String term, String category, int glossaryTypeID) throws SQLException {
		PreparedStatement statement = this.prepareStatement("SELECT termID, definition from glossary_dictionary WHERE term = ? AND category = ? AND glossaryType = ?");
		statement.setString(1, term);
		statement.setString(2, category);
		statement.setInt(3, glossaryTypeID);
		ResultSet resultSet = statement.executeQuery();
		if(resultSet.next()) {
			String termID = resultSet.getString(1);
			String definition = resultSet.getString(2);
			resultSet.close();
			statement.close();
			return new String[] {termID, definition};
		}
		return null;
	}

	public GlossaryDictionaryEntry insertAndGetGlossaryDictionaryEntry(String glossaryType, String term, String category, String definition) throws SQLException {
		this.openConnection();
		int glossaryTypeID = getGlossaryTypeID(glossaryType);
		String[] termIDAndDefinition = getTermIDAndDefinition(term, category, glossaryTypeID);
		if(termIDAndDefinition == null) {
			PreparedStatement statement = this.prepareStatement("INSERT INTO glossary_dictionary (term, category, glossaryType, definition) VALUES (?, ?, ?, ?)");
			statement.setString(1, term);
			statement.setString(2, category);
			statement.setInt(3, glossaryTypeID);
			statement.setString(4, definition);
			statement.executeUpdate();
			statement.close();
			termIDAndDefinition = getTermIDAndDefinition(term, category, glossaryTypeID);
		}
		
		this.closeConnection();
		if(termIDAndDefinition == null)
			throw new IllegalArgumentException();
		return new GlossaryDictionaryEntry(termIDAndDefinition[0], term, category, glossaryType, termIDAndDefinition[1]);
	}

	public List<GlossaryDictionaryEntry> getGlossaryDictionaryEntries(String glossaryType, String term) throws SQLException {
		this.openConnection();
		List<GlossaryDictionaryEntry> result = new LinkedList<GlossaryDictionaryEntry>();
		int glossaryTypeID = getGlossaryTypeID(glossaryType);
		PreparedStatement statement = this.prepareStatement("SELECT * FROM glossary_dictionary WHERE term = ? AND glossaryType = ?");
		statement.setString(1, term);
		statement.setInt(2, glossaryTypeID);
		ResultSet resultSet = statement.executeQuery();
		while(resultSet.next()) {
			String termID = resultSet.getString(1);
			String category = resultSet.getString(3);
			String definition = resultSet.getString(5);
			result.add(new GlossaryDictionaryEntry(termID, term, category, glossaryType, definition));
		}
		resultSet.close();
		statement.close();
		this.closeConnection();
		return result;
	}

}
