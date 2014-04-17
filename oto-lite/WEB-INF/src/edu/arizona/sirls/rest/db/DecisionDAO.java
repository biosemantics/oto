package edu.arizona.sirls.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.sirls.rest.beans.Decision;

public class DecisionDAO extends AbstractDAO {
	
	private DecisionDAO() throws Exception {
		super();
	}

	private static DecisionDAO instance;
	
	public static DecisionDAO getInstance() throws Exception {
		if(instance == null)
			instance = new DecisionDAO();
		return instance;
	}

	public List<Decision> getDecisions(int uploadId) throws Exception {
		List<Decision> result = new ArrayList<Decision>();
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
				String hasSynonymSQL = "SELECT * FROM synonyms WHERE uploadID=" + uploadId + " AND mainTerm='" + mainTerm + "' AND category='" + category +"'";
				PreparedStatement statement = this.executeSQL(hasSynonymSQL);
				ResultSet hasSynonymResult = statement.getResultSet();
				hasSynonym = hasSynonymResult.next();
				hasSynonymResult.close();
				statement.close();
			
				result.add(new Decision(String.valueOf(i++), normalizeTerm(mainTerm), category, hasSynonym));
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
