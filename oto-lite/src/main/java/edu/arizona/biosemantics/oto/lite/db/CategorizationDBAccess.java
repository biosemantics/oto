package edu.arizona.biosemantics.oto.lite.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.common.io.ExecCommmand;
import edu.arizona.biosemantics.oto.lite.beans.Category;
import edu.arizona.biosemantics.oto.lite.beans.ContextBean;
import edu.arizona.biosemantics.oto.lite.beans.Group;
import edu.arizona.biosemantics.oto.lite.beans.SavedTerm;
import edu.arizona.biosemantics.oto.lite.beans.Term;
import edu.arizona.biosemantics.oto.lite.beans.Upload;

/**
 * This class is for all database access pertaining to the Character Marker
 * 
 * @author Partha, Fengqiong
 * 
 */
public class CategorizationDBAccess extends AbstractDBAccess {

	private static final Logger LOGGER = Logger
			.getLogger(CategorizationDBAccess.class);

	private static CategorizationDBAccess instance;

	public static CategorizationDBAccess getInstance() {
		if (instance == null) {
			instance = new CategorizationDBAccess();
		}
		return instance;
	}

	protected CategorizationDBAccess() {

	}

	/**
	 * remove the index when the term is a copy
	 * 
	 * @param term
	 * @return
	 */
	private String removeTermIndex(String term) {
		Pattern p = Pattern.compile("^(.+)_\\d+$");
		Matcher m = p.matcher(term);
		if (m.matches()) {
			term = m.group(1);
		}

		return term;
	}

	/**
	 * when finish categorization, besides setting the finished flag, also
	 * populate tables for to_ontology and hierarchy page
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public boolean finishCategorization(String uploadID) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		Statement stmt = null, stmt_select = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt_select = conn.createStatement();
			conn.setAutoCommit(false);
			String sql = "update uploads set isFinalized = true where uploadID = "
					+ uploadID;
			stmt.executeUpdate(sql);

			// populate to_ontology page: populate table
			// [term_category_pair] (term, category, synonyms, uploadID, removed
			// = false)
			// remove copies index
			sql = "select a.term, a.category, b.synonym, "
					+ uploadID
					+ " as uploadID, false as removed from "
					+ "(select term, category from decisions where uploadID = "
					+ uploadID
					+ " and isMainTerm = true) a "
					+ "left join "
					+ "(select mainTerm, category, group_concat(synonym) as synonym from synonyms "
					+ "where uploadID = " + uploadID + " group by mainTerm) b "
					+ "on a.term = b.mainTerm and a.category = b.category";
			rset = stmt_select.executeQuery(sql);
			while (rset.next()) {
				String term = removeTermIndex(rset.getString("term"));
				String category = rset.getString("category");
				sql = "insert into term_category_pair "
						+ "(term, category, synonyms, uploadID, removed) "
						+ "values " + "('" + term + "', '" + category + "', '"
						+ rset.getString("synonym") + "', " + uploadID
						+ ", false)";
				stmt.executeUpdate(sql);

				// populate table [structures] (uploadID, term, userCreated)
				if (category.equals("structure")) {
					sql = "insert into structures (uploadID, term, userCreated) values "
							+ "(" + uploadID + ", '" + term + "', false)";
					stmt.executeUpdate(sql);
				}
			}

			conn.commit();
			rv = true;
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: finishCategorization "
					+ exe);
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e) {
					exe.printStackTrace();
					LOGGER.error("Exception in rollback  in CategorizationDBAccess:  "
							+ exe);
				}
			}
		} finally {
			closeConnection(stmt, conn);
		}
		return rv;
	}

	/**
	 * 
	 * @param category
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteCategory(String category, int uploadID)
			throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "delete from categories where category = ? and uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, category);
			pstmt.setInt(2, uploadID);
			pstmt.executeUpdate();
			rv = true;
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: deleteCategory "
					+ exe);
		} finally {
			closeConnection(pstmt, conn);
		}
		return rv;
	}

	/**
	 * todo
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public boolean sentToOTO(int uploadID) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get prefix for OTO

			// create dataset and all the tables

			// validation log in

			// insert data: terms, sentences, decisions

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: sentToOTO "
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * fix typo in this upload related tables: typos, terms, decisions, synonyms
	 * 
	 * @param uploadID
	 * @param term
	 * @param replacement
	 * @return
	 * @throws SQLException
	 */
	public boolean fixTypo(int uploadID, String term, String replacement)
			throws SQLException {
		String uploadID_str = Integer.toString(uploadID);
		boolean returnValue = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			// This has to be a transaction
			conn.setAutoCommit(false);

			// table: terms
			String sql = "update terms set term = '" + replacement
					+ "' where term = '" + term + "' and uploadID = "
					+ uploadID_str;
			stmt.executeUpdate(sql);

			ArrayList<String> termCopies = new ArrayList<String>();
			sql = "select term from decisions where uploadID = " + uploadID_str
					+ " and term rlike '^" + term + "(_(\\d)+)?'";
			rset = stmt.executeQuery(sql);
			while (rset.next()) {
				termCopies.add(rset.getString(1));
			}

			for (String termCopy : termCopies) {
				String replacementCopy = replacement;
				if (termCopy.matches("^" + term + "_(\\d)+$")) {
					replacementCopy = replacement
							+ termCopy.substring(termCopy.lastIndexOf("_"),
									termCopy.length());
				}

				// table: decisions
				sql = "update decisions set term = '" + replacementCopy
						+ "' where term = '" + termCopy + "' and uploadID = "
						+ uploadID_str;
				stmt.executeUpdate(sql);

				// table synonyms
				// mainTerm
				sql = "update synonyms set mainTerm = '" + replacementCopy
						+ "' where uploadID = " + uploadID_str
						+ " and mainTerm = '" + termCopy + "'";
				stmt.executeUpdate(sql);

				// synonym
				sql = "update synonyms set synonym = '" + replacementCopy
						+ "' where uploadID = " + uploadID_str
						+ " and synonym = '" + termCopy + "'";
				stmt.executeUpdate(sql);
			}

			// table typos
			// check if this term is already a replacement of an original term
			sql = "select id, originalTerm from typos where uploadID = "
					+ uploadID_str + " and replacedBy = '" + term + "'";
			rset = stmt.executeQuery(sql);
			if (rset.next()) {
				if (rset.getString("originalTerm").equals(replacement)) {
					// changed back to original state
					sql = "delete from typos where id = "
							+ rset.getString("id");
				} else {
					// changed multiple times
					sql = "update typos set replacedBy = '" + replacement
							+ "' where id = " + rset.getString("id");
				}
			} else {
				sql = "insert into typos (uploadID, originalTerm, replacedBy) "
						+ "values (" + uploadID_str + ", '" + term + "', '"
						+ replacement + "')";
			}
			stmt.executeUpdate(sql);

			conn.commit();
			returnValue = true;
		} catch (Exception exe) {
			if (conn != null) {
				LOGGER.error(
						"Couldn't execute fixTypo in CharacterStateDBAccess: ",
						exe);
				exe.printStackTrace();

				try {
					conn.rollback();
				} catch (SQLException ex1) {
					LOGGER.error(
							"Couldn't rollback fixTypo in CharacterStateDBAccess: ",
							exe);
					exe.printStackTrace();
				}
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			closeConnection(stmt, conn);
		}
		return returnValue;
	}

	/**
	 * OTO lite: check if the uploads can be erased, if so delete it condition
	 * 1: readyToDelete = true condition 2: readyToDelete for 2 days
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean cleanUpUploads() throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String query = "select uploadID from uploads where (not readyToDelete IS NULL) "
					+ "and readyToDelete < NOW() - INTERVAL 2 DAY";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				deleteUpload(rset.getInt("uploadID"), conn);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: cleanUpUploads "
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
	}

	/**
	 * OTO lite: delete all the data related to this upload
	 * 
	 * @param uploadID
	 * @throws SQLException
	 */
	public boolean deleteUpload(int uploadID, Connection conn)
			throws SQLException {
		boolean rv = false;
		PreparedStatement pstmt = null;
		try {
			// delete decisions
			String sql = "delete from decisions where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			// delete synonyms
			sql = "delete from synonyms where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			// delete category
			sql = "delete from categories where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			// delete terms
			sql = "delete from terms where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			// delete sentences
			sql = "delete from sentences where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			// delete uploads
			sql = "delete from uploads where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.executeUpdate();

			rv = true;
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: deleteUpload "
					+ exe);
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

		return rv;
	}

	/**
	 * For the OTO lite, get upload info to display on the page
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public Upload getUploadInfo(int uploadID) throws SQLException {
		Upload up = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get total terms numbers
			String query = "select * from uploads where uploadID = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				up = new Upload(uploadID);
				up.setSentToOTO(rset.getBoolean("sentToOTO"));
				up.setUploadTime(rset.getString("uploadTime"));
				up.setFinalized(rset.getBoolean("isFinalized"));

				// get number of terms
				query = "select count(*) from terms where uploadID = ?";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, uploadID);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					up.setNumberTerms(rset.getInt(1));
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CategorizationDBAccess: getUploadInfo "
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return up;
	}

	/**
	 * OTO lite: get categories to display, make sure structure is the first one
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Category> getCategories(int uploadID) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<Category> categoeies = new ArrayList<Category>();
		try {
			conn = getConnection();
			String firstCat = "structure";
			String sql = "select category, definition from categories where "
					+ " category = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstCat);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				Category cat = new Category();
				cat.setName(rset.getString("category"));
				cat.setDef(rset.getString("definition"));
				categoeies.add(cat);
			}

			// get all the other decisions
			sql = "select category, definition, uploadID from categories where (uploadID = ? or uploadID IS NULL"
					+ " or uploadID = 0) "
					+ "and category <> ? "
					+ " order by category";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setString(2, firstCat);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				Category cat = new Category();
				cat.setName(rset.getString("category"));
				cat.setDef(rset.getString("definition"));
				if (rset.getInt("uploadID") > 0) {
					cat.setUserCreated(true);
				}
				categoeies.add(cat);
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getCategories" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return categoeies;
	}

	/**
	 * OTO lite: get types table
	 * 
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer, String> getTermTypes() throws SQLException {
		HashMap<Integer, String> termTypes = new HashMap<Integer, String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select typeID, typeName from types";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				termTypes
						.put(rset.getInt("typeID"), rset.getString("typeName"));
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getTermTypes" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return termTypes;
	}

	/**
	 * OTO lite: get saved terms for all category
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String, ArrayList<SavedTerm>> getSavedTerms(int uploadID)
			throws SQLException {
		HashMap<String, ArrayList<SavedTerm>> catTerms = new HashMap<String, ArrayList<SavedTerm>>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, synRset = null;
		try {
			conn = getConnection();

			// get all the decisions
			String sql = "select category, term, isMainTerm from decisions where uploadID = ? order by category, term";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();

			String lastCatName = "";
			ArrayList<SavedTerm> terms = new ArrayList<SavedTerm>();
			SavedTerm st = null;
			while (rset.next()) {
				// separate categories
				String category = rset.getString("category");
				if (!lastCatName.equals(category)) {
					if (!lastCatName.equals("")) {
						catTerms.put(lastCatName, terms);
						terms = new ArrayList<SavedTerm>();
					}
				}

				lastCatName = category;
				// get each term
				st = new SavedTerm();
				String termName = rset.getString("term");
				st.setTermName(termName);
				st.setAdditional(!rset.getBoolean("isMainTerm"));

				// check synonyms
				if (!st.isAdditional()) {
					String synsql = "select mainTerm, synonym from synonyms where "
							+ "uploadID = ? and mainTerm = ? and category = ?";
					pstmt = conn.prepareStatement(synsql);
					pstmt.setInt(1, uploadID);
					pstmt.setString(2, termName);
					pstmt.setString(3, category);
					synRset = pstmt.executeQuery();
					ArrayList<String> syns = new ArrayList<String>();
					while (synRset.next()) {
						st.setHasSyns(true);
						syns.add(synRset.getString("synonym"));
					}
					if (st.isHasSyns()) {
						st.setSyns(syns);
					}
				}
				terms.add(st);
			}

			// last group
			if (!lastCatName.equals("")) {
				catTerms.put(lastCatName, terms);
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getSavedTerms" + exe);
		} finally {
			if (synRset != null) {
				synRset.close();
			}
			closeConnection(pstmt, rset, conn);
		}
		return catTerms;
	}

	/**
	 * For OTO lite, get available group and terms to display in the left column
	 * of the page
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Group> getGroups(int uploadID) throws SQLException {
		ArrayList<Group> groups = new ArrayList<Group>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			// terms in terms table, but not in decision table
			String sql = "select term, type from terms "
					+ "where uploadID = ? and "
					+ "term not in (select term from decisions where uploadID = ?) "
					+ "order by type, term";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setInt(2, uploadID);
			rset = pstmt.executeQuery();

			int lastGroupId = -1;
			Group group = null;
			ArrayList<String> terms = null;
			while (rset.next()) {
				// separate groups
				if (lastGroupId != rset.getInt("type")) {
					if (group != null) {
						group.setTerms(terms);
						groups.add(group);
					}

					group = new Group();
					terms = new ArrayList<String>();
					group.setGroupID(rset.getInt("type"));
					lastGroupId = group.getGroupID();
				}

				// get each term
				terms.add(rset.getString("term"));
			}

			// last group
			if (group != null) {
				group.setTerms(terms);
				groups.add(group);
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getGroups" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return groups;
	}

	/**
	 * get context for OTO lite
	 * 
	 * @param term
	 * @param uploadID
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ContextBean> getContextForTerm(String term, int uploadID)
			throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<ContextBean> contexts = new ArrayList<ContextBean>();
		if (term != null && !term.equals("")) {

			try {
				conn = getConnection();
				String originalTerm = term;
				boolean isTypo = false;
				// get the original term of the given term
				String sql = "select originalTerm from typos where uploadID = ? and replacedBy = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uploadID);
				pstmt.setString(2, term);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					originalTerm = rset.getString(1);
					isTypo = true;
				}

				sql = "SELECT source, originalsent "
						+ "FROM sentences where uploadID = ? "
						+ "and (originalsent rlike '^(.*[^a-zA-Z])?"
						+ originalTerm + "([^a-zA-Z].*)?$'  "
						+ "or sentence rlike '^(.*[^a-zA-Z])?" + originalTerm
						+ "([^a-zA-Z].*)?$');";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uploadID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String sent = rset.getString("originalsent");
					if (isTypo) {
						sent = "(Typo fixed: " + originalTerm + " -> " + term
								+ ") " + sent;
					}
					ContextBean cbean = new ContextBean(
							rset.getString("source"), sent);
					contexts.add(cbean);
				}

			} catch (Exception exe) {
				LOGGER.error(
						"Couldn't execute db query in CharacterStateDBAccess: getContextForTerm",
						exe);
				exe.printStackTrace();

			} finally {
				closeConnection(pstmt, rset, conn);

			}
		}

		return contexts;
	}

	/**
	 * OTO lite: save decisions
	 * 
	 * @param categories
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean saveCategorizingDecisions(
			ArrayList<Category> categories, int uploadID) throws SQLException {
		String uploadID_str = Integer.toString(uploadID);
		boolean returnValue = false;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			// This has to be a transaction
			conn.setAutoCommit(false);

			for (int i = 0; i < categories.size(); i++) {
				Category category = categories.get(i);

				ArrayList<Term> terms = category.getChanged_terms();
				for (Term term : terms) {
					// delete existing decisions (decisions, synonyms)
					String delete_sql = "delete from decisions where uploadID = "
							+ uploadID_str
							+ " and term = '"
							+ term.getTerm()
							+ "'";
					stmt.executeUpdate(delete_sql);

					// delete synonyms
					delete_sql = "delete from synonyms where uploadID = "
							+ uploadID_str + " and mainTerm = '"
							+ term.getTerm() + "'";
					stmt.executeUpdate(delete_sql);

					// insert decisions
					if (!category.getName().equals("")) {
						String isMainTerm = "true";
						if (term.isAdditional()) {
							isMainTerm = "false";
						}
						String insert_sql = "insert into decisions (uploadID, term, category, isMainTerm) values ("
								+ uploadID_str
								+ ", '"
								+ term.getTerm()
								+ "', '"
								+ category.getName()
								+ "', "
								+ isMainTerm + ")";
						stmt.executeUpdate(insert_sql);

						// insert synonyms
						if (term.hasSyn()) {
							ArrayList<String> syns = term.getSyns();
							for (String syn : syns) {
								insert_sql = "insert into synonyms (uploadID, mainTerm, synonym, category) "
										+ "values ("
										+ uploadID_str
										+ ", '"
										+ term.getTerm()
										+ "', '"
										+ syn
										+ "', '" + category.getName() + "')";
								stmt.executeUpdate(insert_sql);
							}
						}
					}
				}
			}
			conn.commit();
			returnValue = true;
		} catch (Exception exe) {
			if (conn != null) {
				LOGGER.error(
						"Couldn't execute db query in CharacterStateDBAccess: saveCategorizingDecisions: ",
						exe);
				exe.printStackTrace();

				try {
					conn.rollback();
				} catch (SQLException ex1) {
					LOGGER.error(
							"Couldn't rollback in CharacterStateDBAccess: saveCategorizingDecisions",
							exe);
					exe.printStackTrace();
				}
			}
		} finally {
			closeConnection(stmt, conn);
		}
		return returnValue;
	}

	/**
	 * for OTO light
	 * 
	 * @param cats
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public synchronized boolean addNewCategory(ArrayList<Category> cats,
			int uploadID) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String query_sql = "select * from categories where category = ? and uploadID = ?"
					+ " and definition = ?";
			String sql = "insert into categories "
					+ "(category, definition, uploadID) values(?, ?, ?)";
			for (int i = 0; i < cats.size(); i++) {
				Category cat = cats.get(i);

				stmt = conn.prepareStatement(query_sql);
				stmt.setString(1, cat.getName());
				stmt.setInt(2, uploadID);
				stmt.setString(3, cat.getDef());
				rs = stmt.executeQuery();
				if (!rs.next()) {
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, cat.getName());
					stmt.setString(2, cat.getDef());
					stmt.setInt(3, uploadID);
					stmt.executeUpdate();
				}
			}
			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't insert new category in CharacterStateDBAccess:addNewCategory",
					exe);
			exe.printStackTrace();
			return false;
		} finally {
			closeConnection(stmt, conn);
			return returnValue;
		}
	}

	public void runCommand(String comd) throws Exception {
		ExecCommmand ec = new ExecCommmand();
		ec.execShellCmd(comd);
	}

	/**
	 * run commands
	 * 
	 * @param commands
	 * @throws Exception
	 */
	public void runCommands(ArrayList<String> commands) throws Exception {
		for (String cmmd : commands) {
			runCommand(cmmd);
		}
	}

}
