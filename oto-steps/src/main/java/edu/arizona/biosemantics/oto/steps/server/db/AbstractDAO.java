package edu.arizona.biosemantics.oto.steps.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import edu.arizona.biosemantics.oto.steps.server.Configuration;

public abstract class AbstractDAO {
	private String databaseName;
	private String databaseUser;
	private String databasePassword;

	public AbstractDAO() throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		/*Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		this.databaseName = properties.getProperty("otolite_databaseName");
		this.databaseUser = properties.getProperty("otolite_databaseUser");
		this.databasePassword = properties.getProperty("otolite_databasePassword");*/
		this.databaseName = Configuration.otolite_databaseName;
		this.databaseUser = Configuration.otolite_databaseUser;
		this.databasePassword = Configuration.otolite_databasePassword;
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/"
				+ databaseName, databaseUser, databasePassword);
	}

	public void closeConnection(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	public static void close(ResultSet... rsets) throws SQLException {
		for (ResultSet rset : rsets) {
			if (rset != null)
				rset.close();
		}
	}

	public static void close(ResultSet rset) throws SQLException {
		if (rset != null)
			rset.close();
	}

	public static void close(PreparedStatement pstmt) throws SQLException {
		if (pstmt != null)
			pstmt.close();
	}

	public static void close(Statement stmt) throws SQLException {
		if (stmt != null)
			stmt.close();
	}
}
