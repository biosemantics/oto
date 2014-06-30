package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.oto.beans.DatasetBean;
import edu.arizona.biosemantics.oto.oto.beans.DatasetStatistics;
import edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper;
import edu.arizona.biosemantics.oto.oto.beans.User;

public class GeneralDBAccess extends DatabaseAccess {

	public GeneralDBAccess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = Logger
			.getLogger(GeneralDBAccess.class);

	private static GeneralDBAccess instance;

	public static GeneralDBAccess getInstance() throws IOException {
		if (instance == null) {
			instance = new GeneralDBAccess();
		}
		return instance;
	}

	/**
	 * get datasets list with statistics for a given user
	 * 
	 * rules: 1. user owns the dataset
	 * 
	 * 2. list all datasets for super user
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<DatasetStatistics> getDatasetsByUser(User user)
			throws SQLException {
		ArrayList<DatasetStatistics> datasetStats = new ArrayList<DatasetStatistics>();

		Connection conn = null;
		Statement stmt_ds = null;
		Statement stmt_detail = null;
		ResultSet rs_ds = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			// get list of datasets names
			stmt_ds = conn.createStatement();
			String sql = "select dataset from dataset_owner where ownerID = "
					+ Integer.toString(user.getUserId());

			if (user.getRole().equals("S")) {
				sql = "select prefix from datasetprefix";
			}
			rs_ds = stmt_ds.executeQuery(sql);
			while (rs_ds.next()) {
				String datasetName = rs_ds.getString(1);
				DatasetStatistics datasetStat = new DatasetStatistics();
				datasetStat.setDatasetName(datasetName);
				stmt_detail = conn.createStatement();

				// get isPrivate
				rs = stmt_detail
						.executeQuery("select isPrivate from datasetprefix where prefix = '"
								+ datasetName + "'");
				if (rs.next()) {
					datasetStat.setPrivate(rs.getBoolean(1));
				}

				/**
				 * get statistic information of each dataset
				 */
				// categorization page
				rs = stmt_detail
						.executeQuery("select distinct count(term) from "
								+ datasetName + "_web_grouped_terms");
				if (rs.next()) {
					datasetStat.setNumTotalTerms(rs.getInt(1));
				}
				rs = stmt_detail.executeQuery("select count(*) from "
						+ datasetName + "_user_terms_decisions");
				if (rs.next()) {
					datasetStat.setNumDecisions(rs.getInt(1));
				}

				// structure page
				rs = stmt_detail
						.executeQuery("select distinct count(tagName) from "
								+ datasetName + "_web_tags");
				if (rs.next()) {
					int count = rs.getInt(1);
					count = (count >= 7 ? count - 7 : count);
					datasetStat.setNumTotalTags(count);
				}
				rs = stmt_detail.executeQuery("select count(*) from "
						+ datasetName + "_user_tags_decisions");
				if (rs.next()) {
					int count = rs.getInt(1);
					count = (count >= 7 ? count - 7 : count);
					datasetStat.setNumDecisionsInHierarchy(count);
				}

				// order page
				rs = stmt_detail.executeQuery("select distinct count(id) from "
						+ datasetName + "_web_orders");
				// todo: order count need test
				if (rs.next()) {
					datasetStat.setNumTotalOrders(rs.getInt(1));
				}
				rs = stmt_detail.executeQuery("select count(*) from "
						+ datasetName + "_user_orders_decisions");
				if (rs.next()) {
					datasetStat.setNumDecisionsInOrders(rs.getInt(1));
				}

				datasetStats.add(datasetStat);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute getDatasetsByUser in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (rs_ds != null) {
				rs_ds.close();
			}
			if (stmt_ds != null) {
				stmt_ds.close();
			}

			if (stmt_detail != null) {
				stmt_detail.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return datasetStats;
	}

	/**
	 * set dataset privacy
	 * 
	 * @param datasetName
	 * @param isPrivate
	 * @return
	 * @throws SQLException
	 */
	public boolean setDatasetPrivacy(String datasetName, boolean isPrivate)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean rv = false;

		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("update datasetprefix set isPrivate = ? where prefix = ?");
			pstmt.setBoolean(1, isPrivate);
			pstmt.setString(2, datasetName);
			pstmt.executeUpdate();
			rv = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute getDatasetsByUser in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
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
	 * import sentences with duplication removal
	 * 
	 * @param conn
	 * @param stmt
	 * @param dataset
	 * @param fileName
	 * @param sentences
	 * @throws Exception
	 */
	public void importSentences(Connection conn, PreparedStatement pstmt,
			String dataset, String fileName, ArrayList<String> sentences)
			throws Exception {
		ResultSet rset = null;
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			// read out existing sentences for duplication check
			pstmt = conn.prepareStatement("select originalSent from " + dataset
					+ "_sentence");
			rset = pstmt.executeQuery();
			while (rset.next()) {
				map.put(rset.getString(1).trim(), true);
			}

			// insert if does not exist
			for (String sentence : sentences) {
				if (map.get(sentence) == null) {
					pstmt = conn.prepareStatement("insert into " + dataset
							+ "_sentence "
							+ "(sentid, source, sentence, originalSent) values"
							+ " (0, ?, ?, ?)");
					pstmt.setString(1, fileName);
					pstmt.setString(2, sentence);
					pstmt.setString(3, sentence);
					pstmt.executeUpdate();
					map.put(sentence, true);
				}
			}

			conn.commit();
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute importSentences in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
			throw exe;
		} finally {
			if (rset != null) {
				rset.close();
			}
		}
	}

	/**
	 * reset OTO_Demo dataset to initial status for a give page
	 * 
	 * @param pageIndex
	 *            : 1-categorization, 2-hierarchy, 3-orders
	 * @return
	 * @throws SQLException
	 */
	public boolean resetOTODemo(String pageIndex) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		boolean rv = false;

		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("delete from OTO_Demo_comments;");

			if (pageIndex.equals("1")) {
				// categorization page
				stmt.executeUpdate("delete from OTO_Demo_confirmed_category;");
				stmt.executeUpdate("delete from OTO_Demo_review_history;");
				stmt.executeUpdate("delete from OTO_Demo_syns;");
				stmt.executeUpdate("delete from OTO_Demo_term_category;");
				stmt.executeUpdate("delete from OTO_Demo_user_terms_decisions;");
				// set finalized to be false
				stmt.executeUpdate("update datasetprefix set grouptermsdownloadable = false "
						+ "where prefix = 'OTO_Demo'");
			} else if (pageIndex.equals("2")) {
				// hierarchy page
				stmt.executeUpdate("delete from OTO_Demo_confirmed_paths;");
				stmt.executeUpdate("delete from OTO_Demo_user_tags_decisions where tagID > 7;");
				stmt.executeUpdate("update datasetprefix set structurehierarchydownloadable = false "
						+ "where prefix = 'OTO_Demo'");
			} else if (pageIndex.equals("3")) {
				// order page
				stmt.executeUpdate("delete from OTO_Demo_confirmed_orders;");
				stmt.executeUpdate("delete from OTO_Demo_user_orders_decisions;");
				stmt.executeUpdate("update datasetprefix set termorderdownloadable = false "
						+ "where prefix = 'OTO_Demo'");
				
				//delete user created terms and orders (3 orders)
				stmt.executeUpdate("delete from OTO_Demo_web_orders_terms where id > 29;");
				stmt.executeUpdate("delete from OTO_Demo_web_orders where id > 6;");
				
				//reset order name to initial name (3 orders)
				stmt.executeUpdate("update OTO_Demo_web_orders set name = 'Pubescence Order' where id = 2;");
				stmt.executeUpdate("update OTO_Demo_web_orders set name = 'Shape Order' where id = 4;");
				stmt.executeUpdate("update OTO_Demo_web_orders set name = 'Orientation Order' where id = 6;");
			}

			rv = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't execute resetOTODemo in GeneralDBAccess: ",
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

	/**
	 * get selectable datasets in home page
	 * 
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	public ArrayList<DatasetBean> getSelectableDatasets(int userID)
			throws Exception {
		ArrayList<DatasetBean> datasets = new ArrayList<DatasetBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "select prefix, isPrivate from datasetprefix where "
					+ "(isPrivate = false or "
					+ "(prefix in (select distinct dataset from dataset_owner where ownerID = ?))) "
					+ "and "
					+ "(grouptermsdownloadable = false or structurehierarchydownloadable = false "
					+ "or termorderdownloadable = false) "
					+ "order by time_last_accessed desc";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String name = rset.getString(1);
				DatasetBean ds = new DatasetBean(name);
				if (GlossaryNameMapper.getInstance().isGlossaryReservedDataset(
						name)) {
					ds.setSystemReserved(true);
				}
				ds.setPrivate(rset.getBoolean(2));

				datasets.add(ds);
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in GlossaryNameMapper.getInstance(): getSelectableDatasets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

}
