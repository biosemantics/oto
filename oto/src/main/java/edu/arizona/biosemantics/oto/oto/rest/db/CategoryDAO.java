package edu.arizona.biosemantics.oto.oto.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.Category;


public class CategoryDAO extends AbstractDAO {

	private static CategoryDAO instance;

	public static CategoryDAO getInstance() throws Exception {
		if(instance == null)
			instance = new CategoryDAO();
		return instance;
	}
	
	private CategoryDAO() throws Exception {
		super();
	}

	public List<Category> getCategories() throws SQLException {
		List<Category> result = new ArrayList<Category>();
		this.openConnection();
		String sql = "SELECT * FROM categories";
		PreparedStatement preparedStatement = this.executeSQL(sql);
		ResultSet resultSet = preparedStatement.getResultSet();
		while (resultSet.next())
			result.add(new Category(resultSet.getString("category"), resultSet.getString("definition")));
		preparedStatement.close();
		this.closeConnection();	
		return result;
	}

}
