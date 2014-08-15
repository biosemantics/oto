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
				stmt.executeUpdate("delete from OTO_Demo_categories "
						+ "where category not in (select category from categories); ");
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

				// delete user created terms and orders (3 orders)
				stmt.executeUpdate("delete from OTO_Demo_web_orders_terms where id > 29;");
				stmt.executeUpdate("delete from OTO_Demo_web_orders where id > 6;");

				// reset order name to initial name (3 orders)
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
	 * check if glossary table exists in database
	 * 
	 * @param tablename
	 * @param type
	 *            termCategory | synonym
	 * @return
	 * @throws SQLException
	 */
	public boolean checkGlossaryTable(String tablename, String type)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		boolean rv = false;

		try {
			conn = getConnection();
			String sql = "select term, category, termID from " + tablename;
			if (type.equals("synonym")) {
				sql = "select term, category, synonym, termID from "
						+ tablename;
			}
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset != null) {
				rv = true;
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute checkGlossaryTable in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (rset != null) {
				rset.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return rv;
	}

	/**
	 * check if dataset prefix exist in database
	 * 
	 * @param datasetprefix
	 * @return
	 * @throws SQLException
	 */
	public boolean validateDatasetPrefix(String datasetprefix)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		boolean rv = false;

		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select prefix from datasetprefix where prefix = ?");
			pstmt.setString(1, datasetprefix);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = true;
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute validateDatasetPrefix in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (rset != null) {
				rset.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return rv;
	}

	/**
	 * get number of records in table
	 * 
	 * @param tablename
	 * @return
	 * @throws SQLException
	 */
	public int getNumRecords(String tablename) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int count = 0;

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select count(*) from " + tablename);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt(1);
			}
		} catch (Exception exe) {
			LOGGER.error("Couldn't execute getNumRecords in GeneralDBAccess: ",
					exe);
			System.out.println(exe);
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (rset != null) {
				rset.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return count;
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

	/**
	 * all the tables for one dataset
	 * 
	 * @param datasetName
	 * @return
	 */
	public ArrayList<String> getDatasetTableList() {
		ArrayList<String> tableList = new ArrayList<String>();
		// initial data
		tableList.add("_web_grouped_terms");
		tableList.add("_web_orders");
		tableList.add("_web_orders_terms");
		tableList.add("_web_tags");
		tableList.add("_categories");
		tableList.add("_sentence");

		// tables to hold decisions
		tableList.add("_user_orders_decisions");
		tableList.add("_user_tags_decisions");
		tableList.add("_user_terms_decisions");
		tableList.add("_user_terms_relations");
		tableList.add("_review_history");
		tableList.add("_comments");

		// approved decisions
		tableList.add("_confirmed_category");
		tableList.add("_confirmed_orders");
		tableList.add("_confirmed_paths");

		// finalized tables
		tableList.add("_syns");
		tableList.add("_term_category");

		return tableList;
	}

	/**
	 * for categorization page, clean up data with given glossary tables by
	 * deleting existing records related to terms in clean glossary and
	 * re-create matching records for those terms
	 * 
	 * @param dataset
	 * @param clean_term_category_table
	 * @param clean_syns_table
	 * @return
	 * @throws Exception
	 */
	public boolean cleanupDatasetWithGivenGlossaryTables(String dataset,
			String clean_term_category_table, String clean_syns_table,
			int userid) throws Exception {
		boolean success = false;

		boolean hasSynTable = true;
		if (clean_syns_table.equals("")) {
			hasSynTable = false;
		}

		// initialize varibales
		Connection conn = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rset = null, rset2 = null, rset3 = null;
		HashMap<String, Integer> termIndexMap = new HashMap<String, Integer>();
		int termCount = 0;
		int termCategoryCount = 0;
		int termSynPairCount = 0;
		try {
			conn = getConnection();
			System.out.println("*********Update Started************** "
					+ System.currentTimeMillis());
			conn.setAutoCommit(false);

			/**
			 * match category names
			 */
			System.out.println("processing category changes ... "
					+ System.currentTimeMillis());
			String sql = "select distinct category from "
					+ clean_term_category_table + " where category not in "
					+ "(select category from " + dataset + "_categories)";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				// insert new category name
				sql = "insert into " + dataset
						+ "_categories (category, definition) values (?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, rset.getString(1));
				pstmt.setString(2,
						"Created by dataset cleaning up. Definition needs to be added. ");
				pstmt.executeUpdate();
			}

			/**
			 * clean up all the approvals
			 */
			System.out.println("clean up all the approvals ... "
					+ System.currentTimeMillis());
			sql = "update " + dataset + "_confirmed_category "
					+ "set categoryApproved = false, "
					+ "synonymApproved = false, " + "isApprovedSynonym = false";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			System.out
					.println("clean up tracks of terms in the clean glossary ... "
							+ System.currentTimeMillis());

			/**
			 * clean up all records related to terms in the clean glossary here
			 * only consider terms that are going to be re-added
			 */

			if (hasSynTable) {
				sql = "select distinct term from " + "(select term from "
						+ clean_term_category_table + " union "
						+ " (select synonym from " + clean_syns_table
						+ " where term in " + "(select term from "
						+ clean_term_category_table + "))) a";
			} else {
				sql = "select distinct term from " + clean_term_category_table;
			}

			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String term = rset.getString(1);
				/**
				 * delete all decisions related to this term, including the
				 * original term and all its copies
				 */
				sql = "delete from " + dataset + "_user_terms_decisions "
						+ "where term rlike '^" + term + "(_\\d+)?'";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();

				// delete from _confirmed_category table
				sql = "delete from " + dataset + "_confirmed_category "
						+ "where term = ? or synonym = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				pstmt.setString(2, term);
				pstmt.executeUpdate();

				/**
				 * delete all review history except userid = userid
				 */
				sql = "delete from " + dataset
						+ "_review_history where term = ? and userid <> "
						+ userid;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				pstmt.executeUpdate();

				/**
				 * keep all the comments for now
				 */
			}

			System.out.println("Starts to add matching records ... "
					+ System.currentTimeMillis());
			/**
			 * term -> (n) categories -> (m) synonyms
			 * 
			 * might lose some terms if the clean glossary is not valid, e.g.
			 * terms in synonyms table but not in term-category table
			 */
			// only consider main terms at this point
			sql = "select distinct term from " + clean_term_category_table;
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String term = rset.getString(1);

				termCount++;
				System.out.println("[" + termCount + "] processing term: "
						+ term);

				// check if term exist in _web_grouped_terms
				sql = "select term from " + dataset + "_web_grouped_terms "
						+ "where term = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				rset2 = pstmt.executeQuery();
				if (!rset2.next()) {
					// doean't exist, this is a new term
					sql = "insert into "
							+ dataset
							+ "_web_grouped_terms (groupID, term, sourceDataset) "
							+ "values (?, ?, ?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, 0);
					pstmt.setString(2, term);
					pstmt.setString(3, dataset);
					pstmt.executeUpdate();
				}

				/**
				 * insert record to match glossary
				 */

				// get categoies associated with this term
				sql = "select term, category from " + clean_term_category_table
						+ " where term = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				rset2 = pstmt.executeQuery();
				while (rset2.next()) {// for each category of this term
					String category = rset2.getString("category");
					termCategoryCount++;
					System.out.println("\t[" + termCategoryCount
							+ "] processing category: " + category);

					// check if this term is an approved synonym in this
					// category
					if (hasSynTable) {
						sql = "select synonym from " + clean_syns_table
								+ " where synonym = ? and category = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, term);
						pstmt.setString(2, category);
						rset3 = pstmt.executeQuery();
						if (rset3.next()) {
							// is not a main term, do not insert
							continue;
						}
					}

					// get and maintain termIndex
					int termIndex = 0;
					String termWithIndex = term;
					if (termIndexMap.get(term) != null) {
						termIndex = termIndexMap.get(term);
						termWithIndex = term + "_"
								+ Integer.toString(termIndex);
						termIndexMap.put(term, termIndex + 1);
					} else {
						termIndexMap.put(term, 1);
					}

					// get synonyms
					boolean hasSyn = false;
					ArrayList<String> syns = new ArrayList<String>();
					if (hasSynTable) {
						sql = "select * from " + clean_syns_table
								+ " where term = ? and category = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, term);
						pstmt.setString(2, category);
						rset3 = pstmt.executeQuery();
						while (rset3.next()) {
							hasSyn = true;
							syns.add(rset3.getString("synonym"));
						}
					}

					String insert_decision_sql = "insert into "
							+ dataset
							+ "_user_terms_decisions ("
							+ "term, userid, decision, decisiondate, isAdditional, "
							+ "relatedTerms, isActive, isLatest, hasConflict, hasSyn) values "
							+ "(?, ?, ?, sysdate(), ?, "
							+ "?, true, true, false, ?)";
					String insert_confirm_sql = "insert into "
							+ dataset
							+ "_confirmed_category ("
							+ "term, category, userid, confirmdate, categoryApproved, "
							+ "synonymApproved, synonym, termIndex, termWithIndex, synonymWithIndex, "
							+ "isApprovedSynonym) values ("
							+ "?, ?, ?, sysdate(), true, " + "?, ?, ?, ?, ?, "
							+ "?)";

					if (hasSyn) {
						String syns_str = ""; // the synonym string for main
												// term

						for (String syn : syns) {
							// compute synonym with index
							int synIndex = 0;
							String synWithIndex = syn;
							if (termIndexMap.get(syn) != null) {
								synIndex = termIndexMap.get(syn);
								synWithIndex = syn + "_"
										+ Integer.toString(synIndex);
								termIndexMap.put(syn, synIndex + 1);
							} else {
								termIndexMap.put(syn, 1);
							}

							// add synonym to synonym string
							if (syns_str.equals("")) {
								syns_str = "'" + synWithIndex + "'";
							} else {
								syns_str += ", '" + synWithIndex + "'";
							}

							termSynPairCount++;
							System.out.println("\t\t[" + termSynPairCount
									+ "] processing synonym: " + syn);

							// insert synonym to _user_terms_decisions
							pstmt = conn.prepareStatement(insert_decision_sql);
							pstmt.setString(1, synWithIndex); // term
							pstmt.setInt(2, userid);// userid
							pstmt.setString(3, category);// decision
							pstmt.setBoolean(4, true);// isAdditional
							pstmt.setString(5, "synonym of '" + termWithIndex
									+ "'");// related terms
							pstmt.setBoolean(6, false);// hassyn
							pstmt.executeUpdate();

							// insert (term, synonym) to _confirmed_category
							pstmt = conn.prepareStatement(insert_confirm_sql);
							pstmt.setString(1, term);// term
							pstmt.setString(2, category);// category
							pstmt.setInt(3, userid);// userid
							pstmt.setBoolean(4, true);// synonymApproved
							pstmt.setString(5, syn);// synonym
							pstmt.setInt(6, termIndex); // term index
							pstmt.setString(7, termWithIndex);// term with index
							pstmt.setString(8, synWithIndex); // synonymWithIndex
							pstmt.setBoolean(9, false); // isApprovedSynonym
							pstmt.executeUpdate();

							// insert syn to _confirmed_category
							pstmt = conn.prepareStatement(insert_confirm_sql);
							pstmt.setString(1, syn);// term
							pstmt.setString(2, category);// category
							pstmt.setInt(3, userid);// userid
							pstmt.setBoolean(4, false);// synonymApproved
							pstmt.setString(5, "");// synonym
							pstmt.setInt(6, synIndex); // term index
							pstmt.setString(7, synWithIndex);// term with index
							pstmt.setString(8, ""); // synonymWithIndex
							pstmt.setBoolean(9, true); // isApprovedSynonym
							pstmt.executeUpdate();
						}

						// insert main term into _user_terms_decisions table
						pstmt = conn.prepareStatement(insert_decision_sql);
						pstmt.setString(1, termWithIndex); // term
						pstmt.setInt(2, userid);// userid
						pstmt.setString(3, category);// decision
						pstmt.setBoolean(4, false);// isAdditional
						pstmt.setString(5, syns_str);// related terms
						pstmt.setBoolean(6, true);// hassyn
						pstmt.executeUpdate();
					} else { // no synonym
						pstmt2 = conn.prepareStatement(insert_decision_sql);
						pstmt2.setString(1, termWithIndex); // term
						pstmt2.setInt(2, userid);// userid
						pstmt2.setString(3, category);// decision
						pstmt2.setBoolean(4, false);// isAdditional
						pstmt2.setString(5, "");// related terms
						pstmt2.setBoolean(6, false);// hassyn
						pstmt2.executeUpdate();

						pstmt2 = conn.prepareStatement(insert_confirm_sql);
						pstmt2.setString(1, term);// term
						pstmt2.setString(2, category);// category
						pstmt2.setInt(3, userid);// userid
						pstmt2.setBoolean(4, false);// synonymApproved
						pstmt2.setString(5, "");// synonym
						pstmt2.setInt(6, termIndex); // term index
						pstmt2.setString(7, termWithIndex);// term with index
						pstmt2.setString(8, ""); // synonymWithIndex
						pstmt2.setBoolean(9, false); // isApprovedSynonym
						pstmt2.executeUpdate();
					}
				}
			}

			// automatically reopen the dataset
			pstmt = conn
					.prepareStatement("update datasetprefix set grouptermsdownloadable = false "
							+ "where prefix = ?");
			pstmt.setString(1, dataset);
			pstmt.executeUpdate();

			conn.commit();
			success = true;
		} catch (Exception exe) {
			exe.printStackTrace();
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);

			if (rset2 != null) {
				rset2.close();
			}

			if (rset3 != null) {
				rset3.close();
			}

			if (pstmt2 != null) {
				pstmt2.close();
			}
		}

		System.out.println("Done mapping clean dataset: term: " + termCount
				+ "; term category: " + termCategoryCount
				+ "; synonyms pairs: " + termSynPairCount + "   "
				+ System.currentTimeMillis());

		return success;
	}

}
