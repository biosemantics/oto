package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import edu.arizona.biosemantics.oto.oto.beans.SentenceRecordBean;
import edu.arizona.biosemantics.oto.oto.beans.TermAndExtentionBean;
import edu.arizona.biosemantics.oto.oto.beans.User;

public class CategorizationDBAccess extends DatabaseAccess {

	private static CategorizationDBAccess instance;

	public static CategorizationDBAccess getInstance() throws IOException {
		if (instance == null) {
			instance = new CategorizationDBAccess();
		}
		return instance;
	}

	public CategorizationDBAccess() throws IOException {
		super();
	}

	/**
	 * delete existing terms and import terms
	 * 
	 * @param dataset
	 * @param termList
	 * @param fileName
	 * @param sentences
	 * @throws Exception
	 */
	public void importTerms(String dataset, ArrayList<String> termList,
			String fileName, ArrayList<String> sentences) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			// import terms
			stmt.execute("delete from " + dataset + "_web_grouped_terms");
			for (String term : termList) {
				stmt.execute("insert into " + dataset + "_web_grouped_terms "
						+ "(groupId, term) values (0, '" + term + "')");
			}

			// import sentences
			GeneralDBAccess.getInstance().importSentences(conn, pstmt, dataset,
					fileName, sentences);

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
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * generate the sql to insert users log
	 * 
	 * @param dataset
	 * @param operation
	 * @param userID
	 * @return
	 */
	private String getUserLogSQL(String dataset, String operation, int userID) {
		return "insert into users_log(userid, dataset, operation, operateTime) values ("
				+ userID + ", '" + dataset + "', '" + operation + "', now())";
	}

	/**
	 * generate the note value for merged dataset
	 * 
	 * @param datasets
	 *            : the datasets that are going to be merged
	 * @return
	 * @throws SQLException
	 */
	private String generateNoteForMergedDataset(ArrayList<String> datasets)
			throws SQLException {
		String note = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select note from datasetprefix where prefix = ? and note is not null and note <> ''";
			for (String dataset : datasets) {
				String datasetWithNote = dataset;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, dataset);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					datasetWithNote += " (merged from: " + rset.getString(1)
							+ ")";
				}

				if (note.equals("")) {
					note = datasetWithNote;
				} else {
					note += ", " + datasetWithNote;
				}
			}
		} catch (Exception exe) {
			System.out.println(exe.getStackTrace());
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return note;
	}

	/**
	 * print out merging log
	 * 
	 * @param msg
	 * @param time
	 * @return
	 */
	private long LogMerging(String msg, long time) {
		if (time == 0) {
			System.out.println(" ** Merge log: " + msg);
		} else {
			System.out.println(" ** Merge log: " + msg + ", cost "
					+ Long.toString((System.currentTimeMillis() - time) / 1000)
					+ "s");
		}

		return System.currentTimeMillis();
	}

	/**
	 * merge datasets into target dataset:
	 * 
	 * 1. The target has been created before calling this function
	 * 
	 * 2. only deal with merge related stuff in a transaction
	 * 
	 * @param datasets
	 * @param target_ds
	 * @param user
	 * @param glossaryID
	 * @return
	 * @throws Exception
	 */
	public boolean mergeDatasets(ArrayList<String> datasets, String target_ds,
			User user, int glossaryID, boolean isMergeIntoSystem)
			throws Exception {
		/**
		 * initialization
		 */
		boolean success = false;
		Connection conn = null;
		Statement st = null, st2 = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;
		int userID = user.getUserId();
		// datasets to be deleted after merging
		ArrayList<String> toDelete = new ArrayList<String>();
		// datasets cannot be deleted after merging
		ArrayList<String> cannotDelete = new ArrayList<String>();
		String sql = "";
		long time = System.currentTimeMillis();
		LogMerging("merging started", 0);

		/**
		 * remove target_ds from datasets list
		 */
		for (int i = 0; i < datasets.size(); i++) {
			if (datasets.get(i).equals(target_ds)) {
				datasets.remove(i);
				break;
			}
		}

		/**
		 * construct note (merged from) for target dataset
		 */
		String note = generateNoteForMergedDataset(datasets);

		try {
			conn = getConnection();
			st = conn.createStatement();
			st2 = conn.createStatement();

			/**
			 * make the the merging in a transaction
			 */
			conn.setAutoCommit(false);

			/**
			 * update target note
			 */
			rset = st
					.executeQuery("select * from datasetprefix where prefix = '"
							+ target_ds + "'");
			if (rset.next()) {
				// calculate new note
				String oldNote = rset.getString("note");
				if (oldNote != null && !oldNote.equals("")
						&& !oldNote.equals("null")) {
					note = oldNote + ", " + note;
				}
			}
			st.executeUpdate("update datasetprefix set note = '" + note
					+ "' where prefix = '" + target_ds + "'");
			time = LogMerging("update target dataset note", time);

			/**
			 * insert data from source datasets, one dataset at a time
			 */
			for (String source_ds : datasets) {
				Long dataset_start = System.currentTimeMillis();
				time = LogMerging("--per dataset-- starting to process "
						+ source_ds, 0);
				/**
				 * insert new category from source dataset
				 */
				st.executeUpdate("insert into "
						+ target_ds
						+ "_categories select * from "
						+ source_ds
						+ "_categories where category not in (select category from "
						+ target_ds + "_categories)");
				time = LogMerging("--per dataset-- categories inserted", time);

				/**
				 * comments table
				 */
				st.executeUpdate("insert into "
						+ target_ds
						+ "_comments (comments, term, userid, commentDate, tagID, orderID) "
						+ "select comments, term, userid, commentDate, tagID, orderID from "
						+ source_ds + "_comments");
				time = LogMerging("--per dataset-- comments inserted", time);

				/**
				 * review_history: non duplicate ones
				 */
				st.executeUpdate("insert into " + target_ds
						+ "_review_history (userid, term, reviewTime) "
						+ "select a.userid, a.term, a.reviewTime from "
						+ source_ds + "_review_history a "
						+ "left join (select userid, term, 1 as exist from "
						+ target_ds + "_review_history) b "
						+ "on a.userid = b.userid and a.term = b.term "
						+ "where b.exist is null");
				time = LogMerging("--per dataset-- review history inserted",
						time);

				/**
				 * sentence table
				 */
				st.executeUpdate("insert into " + target_ds + "_sentence "
						+ "select * from " + source_ds + "_sentence;");
				time = LogMerging("--per dataset-- sentence insserted", time);

				/*
				 * // user_terms_relations st.executeUpdate("insert into " +
				 * target_ds + "_user_terms_relations " + "select * from " +
				 * source_ds + "_user_terms_relations");
				 */

				/**
				 * web_grouped_terms
				 */
				// Step 1: prepare source dataset
				st.executeUpdate("update  " + source_ds
						+ "_web_grouped_terms set sourceDataset = '"
						+ source_ds
						+ "' where sourceDataset is null or sourceDataset = ''");
				// step 2: insert into target
				st.executeUpdate("insert into "
						+ target_ds
						+ "_web_grouped_terms(groupId, term, cooccurTerm, sourceDataset) "
						+ "select groupId, term, cooccurTerm, sourceDataset from "
						+ source_ds + "_web_grouped_terms");
				time = LogMerging(
						"--per dataset-- _web_grouped_terms inserted", time);

				/**
				 * user_terms_decisions: only insert non-duplicate decisions
				 */
				if (isMergeIntoSystem) { // ignore userid = 1
					sql = "insert into "
							+ target_ds
							+ "_user_terms_decisions "
							+ "(term, userid, decision, decisiondate, isAdditional, relatedTerms, isActive, "
							+ "isLatest, hasConflict, hasSyn, groupid) "
							+ "select a.term, a.userid, a.decision, a.decisiondate, a.isAdditional, "
							+ "a.relatedTerms, a.isActive, a.isLatest, a.hasConflict, a.hasSyn, a.groupid "
							+ "from (select * from "
							+ source_ds
							+ "_user_terms_decisions where userid <> 1) a left join (select distinct term, decision, userid, "
							+ "relatedTerms, isActive, 1 as exist from "
							+ target_ds
							+ "_user_terms_decisions) b "
							+ "on a.term = b.term and a.decision = b.decision and a.userid = b.userid "
							+ "and a.relatedTerms = b.relatedTerms and a.isActive = b.isActive "
							+ "where b.exist is null;";
				} else {
					sql = "insert into "
							+ target_ds
							+ "_user_terms_decisions "
							+ "(term, userid, decision, decisiondate, isAdditional, relatedTerms, isActive, "
							+ "isLatest, hasConflict, hasSyn, groupid) "
							+ "select a.term, a.userid, a.decision, a.decisiondate, a.isAdditional, "
							+ "a.relatedTerms, a.isActive, a.isLatest, a.hasConflict, a.hasSyn, a.groupid "
							+ "from "
							+ source_ds
							+ "_user_terms_decisions a left join (select distinct term, decision, userid, "
							+ "relatedTerms, isActive, 1 as exist from "
							+ target_ds
							+ "_user_terms_decisions) b "
							+ "on a.term = b.term and a.decision = b.decision and a.userid = b.userid "
							+ "and a.relatedTerms = b.relatedTerms and a.isActive = b.isActive "
							+ "where b.exist is null;";
				}
				st.executeUpdate(sql);
				time = LogMerging(
						"--per dataset-- _user_terms_decisions inserted", time);

				/**
				 * _confirmed_category
				 */
				// insert non-duplicate records
				if (isMergeIntoSystem) {// ignore userid = 1
					sql = "insert into "
							+ target_ds
							+ "_confirmed_category "
							+ "(term, category, userid, confirmDate, categoryApproved, synonymApproved, synonym, termIndex, "
							+ "termWithIndex, synonymWithIndex) "
							+ "select a.term, a.category, a.userid, a.confirmDate, a.categoryApproved, a.synonymApproved, "
							+ "a.synonym, a.termIndex, a.termWithIndex, a.synonymWithIndex "
							+ "from (select * from "
							+ source_ds
							+ "_confirmed_category where userid <> 1) a "
							+ "left join (select distinct termWithIndex, category, synonym, synonymWithIndex, userid, 1 as exist "
							+ "from "
							+ target_ds
							+ "_confirmed_category) b "
							+ "on a.termWithIndex = b.termWithIndex and a.category = b.category and a.synonym = b.synonym "
							+ "and a.synonymWithIndex = b.synonymWithIndex and a.userid = b.userid "
							+ "where b.exist is null;";
				} else {
					sql = "insert into "
							+ target_ds
							+ "_confirmed_category "
							+ "(term, category, userid, confirmDate, categoryApproved, synonymApproved, synonym, termIndex, "
							+ "termWithIndex, synonymWithIndex) "
							+ "select a.term, a.category, a.userid, a.confirmDate, a.categoryApproved, a.synonymApproved, "
							+ "a.synonym, a.termIndex, a.termWithIndex, a.synonymWithIndex "
							+ "from "
							+ source_ds
							+ "_confirmed_category a "
							+ "left join (select distinct termWithIndex, category, synonym, synonymWithIndex, userid, 1 as exist "
							+ "from "
							+ target_ds
							+ "_confirmed_category) b "
							+ "on a.termWithIndex = b.termWithIndex and a.category = b.category and a.synonym = b.synonym "
							+ "and a.synonymWithIndex = b.synonymWithIndex and a.userid = b.userid "
							+ "where b.exist is null;";
				}
				st.executeUpdate(sql);

				// update categoryApproved
				sql = "update "
						+ target_ds
						+ "_confirmed_category a "
						+ "left join (select term, category, categoryApproved from "
						+ source_ds
						+ "_confirmed_category where categoryApproved = true) b "
						+ "on a.term = b.term and a.category = b.category set a.categoryApproved = true "
						+ "where a.categoryApproved = false and b.categoryApproved = true;";
				st.executeUpdate(sql);

				// update synonymApproved
				sql = "update "
						+ target_ds
						+ "_confirmed_category a "
						+ "left join (select term, category, synonym, synonymApproved from "
						+ source_ds
						+ "_confirmed_category where synonymApproved = true) b "
						+ "on a.term = b.term and a.category = b.category and a.synonym = b.synonym "
						+ "set a.synonymApproved = true "
						+ "where a.synonymApproved = false and b.synonymApproved = true;";
				st.executeUpdate(sql);

				// update isApprovedSynonym
				sql = "update "
						+ target_ds
						+ "_confirmed_category a "
						+ "left join (select distinct category, synonym, synonymApproved from "
						+ source_ds
						+ "_confirmed_category where synonymApproved = true) b "
						+ "on a.term = b.synonym and a.category = b.category "
						+ "set a.isApprovedSynonym = true "
						+ "where a.isApprovedSynonym = false and b.synonymApproved = true;";
				st.executeUpdate(sql);

				time = LogMerging(
						"--per dataset-- _confirmed_category inserted", time);
				dataset_start = LogMerging(
						"--per dataset-- adding data of dataset " + source_ds
								+ " finished", dataset_start);
			}

			// use new termIndex if one termWithIndex matches more than one
			// category, use a list to store the mapping
			ArrayList<TermAndExtentionBean> termMappings = new ArrayList<TermAndExtentionBean>();
			sql = "select b.term, b.userid, termWithIndex, count, categoryList, maxIndex from "
					+ "(select term, userid, termWithIndex, count(category) as count, "
					+ "group_concat(category) as categoryList "
					+ "from (select distinct term, userid, termWithIndex, category "
					+ "from "
					+ target_ds
					+ "_confirmed_category) a group by termWithIndex, userid) b "
					+ "left join (select term, max(termIndex) as maxIndex "
					+ "from "
					+ target_ds
					+ "_confirmed_category group by term) c "
					+ "on b.term = c.term where count > 1;";
			rset = st.executeQuery(sql);
			while (rset.next()) {
				int index = rset.getInt("maxIndex");
				String[] categories = rset.getString("categoryList").split(",");
				for (int i = 1; i < categories.length; i++) {
					index = index + 1;
					TermAndExtentionBean termMapping = new TermAndExtentionBean(
							rset.getString("term"), index, categories[i],
							rset.getInt("userid"),
							rset.getString("termWithIndex"));
					termMappings.add(termMapping);
				}
			}

			// map old term to new indexed term one by one
			for (TermAndExtentionBean termMapping : termMappings) {
				// update _confirmed_category (both term and synonym)
				sql = "update " + target_ds
						+ "_confirmed_category set termWithIndex = '"
						+ termMapping.getTermWithIndex() + "', termIndex = "
						+ termMapping.getIndex() + " where termWithIndex = '"
						+ termMapping.getOld_term() + "' and category = '"
						+ termMapping.getCategory() + "' and userid = "
						+ termMapping.getUserid();
				st.executeUpdate(sql);
				sql = "update " + target_ds
						+ "_confirmed_category set synonymWithIndex = '"
						+ termMapping.getTermWithIndex()
						+ "' where synonymWithIndex = '"
						+ termMapping.getOld_term() + "' and category = '"
						+ termMapping.getCategory() + "' and userid = "
						+ termMapping.getUserid();

				// update _user_terms_decisions (term and relatedTerms)
				sql = "select term, decision, hasSyn, isAdditional, relatedTerms from "
						+ target_ds
						+ "_user_terms_decisions where term = '"
						+ termMapping.getOld_term()
						+ "' and decision = '"
						+ termMapping.getCategory()
						+ "' and userid = "
						+ termMapping.getUserid();
				rset = st2.executeQuery(sql);
				while (rset.next()) {
					// update term
					sql = "update " + target_ds
							+ "_user_terms_decisions set term = '"
							+ termMapping.getTermWithIndex()
							+ "' where term = '" + termMapping.getOld_term()
							+ "' and decision = '" + termMapping.getCategory()
							+ "' and userid = " + termMapping.getUserid();
					st.executeUpdate(sql);

					if (rset.getBoolean("hasSyn")) {
						// update its synonyms' relatedTerms if hasSyn
						sql = "update "
								+ target_ds
								+ "_user_terms_decisions set relatedTerms = \"synonym of '"
								+ termMapping.getTermWithIndex()
								+ "'\" where decision = '"
								+ termMapping.getCategory() + "' and userid = "
								+ termMapping.getUserid() + " and term in ("
								+ rset.getString("relatedTerms") + ")";
						st.executeUpdate(sql);
					} else if (rset.getBoolean("isAdditional")) {
						// update its mainTerm' relatedTerms if isAdditional
						String mainTermWithQuote = rset.getString(
								"relatedTerms").replace("synonym of", "");
						sql = "update "
								+ target_ds
								+ "_user_terms_decisions set relatedTerms = REPLACE(relatedTerms, '"
								+ termMapping.getOld_term() + "', '"
								+ termMapping.getTermWithIndex() + "') "
								+ "where term = " + mainTermWithQuote
								+ " and decision = '"
								+ termMapping.getCategory() + "' and userid = "
								+ termMapping.getUserid();
						st.executeUpdate(sql);
					}
				}

				// update term_review: insert a review record about
				// term_newIndex
				sql = "insert into " + target_ds
						+ "_review_history (userid, term, reviewTime) values ("
						+ termMapping.getUserid() + ", '"
						+ termMapping.getTermWithIndex() + "', sysdate())";
				st.executeUpdate(sql);
			}
			time = LogMerging("update term index in decision table finished",
					time);

			// update isActive of _user_terms_decisions table
			/**
			 * select maxDate of all the active decisions of each term and each
			 * user -> among multiple maxDate, select max decisionID to be the
			 * real latest decision -> update the others setting isActive =
			 * false
			 */

			/**
			 * isActive has to be done before isLatest because if is not Active,
			 * then it is definitely not latest
			 */
			sql = "update "
					+ target_ds
					+ "_user_terms_decisions t5 "
					+ "right join (select userid, term, numActive from "
					+ "(select userid, term, count(*) as numActive "
					+ "from (select userid, term "
					+ "from "
					+ target_ds
					+ "_user_terms_decisions where isActive = true) t9 "
					+ "group by userid, term) t8 "
					+ "where numActive > 1) t7 "
					+ "on t7.term = t5.term and t7.userid = t5.userid "
					+ "left join (select max(decisionID) as maxID, userID, term, 1 as isMaxID "
					+ "from (select decisionID, term, userID "
					+ "from (select t1.decisionID, t1.term, t1.userID, t3.isMaxDate "
					+ "from "
					+ target_ds
					+ "_user_terms_decisions t1 "
					+ "left join (select max(decisionDate) as maxDate, term, userID, 1 as isMaxDate "
					+ "from "
					+ target_ds
					+ "_user_terms_decisions where isActive = true group by term, userID) t3 "
					+ "on t1.decisionDate = t3.maxDate and t1.term = t3.term and t1.userID = t3.userID) t2 "
					+ "where isMaxDate = 1) t4 group by term, userID) t6 "
					+ "on t5.decisionID = t6.maxID "
					+ "set t5.isActive = false, t5.isLatest = false "
					+ "where t5.isActive = true and isMaxID is null and numActive is not null and numActive > 1;";
			st.executeUpdate(sql);

			time = LogMerging("update isactive in decision table finished",
					time);

			// update isLatest of _user_terms_decisions table
			/**
			 * select maxDate of all the latest decisions of each term -> among
			 * multiple maxDate, select max decisionID to be the real latest
			 * decision -> update the others setting isLatest = false
			 */
			sql = "update "
					+ target_ds
					+ "_user_terms_decisions t5 "
					+ "right join (select term, numLatest from "
					+ "(select term, count(*) as numLatest "
					+ "from "
					+ target_ds
					+ "_user_terms_decisions where isLatest = true "
					+ "group by term) t8 "
					+ "where numLatest > 1) t7 "
					+ "on t5.term = t7.term "
					+ "left join (select max(decisionID) as maxID, term, 1 as isMaxID from "
					+ "(select decisionID, term from "
					+ "(select t1.decisionID, t1.term, t3.isMaxDate from "
					+ target_ds
					+ "_user_terms_decisions t1 "
					+ "left join (select max(decisionDate) as maxDate, term, 1 as isMaxDate from "
					+ target_ds
					+ "_user_terms_decisions where isLatest = true group by term) t3 "
					+ "on t1.decisionDate = t3.maxDate and t1.term = t3.term) t2 "
					+ "where isMaxDate = 1) t4 group by term) t6 "
					+ "on t5.decisionID = t6.maxID "
					+ "set t5.isLatest = false "
					+ "where t5.isLatest = true and isMaxID is null and numLatest is not null and numLatest > 1";
			st.executeUpdate(sql);
			time = LogMerging("update isLatest in decision table finished",
					time);

			// update hasConflict of _user_terms_decisions
			/**
			 * select the ones with different active decisions, then set
			 * hasConflict = true
			 */
			sql = "update "
					+ target_ds
					+ "_user_terms_decisions t4 "
					+ "left join (select term, 1 as addConflict from "
					+ "(select term, count(*) as numDecisions from "
					+ "(select distinct term, decision from "
					+ target_ds
					+ "_user_terms_decisions where isActive = true) t1 group by term) t2 "
					+ "where numDecisions > 1) t3 "
					+ "on t4.term = t3.term set t4.hasConflict = true "
					+ "where t3.addConflict is not null and hasConflict = false ";
			st.executeUpdate(sql);
			time = LogMerging("update hasConflict in decision table finished",
					time);

			/**
			 * remove source datasets when standard applies
			 */
			for (String dataset : datasets) {
				// check tree decisions
				boolean hasOtherData = false;
				rset = st.executeQuery("select count(*) from " + dataset
						+ "_user_tags_decisions");
				if (rset.next()) {
					if (rset.getInt(1) > 7) // there are 7 default records
						hasOtherData = true;
				}

				// check orders
				if (!hasOtherData) {
					rset = st.executeQuery("select * from " + dataset
							+ "_web_orders limit 1");
					if (rset.next()) {
						hasOtherData = true;
					}
				}

				// either to delete or cannot be deleted
				if (hasOtherData) { // cannot be deleted
					cannotDelete.add(dataset);
				} else {
					toDelete.add(dataset);
				}
			}

			// delete toDelete
			for (String ds : toDelete) {
				// delete from datasetprefix
				st.executeUpdate("delete from datasetprefix where prefix = '"
						+ ds + "'");

				// delete from dataset owner
				st.executeUpdate("delete from dataset_owner where dataset = '"
						+ ds + "'");

				// update mergedInto related to this dataset
				st.executeUpdate("update datasetprefix set mergedInto = '"
						+ target_ds + "' where mergedInto = '" + ds + "'");

				// add deleting log
				st.executeUpdate(getUserLogSQL(ds, "Delete (from merge)",
						userID));
			}

			// set mergeinto field for cannotDelete
			for (String ds : cannotDelete) {
				st.executeUpdate("update datasetprefix set mergedInto = '"
						+ target_ds + "' where prefix = '" + ds + "'");

				// user log
				st.executeUpdate(getUserLogSQL(ds, "Merged into " + target_ds,
						userID));
			}

			// for all source datasets, update merge in glossary version
			for (String ds : datasets) {
				// update 1st time merge
				st.executeUpdate("update glossary_versions set mergedInto = '"
						+ target_ds + "' " + "where dataset = '" + ds + "'");

				// update merge chain
				st.executeUpdate("update glossary_versions set mergedInto = '"
						+ target_ds + "' " + "where mergedInto = '" + ds + "'");
				st.executeUpdate("update datasetprefix set mergedInto = '"
						+ target_ds + "' where mergedInto = '" + ds + "'");
			}

			time = LogMerging(
					"delete dataset or update merge information finished", time);

			// till here, merge dataset succeeded
			conn.commit();

			success = true;
		} catch (SQLException exe) {
			exe.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					ex1.printStackTrace();
				}
			}
		} finally {
			closeConnection(st, rset, conn);
			if (rset2 != null) {
				rset2.close();
			}
			if (st2 != null) {
				st2.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

		/**
		 * delete tables: cannot rollback, therefore separate from previous
		 * changes
		 */
		if (success) {
			try {
				conn = getConnection();
				st = conn.createStatement();
				for (String ds : toDelete) {
					ArrayList<String> tables = GeneralDBAccess.getInstance()
							.getDatasetTableList();
					for (int i = 0; i < tables.size(); i++) {
						st.executeUpdate("drop table if exists " + ds
								+ tables.get(i));
					}
				}
				time = LogMerging("drop tables for deleted datasets finished",
						time);
			} catch (SQLException exe) {
				exe.printStackTrace();
			} finally {
				closeConnection(st, conn);
			}

			/**
			 * de-dupliate the sentence table in a transaction
			 */
			try {
				conn = getConnection();
				st = conn.createStatement();
				// read in sentences
				HashMap<String, SentenceRecordBean> sentences = new HashMap<String, SentenceRecordBean>();

				// rset = st.executeQuery("select count(*) from " + target_ds
				// + "_sentence");
				// String strRecords = "/";
				// if (rset.next()) {
				// strRecords += Integer.toString(rset.getInt(1));
				// }

				rset = st
						.executeQuery("select sentid, source, sentence, originalsent, status, tag from "
								+ target_ds + "_sentence");
				int count = 0;
				while (rset.next()) {
					count++;
					// LogMerging("read in sentence " +
					// Integer.toString(count)
					// + strRecords, 0);
					String sentence = rset.getString("originalSent");
					if (sentences.get(sentence) == null) {
						sentences.put(
								sentence,
								new SentenceRecordBean(rset.getInt("sentid"),
										rset.getString("source"), rset
												.getString("sentence"),
										sentence, rset.getString("status"),
										rset.getString("tag")));
					}
				}
				rset.close();
				time = LogMerging("read in sentences to hash table finished",
						time);

				// delete duplicate sentences and insert one record
				// delete
				conn.setAutoCommit(false);
				st.executeUpdate("delete from " + target_ds + "_sentence");
				time = LogMerging("deleting sentence table finished", time);

				// insert
				count = 0;
				// strRecords = "/" + Integer.toString(sentences.size());
				pstmt = conn
						.prepareStatement("insert into "
								+ target_ds
								+ "_sentence (sentid, source, sentence, originalsent, status, tag) "
								+ "values (?, ?, ?, ?, ?, ?)");
				for (String sentence : sentences.keySet()) {
					count++;
					// LogMerging(
					// "adding inserting sentence to batch "
					// + Integer.toString(count) + strRecords, 0);
					SentenceRecordBean srb = sentences.get(sentence);
					pstmt.setInt(1, count);
					pstmt.setString(2, srb.getSource());
					pstmt.setString(3, srb.getSentence());
					pstmt.setString(4, srb.getOriginalSent());
					pstmt.setString(5, srb.getStatus());
					pstmt.setString(6, srb.getTag());
					pstmt.addBatch();
				}
				time = LogMerging("executing inserting sentence batch ", time);
				pstmt.executeBatch();
				conn.commit();
				time = LogMerging("inserting sentence table finished", time);
			} catch (Exception e) {
				e.printStackTrace();
				if (conn != null) {
					try {
						conn.rollback();
					} catch (SQLException ex1) {
						ex1.printStackTrace();
					}
				}
			} finally {
				closeConnection(pstmt, conn);
			}
		}

		time = LogMerging("merge finished!", 0);
		return success;
	}

}
