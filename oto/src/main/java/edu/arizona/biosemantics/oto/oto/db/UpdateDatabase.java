package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.oto.beans.TermAndExtentionBean;

public class UpdateDatabase extends DatabaseAccess {
	private static UpdateDatabase instance;

	public static UpdateDatabase getInstance() throws IOException {
		if (instance == null) {
			instance = new UpdateDatabase();
		}
		return instance;
	}

	public UpdateDatabase() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = Logger.getLogger(UpdateDatabase.class);

	public boolean executeSqlForEachDataset(ArrayList<String> sqls)
			throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Statement st = null;

		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select distinct prefix from datasetprefix");
			rset = pstmt.executeQuery();
			ArrayList<String> datasets = new ArrayList<String>();
			while (rset.next()) {
				datasets.add(rset.getString(1));
			}

			st = conn.createStatement();

			// update table schema
			for (String dataset : datasets) {
				for (String sql : sqls) {
					sql.replaceAll("prefix_to_be_replaced", dataset);
				}
				System.out.println("Dataset '" + dataset + "' update done!");
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to check if the email exists");
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
		}

		return success;
	}

	/**
	 * 1. modify _confirmed_category table schema 2. update data in
	 * _confirmed_category from _user_terms_decisions
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean updateDB_on_070913() throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet rset_2 = null;
		Statement st = null;

		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select distinct prefix from datasetprefix");
			rset = pstmt.executeQuery();
			ArrayList<String> datasets = new ArrayList<String>();
			while (rset.next()) {
				datasets.add(rset.getString(1));
			}

			st = conn.createStatement();

			// update table schema
			for (String dataset : datasets) {
				if (dataset.equals("OTO_Demo")) {
					// continue;
				}

				// delete empty terms in _web_grouped_terms
				String sql = "delete from " + dataset
						+ "_web_grouped_terms where term is null or term = ''";
				st.executeUpdate(sql);

				// delete empty terms in _user_terms_decisions
				sql = "delete from "
						+ dataset
						+ "_user_terms_decisions where term is null or term = ''";
				st.executeUpdate(sql);

				sql = "select term, category, confirmDate from " + dataset
						+ "_confirmed_category";
				pstmt = conn.prepareStatement(sql);
				rset_2 = pstmt.executeQuery(); // hold existing confirmed
												// categorizations

				// delete old records
				sql = "delete from " + dataset
						+ "_confirmed_category where userid is null";
				st.execute(sql);

				st.executeUpdate("alter table " + dataset
						+ "_confirmed_category drop accepted;");
				st.executeUpdate("alter table " + dataset
						+ "_confirmed_category drop confirmedBy;");
				st.executeUpdate("alter table " + dataset
						+ "_confirmed_category drop copiedFrom;");
				st.executeUpdate("alter table "
						+ dataset
						+ "_confirmed_category add categoryApproved Boolean default false;");
				st.executeUpdate("alter table "
						+ dataset
						+ "_confirmed_category add synonymApproved Boolean default false;");
				st.executeUpdate("alter table "
						+ dataset
						+ "_confirmed_category add synonym varchar(100) default null;");
				st.executeUpdate("alter table " + dataset
						+ "_confirmed_category add termIndex int default null;");

				st.executeUpdate("alter table "
						+ dataset
						+ "_confirmed_category add termWithIndex varchar(100) default null;");
				st.executeUpdate("alter table "
						+ dataset
						+ "_confirmed_category add synonymWithIndex varchar(100) default null;");

				// populate data into _confirmed_category
				// insert as categoryApproved = false, update categoryApproved
				// later
				sql = "select distinct userid, term, decision, hasSyn, relatedTerms from "
						+ dataset
						+ "_user_terms_decisions where isActive = true and decision <> ''";
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String termWithIndex = rset.getString("term");
					String category = rset.getString("decision");
					boolean categoryApproved = false;
					String confirmDate = null;

					// insert one by one
					sql = "insert into "
							+ dataset
							+ "_confirmed_category "
							+ "(term, category, userid, categoryApproved, synonymApproved, synonym, "
							+ "termIndex, termWithIndex, synonymWithIndex, confirmDate) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					TermAndExtentionBean termBean = new TermAndExtentionBean(
							termWithIndex);

					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, termBean.getTerm());
					pstmt.setString(2, category);
					pstmt.setInt(3, rset.getInt("userid"));
					pstmt.setBoolean(4, categoryApproved);
					pstmt.setBoolean(5, false);
					pstmt.setInt(7, termBean.getIndex());
					pstmt.setString(8, termBean.getTermWithIndex());
					pstmt.setString(10, confirmDate);

					if (rset.getBoolean("hasSyn")) {
						// format: 'newer','later'
						String[] syns = rset.getString("relatedTerms").split(
								",");
						for (String syn : syns) {
							syn = syn.replaceAll("'", "");
							TermAndExtentionBean synBean = new TermAndExtentionBean(
									syn);

							pstmt.setString(6, synBean.getTerm());
							pstmt.setString(9, synBean.getTermWithIndex());
							pstmt.executeUpdate();
						}
					} else {
						pstmt.setString(6, "");
						pstmt.setString(9, "");
						pstmt.executeUpdate();
					}
				}

				// update categoryApproved
				while (rset_2.next()) {
					String term = rset_2.getString("term");
					sql = "update "
							+ dataset
							+ "_confirmed_category set categoryApproved = true, confirmDate = ? "
							+ "where category = ? and (term = ? or termWithIndex = ?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, rset_2.getString("confirmDate"));
					pstmt.setString(2, rset_2.getString("category"));
					pstmt.setString(3, term);
					pstmt.setString(4, term);
					pstmt.executeUpdate();
				}

				System.out.println("Dataset '" + dataset + "' update done!");
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to check if the email exists");
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
			if (rset_2 != null) {
				rset_2.close();
			}
		}

		return success;
	}

	/**
	 * 1. add isApprovedSynonym in _confirmed_category table 2. update
	 * isApprovedSynonym for each dataset
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean updateDB_on_072413() throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Statement st = null;

		try {
			conn = getConnection();
			pstmt = conn
					.prepareStatement("select distinct prefix from datasetprefix");
			rset = pstmt.executeQuery();
			ArrayList<String> datasets = new ArrayList<String>();
			while (rset.next()) {
				datasets.add(rset.getString(1));
			}

			st = conn.createStatement();
			st.executeUpdate("update dataset_owner set ownerID = 1 where ownerID = 23;");

			for (String dataset : datasets) {

				// update table schema
				String sql = "alter table "
						+ dataset
						+ "_confirmed_category add isApprovedSynonym boolean default false;";
				st.executeUpdate(sql);

				// update data
				sql = "select distinct synonym, category from " + dataset
						+ "_confirmed_category where synonymApproved = true";
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String synonym = rset.getString("synonym");
					String category = rset.getString("category");

					// insert one by one
					sql = "update "
							+ dataset
							+ "_confirmed_category set isApprovedSynonym = true where term = '"
							+ synonym + "' and category = '" + category + "'";
					st.executeUpdate(sql);
				}
				System.out.println("Dataset '" + dataset + "' update done!");
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to check if the email exists");
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
		}

		return success;
	}

	/**
	 * add active 'discarded' decision into _confirmed_category table
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean updateDB_on_092313() throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Statement st = null;

		try {
			conn = getConnection();
			// set Hong to be super user
			pstmt = conn
					.prepareStatement("update users set role = 'S' where userid = 2;");
			pstmt.executeUpdate();

			// get datasets
			pstmt = conn
					.prepareStatement("select distinct prefix from datasetprefix");
			rset = pstmt.executeQuery();
			ArrayList<String> datasets = new ArrayList<String>();
			while (rset.next()) {
				datasets.add(rset.getString(1));
			}

			for (String dataset : datasets) {
				int count = 0;
				String sql = "select term, userid from " + dataset
						+ "_user_terms_decisions "
						+ "where decision = '' and isActive = true";
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();

				while (rset.next()) {
					count++;
					String term = rset.getString("term");
					int userid = rset.getInt("userid");

					// delete current active decisions from confirmed_category
					String update_sql = "delete from " + dataset
							+ "_confirmed_category "
							+ "where termWithIndex = ? and userid = ?";
					pstmt = conn.prepareStatement(update_sql);
					pstmt.setString(1, term);
					pstmt.setInt(2, userid);
					pstmt.executeUpdate();

					// insert record
					// get term, termWithIndex
					TermAndExtentionBean termBean = new TermAndExtentionBean(
							rset.getString("term"));

					sql = "insert into "
							+ dataset
							+ "_confirmed_category "
							+ "(term, category, userid, categoryApproved, isApprovedSynonym, "
							+ "synonymApproved, synonym, termIndex, termWithIndex, synonymWithIndex) "
							+ "values (?, ?, ?, false, false, "
							+ "false, '', ?, ?, '')";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, termBean.getTerm());
					pstmt.setString(2, "discarded");
					pstmt.setInt(3, userid);
					pstmt.setInt(4, termBean.getIndex());
					pstmt.setString(5, term);
					pstmt.executeUpdate();
				}
				System.out.println("Dataset '" + dataset + "' update done: "
						+ count + " terms discarded. ");
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
		}

		return success;

	}

	/**
	 * force all category names to be lower case without space
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean cleanupCategoryNames() throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rset = null, rset2 = null, rset3 = null;

		try {
			conn = getConnection();

			// get datasets
			pstmt = conn
					.prepareStatement("select distinct prefix, glossaryType from datasetprefix");
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String dataset = rset.getString(1);
				System.out.println("processing dataset: " + dataset);

				int glosssaryType = rset.getInt(2);

				// find invalid category names in this dataset
				String sql = "select category from " + dataset + "_categories";
				pstmt = conn.prepareStatement(sql);
				rset2 = pstmt.executeQuery();
				while (rset2.next()) {
					String changeFrom = rset2.getString(1);
					if (changeFrom.matches("^[a-z_]+$")) {
						// category name is valid
					} else {
						// invalid category name
						String changeTo = changeFrom.toLowerCase().replaceAll(
								"[^a-z_]", "_");

						System.out.println("\t" + changeFrom + " -> "
								+ changeTo);

						/**
						 * in dataset range: if changeTo already exist 1.
						 * _user_terms_decisions: update changeFrom -> changeTo
						 * 2. _confirmed_category: update changeFrom -> changeTo
						 * 3. if changeTo already exist in this dataset, delete
						 * it; otherwise, update changeFrom -> changeTo
						 */
						sql = "update " + dataset + "_user_terms_decisions "
								+ "set decision = ? where decision = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, changeTo);
						pstmt.setString(2, changeFrom);
						pstmt.executeUpdate();

						sql = "update " + dataset + "_confirmed_category "
								+ "set category = ? where category = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, changeTo);
						pstmt.setString(2, changeFrom);
						pstmt.executeUpdate();

						sql = "select category from " + dataset
								+ "_categories where category = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, changeTo);
						rset3 = pstmt.executeQuery();
						if (rset3.next()) {
							// changeTo already exist, delete changeFrom
							sql = "delete from " + dataset
									+ "_categories where category = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, changeTo);
							pstmt.executeUpdate();

							System.out.println("\t\tdeleted " + changeFrom);
						} else {
							// update changeFrom -> changeTo
							sql = "update " + dataset + "_categories "
									+ "set category = ? where category = ?";
							pstmt2 = conn.prepareStatement(sql);
							pstmt2.setString(1, changeTo);
							pstmt2.setString(2, changeFrom);
							pstmt2.executeUpdate();
							System.out.println("\t\tupdated" + changeFrom);
						}

						/**
						 * in glossary range: if changeTo already exist, do
						 * nothing else, update changeFrom -> changeTo
						 */

						sql = " select category from glossary_dictionary where category = ? and glossaryType = ? limit 1";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, changeTo);
						pstmt.setInt(2, glosssaryType);
						rset3 = pstmt.executeQuery();
						if (rset3.next()) {
							// already exist in glossary dictionary
							// do nothing
							System.out.println("\t\t" + changeTo
									+ " already exist in glossary type ("
									+ glosssaryType + ")");
						} else {
							// update changeFrom -> changeTo
							sql = "update glossary_dictionary set category = ? where category = ? and glossaryType = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, changeTo);
							pstmt.setString(2, changeFrom);
							pstmt.setInt(3, glosssaryType);
							pstmt.executeUpdate();
							System.out.println("\t\tupdated " + changeFrom
									+ " in glossary type (" + glosssaryType
									+ ")");
						}
					}
				}
			}
			System.out.println("clean up category names done.");
		} catch (Exception exe) {
			exe.printStackTrace();
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset2 != null) {
				rset2.close();
			}

			if (pstmt2 != null) {
				pstmt2.close();
			}

			if (rset3 != null) {
				rset3.close();
			}
		}

		return success;

	}

	private String getSQL(String term, String category, String termID) {
		return "update glossary_dictionary set term = '" + term
				+ "', category = '" + category + "' where termID = '" + termID
				+ "'";
	}

	/**
	 * mannually fix term, category by termID
	 * 
	 * @param sqls
	 * @return
	 * @throws Exception
	 */
	public boolean cleanupHymenopteraGlossary(ArrayList<String> sqls)
			throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Statement st = null;

		try {
			conn = getConnection();
			st = conn.createStatement();

			st.execute(getSQL("absent", "presence",
					"14a18cec-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("alcohol", "substance",
					"14a46926-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("blind", "behavior",
					"149caf9c-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("chewing", "behavior",
					"149cd864-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("colour", "coloration",
					"14a20500-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("complex", "architecture",
					"14a46264-21de-11e3-a402-0026b9326338"));

			st.execute(getSQL("copulation", "behavior",
					"149d1c70-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("exudatory", "behavior",
					"14a509a8-21de-11e3-a402-0026b9326338"));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));

			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));

			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));

			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));

			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
			st.execute(getSQL("", "", ""));
		} catch (Exception exe) {
			exe.printStackTrace();
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
		}

		return success;
	}

	public boolean cleanup_hymenoptera_glossary() throws Exception {
		boolean success = false;

		Connection conn = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rset = null, rset2 = null, rset3 = null;

		String dataset = "Hymenoptera_glossary";
		String clean_term_category_table = " hymenoptera_glossary_term_category_cleaned ";
		String clean_syns_table = " hymenoptera_glossary_syns_cleaned ";
		int userid = 2;
		HashMap<String, Integer> termIndexMap = new HashMap<String, Integer>();
		int termCount = 0;
		int termCategoryCount = 0;
		int termSynPairCount = 0;

		try {
			conn = getConnection();
			/**
			 * new category name
			 */
			System.out.println("*********Update Started************** "
					+ System.currentTimeMillis());

			String sql = "select distinct category from "
					+ clean_term_category_table + "where category not in "
					+ "(select category from " + dataset + "_categories)";

			pstmt = conn.prepareStatement(sql);

			System.out.println("processing category changes ... "
					+ System.currentTimeMillis());
			rset = pstmt.executeQuery();
			while (rset.next()) {
				// insert new category name
				sql = "insert into " + dataset
						+ "_categories (category, definition) values (?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, rset.getString(1));
				pstmt.setString(2,
						"Created by dataset cleaning up. Need to update later. ");
				pstmt.executeUpdate();
			}

			System.out.println("clean up all the approvals ... "
					+ System.currentTimeMillis());
			/**
			 * clean up all the approvals
			 */
			sql = "update " + dataset + "_confirmed_category "
					+ "set categoryApproved = false, "
					+ "synonymApproved = false, " + "isApprovedSynonym = false";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();

			System.out
					.println("clean up tracks of terms in the clean glossary ... "
							+ System.currentTimeMillis());
			/**
			 * clean up all track related to terms in the clean glossary here
			 * only consider terms that are going to be re-added
			 */
			sql = "select distinct term from " + "(select term from "
					+ clean_term_category_table + "union "
					+ "(select synonym from " + clean_syns_table
					+ " where term in " + "(select term from "
					+ clean_term_category_table + "))) a";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String term = rset.getString(1);
				/**
				 * delete all decisions related to this term, including original
				 * term and its copies
				 */
				sql = "delete from " + dataset + "_user_terms_decisions "
						+ "where term rlike '^" + term + "(_\\d+)?$'";
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
			 * term -> (n) category -> (m) synonyms
			 * 
			 * might lose some terms if the clean glossary is not valid
			 */
			// only main terms
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
						+ "where term = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				rset2 = pstmt.executeQuery();
				while (rset2.next()) {// for each category of this term
					String category = rset2.getString("category");
					termCategoryCount++;
					System.out.println("\t[" + termCategoryCount
							+ "] processing category: " + category);

					// check if this term is approved synonym in this category
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
					sql = "select * from " + clean_syns_table
							+ "where term = ? and category = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, term);
					pstmt.setString(2, category);
					rset3 = pstmt.executeQuery();
					ArrayList<String> syns = new ArrayList<String>();
					boolean hasSyn = false;
					while (rset3.next()) {
						hasSyn = true;
						syns.add(rset3.getString("synonym"));
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
						// insert main term to _user_terms_decisions
						String syns_str = "";
						for (String syn : syns) {
							if (syns_str.equals("")) {
								syns_str = "'" + syn + "'";
							} else {
								syns_str += ", '" + syn + "'";
							}
						}
						pstmt = conn.prepareStatement(insert_decision_sql);
						pstmt.setString(1, termWithIndex); // term
						pstmt.setInt(2, userid);// userid
						pstmt.setString(3, category);// decision
						pstmt.setBoolean(4, false);// isAdditional
						pstmt.setString(5, syns_str);// related terms
						pstmt.setBoolean(6, true);// hassyn
						pstmt.executeUpdate();

						for (String syn : syns) {
							int synIndex = 0;
							String synWithIndex = syn;
							if (termIndexMap.get(syn) != null) {
								synIndex = termIndexMap.get(syn);
								synWithIndex = term + "_"
										+ Integer.toString(synIndex);
								termIndexMap.put(syn, synIndex + 1);
							} else {
								termIndexMap.put(syn, 1);
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

							// insert term to _confirmed_category
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

			/**
			 * correct unmatching termID
			 */
			// prepare _term_category and _syns table
			CharacterDBAccess cbda = new CharacterDBAccess();
			cbda.prepareCategoryTablesForDownloading(dataset);

			sql = "select * from "
					+ "(select a.term, a.category, a.termID, b.termID as correctID from "
					+ dataset + "_term_category a " + "left join "
					+ clean_term_category_table
					+ " b on a.term = b.term and a.category = b.category) c "
					+ "where termID <> correctID;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String term = rset.getString("term");
				String category = rset.getString("category");
				String correctID = rset.getString("correctID");
				/**
				 * delete all record related to <term, category, glossaryType =
				 * 2>
				 */
				sql = "delete from glossary_dictionary where term = ? and category = ? "
						+ "and glossaryType = ? and termID <> ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				pstmt.setString(2, category);
				pstmt.setInt(3, 2);
				pstmt.setString(4, correctID);
				pstmt.executeUpdate();

				/**
				 * update the correct record <term, category, glossaryType = 2,
				 * correctID>
				 */
				sql = "update glossary_dictionary set term = ?, category = ? where termID = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				pstmt.setString(2, category);
				pstmt.setString(3, correctID);
				pstmt.executeUpdate();
			}

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
