package edu.arizona.biosemantics.oto.lite.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.lite.Synonym;

public class SynonymDAO extends AbstractDAO {

	private SynonymDAO() throws Exception {
		super();
	}

	private static SynonymDAO instance;
	
	public static SynonymDAO getInstance() throws Exception {
		if(instance == null)
			instance = new SynonymDAO();
		return instance;
	}

	public List<Synonym> getSynonyms(int uploadId) throws SQLException {
		List<Synonym> result = new ArrayList<Synonym>();
		this.openConnection();
		
		String sql = "SELECT * FROM decisions WHERE uploadID=" + uploadId;
		PreparedStatement preparedStatement = this.executeSQL(sql);
		ResultSet resultSet = preparedStatement.getResultSet();
		int i=0; 
		while (resultSet.next()) {
			boolean isMainTerm = resultSet.getBoolean("isMainTerm");
			String mainTerm = resultSet.getString("term");
			String category = resultSet.getString("category");
			if(isMainTerm) {
				boolean hasSynonym = false;
				String hasSynonymSQL = "SELECT * FROM synonyms WHERE uploadID=" + uploadId + " AND mainTerm='" + mainTerm + "' AND category='" + category + "'";
				PreparedStatement statement = this.executeSQL(hasSynonymSQL);
				ResultSet hasSynonymResult = statement.getResultSet();
				
				while(hasSynonymResult.next()) {
					String synonym = hasSynonymResult.getString("synonym");
					result.add(new Synonym(String.valueOf(i), normalizeTerm(mainTerm), category, normalizeTerm(synonym)));
				}
				i++;
				hasSynonymResult.close();
				statement.close();
			}
		}
		this.closeConnection();
		return result;
	}
	
	private String normalizeTerm(String term) {
		if(term.matches(".*_\\d")) {
			return term.substring(0, term.lastIndexOf("_"));
		}
		return term;
	}
}
