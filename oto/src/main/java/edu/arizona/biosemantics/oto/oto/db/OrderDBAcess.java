package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class OrderDBAcess extends DatabaseAccess {

	public OrderDBAcess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = Logger.getLogger(OrderDBAcess.class);

	private static OrderDBAcess instance;

	public static OrderDBAcess getInstance() throws IOException {
		if (instance == null) {
			instance = new OrderDBAcess();
		}
		return instance;
	}

	/**
	 * change order name from oldName to replacement in given group
	 * 
	 * @param dataset
	 * @param groupID
	 * @param oldName
	 * @param replacement
	 * @return
	 * @throws SQLException
	 */
	public boolean changeOrderName(String dataset, String groupID,
			String oldName, String replacement) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			String sql = "update " + dataset + "_web_orders " + "set name = '"
					+ replacement + "' " + "where base = " + groupID
					+ " and name = '" + oldName + "'";
			stmt = conn.createStatement();
			stmt.execute(sql);
			rv = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't execute changeOrderName in OrderDBAcess: ",
					exe);
			System.out.println(exe);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return rv;
	}

}
