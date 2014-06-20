package edu.arizona.biosemantics.oto.steps.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import edu.arizona.biosemantics.oto.steps.shared.beans.orders.Order;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderCategory;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.OrderSet;
import edu.arizona.biosemantics.oto.steps.shared.beans.orders.TermInOrder;

public class OrderDAO extends AbstractDAO {

	private static OrderDAO instance;

	public static OrderDAO getInstance() throws Exception {
		if (instance == null)
			instance = new OrderDAO();
		return instance;
	}

	private OrderDAO() throws Exception {
		super();
	}

	/**
	 * save the orderset in a transaction
	 * 
	 * @param orderSet
	 * @throws SQLException
	 */
	public void saveOrderSet(OrderSet orderSet) throws SQLException {
		Statement st = null;
		Connection conn = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			st = conn.createStatement();
			conn.setAutoCommit(false);

			// process new terms
			ArrayList<String> terms = orderSet.getTerms();
			String sql = "insert into terms_in_order_category (categoryID, termName) "
					+ "values ";
			for (String term : terms) {
				st.executeUpdate(sql + "(" + orderSet.getCategoryID() + ", '"
						+ term + "')");
			}

			// process order
			ArrayList<Order> orders = orderSet.getOrders();
			for (Order order : orders) {
				String orderID = order.getOrderID();
				if (order.isNewlyCreated()) {
					sql = "insert into orders_in_order_category (categoryID, orderName, orderDescription) "
							+ "values ("
							+ orderSet.getCategoryID()
							+ ", '"
							+ order.getOrderName()
							+ "', '"
							+ order.getOrderDescription() + "')";
					st.executeUpdate(sql);
					sql = "SELECT LAST_INSERT_ID();";
					rset = st.executeQuery(sql);
					if (rset.next()) {
						orderID = Integer.toString(rset.getInt(1));
					}
				} else {
					// delete existing terms
					sql = "delete from term_position_in_order where orderID = "
							+ orderID;
					st.executeUpdate(sql);
				}

				// insert terms in to term_position_in_order
				ArrayList<TermInOrder> termsInOrder = order.getTermsInOrder();
				for (TermInOrder termInOrder : termsInOrder) {
					sql = "insert into term_position_in_order (orderID, termName, position) "
							+ "values ("
							+ orderID
							+ ", '"
							+ termInOrder.getTermName()
							+ "', "
							+ termInOrder.getPosition() + ")";
					st.executeUpdate(sql);
				}
			}

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(st);
			close(rset);
		}
	}

	public ArrayList<OrderCategory> getOrderCategories(int uploadID)
			throws SQLException {
		ArrayList<OrderCategory> categories = new ArrayList<OrderCategory>();
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select categoryID, categoryName from order_categories "
					+ "where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				categories.add(new OrderCategory(rset.getString("categoryID"),
						rset.getString("categoryName")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(rset);
			close(pstmt);
		}

		return categories;
	}

	/**
	 * get all order sets from db
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<OrderSet> getOrderSets(int uploadID) throws SQLException {
		ArrayList<OrderSet> orderSets = new ArrayList<OrderSet>();
		ResultSet rset = null, rset2 = null, rset3 = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select categoryID, categoryName from order_categories "
					+ "where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				int categoryID = rset.getInt("categoryID");
				OrderSet orderSet = new OrderSet(Integer.toString(categoryID),
						rset.getString("categoryName"));

				// get base terms in this category
				ArrayList<String> baseTerms = new ArrayList<String>();
				sql = "select termName from terms_in_order_category where categoryID = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, categoryID);
				rset2 = pstmt.executeQuery();
				
				while (rset2.next()) {
					baseTerms.add(rset2.getString("termName"));
				}
				orderSet.setTerms(baseTerms);

				// get orders in this category
				ArrayList<Order> orders = new ArrayList<Order>();
				sql = "select orderID, orderName, orderDescription from orders_in_order_category "
						+ "where categoryID = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, categoryID);
				rset2 = pstmt.executeQuery();
				while (rset2.next()) {
					// prepare order
					int orderID = rset2.getInt("orderID");
					Order order = new Order(Integer.toString(orderID),
							rset2.getString("orderName"),
							rset2.getString("orderDescription"));

					sql = "select termName, position from term_position_in_order "
							+ "where orderID = ? order by position, termName";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, orderID);
					rset3 = pstmt.executeQuery();
					ArrayList<TermInOrder> termsInOrder = new ArrayList<TermInOrder>();
					while (rset3.next()) {
						termsInOrder.add(new TermInOrder(rset3
								.getString("termName"), rset3
								.getInt("position")));
					}
					order.setTermsInOrder(termsInOrder);

					// add order into order list
					orders.add(order);
				}
				orderSet.setOrders(orders);

				// add this orderset to result
				orderSets.add(orderSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// close everything with or without exception
			close(rset);
			close(rset2);
			close(rset3);
			close(pstmt);
			closeConnection(conn);
		}

		return orderSets;
	}

	public OrderSet getOrderSetByID(int categoryID) throws SQLException {
		OrderSet orderSet = null;
		ResultSet rset = null, rset2 = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			orderSet = new OrderSet();
			orderSet.setCategoryID(Integer.toString(categoryID));

			// get base terms in this category
			ArrayList<String> baseTerms = new ArrayList<String>();
			String sql = "select termName from terms_in_order_category where categoryID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, categoryID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				baseTerms.add(rset.getString("termName"));
			}
			orderSet.setTerms(baseTerms);

			// get orders in this category
			ArrayList<Order> orders = new ArrayList<Order>();
			sql = "select orderID, orderName, orderDescription from orders_in_order_category "
					+ "where categoryID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, categoryID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				// prepare order
				int orderID = rset.getInt("orderID");
				Order order = new Order(Integer.toString(orderID),
						rset.getString("orderName"),
						rset.getString("orderDescription"));

				sql = "select termName, position from term_position_in_order "
						+ "where orderID = ? order by position, termName";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, orderID);
				rset2 = pstmt.executeQuery();
				ArrayList<TermInOrder> termsInOrder = new ArrayList<TermInOrder>();
				while (rset2.next()) {
					termsInOrder.add(new TermInOrder(rset2
							.getString("termName"), rset2.getInt("position")));
				}
				order.setTermsInOrder(termsInOrder);

				// add order into order list
				orders.add(order);
			}
			orderSet.setOrders(orders);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// close everything with or without exception
			close(rset);
			close(rset2);
			close(pstmt);
			closeConnection(conn);
		}

		return orderSet;
	}
}
