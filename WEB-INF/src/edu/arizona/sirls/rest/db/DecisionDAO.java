package edu.arizona.sirls.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public List<Decision> getDecisions(int uploadId) throws SQLException {
		List<Decision> result = new ArrayList<Decision>();
		this.openConnection();
		
		String sql = "SELECT * FROM decisions WHERE uploadID=" + uploadId;
		PreparedStatement preparedStatement = this.executeSQL(sql);
		ResultSet resultSet = preparedStatement.getResultSet();
		while (resultSet.next())
			result.add(new Decision(resultSet.getString("term"), resultSet.getBoolean("isMainTerm"), resultSet.getString("category")));
		
		this.closeConnection();
		return result;
	}
}
