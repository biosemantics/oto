package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import edu.arizona.biosemantics.oto.oto.beans.SimpleOrderBean;

public class OrderDBAcess extends DatabaseAccess {

	public OrderDBAcess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

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
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "update " + dataset + "_web_orders " + "set name = ? "
					+ "where base = " + groupID + " and name = '" + oldName
					+ "'";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, replacement);
			pstmt.executeUpdate();
			rv = true;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return rv;
	}

	/**
	 * delete existing orders and import orders
	 * 
	 * @param dataset
	 * @param orders
	 * @throws Exception 
	 */
	public void importOrders(String dataset, ArrayList<SimpleOrderBean> orders)
			throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			// delete existing orders and terms in orders
			stmt.executeUpdate("delete from " + dataset + "_web_orders_terms");
			stmt.executeUpdate("delete from " + dataset + "_web_orders");

			for (SimpleOrderBean order : orders) {
				// insert base order
				stmt.executeUpdate(
						"insert into " + dataset + "_web_orders "
								+ "(name, isBase) " + "values " + "('"
								+ order.getOrderName() + "', true)",
						Statement.RETURN_GENERATED_KEYS);
				rset = stmt.getGeneratedKeys();
				if (rset.next()) {
					// insert base order terms
					ArrayList<String> terms = order.getTerms();
					int baseOrderID = rset.getInt(1);
					boolean isBase = true; /*
											 * make the first term the base. May
											 * sounds not exact reasonable for
											 * now, but should be fixed after
											 * the change of order page
											 */
					for (String term : terms) {
						String sql = "insert into " + dataset
								+ "_web_orders_terms "
								+ "(orderID, name, isBase) values " + "("
								+ baseOrderID + ", '" + term + "', ";
						if (isBase) {
							sql += "true)";
							isBase = false;
						} else {
							sql += "false)";
						}
						stmt.executeUpdate(sql);
					}
				}
			}

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					ex1.printStackTrace();
					throw ex1;
				}
			}
			throw e;
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

}
