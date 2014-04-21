package edu.arizona.biosemantics.oto.oto.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GlossaryTypeDAO extends AbstractDAO {

	public GlossaryTypeDAO() throws Exception {
		super();
	}

	private static GlossaryTypeDAO instance = null;
	
	public static GlossaryTypeDAO getInstance() throws Exception {
		if(instance == null)
			instance = new GlossaryTypeDAO();
		return instance;
	}

	public List<String> getGlossaryTypes() throws SQLException {
		List<String> result = new ArrayList<String>();
		this.openConnection();
		
		String sql = "SELECT glossaryName FROM glossarytypes";
		PreparedStatement preparedStatement = this.prepareStatement(sql);
		ResultSet resultSet = preparedStatement.executeQuery();
		while(resultSet.next()) {
			String glossaryType = resultSet.getString("glossaryName");
			result.add(glossaryType);
		}
		preparedStatement.close();		
		this.closeConnection();
		return result;
	}

}
