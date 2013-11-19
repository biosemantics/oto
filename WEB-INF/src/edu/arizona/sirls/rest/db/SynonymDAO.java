package edu.arizona.sirls.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.sirls.rest.beans.Synonym;

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
		
		String sql = "SELECT * FROM synonyms WHERE uploadID=" + uploadId;
		PreparedStatement preparedStatement = this.executeSQL(sql);
		ResultSet resultSet = preparedStatement.getResultSet();
		while (resultSet.next())
			result.add(new Synonym(resultSet.getString("mainTerm"), resultSet.getString("synonym")));
		
		this.closeConnection();
		return result;
	}
}
