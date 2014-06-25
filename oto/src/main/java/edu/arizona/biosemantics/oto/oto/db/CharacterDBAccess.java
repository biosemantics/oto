package edu.arizona.biosemantics.oto.oto.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.gitclient.AddResult;
import edu.arizona.biosemantics.gitclient.GitClient;
import edu.arizona.biosemantics.oto.common.io.ExecCommmand;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.AdminDecisionBean;
import edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionBean;
import edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionsHolderBean;
import edu.arizona.biosemantics.oto.oto.beans.CategoryBean;
import edu.arizona.biosemantics.oto.oto.beans.CategoryHolder;
import edu.arizona.biosemantics.oto.oto.beans.Character;
import edu.arizona.biosemantics.oto.oto.beans.ContextBean;
import edu.arizona.biosemantics.oto.oto.beans.DatasetBean;
import edu.arizona.biosemantics.oto.oto.beans.DatasetStatistics;
import edu.arizona.biosemantics.oto.oto.beans.GlossaryGroupBean;
import edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper;
import edu.arizona.biosemantics.oto.oto.beans.ManagerDecisionBean;
import edu.arizona.biosemantics.oto.oto.beans.Order;
import edu.arizona.biosemantics.oto.oto.beans.OrderDecisionBean;
import edu.arizona.biosemantics.oto.oto.beans.StructureNodeBean;
import edu.arizona.biosemantics.oto.oto.beans.TagBean;
import edu.arizona.biosemantics.oto.oto.beans.Term;
import edu.arizona.biosemantics.oto.oto.beans.TermAndExtentionBean;
import edu.arizona.biosemantics.oto.oto.beans.TermComparator;
import edu.arizona.biosemantics.oto.oto.beans.TermDecision;
import edu.arizona.biosemantics.oto.oto.beans.TermForBioportalBean;
import edu.arizona.biosemantics.oto.oto.beans.TermGlossaryBean;
import edu.arizona.biosemantics.oto.oto.beans.TermRelationBean;
import edu.arizona.biosemantics.oto.oto.beans.TermsForBioportalBean;
import edu.arizona.biosemantics.oto.oto.beans.TermsGroup;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.beans.UserStatisticsBean;
import edu.arizona.biosemantics.oto.oto.beans.VersionBean;
import edu.arizona.biosemantics.oto.oto.mail.NotifyEmail;

/**
 * This class is for all database access pertaining to the Character Marker
 * 
 * @author Partha
 * 
 */
public class CharacterDBAccess extends DatabaseAccess {

	private static final Logger LOGGER = Logger
			.getLogger(CharacterDBAccess.class);
	private int row_per_page = 200;

	private boolean evaluateExecutionTime = false;
	private Configuration configuration;

	public int getRowPerPage() {
		return this.row_per_page;
	}

	public CharacterDBAccess() throws Exception {
		super();
		configuration = Configuration.getInstance();
		evaluateExecutionTime = configuration.getToPrintTime().equals("yes") ? true
				: false;
		row_per_page = Integer.parseInt(configuration.getRowPerPage());
	}

	/**
	 * copy system decisions into _user_terms_decisions: prepopulate decisions
	 * to save work
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public int copySystemDecisions(String dataset) throws SQLException {
		boolean success = false;
		int numCopied = 0;
		Connection conn = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		Statement st = null;
		ResultSet rset = null, rset2 = null;
		try {
			conn = getConnection();
			String sql = "select glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				String glossaryName = GlossaryNameMapper.getInstance()
						.getGlossaryName(rset.getInt(1));
				if (!glossaryName.equals("")) {
					String fromDataset = glossaryName + "_glossary";
					if (!fromDataset.equals(dataset)) {
						st = conn.createStatement();
						conn.setAutoCommit(false);
						// copy new categories
						sql = "insert into "
								+ dataset
								+ "_categories(category, definition) "
								+ "select category, definition from "
								+ fromDataset
								+ "_categories where category in "
								+ "(select distinct c.category from "
								+ "(select a.term, b.category, b.inSystem "
								+ "from (select distinct term from "
								+ dataset
								+ "_web_grouped_terms) a "
								+ "left join (select distinct term, category, 1 as inSystem from "
								+ fromDataset
								+ "_term_category) b "
								+ "on a.term = b.term) c "
								+ "left join (select distinct term, 1 as decided from "
								+ dataset
								+ "_user_terms_decisions) d "
								+ "on c.term = d.term "
								+ "where d.decided is null and c.inSystem is not null) "
								+ "and category not in (select category from "
								+ dataset + "_categories);";
						st.executeUpdate(sql);

						// copy into _user_terms_decisions
						sql = "select c.term, c.category as decision from "
								+ "(select distinct a.term, b.category, b.inSystem from (select distinct term from "
								+ dataset
								+ "_web_grouped_terms) a left join  (select term, category, 1 as inSystem from "
								+ fromDataset
								+ "_term_category) b on a.term = b.term) c "
								+ "left join (select distinct term, 1 as decided from "
								+ dataset
								+ "_user_terms_decisions) d on c.term = d.term "
								+ "where d.decided is null and c.inSystem is not null "
								+ "order by term, decision;";
						pstmt = conn.prepareStatement(sql);
						rset = pstmt.executeQuery();

						// process (term, category) pair one by one because
						// could have more than one category for a term, need to
						// create index
						String lastTerm = "";
						int count = 0;
						while (rset.next()) {
							String term = rset.getString("term");
							String category = rset.getString("decision");
							String termToSave = term;
							if (term.equals(lastTerm)) {
								count++;
								termToSave = term + "_"
										+ Integer.toString(count);
							} else {
								count = 0;
							}

							// insert into _user_terms_decisions table
							sql = "insert into "
									+ dataset
									+ "_user_terms_decisions (term, userID, decision, decisionDate, isAdditional, "
									+ "relatedTerms, isActive, isLatest, hasConflict, hasSyn) values "
									+ "('"
									+ termToSave
									+ "', 1, '"
									+ category
									+ "', sysdate(), false, '', true, true, false, false);";
							st.executeUpdate(sql);

							// insert into _confirmed_category table
							sql = "insert into "
									+ dataset
									+ "_confirmed_category "
									+ "(term, category, userid, categoryApproved, synonymApproved, synonym, "
									+ "termIndex, termWithIndex, synonymWithIndex) "
									+ "values ('" + term + "', '" + category
									+ "', 1, false, false, '', " + count
									+ ", '" + termToSave + "', '')";
							st.execute(sql);

							lastTerm = term;
							numCopied++;
						}

						// copy synonyms if synonym has also been copied from
						// system
						// may have other people modified the decision but as
						// for user system, it is correct
						sql = "select c.term, c.category, c.userid, c.termWithIndex, d.term as synonym, "
								+ "d.termWithIndex as synonymWithIndex "
								+ "from (select a.term, a.category, a.userid, a.termWithIndex, b.synonym "
								+ "from (select term, category, userid, termWithIndex "
								+ "from "
								+ dataset
								+ "_confirmed_category where userid = 1) a "
								+ "left join "
								+ fromDataset
								+ "_syns b on a.term = b.term and a.category = b.category "
								+ "where b.synonym is not null) c "
								+ "left join "
								+ "(select term, category, userid, termWithIndex, 1 as synonymCopied "
								+ "from "
								+ dataset
								+ "_confirmed_category where userid = 1) d "
								+ "on c.synonym = d.term and c.category = d.category where d.synonymCopied is not null;";
						// get termWithIndex, synonymWithIndex, link them
						// together in _user_terms_decision and
						// _confirmed_category tables
						pstmt = conn.prepareStatement(sql);
						rset = pstmt.executeQuery();
						while (rset.next()) {
							String term = rset.getString("term");
							String category = rset.getString("category");
							String termWithIndex = rset
									.getString("termWithIndex");
							String synonym = rset.getString("synonym");
							String synonymWithIndex = rset
									.getString("synonymWithIndex");

							// update mainterm in _user_terms_decisions
							sql = "select decisionid, relatedTerms from "
									+ dataset
									+ "_user_terms_decisions where term = ? and decision = ? and userid = 1";
							pstmt2 = conn.prepareStatement(sql);
							pstmt2.setString(1, termWithIndex);
							pstmt2.setString(2, category);
							rset2 = pstmt2.executeQuery();
							if (rset2.next()) {
								String old_relatedTerm = rset2
										.getString("relatedTerms");
								String new_relatedTerm = "";
								if (old_relatedTerm == null
										|| old_relatedTerm.equals("null")
										|| old_relatedTerm.equals("")) {
									new_relatedTerm = "'" + synonymWithIndex
											+ "'";
								} else {
									new_relatedTerm = old_relatedTerm + ",'"
											+ synonymWithIndex + "'";
								}
								// update hasSyn = true and relatedTerms
								sql = "update "
										+ dataset
										+ "_user_terms_decisions set hasSyn = true, relatedTerms = \""
										+ new_relatedTerm
										+ "\" where userid = 1 and decisionid = "
										+ Integer.toString(rset2
												.getInt("decisionid"));
								st.executeUpdate(sql);
							}

							// update the synonym in _user_terms_decisions
							// table
							sql = "update "
									+ dataset
									+ "_user_terms_decisions set isAdditional = true, relatedTerms = \"synonym of '"
									+ termWithIndex + "'\" where term = '"
									+ synonymWithIndex + "' and decision = '"
									+ category + "'";
							st.executeUpdate(sql);

							// update _confirmed_category
							// delete the one with no synonym
							sql = "delete from "
									+ dataset
									+ "_confirmed_category where userid = 1 and term = '"
									+ term + "' and category = '" + category
									+ "' and termWithIndex = '" + termWithIndex
									+ "' and (synonym is null or synonym = '')";
							st.executeUpdate(sql);

							// insert a new record with synonym
							TermAndExtentionBean termBean = new TermAndExtentionBean(
									termWithIndex);
							sql = "insert into "
									+ dataset
									+ "_confirmed_category "
									+ "(term, category, userid, categoryApproved, synonymApproved, synonym, "
									+ "termIndex, termWithIndex, synonymWithIndex) "
									+ "values ('" + term + "', '" + category
									+ "', 1, false, false, '" + synonym + "', "
									+ termBean.getIndex() + ", '"
									+ termWithIndex + "', '" + synonymWithIndex
									+ "')";
							st.executeUpdate(sql);
						}

						conn.commit();
						success = true;
					}
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: copySystemDecisions: "
					+ exe);

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					LOGGER.error(
							"Couldn't roll back sqls in CharacterDBAccess: copySystemDecisions",
							ex1);
					ex1.printStackTrace();
				}
			}
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
			closeConnection(pstmt2, rset2, conn);
		}

		if (success)
			return numCopied;
		else
			return -1;
	}

	/**
	 * copy accepted categorization decision from system reserved dataset to
	 * another dataset
	 * 
	 * @param fromDataset
	 *            : should be finalized, therefore data comes from
	 *            _confirmed_category
	 * @param toDataset
	 *            : should be unfinalized, insert into confirmed dataset
	 * @return
	 * @throws SQLException
	 */
	public boolean copyAcceptedCategorizationDecisions(String toDataset)
			throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, toDataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				String glossaryName = GlossaryNameMapper.getInstance()
						.getGlossaryName(rset.getInt(1));
				if (!glossaryName.equals("")) {
					String fromDataset = glossaryName + "_glossary";
					if (!fromDataset.equals(toDataset)) {
						// copy approved categories
						sql = "update "
								+ toDataset
								+ "_confirmed_category a "
								+ "left join (select term, category, 1 as systemApproved from "
								+ fromDataset
								+ "_term_category) b "
								+ "on a.term = b.term and a.category = b.category "
								+ "set a.categoryApproved = true "
								+ "where b.systemApproved is not null;";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();

						// copy approved synonyms
						sql = "update "
								+ toDataset
								+ "_confirmed_category a "
								+ "left join (select term, category, synonym, 1 as systemApproved from "
								+ fromDataset
								+ "_syns) b "
								+ "on a.term = b.term and a.category = b.category and a.synonym = b.synonym "
								+ "set a.synonymApproved = true "
								+ "where a.categoryApproved = true and b.systemApproved is not null;";
						pstmt = conn.prepareStatement(sql);
						pstmt.executeUpdate();
					}
				}
			}
			rv = true;
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: copyAcceptedCategorizationDecisions"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * Check if the user has modify right to a dataset
	 * 
	 * @param dataset
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public boolean hasRightToDataset(String dataset, User user)
			throws SQLException {
		boolean rv = false;
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "select * from dataset_owner where dataset = ? and ownerID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			pstmt.setInt(2, user.getUserId());
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = true;
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: hasRightToDataset"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * separate latest version and older versions return in xml nodes format
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public String generateResponseXMLForDownload(String dataset)
			throws SQLException {
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		String rv = "";
		try {
			conn = getConnection();
			String sql = "select * from glossary_versions "
					+ "where dataset = ? "
					+ "order by primaryVersion, secondaryVersion desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			int lastPrimary = -1;
			int lastSecondary = -1;
			boolean isLastLatest = false;
			while (rset.next()) {
				int thisPrimary = rset.getInt("primaryVersion");
				int thisSecondary = rset.getInt("secondaryVersion");
				boolean isLatest = rset.getBoolean("isLatest");
				if (lastPrimary != thisPrimary
						|| lastSecondary != thisSecondary) {
					// a different version: close last version and start a new
					// version
					if (lastPrimary >= 0 && lastSecondary >= 0) {
						if (isLastLatest) {
							rv += "</latestVersion>";
						} else {
							rv += "</oldVersion>";
						}
					}

					if (isLatest) {
						rv += "<latestVersion>";
					} else {
						rv += "<oldVersion>";
					}
				}

				rv += "<file>" + rset.getString("filename") + "</file>";
				lastPrimary = thisPrimary;
				lastSecondary = thisSecondary;
				isLastLatest = isLatest;
			}
			// close the last version
			if (lastPrimary >= 0 && lastSecondary >= 0) {
				if (isLastLatest) {
					rv += "</latestVersion>";
				} else {
					rv += "</oldVersion>";
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: generateResponseXMLForDownload"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
	}

	/**
	 * if this dataset has been merged into another dataset and this other
	 * dataset is also available for download, redirect download to the other
	 * dataset
	 */
	public String getDownloadRedirection(String dataset) throws SQLException {
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		String redirect = "";
		try {
			conn = getConnection();
			String sql = "select distinct mergedInto from glossary_versions where dataset = ? and mergedInto is not null and mergedInto <> ''";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) { // has mergedInto
				String mergedInto = rset.getString(1);
				sql = "select prefix from datasetprefix where prefix = ? and grouptermsdownloadable = true";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, mergedInto);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					redirect = mergedInto;
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getMergedInto" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return redirect;
	}

	/**
	 * if dataset has been merged into some other dataset and the merged one is
	 * also categorization finalized, return the merged big one otherwise return
	 * dataset
	 * 
	 * @param dataset
	 * @param requireFinalized
	 *            : should the return value be finalized or just return the
	 *            mergedInto
	 * @return
	 * @throws SQLException
	 */
	public String getMergedInto(String dataset, boolean requireFinalized)
			throws SQLException {
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		String rv = "";
		try {
			conn = getConnection();
			String sql = "select mergedInto from datasetprefix where prefix = ?";
			if (requireFinalized) {
				sql = "select a.prefix, a.mergedInto, b.grouptermsdownloadable as mergedFinalized "
						+ "from datasetprefix a "
						+ "left join datasetprefix b "
						+ "on a.mergedInto = b.prefix " + "where a.prefix = ?";
			}

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				String redirect = rset.getString("mergedInto");
				if (redirect != null && !redirect.equals("")
						&& !redirect.toLowerCase().equals("null")) {
					if (requireFinalized) {
						if (rset.getBoolean("mergedFinalized")) {
							rv = redirect;
						}
					} else {
						rv = redirect;
					}
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getMergedInto" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
	}

	/**
	 * get all deleted submissions
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<BioportalSubmissionBean> getDeletedSubmissions(
			String dataset) throws SQLException {
		ArrayList<BioportalSubmissionBean> submissions = new ArrayList<BioportalSubmissionBean>();
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "select glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				int glossaryType = rset.getInt("glossaryType");

				sql = "select * from bioportal_deleted_submissions a "
						+ "left join (select userid, concat(firstname, ' ', lastname) as submitterName from users) u1 "
						+ "on a.submittedBy = u1.userid "
						+ "left join (select userid, concat(firstname, ' ', lastname) as deleterName from users) u2 "
						+ "on a.deletedBy = u2.userid " + "order by deleteTime";
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					BioportalSubmissionBean submission = new BioportalSubmissionBean(
							rset.getString("term"));
					submission.setLocalID(rset.getLong("localID"));
					submission.setTmpID(rset.getString("temporaryID"));
					submission.setPermanentID(rset.getString("permanentId"));
					submission.setSuperClass(rset.getString("superClass"));
					submission.setUserid(rset.getInt("userid"));
					submission.setUsername(rset.getString("submitterName"));
					submission.setDefinition(rset.getString("definition"));
					submission.setOntologyIDs(rset.getString("ontologyIds"));
					submission.setSynonyms(rset.getString("synonyms"));
					submission.setTemCategory(rset.getString("termCategory"));
					submission.setDataset(rset.getString("dataset"));
					submission.setSource(rset.getString("source"));
					submission.setGlossaryType(glossaryType);
					submission.setDeletedBy(rset.getString("deleterName"));
					submission.setDeleteTime(rset.getString("deleteTime"));
					submissions.add(submission);
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getDeletedSubmissions"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return submissions;
	}

	/**
	 * get submissions submitted by specified user
	 * 
	 * @param dataset
	 * @param userID
	 * @param show
	 *            : show all or my
	 * @param orderby
	 *            : order by
	 * @return
	 * @throws SQLException
	 */
	public BioportalSubmissionsHolderBean getMySubmissions(String dataset,
			int userID, String show, String orderby) throws SQLException {
		BioportalSubmissionsHolderBean resultHolder = new BioportalSubmissionsHolderBean();
		int pendingCount = 0;
		ArrayList<BioportalSubmissionBean> submissions = new ArrayList<BioportalSubmissionBean>();
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "select glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				int glossaryType = rset.getInt("glossaryType");

				sql = "select b.localID, b.term, b.temporaryID, b.permanentId, "
						+ "b.superClass, u.userid, u.userName, b.definition, b.ontologyIds, "
						+ "b.synonyms, b.termCategory, b.dataset, b.source "
						+ "from bioportal_adoption b "
						+ "left join "
						+ "(select userid, concat(firstname, ' ', lastname) as userName from users) u "
						+ "on b.submittedBy = u.userid "
						+ "where glossaryType = ?";

				if (show.equals("my")) {
					sql += " and userid = ? ";
				}

				if (orderby.equals("")) {
					sql += " order by localID";
				} else if (orderby.equals("status")) {
					sql += " order by permanentId, localID";
				} else if (orderby.equals("term")) {
					sql += " order by term";
				} else if (orderby.equals("category")) {
					sql += " order by termCategory";
				} else if (orderby.equals("dataset")) {
					sql += " order by dataset";
				} else if (orderby.equals("user")) {
					sql += " order by userName, localID";
				}

				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, glossaryType);

				if (show.equals("my")) {
					pstmt.setInt(2, userID);
				}

				rset = pstmt.executeQuery();
				while (rset.next()) {
					BioportalSubmissionBean submission = new BioportalSubmissionBean(
							rset.getString("term"));
					submission.setLocalID(rset.getLong("localID"));
					submission.setTmpID(rset.getString("temporaryID"));
					submission.setPermanentID(rset.getString("permanentId"));
					submission.setSuperClass(rset.getString("superClass"));
					submission.setUserid(rset.getInt("userid"));
					submission.setUsername(rset.getString("userName"));
					submission.setDefinition(rset.getString("definition"));
					submission.setOntologyIDs(rset.getString("ontologyIds"));
					submission.setSynonyms(rset.getString("synonyms"));
					submission.setTemCategory(rset.getString("termCategory"));
					submission.setDataset(rset.getString("dataset"));
					submission.setSource(rset.getString("source"));
					submission.setGlossaryType(glossaryType);
					submissions.add(submission);
					if (!submission.isAdopted()) {
						pendingCount++;
					}
				}
				resultHolder.setSubmissions(submissions);
				resultHolder.setNumOfPending(pendingCount);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getMySubmissions"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return resultHolder;
	}

	/**
	 * move term between removed list and regular list
	 * 
	 * @param term
	 * @param glossaryID
	 * @return
	 * @throws SQLException
	 */
	public boolean bioportalMoveTerm(String term, int glossaryID)
			throws SQLException {
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		boolean rv = false;

		try {
			conn = getConnection();
			String sql = "select * from bioportal_removedterms where term = ? and glossaryID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, term);
			pstmt.setInt(2, glossaryID);
			rset = pstmt.executeQuery();

			if (rset.next()) {
				// delete the record
				sql = "delete from bioportal_removedterms where term = ? and  glossaryID = ?";
			} else {
				// insert the record
				sql = "insert into bioportal_removedterms (term, glossaryID) values (?, ?)";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, term);
			pstmt.setInt(2, glossaryID);
			pstmt.executeUpdate();

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: bioportalMoveTerm"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * get dataset information
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public DatasetBean getDataset(String dataset) throws SQLException {
		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		DatasetBean ds = new DatasetBean(dataset);

		try {
			conn = getConnection();
			String sql = "select prefix, glossaryType "
					+ "from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();

			if (rset.next()) {
				ds.setGlossaryID(rset.getInt("glossaryType"));
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getDataset" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return ds;
	}

	/**
	 * getRelatedTerms
	 * 
	 * @param user
	 * @param dataset
	 * @param term
	 * @return String: synonym: term1, term2; exclusive: term3, term4
	 * @throw Exception
	 */

	public String getRelatedTerms(String term, String dataset, User user)
			throws Exception {
		String relatedTerms = "";
		int userid = user.getUserId();

		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;

		try {
			conn = getConnection();
			String sql = "select * from " + dataset
					+ "_user_terms_relations where userid = ? "
					+ "and term1 = ?";
			String syn = "", exl = "";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			pstmt.setString(2, term);
			rset = pstmt.executeQuery();

			if (rset != null) {
				while (rset.next()) {
					String r_term = rset.getString("term2");
					boolean isSynonym = rset.getBoolean("relation");
					if (isSynonym) {
						if (syn.equals("")) {
							syn = "synonym: " + r_term;
						} else {
							syn += ", " + r_term;
						}
					} else {
						if (exl.equals("")) {
							exl = "exclusive: " + r_term;
						} else {
							exl += ", " + r_term;
						}
					}
				}
			}

			if (!syn.equals("")) {
				if (!exl.equals("")) {
					relatedTerms = syn + "; " + exl;
				} else {
					relatedTerms = syn;
				}
			} else {
				relatedTerms = exl;
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getRelatedTerms" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return relatedTerms;
	}

	/**
	 * This method returns terms list that belong to certain decision category
	 * which contains user's latest decisions and other people's latest
	 * decisions for pre-population purpose
	 * 
	 * @param categoryName
	 * @param dataset
	 * @param user
	 * @return ArrayList<Term>
	 * @throw Exception by f.huang
	 */
	public CategoryHolder getTermsDecidedInCategory(String categoryName,
			String dataset, User user) throws Exception {
		long start = System.currentTimeMillis();
		ArrayList<Term> decidedTerms = new ArrayList<Term>();
		boolean finishedReviewing = true;
		int userid = user.getUserId();

		Connection conn = null;
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			// should seperate the two parts since syn list uses different sql

			// user's active decisions in this category
			String sql = "select * from " + dataset
					+ "_user_terms_decisions where userid = ? "
					+ "and isActive = true and decision = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			pstmt.setString(2, categoryName);
			rset = pstmt.executeQuery();
			boolean reviewed;
			if (rset != null) {
				while (rset.next()) {
					Term term = new Term(rset.getString("term"));
					// get reviewed
					reviewed = getReviewedStatus(dataset, conn,
							user.getUserId(), rset.getString("term"));
					term.setReviewed(reviewed);
					finishedReviewing = finishedReviewing && reviewed;

					boolean isAdditional = rset.getBoolean("isAdditional");
					String relatedTerms = rset.getString("relatedTerms");
					term.setRelatedTerms(relatedTerms);
					term.setIsAdditional(isAdditional);
					term.setConflict(rset.getBoolean("hasConflict"));
					term.setHasSyn(rset.getBoolean("hasSyn"));
					if (term.hasSyn()) {
						// get syn list
						String syn_sql = "select * from "
								+ dataset
								+ "_user_terms_decisions where decision = ? and isActive = true and userid = ? "
								+ "and term in (" + relatedTerms + ")";
						pstmt = conn.prepareStatement(syn_sql);
						pstmt.setString(1, categoryName);
						pstmt.setInt(2, userid);
						ResultSet rs = pstmt.executeQuery();
						ArrayList<Term> synList = new ArrayList<Term>();
						while (rs.next()) {
							Term syn = new Term(rs.getString("term"));

							// get reviewed
							reviewed = getReviewedStatus(dataset, conn,
									user.getUserId(), rset.getString("term"));
							syn.setReviewed(reviewed);
							finishedReviewing = finishedReviewing && reviewed;

							syn.setConflict(rs.getBoolean("hasConflict"));
							synList.add(syn);
						}
						term.setSyns(synList);
					}
					decidedTerms.add(term);
				}
			}

			sql = "select a.term, isAdditional, hasSyn, relatedTerms, hasConflict "
					+ "from (select term, isAdditional, hasSyn, relatedTerms, hasConflict from "
					+ dataset
					+ "_user_terms_decisions where userid <> ? and isLatest = true and  decision = ?) a "
					+ "left join "
					+ "(select distinct term, 1 as hasOwnDecision from "
					+ dataset
					+ "_user_terms_decisions "
					+ "where userid = ? and isActive = true) b "
					+ "on a.term = b.term " + "where hasOwnDecision is null";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			pstmt.setString(2, categoryName);
			pstmt.setInt(3, userid);
			rset = pstmt.executeQuery();
			if (rset != null) {
				while (rset.next()) {
					Term term = new Term(rset.getString("term"));

					// get reviewed
					reviewed = getReviewedStatus(dataset, conn,
							user.getUserId(), rset.getString("term"));
					term.setReviewed(reviewed);
					finishedReviewing = finishedReviewing && reviewed;

					boolean isAdditional = rset.getBoolean("isAdditional");
					String relatedTerms = rset.getString("relatedTerms");
					term.setRelatedTerms(relatedTerms);
					term.setIsAdditional(isAdditional);
					term.setConflict(rset.getBoolean("hasConflict"));
					term.setHasSyn(rset.getBoolean("hasSyn"));
					if (term.hasSyn() && !relatedTerms.equals("")) {
						// get syn list
						String syn_sql = "select * from "
								+ dataset
								+ "_user_terms_decisions where decision = ? and isLatest = true and "
								+ "term in (" + relatedTerms
								+ ") and userid <> ?";
						pstmt = conn.prepareStatement(syn_sql);
						pstmt.setString(1, categoryName);
						pstmt.setInt(2, userid);
						ResultSet rs = pstmt.executeQuery();
						ArrayList<Term> synList = new ArrayList<Term>();
						while (rs.next()) {
							Term syn = new Term(rs.getString("term"));

							// get reviewed
							reviewed = getReviewedStatus(dataset, conn,
									user.getUserId(), rset.getString("term"));
							syn.setReviewed(reviewed);
							finishedReviewing = finishedReviewing && reviewed;

							syn.setConflict(rs.getBoolean("hasConflict"));
							synList.add(syn);
						}
						term.setSyns(synList);
					}
					decidedTerms.add(term);
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getDecidedTermsByUser"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		CategoryHolder ch = new CategoryHolder();
		// sort decidedTerms
		Collections.sort(decidedTerms, new TermComparator());
		ch.setTerms(decidedTerms);
		ch.setFinishedReviewing(finishedReviewing);
		if (evaluateExecutionTime) {
			System.out
					.println("\t* getTermsDecidedInCategory for category '"
							+ categoryName + "' costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		}
		return ch;

	}

	/**
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public DatasetStatistics getDataSetStatistics(User user, String dataset)
			throws Exception {
		DatasetStatistics stat = new DatasetStatistics();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;
		try {
			conn = getConnection();
			// check if dataset exists
			pstmt = conn
					.prepareStatement("select * from datasetprefix where prefix = '"
							+ dataset + "'");
			rset = pstmt.executeQuery();
			if (!rset.next()) {
				return null;
			}
			/**
			 * page 1: categorization
			 */
			// get total number of terms
			String sql = "select distinct count(term) from (select term from "
					+ dataset + "_web_grouped_terms  union (select term from "
					+ dataset + "_user_terms_decisions)) b where term <> ''";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumTotalTerms(rset.getInt(1));
			}

			// get number of uncategorized terms: terms that have no decision
			// record or latest decision is empty
			sql = "select distinct count(a.term) "
					+ "from (select distinct term from "
					+ dataset
					+ "_web_grouped_terms where term <> '') a "
					+ "left join (select distinct term, 1 as decided from "
					+ dataset
					+ "_user_terms_decisions "
					+ "where (isLatest = true and decision <> '') or isLatest = false) b "
					+ "on a.term = b.term " + "where decided is null";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumUnCategorizedTerms(rset.getInt(1));
			}

			// get number of untouched terms: not in decision and not in review
			// history
			sql = "select distinct count(a.term) "
					+ "from (select distinct term from " + dataset
					+ "_web_grouped_terms where term <> '') a "
					+ "left join (select distinct term, 1 as touched from "
					+ "(select distinct term from " + dataset
					+ "_user_terms_decisions union "
					+ "select distinct term from " + dataset
					+ "_review_history) dd) b " + "on a.term = b.term "
					+ "where touched is null";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumUnTouchedTerms(rset.getInt(1));
			}

			// get number of decisions in categorization page
			sql = "select count(*) from " + dataset + "_user_terms_decisions";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumDecisions(rset.getInt(1));
			}

			// get number of review records
			sql = "select count(*) from "
					+ "(select distinct term, userid from " + dataset
					+ "_review_history) a";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumReviews(rset.getInt(1));
			}

			// get users statistics in categorization page
			sql = "select b.userid, c.name, b.count from "
					+ "(select userid, count(term) as count from "
					+ "(select distinct userid, term from "
					+ "(select distinct userid, term from "
					+ dataset
					+ "_user_terms_decisions "
					+ "union select distinct userid, term from "
					+ dataset
					+ "_review_history) d) a "
					+ "group by userid) b "
					+ "left join (select userid, concat(firstname, ' ', lastname) as name from users) c "
					+ "on b.userid = c.userid;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			ArrayList<UserStatisticsBean> userStats = new ArrayList<UserStatisticsBean>();
			while (rset.next()) {
				int userid = rset.getInt("userid");
				UserStatisticsBean userStat = new UserStatisticsBean(userid,
						rset.getString("name"), rset.getInt("count"));
				sql = "select count(*) from " + "(select distinct term from "
						+ dataset + "_user_terms_decisions "
						+ "where userid = ?) a";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, userid);
				rset2 = pstmt.executeQuery();
				if (rset2.next()) {
					userStat.setCount_decidedTerms(rset2.getInt(1));
				}
				userStats.add(userStat);
			}
			stat.setUserStatsInCategorizationWithReview(userStats);

			// get numTags
			sql = "select distinct count(tagName) from " + dataset
					+ "_web_tags";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumTotalTags(rset.getInt(1));
			}

			// get unTouched tags
			sql = "select distinct count(a.tagID) from "
					+ "(select tagID from " + dataset + "_web_tags) a "
					+ "left join (select distinct tagID, 1 as decided "
					+ "from " + dataset
					+ "_user_tags_decisions) b on a.tagID = b.tagID "
					+ "where decided is null;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumUnTouchedTags(rset.getInt(1));
			}

			// get num of total decisions in hierarchy page
			sql = "select count(*) from " + dataset
					+ "_user_tags_decisions where userid <> 0";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumDecisionsInHierarchy(rset.getInt(1));
			}

			// get user info of decisions in hierarchy page
			sql = "select b.userid, c.name, b.count from "
					+ "(select userid, count(*) as count from "
					+ dataset
					+ "_user_tags_decisions "
					+ "group by userid) b "
					+ "left join (select userid, concat(firstname, ' ', lastname) as name from users) c "
					+ "on b.userid = c.userid " + "where b.userid <> 0;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			userStats = new ArrayList<UserStatisticsBean>();
			while (rset.next()) {
				UserStatisticsBean userStat = new UserStatisticsBean(
						rset.getInt("userid"), rset.getString("name"),
						rset.getInt("count"));
				userStats.add(userStat);
			}
			stat.setUserStatsInHierarchy(userStats);

			// get number of order
			sql = "select count(*) from " + dataset
					+ "_web_orders where isBase = false";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumTotalOrders(rset.getInt(1));
			}

			// get number of terms in orders
			sql = "select count(*) from " + dataset + "_web_orders_terms";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumTotalTermsInOrders(rset.getInt(1));
			}

			// get num of decisions in order page
			sql = "select count(*) from " + dataset + "_user_orders_decisions";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumDecisionsInOrders(rset.getInt(1));
			}

			// get user info of decisions in order page
			sql = "select b.userid, c.name, b.count from "
					+ "(select userid, count(*) as count from "
					+ dataset
					+ "_user_orders_decisions "
					+ "group by userid) b "
					+ "left join (select userid, concat(firstname, ' ', lastname) as name from users) c "
					+ "on b.userid = c.userid;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			userStats = new ArrayList<UserStatisticsBean>();
			while (rset.next()) {
				UserStatisticsBean userStat = new UserStatisticsBean(
						rset.getInt("userid"), rset.getString("name"),
						rset.getInt("count"));
				userStats.add(userStat);
			}
			stat.setUserStatsInOrders(userStats);

			// get number of comments
			sql = "select count(*) from " + dataset + "_comments";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				stat.setNumComments(rset.getInt(1));
			}

			// get users info of comments in categorization page
			sql = "select a.userid, b.name, a.count from "
					+ "(select userid, count(*) as count from "
					+ dataset
					+ "_comments "
					+ "group by userid) a "
					+ "left join (select userid, concat(firstname, ' ', lastname) as name from users) b "
					+ "on a.userid = b.userid;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			userStats = new ArrayList<UserStatisticsBean>();
			while (rset.next()) {
				UserStatisticsBean userStat = new UserStatisticsBean(
						rset.getInt("userid"), rset.getString("name"),
						rset.getInt("count"));
				userStats.add(userStat);
			}
			stat.setUserStatsInComments(userStats);
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getDataSetStatistics"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset2 != null) {
				rset2.close();
			}
		}

		return stat;
	}

	/**
	 * For the manageDataset page in admin
	 * 
	 * @param user
	 * @param dataset
	 * @return a list of integers, 3 numbers for each page, then # of comments
	 *         and review history first three are categorizing (# of terms, # of
	 *         decisoins by user, # of decisions by others, # of comments)
	 *         second three are hierarchical (# of terms, # of decisoins by
	 *         user, # of decisions by others, # of comments) third three are
	 *         order (# of terms, # of decisoins by user, # of decisions by
	 *         others, # of comments)
	 * @throws Exception
	 */
	public ArrayList<Integer> getDataSetInfo(User user, String dataset)
			throws Exception {
		ArrayList<Integer> infoList = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			// check if dataset exists
			pstmt = conn
					.prepareStatement("select * from datasetprefix where prefix = '"
							+ dataset + "'");
			rset = pstmt.executeQuery();
			if (!rset.next()) {
				return infoList;
			}

			String tablename = "";
			String query = "";
			for (int i = 0; i < 3; i++) {
				// get total terms numbers
				if (i == 0) {// categorizing
					tablename = "_user_terms_decisions";
					query = "select distinct count(term) from (select term from "
							+ dataset
							+ "_web_grouped_terms union (select term from "
							+ dataset + tablename + ")) b";
				} else if (i == 1) {// tree
					tablename = "_user_tags_decisions";
					query = "select count(*) from " + dataset + "_web_tags";
				} else {// orders
					tablename = "_user_orders_decisions";
					query = "select count(*) from " + dataset
							+ "_web_orders_terms";
				}
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					infoList.add(rset.getInt(1));
				} else {
					infoList.add(0);
				}

				// get # of decisions by self
				query = "select count(*) from " + dataset + tablename;
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					infoList.add(rset.getInt(1));
				} else {
					infoList.add(0);
				}

				// get number of users of decisions
				query = "select count(distinct userid) from " + dataset
						+ tablename;
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					infoList.add(rset.getInt(1));
				} else {
					infoList.add(0);
				}
			}

			// get # of orders
			query = "select count(*) from " + dataset + "_web_orders";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}

			// get number of comments
			query = "select count(*) from " + dataset + "_comments";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}

			// get number of uses who made comments
			query = "select count(distinct userid) from " + dataset
					+ "_comments";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}

			// get number of review records
			query = "select count(*) from " + dataset + "_review_history";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}

			// get number of users who reviewed records
			query = "select count(distinct userid) from " + dataset
					+ "_review_history";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getdataSets" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return infoList;
	}

	/**
	 * Get dataset status and related info for webpage
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public DatasetBean getDatasetInfoForCategorizePage(User user, String dataset)
			throws SQLException {
		DatasetBean datasetInfo = new DatasetBean(dataset);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			// get finalized and mergedInto info
			String query = "select grouptermsdownloadable, mergedInto from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				datasetInfo.setCategorizationFinalized(rset
						.getBoolean("grouptermsdownloadable"));
				String mergedInto = rset.getString("mergedInto");
				if (mergedInto != null && !mergedInto.equals("")
						&& !mergedInto.equals("null")) {
					datasetInfo.setHasBeenMerged(true);
					datasetInfo.setMergedInto(mergedInto);
				} else {
					datasetInfo.setHasBeenMerged(false);
					datasetInfo.setMergedInto("");
				}
			}

			// get total terms numbers
			query = "select distinct count(term) from (select term from "
					+ dataset + "_web_grouped_terms union (select term from "
					+ dataset + "_user_terms_decisions)) b";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				// infoList.add(rset.getInt(1));
				datasetInfo.setNumTermsInCategorizePage(rset.getInt(1));
			} else {
				datasetInfo.setNumTermsInCategorizePage(0);
			}

			// get number of terms reviewed by current user
			query = "select distinct count(*) from " + dataset
					+ "_review_history where userid = " + user.getUserId();
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				datasetInfo.setNumTemrsReviewedInCategorizePage(rset.getInt(1));
			} else {
				datasetInfo.setNumTemrsReviewedInCategorizePage(0);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getDatasetInfoForCategorizePage"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return datasetInfo;
	}

	/**
	 * get dataset info for categorize page. Info includes number of all terms,
	 * number of terms that has been reviewed by current user.
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Integer> getCategorizingInfo(User user, String dataset)
			throws Exception {
		ArrayList<Integer> infoList = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get total terms numbers
			String query = "select distinct count(term) from (select term from "
					+ dataset
					+ "_web_grouped_terms union (select term from "
					+ dataset + "_user_terms_decisions)) b";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}

			// get number of terms reviewed by current user
			query = "select distinct count(*) from " + dataset
					+ "_review_history where userid = " + user.getUserId();
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				infoList.add(rset.getInt(1));
			} else {
				infoList.add(0);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getdataSets" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return infoList;
	}

	/**
	 * get the value of field 'note' in table datasetprefix - will have value
	 * for merged dataset
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public String getDatasetNote(String dataset) throws Exception {
		String rv = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get total terms numbers
			String query = "select note from datasetprefix where prefix = '"
					+ dataset + "'";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = rset.getString("note");
				rv = rv == null ? "" : rv;
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getDatasetNote" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * check if the dataset is empty or not: called before merge dataset
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public boolean isEmptyDataset(String dataset) throws Exception {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get total terms numbers
			String query = "select * from " + dataset
					+ "_web_grouped_terms limit 1";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = true;
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: isEmptyDataset" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * check if the dataset is already exist in datasetprefix table
	 * 
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public boolean isPrefixExist(String dataset) throws Exception {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			// get total terms numbers
			String query = "select * from datasetprefix where prefix = '"
					+ dataset + "'";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = true;
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: isPrefixExist" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * Get terms for the left column of categorizing page, which contains terms
	 * to be decided
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<TermsGroup> getTermsGroupList(User user, String dataset)
			throws Exception {
		ArrayList<TermsGroup> tgList = new ArrayList<TermsGroup>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		HashMap<String, Integer> allTermsList = new HashMap<String, Integer>();
		try {
			conn = getConnection();

			/*
			 * performance improvement: 'not in' is too slow here, use left join
			 * instead - Fengqiong Huang String sql =
			 * "select distinct groupID, term from " + dataset +
			 * "_web_grouped_terms " +
			 * "where term not in (select distinct term from " + dataset +
			 * "_user_terms_decisions) and term <> '' " +
			 * "order by groupID, term";
			 */
			String sql = "select a.groupID, a.term "
					+ "from (select distinct groupID, term from " + dataset
					+ "_web_grouped_terms where term <> '') a "
					+ "left join (select distinct term, 1 as decided from "
					+ dataset + "_user_terms_decisions) b on a.term = b.term "
					+ "where decided is null order by groupID, term;";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			int lastGroupid = 0, groupid = 0;
			TermsGroup tg = new TermsGroup(0);
			ArrayList<Term> termslist = new ArrayList<Term>();
			while (rset.next()) {
				lastGroupid = groupid;
				groupid = rset.getInt(1);
				if (groupid == 0) {
					// if groupid = 0, each term is a group
					if (tg.GetTermsInGroup() != null
							&& tg.GetTermsInGroup().size() > 0) {
						tgList.add(tg);
					}
					tg = new TermsGroup(0);
					termslist = new ArrayList<Term>();
				} else {
					if (lastGroupid != groupid) {
						// add the previous TermsGroup into result list
						if (lastGroupid != 0 && tg.GetTermsInGroup() != null
								&& tg.GetTermsInGroup().size() > 0) {
							tgList.add(tg);
						}
						// and initiate a new TermsGroup
						tg = new TermsGroup(groupid);
						termslist = new ArrayList<Term>();
					}
				}

				String termName = rset.getString(2);
				if (allTermsList.get(termName) != null) {
					// if this term has already be added into the complete term
					// list
					// don't add this term into this group
				} else {
					Term t = new Term(termName);
					t.setConflict(false);
					// get reviewed
					t.setReviewed(getReviewedStatus(dataset, conn,
							user.getUserId(), termName));
					termslist.add(t);
					tg.setTermsInGroup(termslist);
					allTermsList.put(termName, 1);
				}
			}

			if (tg.GetTermsInGroup() != null && tg.GetTermsInGroup().size() > 0) {
				tgList.add(tg);
			}

			// previous part contains terms that have never been made decision
			// next part contains terms that being dragged from right side to
			// left side
			sql = "select distinct term, hasConflict from ("
					+ " select term, hasConflict from "
					+ dataset
					+ "_user_terms_decisions "
					+ " where isActive = true and decision = '' and userid = ? "
					// previous part: self decided to be empty category

					// next part: others decided to be empty category, for
					// pre-populate purpose
					+ " union (select term, hasConflict from "
					+ dataset
					+ "_user_terms_decisions where isLatest = ? and decision = ? and userid <> ? "
					+ " and term not in (select distinct term from " + dataset
					+ "_user_terms_decisions where userid = ?))) a "
					+ "order by term";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			pstmt.setBoolean(2, true);
			pstmt.setString(3, "");
			pstmt.setInt(4, user.getUserId());
			pstmt.setInt(5, user.getUserId());
			rset = pstmt.executeQuery();

			termslist = new ArrayList<Term>();
			while (rset.next()) {
				Term term = new Term(rset.getString(1));
				term.setConflict(rset.getBoolean(2));
				// get reviewed
				term.setReviewed(getReviewedStatus(dataset, conn,
						user.getUserId(), rset.getString(1)));
				termslist.add(term);
			}
			if (termslist.size() > 0) {
				tg = new TermsGroup(-1);
				tg.setTermsInGroup(termslist);
				tgList.add(tg);
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getTermsGroupList" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return tgList;
	}

	/**
	 * get the bioportal related information for a given term in a given dataset
	 * including syns, available categories for submission, related submissions
	 * 
	 * @param dataset
	 * @param term
	 * @return
	 * @throws SQLException
	 */
	public TermForBioportalBean getTermForBioportal(String dataset, String term)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet sentenceSet = null;
		TermForBioportalBean termBio = new TermForBioportalBean(term);

		try {
			conn = getConnection();

			// determine if dataset is finalized
			// and get glossaryType of this dataset
			boolean isFinalized = false;
			int glossaryType = 0;
			String sql = "select grouptermsdownloadable as isFinalized, glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				if (rset.getBoolean("isFinalized")) {
					isFinalized = true;
				}
				glossaryType = rset.getInt("glossaryType");
			}

			// get available categories
			if (isFinalized) {
				sql = "select distinct category from " + dataset
						+ "_term_category "
						+ "where term = ? and category not in "
						+ "(select termCategory from bioportal_adoption "
						+ "where term = ? and glossaryType = ?)";
			} else {
				sql = "select distinct decision as category from "
						+ dataset
						+ "_user_terms_decisions "
						+ "where term = ? and isActive = true and decision <> '' and decision not in "
						+ "(select termCategory from bioportal_adoption "
						+ "where term = ? and glossaryType = ?)";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, term);
			pstmt.setString(2, term);
			pstmt.setInt(3, glossaryType);
			rset = pstmt.executeQuery();
			ArrayList<String> categories = new ArrayList<String>();
			while (rset.next()) {
				categories.add(rset.getString("category"));
			}
			termBio.setAvailableCategories(categories);

			// get synonyms
			if (isFinalized) {
				if (categories.size() > 0) {
					/*
					 * for new submission only, therefore, if no available
					 * category, no need to get the synonyms
					 */
					sql = "select term, group_concat(synonym) as syns from "
							+ dataset + "_syns where term = ? group by term";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, term);
					rset = pstmt.executeQuery();
					if (rset.next()) {
						termBio.setSyns(rset.getString("syns"));
					}
				}
			}

			// get source
			if (categories.size() > 0) {
				// get source from _sentence table
				String sentence_sql = "select '"
						+ term
						+ "' as term, group_concat(source) as source from ( "
						+ "select distinct SUBSTRING_INDEX(source, '-', 1) as source "
						+ "from " + dataset + "_sentence "
						+ "where sentence rlike '^(.*[^a-zA-Z])?" + term
						+ "([^a-zA-Z].*)?$' "
						+ "order by source) a group by term";
				pstmt = conn.prepareStatement(sentence_sql);
				sentenceSet = pstmt.executeQuery();
				if (sentenceSet.next()) {
					termBio.setSource(sentenceSet.getString("source"));
				} else {
					termBio.setSource("");
				}
			}

			// get related submissions
			sql = "select b.localID, b.term, b.temporaryID, b.permanentId, "
					+ "b.superClass, u.userid, u.userName, b.definition, b.ontologyIds, "
					+ "b.synonyms, b.termCategory, b.dataset, b.source "
					+ "from bioportal_adoption b "
					+ "left join "
					+ "(select userid, concat(firstname, ' ', lastname) as userName from users) u "
					+ "on b.submittedBy = u.userid "
					+ "where term = ? and glossaryType = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, term);
			pstmt.setInt(2, glossaryType);
			rset = pstmt.executeQuery();
			ArrayList<BioportalSubmissionBean> submissions = new ArrayList<BioportalSubmissionBean>();
			while (rset.next()) {
				BioportalSubmissionBean submission = new BioportalSubmissionBean(
						term);
				submission.setLocalID(rset.getLong("localID"));
				submission.setTmpID(rset.getString("temporaryID"));
				submission.setPermanentID(rset.getString("permanentId"));
				submission.setSuperClass(rset.getString("superClass"));
				submission.setUserid(rset.getInt("userid"));
				submission.setUsername(rset.getString("userName"));
				submission.setDefinition(rset.getString("definition"));
				submission.setOntologyIDs(rset.getString("ontologyIds"));
				submission.setSynonyms(rset.getString("synonyms"));
				submission.setTemCategory(rset.getString("termCategory"));
				submission.setDataset(rset.getString("dataset"));
				submission.setSource(rset.getString("source"));
				submission.setGlossaryType(glossaryType);
				submissions.add(submission);
			}
			termBio.setExistingSubmissions(submissions);

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getTermForBioportal"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
			if (sentenceSet != null) {
				sentenceSet.close();
			}
		}
		return termBio;
	}

	/**
	 * Get the available terms for bioportal submission
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public TermsForBioportalBean getAvailableTermsForSubmission(String dataset)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet sentenceSet = null;

		ArrayList<String> regStrctures = new ArrayList<String>();
		ArrayList<String> regCharacters = new ArrayList<String>();
		ArrayList<String> removedStructures = new ArrayList<String>();
		ArrayList<String> removedCharacters = new ArrayList<String>();
		TermsForBioportalBean terms = new TermsForBioportalBean();
		try {
			conn = getConnection();

			// determine if dataset is finalized
			boolean isFinalized = false;
			int glossaryType = 0;
			String sql = "select grouptermsdownloadable as isFinalized, glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				if (rset.getBoolean("isFinalized")) {
					isFinalized = true;
				}
				glossaryType = rset.getInt("glossaryType");
			}

			if (isFinalized) {
				// get terms from _term_category table
				sql = "select distinct tc.term, tc.category, r.removed from "
						+ dataset
						+ "_term_category tc "
						+ "left join (select term, termCategory, 1 as submitted from bioportal_adoption "
						+ "where glossaryType = "
						+ glossaryType
						+ ") b "
						+ "on b.term = tc.term and b.termCategory = tc.category "
						+ "left join (select term, 1 as removed from bioportal_removedterms "
						+ "where glossaryID = " + glossaryType + ") r "
						+ "on tc.term = r.term "
						+ "where submitted IS NULL order by tc.term";
			} else {
				// get terms from _user_terms_decisions table
				sql = "select distinct tc.term, tc.decision as category, r.removed from "
						+ "(select term, decision from "
						+ dataset
						+ "_user_terms_decisions where isActive = true) tc "
						+ "left join (select term, termCategory, 1 as submitted from bioportal_adoption "
						+ "where glossaryType = "
						+ glossaryType
						+ ") b "
						+ "on b.term = tc.term and b.termCategory = tc.decision "
						+ "left join (select term, 1 as removed from bioportal_removedterms "
						+ "where glossaryID = "
						+ glossaryType
						+ ") r "
						+ "on tc.term = r.term "
						+ "where submitted IS NULL order by tc.term";
			}

			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			String lastCharacterTerm = "";
			while (rset.next()) {
				String term = rset.getString("term");
				if (!term.equals("")) {
					if (rset.getString("category").toLowerCase()
							.equals("structure")) {
						if (rset.getBoolean("removed")) {
							// removed structure
							removedStructures.add(term);
						} else {
							regStrctures.add(term);
						}
					} else if (!lastCharacterTerm.equals(term)) {// Characters
						if (rset.getBoolean("removed")) {
							removedCharacters.add(term);
						} else {
							regCharacters.add(term);
						}
						lastCharacterTerm = term;
					}
				}

			}
			terms.setRegCharacters(regCharacters);
			terms.setRegStructures(regStrctures);
			terms.setRemovedCharacters(removedCharacters);
			terms.setRemovedStructures(removedStructures);

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getAvailableTermsForSubmission"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
			if (sentenceSet != null) {
				sentenceSet.close();
			}
		}
		return terms;
	}

	/**
	 * Get the available terms for bioportal submission
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<TermForBioportalBean> getAvailableTermsForBioportal(
			String dataset) throws SQLException {
		ArrayList<TermForBioportalBean> termBios = new ArrayList<TermForBioportalBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet sentenceSet = null;

		try {
			conn = getConnection();

			// determine if dataset is finalized
			boolean isFinalized = false;
			int glossaryType = 0;
			String sql = "select grouptermsdownloadable as isFinalized, glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				if (rset.getBoolean("isFinalized")) {
					isFinalized = true;
				}
				glossaryType = rset.getInt("glossaryType");
			}

			if (isFinalized) {
				// get terms from _term_category table, get syns (String) from
				// _syns table
				sql = "select tc.term, tc.category, s.syns, b.submitted "
						+ "from "
						+ dataset
						+ "_term_category tc "
						+ "left join (select term, group_concat(synonym) as syns from fna_gloss_syns group by term) s "
						+ "on s.term = tc.term "
						+ "left join (select term, termCategory, 1 as submitted from bioportal_adoption "
						+ "where glossaryType = "
						+ glossaryType
						+ ") b "
						+ "on b.term = tc.term and b.termCategory = tc.category "
						+ "where submitted IS NULL "
						+ "order by tc.term, tc.category " + "";
			} else {
				// get terms from _user_terms_decisions table, leave syns to be
				// null
				sql = "select d.term, d.decision as category, '' as syns, b.submitted "
						+ "from "
						+ dataset
						+ "_user_terms_decisions d "
						+ "left join "
						+ "(select term, termCategory, 1 as submitted from bioportal_adoption "
						+ "where glossaryType = "
						+ glossaryType
						+ ") b "
						+ "on b.term = d.term and b.termCategory = d.decision "
						+ "where submitted IS NULL "
						+ "order by term, category " + "";
			}

			// get available terms
			TermForBioportalBean tb = null;
			String lastTerm = "", lastCategory = "";
			String term = "", category = "";
			ArrayList<String> categories = null;
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				term = rset.getString("term");
				category = rset.getString("category");

				if (term.equals(lastTerm)) { // add category

				} else {
					// insert term
					if (tb != null) {
						if (categories != null && categories.size() > 1) {
							tb.setAvailableCategories(categories);

							// get source from _sentence table
							String sentence_sql = "select '"
									+ tb.getTermName()
									+ "' as term, group_concat(source) as source from ( "
									+ "select distinct SUBSTRING_INDEX(source, '-', 1) as source "
									+ "from " + dataset + "_sentence "
									+ "where sentence rlike '^(.*[^a-zA-Z])?"
									+ tb.getTermName() + "([^a-zA-Z].*)?$' "
									+ "order by source) a group by term";
							pstmt = conn.prepareStatement(sentence_sql);
							sentenceSet = pstmt.executeQuery();
							if (sentenceSet.next()) {
								tb.setSource(sentenceSet.getString("source"));
							}

							termBios.add(tb);
						}
					}

					tb = new TermForBioportalBean();
					tb.setTermName(term);
					tb.setSyns(rset.getString("syns"));
					categories = new ArrayList<String>();
				}

				if (!category.equals(lastCategory) && !category.equals("")) {
					categories.add(category);
				}

				lastTerm = term;
				lastCategory = category;
			}

			// get the last tb
			if (tb != null) {
				if (categories != null && categories.size() > 1) {
					tb.setAvailableCategories(categories);

					// get source from _sentence table
					String sentence_sql = "select '"
							+ tb.getTermName()
							+ "' as term, group_concat(source) as source from ( "
							+ "select distinct SUBSTRING_INDEX(source, '-', 1) as source "
							+ "from " + dataset + "_sentence "
							+ "where sentence rlike '^(.*[^a-zA-Z])?"
							+ tb.getTermName() + "([^a-zA-Z].*)?$' "
							+ "order by source) a group by term";
					pstmt = conn.prepareStatement(sentence_sql);
					sentenceSet = pstmt.executeQuery();
					if (sentenceSet.next()) {
						tb.setSource(sentenceSet.getString("source"));
					}

					termBios.add(tb);
				}
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in CharacterDBAccess: getAvailableTermsForBioportal"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
			if (sentenceSet != null) {
				sentenceSet.close();
			}
		}
		return termBios;
	}

	/**
	 * this is to get reviewed status of the giving term for categorizing page
	 * 
	 * @param dataset
	 * @param conn
	 * @param userid
	 * @param termName
	 * @return
	 * @throws SQLException
	 */
	private boolean getReviewedStatus(String dataset, Connection conn,
			int userid, String termName) throws SQLException {
		String r_sql = "select * from " + dataset
				+ "_review_history where userid = ? and term = ?";
		PreparedStatement stmt = conn.prepareStatement(r_sql);
		stmt.setInt(1, userid);
		stmt.setString(2, termName);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return true;
		} else
			return false;
	}

	public ArrayList<String> getDownloadableDatasets() throws Exception {

		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "select distinct prefix "
					+ "from "
					+ "(select prefix from datasetprefix where grouptermsdownloadable = true or "
					+ "structurehierarchydownloadable = true or termorderdownloadable = true "
					+ "union (select distinct dataset from glossary_versions)) a;";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				datasets.add(rset.getString("prefix"));
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getConfirmedDataSets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	/**
	 * on home page, when list selectable datasets for a user:
	 * 
	 * 1. dataset is not finalized
	 * 
	 * 2. dataset is either public or owned by given user
	 * 
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getSelectableDatasets(int userID) throws Exception {
		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "select prefix from datasetprefix where "
					+ "(isPrivate = false or "
					+ "(prefix in (select distinct dataset from dataset_owner where ownerID = ?))) "
					+ "and "
					+ "(grouptermsdownloadable = false or structurehierarchydownloadable = false "
					+ "or termorderdownloadable = false) "
					+ "order by time_last_accessed desc";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userID);
			// pstmt.setString(1, schema);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				datasets.add(rset.getString("prefix"));
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getSelectableDatasets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	/**
	 * 
	 * This method returns all the datasets that have grouped terms table
	 * associated with them.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public ArrayList<String> getUnfinishedDataSets() throws Exception {

		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "select prefix from datasetprefix where "
					+ " (grouptermsdownloadable = false or structurehierarchydownloadable = false "
					+ "or termorderdownloadable = false) "
					+ "order by time_last_accessed desc";
			pstmt = conn.prepareStatement(query);
			// pstmt.setString(1, schema);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				datasets.add(rset.getString("prefix"));
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getdataSets" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	/**
	 * This method returns all the datasets that have grouped terms table
	 * associated with them.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getDataSets() throws Exception {

		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "select prefix from datasetprefix  "
					+ "order by time_last_accessed desc";
			pstmt = conn.prepareStatement(query);
			// pstmt.setString(1, schema);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				datasets.add(rset.getString("prefix"));
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getdataSets" + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	/**
	 * generae the .txt file containing all the users. Call this function when
	 * registering a new user
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public boolean generateUserFile(String filePath) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean rv = false;

		try {
			conn = getConnection();
			String sql = "select userid, email from users into outfile "
					+ filePath;
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
			rv = true;
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: generateUserFile" + exe);
		} finally {
			closeConnection(pstmt, conn);
		}
		return rv;
	}

	/**
	 * since merge dataset only merge group terms page, we here only consider
	 * grouptermsdownloadable
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean hasMergeableDatasets() throws Exception {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "";
			query = "select prefix from datasetprefix where grouptermsdownloadable = true";
			pstmt = conn.prepareStatement(query);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				rv = true;
			}

		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: hasMergeableDatasets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return rv;
	}

	/**
	 * get mergeable dataset for a user and glossaryType
	 * 
	 * @param user
	 * @param glossaryType
	 * @param finalized
	 * @return
	 * @throws SQLException
	 */
	public GlossaryGroupBean getMergeableDatasets(User user, int glossaryType,
			boolean finalized) throws SQLException {
		GlossaryGroupBean ggb = new GlossaryGroupBean();
		ggb.setGlossaryID(glossaryType);
		ggb.setGlossaryName(GlossaryNameMapper.getInstance().getGlossaryName(
				glossaryType));
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "";
			if (user.getRole().equals("A") || user.getRole().equals("S")) { // admin
				// if a dataset has been merged into another dataset, this
				// dataset cannt be merged again
				query = "select * from datasetprefix where (mergedInto is null or mergedInto = '') "
						+ "and glossaryType = ? and grouptermsdownloadable = ?";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, glossaryType);
				pstmt.setBoolean(2, finalized);
				rset = pstmt.executeQuery();
			} else {// can only merge the datasets that he owns
				// the copy reviewed decision feature will be moved to the
				// dataset management page
				query = "select * from datasetprefix where "
						+ "(prefix in (select dataset from dataset_owner where ownerID = ?) "
						+ ") and (mergedInto is null or mergedInto = '') "
						+ "and glossaryType = ? and grouptermsdownloadable = ?";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, user.getUserId());
				pstmt.setInt(2, glossaryType);
				pstmt.setBoolean(3, finalized);
				rset = pstmt.executeQuery();
			}
			ArrayList<DatasetBean> datasets = new ArrayList<DatasetBean>();
			// do not list system reserved datasets
			while (rset.next()) {
				String name = rset.getString("prefix");
				if (!GlossaryNameMapper.getInstance().isSystemReservedDataset(
						name)) {
					DatasetBean ds = new DatasetBean();
					ds.setName(name);
					ds.setCategorizationFinalized(finalized);
					ds.setNote(rset.getString("note"));
					datasets.add(ds);
				}
			}
			ggb.setDatasets(datasets);
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getMergeableDatasets: "
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return ggb;
	}

	/**
	 * get mergeable datasets for specific user and group them with glossary
	 * type
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<GlossaryGroupBean> getMergeableGlossaries(User user)
			throws SQLException {
		ArrayList<GlossaryGroupBean> glosses = new ArrayList<GlossaryGroupBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "";
			if (user.getRole().equals("A") || user.getRole().equals("S")) { // admin
				// if a dataset has been merged into another dataset, this
				// dataset cannt be merged again
				query = "select * from datasetprefix where mergedInto is null or mergedInto = '' "
						+ "order by glossaryType, time_last_accessed desc";
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
			} else {// dataset owner or just a normal user
				query = "select * from datasetprefix where "
						+ "(prefix in (select dataset from dataset_owner where ownerID = ?) "
						+ "or grouptermsdownloadable = true) and (mergedInto is null or mergedInto = '') "
						+ "order by glossaryType, time_last_accessed desc;";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, user.getUserId());
				rset = pstmt.executeQuery();
			}
			ArrayList<DatasetBean> datasets = null;
			GlossaryGroupBean gloss = null;
			int lastGlossID = 0;
			int datasetsCount = 0; // count should be greater than 2 to enable
									// merge
			while (rset.next()) {
				int glossID = rset.getInt("glossaryType");
				if (glossID != lastGlossID) {
					if (gloss != null && datasetsCount > 1) {
						glosses.add(gloss);
					}

					gloss = new GlossaryGroupBean();
					gloss.setGlossaryID(glossID);
					datasets = new ArrayList<DatasetBean>();
					datasetsCount = 0; // reset count
				}

				DatasetBean ds = new DatasetBean();
				datasetsCount++;
				ds.setName(rset.getString("prefix"));
				ds.setCategorizationFinalized(rset
						.getBoolean("grouptermsdownloadable"));
				ds.setNote(rset.getString("note"));
				datasets.add(ds);
				gloss.setDatasets(datasets);

				lastGlossID = glossID;
			}
			// add the last gloss
			if (gloss != null) {
				glosses.add(gloss);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getMergeableGlossaries: "
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return glosses;
	}

	/**
	 * mergeable datasets include owned dataset or groupTerms finalized datasets
	 * when user is ownner or user
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public ArrayList<DatasetBean> getMergeableDatasets(User user)
			throws Exception {

		ArrayList<DatasetBean> datasets = new ArrayList<DatasetBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "";
			if (user.getRole().equals("A") || user.getRole().equals("S")) { // admin
				// if a dataset has been merged into another dataset, this
				// dataset cannt be merged again
				query = "select * from datasetprefix where mergedInto is null or mergedInto = '' "
						+ "order by time_last_accessed desc";
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
			} else {// dataset owner or just a normal user
				query = "select * from datasetprefix where "
						+ "(prefix in (select dataset from dataset_owner where ownerID = ?) "
						+ "or grouptermsdownloadable = true) and (mergedInto is null or mergedInto = '') ;";
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, user.getUserId());
				rset = pstmt.executeQuery();
			}

			while (rset.next()) {
				DatasetBean ds = new DatasetBean();
				ds.setName(rset.getString("prefix"));
				ds.setCategorizationFinalized(rset
						.getBoolean("grouptermsdownloadable"));
				ds.setGlossaryID(rset.getInt("glossaryType"));
				ds.setNote(rset.getString("note"));
				datasets.add(ds);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getMergeableDatasets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	/**
	 * list all datasets the user can manage: either admin or dataset owner
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public ArrayList<String> getManageableDatasets(User user) throws Exception {

		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try {
			conn = getConnection();
			String query = "";
			// clear dataset_owners

			query = "delete from dataset_owner where dataset not in ("
					+ "select prefix from datasetprefix)";
			pstmt = conn.prepareStatement(query);
			pstmt.executeUpdate();

			// get tables
			if (user.getRole().equals("A") || user.getRole().equals("S")) { // admin
				query = "select prefix from datasetprefix  "
						+ "order by time_last_accessed desc";
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					datasets.add(rset.getString("prefix"));
				}
			} else {// dataset owner
				query = "select distinct dataset from dataset_owner "
						+ "where ownerID = " + user.getUserId();
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					datasets.add(rset.getString("dataset"));
				}
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in DatabaseAccess: getManageableDatasets"
					+ exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return datasets;
	}

	public ArrayList<TermGlossaryBean> getGlossaryFromDB(String term,
			String dataset) throws Exception {
		ArrayList<TermGlossaryBean> glossaries = new ArrayList<TermGlossaryBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select glossaryType from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				sql = "select category, definition from glossary_dictionary "
						+ "where term = ? and glossaryType = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, term);
				pstmt.setInt(2, rset.getInt(1));
				rset = pstmt.executeQuery();
				while (rset.next()) {
					TermGlossaryBean gloss = new TermGlossaryBean(term,
							rset.getString(1), rset.getString(2),
							"In Glossary: ");
					glossaries.add(gloss);
				}
			}

			// from local accepted decisions
			sql = "select distinct category from "
					+ dataset
					+ "_confirmed_category where term = ? and categoryApproved = true";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, term);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				TermGlossaryBean gloss = new TermGlossaryBean(term,
						rset.getString(1), "null", "Accepted: ");
				glossaries.add(gloss);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getGlossaryFromDB",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(pstmt, rset, conn);

		}

		return glossaries;
	}

	/**
	 * This is the getContext method for the hierarchy tree page. get
	 * sourceFiles first
	 * 
	 * @param tag
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ContextBean> getContextForTerm(String term, String dataset)
			throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<ContextBean> contexts = new ArrayList<ContextBean>();
		if (term != null && !term.equals("")) {
			// String sql = "SELECT tag, source, originalsent FROM "
			// + dataset
			// + "_sentence where originalsent rlike '^" + term
			// + "[^a-zA-Z]' or originalsent rlike '[^a-zA-Z]" + term
			// + "[^a-zA-Z]' or originalsent rlike '[^a-zA-Z]" + term
			// + "$' or sentence rlike '^" + term
			// + "[^a-zA-Z]' or sentence rlike '[^a-zA-Z]" + term
			// + "[^a-zA-Z]' or sentence rlike '[^a-zA-Z]" + term + "$'";

			String sql = "select source, originalsent from "
					+ "(SELECT SUBSTRING_INDEX(source, '-', 1) as source, originalsent "
					+ "FROM " + dataset + "_sentence "
					+ "where (originalsent like '%" + term
					+ "%' or sentence like '%" + term
					+ "%') and (originalsent rlike '^(.*[^a-zA-Z])?" + term
					+ "([^a-zA-Z].*)?$' "
					+ "or sentence rlike '^(.*[^a-zA-Z])?" + term
					+ "([^a-zA-Z].*)?$') limit 300) a "
					+ "group by source limit 100";

			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();

				while (rset.next()) {
					ContextBean cbean = new ContextBean(
							rset.getString("source"),
							rset.getString("originalsent"));
					contexts.add(cbean);
				}

			} catch (Exception exe) {
				LOGGER.error(
						"Couldn't execute db query in CharacterDBAccess:getContextForTag",
						exe);
				exe.printStackTrace();

			} finally {
				closeConnection(pstmt, rset, conn);

			}
		}

		return contexts;
	}

	/**
	 * This method gets all the context information for an array of sourceFiles
	 * 
	 * @param sourceFiles
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ContextBean> getContext(String[] sourceFiles,
			String dataset) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<ContextBean> contexts = new ArrayList<ContextBean>();
		String sql = "SELECT source, originalsent FROM " + dataset
				+ "_sentence where source in (";
		for (String source : sourceFiles) {
			sql += "'" + source + "',";
		}

		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				ContextBean cbean = new ContextBean(rset.getString("source"),
						rset.getString("originalsent"));
				contexts.add(cbean);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getContext",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(pstmt, rset, conn);

		}
		return contexts;
	}

	/**
	 * generate the dTreeNode div
	 * 
	 * @param node
	 * @param isForReport
	 * @return
	 */
	public String getDTreeNode(StructureNodeBean node, boolean isForReport) {
		String dTreeNode = "", img = "";
		dTreeNode += "<div class='dTreeNode' id='" + node.getID() + "'>";

		// get join or joinbottom: if self isLastChild, joinbottom, else join
		if (node.IsLastChild()) {
			img = "<img src='images/tree/joinbottom.gif'/>" + img;
		} else {
			img = "<img src='images/tree/join.gif'/>" + img;
		}
		// get all the lines and empties
		// while parent isnot root, if isLastChild, empty, else line
		StructureNodeBean temp = node;
		if (temp.IsRoot()) {
			img = "<img src='images/tree/globe.gif'/>";
		} else {
			while (!temp.IsRoot() && temp.getParentNode() != null
					&& !temp.getParentNode().IsRoot()) {
				temp = temp.getParentNode();
				if (temp.IsLastChild()) {
					img = "<img src='images/tree/empty.gif'/>" + img;
				} else {
					img = "<img src='images/tree/line.gif'/>" + img;
				}
			}
			img = img + "<img src='images/tree/page.gif'/>";
		}

		if (isForReport && node.getID() > 7) {
			dTreeNode += img
					+ "<a class='node'><font style='font-weight: bolder'>"
					+ node.getName() + "</font></a> ( ";
			if (node.isAccepted()) {
				dTreeNode += "<font class='font-text-style' color='green'>Accepted</font>";
			} else if (node.isDecided()) {
				dTreeNode += "<font class='font-text-style' color='purple'>Good Suggestion</font>";
			} else {
				dTreeNode += "<font class='font-text-style'>Pending... </font>";
			}
			dTreeNode += ", <label style='color: grey'>"
					+ node.getDecisionDate() + "</label> )</div>";
		} else {
			dTreeNode += img + "<a class='node'>" + node.getName()
					+ "</a></div>";
		}
		return dTreeNode;
	}

	/**
	 * generate the html of node and its childen
	 * 
	 * @param node
	 * @param isForReport
	 * @return
	 */
	public String getCompleteNode(StructureNodeBean node, boolean isForReport) {
		String completeNode = "";
		completeNode = getDTreeNode(node, isForReport);

		if (!node.getIsLeaf()) {
			completeNode += "<div class='clip' id='" + node.getID() + "'>";
			ArrayList<StructureNodeBean> childenList = node.getChildenList();

			for (int i = 0; i < childenList.size(); i++) {
				completeNode += getCompleteNode(childenList.get(i), isForReport);
			}
			completeNode += "</div>";
		}
		return completeNode;
	}

	/**
	 * generate the whole tree html(for both decision and report page, use
	 * isForReport to specify)
	 * 
	 * @param dataPrefix
	 * @param user
	 * @param isForReport
	 * @return HTML as String
	 * @throws SQLException
	 */
	public String getHierarchyNode(String dataPrefix, User user,
			boolean isForReport) throws SQLException {
		String tree = "";
		ArrayList<StructureNodeBean> nodes = getNodeChildenList(dataPrefix,
				user, 0);// should be only one item: root

		for (StructureNodeBean node : nodes) {
			tree += getCompleteNode(node, isForReport);
		}
		return tree;
	}

	/**
	 * This method returns saved nodes path, key is nodeid, path[0] is
	 * pathWithID in db path[1] is pathWithName in db
	 * 
	 * @param dataPrefix
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String, ArrayList<String>> getSavedNodesPath(
			String dataPrefix, User user) throws SQLException {
		HashMap<String, ArrayList<String>> nodesPath = new HashMap<String, ArrayList<String>>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int userid = user.getUserId();
		try {
			conn = getConnection();
			String sql = "select * from " + dataPrefix
					+ "_user_tags_decisions where userid = ? or userid = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, userid);
			stmt.setLong(2, 0);
			rset = stmt.executeQuery();

			while (rset.next()) {
				long id = rset.getLong("tagID");
				String pathWithID = rset.getString("path");
				String pathWithName = rset.getString("pathWithName");
				ArrayList<String> path = new ArrayList<String>();
				path.add(pathWithID);
				path.add(pathWithName);
				nodesPath.put(Long.toString(id), path);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getNodeChildenList",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
		}
		return nodesPath;
	}

	/**
	 * get hierarchy decision tree of specific user; this is the entry of
	 * getting the whole hierarchy tree
	 * 
	 * @param dataPrefix
	 * @param user
	 * @param nodeID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<StructureNodeBean> getNodeChildenList(String dataPrefix,
			User user, long nodeID) throws SQLException {
		// getFirstLevelNodeList when nodeID = 1
		ArrayList<StructureNodeBean> nodeList = new ArrayList<StructureNodeBean>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int userid = user.getUserId();
		try {
			conn = getConnection();
			String sql = "select * from "
					+ dataPrefix
					+ "_user_tags_decisions where (userid = ? or userid = ?) and tagPID = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, userid);
			stmt.setLong(2, 0);
			stmt.setLong(3, nodeID);
			rset = stmt.executeQuery();

			while (rset.next()) {
				long tagID = rset.getLong("tagID");
				boolean isLeaf = rset.getBoolean("isLeaf");
				// long tagPID = rset.getLong("tagPID");
				String name = rset.getString("name");
				StructureNodeBean node = new StructureNodeBean(tagID, name);
				node.setIsLeaf(isLeaf);
				node.setPath(rset.getString("path"));

				if (nodeID == 0) {
					node.setIsRoot(true);
				}
				if (rset.isFirst()) {
					node.setIsFirstChild(true);
				}
				if (rset.isLast()) {
					node.setIsLastChild(true);
				}

				if (!isLeaf) {
					ArrayList<StructureNodeBean> childenList = getNodeChildenList(
							dataPrefix, user, node);
					node.setChildenList(childenList);
				}

				nodeList.add(node);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getNodeChildenList",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
		}

		return nodeList;
	}

	/**
	 * Get the Children list of specific node: use this method recursively to
	 * get the whole tree
	 * 
	 * @param dataPrefix
	 * @param user
	 * @param pnode
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<StructureNodeBean> getNodeChildenList(String dataPrefix,
			User user, StructureNodeBean pnode) throws SQLException {

		long nodeID = pnode.getID();
		// getFirstLevelNodeList when nodeID = 1
		ArrayList<StructureNodeBean> nodeList = new ArrayList<StructureNodeBean>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null, rset_status = null;
		int userid = user.getUserId();
		try {
			conn = getConnection();
			String sql = "select * from "
					+ dataPrefix
					+ "_user_tags_decisions where (userid = ? or userid = ?) and tagPID = ? and path like ?";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, userid);
			stmt.setLong(2, 0);
			stmt.setLong(3, nodeID);
			stmt.setString(4, "%" + pnode.getPath() + "%");
			rset = stmt.executeQuery();
			SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");

			while (rset.next()) {
				long tagID = rset.getLong("tagID");
				boolean isLeaf = rset.getBoolean("isLeaf");
				// long tagPID = rset.getLong("tagPID");
				String name = rset.getString("name");
				StructureNodeBean node = new StructureNodeBean(tagID, name);
				node.setIsLeaf(isLeaf);
				node.setPath(rset.getString("path"));
				node.setParentNode(pnode);

				if (tagID > 7) {
					// only when tagID>7,decisionDate != null
					node.setDecisionDate(format.format(rset
							.getDate("decisionDate")));
				}
				// get status
				String statusSQL = "select * from "
						+ dataPrefix
						+ "_confirmed_paths where term = ? and pathWithName = ?";
				stmt = conn.prepareStatement(statusSQL);
				stmt.setString(1, name);
				stmt.setString(2, rset.getString("pathWithName"));
				rset_status = stmt.executeQuery();
				if (rset_status.next()) {
					if (rset_status.getBoolean("accepted")) {
						node.setAccepted(true);
						node.setDecided(false);
					}
				} else {
					statusSQL = "select * from " + dataPrefix
							+ "_confirmed_paths where term = ?";
					stmt = conn.prepareStatement(statusSQL);
					stmt.setString(1, name);
					rset_status = stmt.executeQuery();
					if (rset_status.next()) {
						node.setAccepted(false);
						node.setDecided(true);
					}
				}

				if (nodeID == 0) {
					node.setIsRoot(true);
				}
				if (rset.isFirst()) {
					node.setIsFirstChild(true);
				}
				if (rset.isLast()) {
					node.setIsLastChild(true);
				}

				if (!isLeaf) {
					ArrayList<StructureNodeBean> childenList = getNodeChildenList(
							dataPrefix, user, node);
					node.setChildenList(childenList);
				}

				nodeList.add(node);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getNodeChildenList",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
			rset_status = null;
		}

		return nodeList;
	}

	/**
	 * this method return an list of tags for specific user
	 * 
	 * @param dataPrefix
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<TagBean> getTagsList(String dataPrefix, User user)
			throws SQLException {
		ArrayList<TagBean> tagsList = new ArrayList<TagBean>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null, tags = null;
		int userid = user.getUserId();
		try {
			conn = getConnection();
			String sql = "select count(*) from " + dataPrefix + "_web_tags";
			stmt = conn.prepareStatement(sql);

			rset = stmt.executeQuery();
			if (rset.next()) {
				int count = rset.getInt(1);
				if (count <= 7) {
					// if no record at all, insert 7 default records
					if (count == 0) {
						sql = "insert into "
								+ dataPrefix
								+ "_web_tags(tagID, tagName) values "
								+ "(1, 'Plant'), (2, 'Root'), (3, 'Stem'), (4, 'Leaf'), (5, 'Fruit'), (6, 'Seed'), (7, 'Flower');";
						stmt = conn.prepareStatement(sql);
						stmt.executeUpdate();
					}
					// if no record except the required 7 root records in table,
					// get all tags from table _sentences
					// and insert them into _web_tags
					sql = "select distinct(tag) from " + dataPrefix
							+ "_sentence where tag is not null";
					stmt = conn.prepareStatement(sql);
					tags = stmt.executeQuery();
					String treeExisted = "plant, root, stem, leaf, flower, fruit, seed";

					while (tags.next()) {
						// insert into table _web_tags one by one
						String tagName = tags.getString(1).trim();
						if (!tagName.equals("") && tagName.indexOf(" ") == -1
								&& tagName.indexOf("[") == -1
								&& tagName.indexOf("(") == -1) {
							if (!treeExisted.contains(tagName.toLowerCase())) {
								sql = "select * from " + dataPrefix
										+ "_web_tags where tagName = ?";
								stmt = conn.prepareStatement(sql);
								stmt.setString(1, tagName);
								ResultSet rs = stmt.executeQuery();
								if (!rs.next()) {
									String insert_sql = "insert into "
											+ dataPrefix
											+ "_web_tags (tagName) values (?)";
									stmt = conn.prepareStatement(insert_sql);
									stmt.setString(1, tagName);
									stmt.executeUpdate();
								}
							}
						}
					}

					// delete first
					sql = "delete from " + dataPrefix + "_user_tags_decisions";
					stmt = conn.prepareStatement(sql);
					stmt.executeUpdate();
				}
			}

			// insert default info into table _user_tags_decisions,
			sql = "select * from " + dataPrefix + "_user_tags_decisions";
			stmt = conn.prepareStatement(sql);
			ResultSet temp = stmt.executeQuery();
			if (!temp.next()) {
				sql = "insert into "
						+ dataPrefix
						+ "_user_tags_decisions"
						+ "(tagID, tagPID, isLeaf, name, userid, path, pathWithName) "
						+ "values (1, 0, false, 'Plant', 0, '1', 'Plant'), "
						+ "(2, 1, false, 'Root', 0, '1_2', 'Plant-Root'),"
						+ "(3, 1, false, 'Stem', 0, '1_3', 'Plant-Stem'),"
						+ "(4, 1, false, 'Leaf', 0, '1_4', 'Plant-Leaf'),"
						+ "(5, 1, false, 'Fruit', 0, '1_5', 'Plant-Fruit'),"
						+ "(6, 1, false, 'Seed', 0, '1_6', 'Plant-Seed'),"
						+ "(7, 1, false, 'Flower', 0, '1_7', 'Plant-Flower')";
				stmt = conn.prepareStatement(sql);
				stmt.executeUpdate();
			}

			// get tags for this user
			sql = "select tagID, tagName from "
					+ dataPrefix
					+ "_web_tags where tagID > 7 and tagID not in "
					+ "(select tagID from "
					+ dataPrefix
					+ "_user_tags_decisions where userid = ? and removed = ?) order by tagName";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setBoolean(2, true);
			tags = stmt.executeQuery();

			while (tags.next()) {
				TagBean tag = new TagBean(tags.getInt(1), tags.getString(2));
				// get whether this tag has been decided before
				sql = "select * from " + dataPrefix
						+ "_user_tags_decisions where userid = ? and tagID = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, userid);
				stmt.setInt(2, tags.getInt(1));
				rset = stmt.executeQuery();
				if (rset.next()) {
					tag.setDecided(true);
					tag.setHasConflict(rset.getBoolean("hasConflict"));
				} else {
					tag.setDecided(false);
					tag.setHasConflict(false);
				}
				tagsList.add(tag);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getTagList",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
			tags.close();
		}
		return tagsList;
	}

	/**
	 * For instruction page: get all category of all datasets
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<CategoryBean> getCategoryDefinitions() throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		// ArrayList<String> decisions = new ArrayList<String>();
		ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
		try {
			// "show tables like '%_categories'"

			conn = getConnection();

			// general
			String prefix = "";
			String sql = "select category, definition from "
					+ "categories order by category";
			stmt = conn.prepareStatement(sql);
			rset = stmt.executeQuery();
			while (rset.next()) {
				String name = rset.getString("category") + prefix;
				CategoryBean cat = new CategoryBean(name);
				cat.setDef(rset.getString("definition"));
				categories.add(cat);
			}

			// sql = "show tables like '%_categories'";
			// stmt = conn.prepareStatement(sql);
			// rset = stmt.executeQuery();

			DatabaseMetaData md = conn.getMetaData();
			rset = md.getTables(null, null, "%_categories", null);

			while (rset.next()) {
				prefix = rset.getString("TABLE_NAME");
				String eachSQL = "select category, definition from "
						+ prefix
						+ " where category not in (select category from categories) order by category";
				prefix = " (" + prefix.substring(0, prefix.lastIndexOf("_"))
						+ ")";
				stmt = conn.prepareStatement(eachSQL);
				ResultSet eachRset = stmt.executeQuery();
				while (eachRset.next()) {
					String name = eachRset.getString("category") + prefix;
					CategoryBean cat = new CategoryBean(name);
					cat.setDef(eachRset.getString("definition"));
					categories.add(cat);
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getAllCategory",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
		}
		return categories;
	}

	/**
	 * This method returns an ArrayList of Decision Categories
	 * 
	 * @param dataPrefix
	 * @return
	 * @throws SQLException
	 */
	/*
	 * This is dataset specific, need to change this to generalize for other
	 * datasets
	 */
	public ArrayList<CategoryBean> getAllCategory(String dataPrefix)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		// ArrayList<String> decisions = new ArrayList<String>();
		ArrayList<CategoryBean> categories = new ArrayList<CategoryBean>();
		try {

			conn = getConnection();
			// String sql = "SELECT distinct category FROM " +
			// tablePrefix+"_character order by category";
			/*
			 * String sql = "select distinct decision from " +
			 * "(select decision from " + dataPrefix +
			 * "_user_terms_decisions union " +
			 * "select distinct category as decision from fnaglossary) tempDecision "
			 * + "order by decision";
			 */
			String sql = "select category, definition from " + dataPrefix
					+ "_categories order by category";
			stmt = conn.prepareStatement(sql);
			rset = stmt.executeQuery();
			while (rset.next()) {
				CategoryBean cat = new CategoryBean(rset.getString("category"));
				cat.setDef(rset.getString("definition"));
				categories.add(cat);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getAllCategory",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
		}
		return categories;
	}

	/**
	 * check if this order has been saved before, if yes, this action is a
	 * resend
	 * 
	 * @param dataset
	 * @param user
	 * @param order
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public boolean isResendingOrder(String dataset, User user, Order order)
			throws SQLException {
		boolean retv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql = "select orderID from " + dataset
				+ "_user_orders_decisions "
				+ "where userid = ? and orderID = ?";
		try {
			conn = getConnection();
			if (order != null) {
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, user.getUserId());
				stmt.setInt(2, order.getID());
				rset = stmt.executeQuery();
				if (rset.next()) {
					retv = true; // this whole category has been saved before
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: isResendingOrder",
					exe);
			exe.printStackTrace();
			return retv;
		} finally {
			closeConnection(stmt, conn);
			rset.close();
			return retv;
		}
	}

	/**
	 * check if the first node has been saved before, if yes, this must be a
	 * resend
	 * 
	 * @param dataset
	 * @param user
	 * @param nodes
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public boolean isResendingTree(String dataset, User user,
			ArrayList<StructureNodeBean> nodes) throws SQLException {
		boolean retv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql = "select tagID from " + dataset + "_user_tags_decisions "
				+ "where userid = ? and tagID = ? and tagPID = ? ";
		try {
			conn = getConnection();
			if (nodes.size() > 0) {
				StructureNodeBean node = nodes.get(0);
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, user.getUserId());
				stmt.setLong(2, node.getID());
				stmt.setLong(3, node.getPID());
				rset = stmt.executeQuery();
				if (rset.next()) {
					retv = true; // this whole category has been saved before
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: isResendingTree",
					exe);
			exe.printStackTrace();
			return retv;
		} finally {
			closeConnection(stmt, conn);
			rset.close();
			return retv;
		}
	}

	/**
	 * check if the decisions has been saved before. if saved before, this must
	 * be a resend, since decisions can only be made once. In this case, don't
	 * save the decisions again. Otherwise there will be duplicated data in db
	 * 
	 * @param dataset
	 * @param user
	 * @param category
	 * @param trb
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public boolean isResendingGroup(String dataset, User user,
			CategoryBean category, TermRelationBean trb) throws SQLException {
		boolean retv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			if (category != null) {
				String term = category.getTerms().get(0);
				String sql = "select decisionid from " + dataset
						+ "_user_terms_decisions "
						+ "where userid = ? and decision = ? and term = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, user.getUserId());
				stmt.setString(2, category.getName());
				stmt.setString(3, term);
				rset = stmt.executeQuery();
				if (rset.next()) {
					retv = true; // this whole category has been saved before
				}
			}

			if (trb != null) {
				String sql = "select * from "
						+ dataset
						+ "_user_terms_relations "
						+ "where userid = ? and decision = ? and term1 = ? and term2 = ? and relation = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, user.getUserId());
				stmt.setString(2, trb.getDecision());
				stmt.setString(3, trb.getTerm1());
				stmt.setString(4, trb.getTerm2());
				stmt.setInt(5, trb.getRelation());
				rset = stmt.executeQuery();
				if (rset.next()) {
					retv = true;
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: isResendingGroup",
					exe);
			exe.printStackTrace();
			return retv;
		} finally {
			closeConnection(stmt, conn);
			rset.close();
			return retv;
		}
	}

	/**
	 * This method saves the term relations information to the database.
	 * Fengqing 20120125: no longer in use
	 * 
	 * @param trb
	 * @param dataPrefix
	 * @param user
	 * @return boolean
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public synchronized boolean saveTermsRelation(TermRelationBean trb,
			String dataPrefix, User user) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			String term1 = trb.getTerm1();
			String term2 = trb.getTerm2();
			String decision = trb.getDecision();
			int relation = trb.getRelation();
			int userid = user.getUserId();

			conn = getConnection();
			// This has to be a transaction
			conn.setAutoCommit(false);

			// insert data into _user_terms_relations
			String sql = "insert into "
					+ dataPrefix
					+ "_user_terms_relations (term1, term2, relation, decision, userid) "
					+ "values (?, ?, ?, ?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, term1);
			stmt.setString(2, term2);
			stmt.setInt(3, relation);
			stmt.setString(4, decision);
			stmt.setLong(5, userid);
			stmt.execute();

			// update isAdditional: term2
			sql = "update "
					+ dataPrefix
					+ "_user_terms_decisions "
					+ "set isAdditional = ? where userid = ? and decision = ? and term = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, true);
			stmt.setLong(2, userid);
			stmt.setString(3, decision);
			stmt.setString(4, term2);
			stmt.executeUpdate();
			conn.commit();

			// get new related terms string for term1
			sql = "select term2, relation from " + dataPrefix
					+ "_user_terms_relations where term1 = ?"
					+ " and userid = ? and decision = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, term1);
			stmt.setLong(2, userid);
			stmt.setString(3, decision);
			ResultSet rset = stmt.executeQuery();

			String syn = "", exl = "", relatedTerms = "";
			while (rset.next()) {
				String r_term = rset.getString("term2");
				boolean isSynonym = rset.getBoolean("relation");
				if (isSynonym) {
					if (syn.equals("")) {
						syn = "synonym: " + r_term;
					} else {
						syn += ", " + r_term;
					}
				} else {
					if (exl.equals("")) {
						exl = "exclusive: " + r_term;
					} else {
						exl += ", " + r_term;
					}
				}
			}
			if (!syn.equals("")) {
				if (!exl.equals("")) {
					relatedTerms = syn + "; " + exl;
				} else {
					relatedTerms = syn;
				}
			} else {
				relatedTerms = exl;
			}

			// update relatedTerms for term1 update user_terms_decisions table
			if (!relatedTerms.equals("")) {
				sql = "update "
						+ dataPrefix
						+ "_user_terms_decisions "
						+ "set relatedTerms = ? where userid = ? and decision = ? and term = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, relatedTerms);
				stmt.setLong(2, userid);
				stmt.setString(3, decision);
				stmt.setString(4, term1);
				stmt.executeUpdate();
			}

			// commit the transaction
			conn.commit();
			returnValue = true;

			// todo: neen to update hasConflict, isActive

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:saveTermsRelation",
					exe);
			exe.printStackTrace();
			return false;
		} finally {
			closeConnection(stmt, conn);
			return returnValue;
		}
	}

	/**
	 * This method is to save order into database and then calculate the entropy
	 * score of order
	 * 
	 * @param order
	 * @param dataPrefix
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("finally")
	public synchronized String saveOrder(Order order, String dataPrefix,
			User user) throws SQLException {
		String returnValue = "";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "";
			int order_id = 0, i;
			int base_distance = 0;
			String base_term = "";

			// insert new terms
			ArrayList<Character> new_terms = order.getTerms();
			sql = "insert into " + dataPrefix
					+ "_web_orders_terms(orderID, name, isBase) "
					+ "values (?, ?, ?)";
			String query_sql = "select * from "
					+ dataPrefix
					+ "_web_orders_terms where orderID = ? and name = ? and isBase = ?";
			for (i = 0; i < new_terms.size(); i++) {
				// check if term already exists
				stmt = conn.prepareStatement(query_sql);
				stmt = conn.prepareStatement(query_sql);
				stmt.setInt(1, order.getBaseOrderID());
				stmt.setString(2, new_terms.get(i).getName());
				stmt.setBoolean(3, false);
				rs = stmt.executeQuery();
				if (!rs.next()) {
					stmt = conn.prepareStatement(sql);
					stmt.setInt(1, order.getBaseOrderID());
					stmt.setString(2, new_terms.get(i).getName());
					stmt.setBoolean(3, false);
					stmt.executeUpdate();
				}
			}

			// save sub-orders
			ArrayList<Order> subOrders = order.getSubOrders();
			for (i = 0; i < subOrders.size(); i++) {
				Order subOrder = subOrders.get(i);
				ArrayList<Character> terms = subOrder.getTerms();
				if (subOrder.getID() == 0) {
					// insert new order (may insert two identical order)
					sql = "insert into "
							+ dataPrefix
							+ "_web_orders (name, isBase, base, explanation) values (?, ?, ?, ?)";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, subOrder.getName());
					stmt.setBoolean(2, false);
					stmt.setInt(3, order.getBaseOrderID());
					stmt.setString(4, subOrder.getExplanation());

					stmt.executeUpdate();

					sql = "SELECT LAST_INSERT_ID();";
					stmt = conn.prepareStatement(sql);
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						order_id = rs.getInt(1);
					}
				} else {
					order_id = subOrder.getID();

					// delete previous decisions; only needed for existing order
					String delete_sql = "delete from " + dataPrefix
							+ "_user_orders_decisions "
							+ "where orderID = ? and userID = ?";
					stmt = conn.prepareStatement(delete_sql);
					stmt.setInt(1, order_id);
					stmt.setInt(2, user.getUserId());
					stmt.executeUpdate();
				}

				// update islatest
				String update_sql = "update " + dataPrefix
						+ "_user_orders_decisions "
						+ "set isLatest = ?  where orderID = ? ";
				stmt = conn.prepareStatement(update_sql);
				stmt.setBoolean(1, false);
				stmt.setInt(2, order_id);
				stmt.executeUpdate();

				// get base-term and base-distance
				String base_sql = "select * from "
						+ dataPrefix
						+ "_web_orders_terms where orderID  = ? and isBase = ? ";
				stmt = conn.prepareStatement(base_sql);
				stmt.setInt(1, order_id);
				stmt.setBoolean(2, true);
				rs = stmt.executeQuery();
				if (rs.next()) {
					// has base
					base_term = rs.getString("name");

					for (int j = 0; j < terms.size(); j++) {
						if (terms.get(j).getName().equals(base_term)) {
							base_distance = terms.get(j).getDistance();
							break;
						}
					}
				} else {
					// insert baseTerm into _web_orders_terms
					if (terms.size() > 0) {
						base_distance = 0;
						base_term = terms.get(0).getName();
						sql = "insert into "
								+ dataPrefix
								+ "_web_orders_terms (orderID, name, isBase) values (?, ?, ?) ";
						stmt = conn.prepareStatement(sql);
						stmt.setInt(1, order_id);
						stmt.setString(2, base_term);
						stmt.setBoolean(3, true);
						stmt.executeUpdate();
					}
				}

				// insert decision
				sql = "insert into "
						+ dataPrefix
						+ "_user_orders_decisions (userID, orderID, termName, distance, decisionDate, isBase)"
						+ " values (?, ?, ?, ?, sysdate(), ?)";
				int last_position = 0;
				String decision = "";
				for (int j = 0; j < terms.size(); j++) {
					int distance = terms.get(j).getDistance();
					boolean isBase = terms.get(j).getName().equals(base_term) ? true
							: false;
					stmt = conn.prepareStatement(sql);
					stmt.setInt(1, user.getUserId());
					stmt.setInt(2, order_id);
					stmt.setString(3, terms.get(j).getName());
					stmt.setInt(4, distance - base_distance);
					stmt.setBoolean(5, isBase);
					stmt.executeUpdate();
					// get complete order string
					if (distance == last_position) {
						if (!decision.equals("")) {
							decision += ", ";
						}
						decision += terms.get(j).getName();
					} else {
						if (!decision.equals("")) {
							decision += "->";
						}
						decision += terms.get(j).getName();
					}
					last_position = distance;
				}
				// insert the whole order as a string for entropy calculation
				// purpose
				sql = "insert into "
						+ dataPrefix
						+ "_user_orders_decisions (userID, orderID, decisionDate, decision, isTerm)"
						+ " values (?, ?, sysdate(), ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, user.getUserId());
				stmt.setInt(2, order_id);
				stmt.setString(3, decision);
				stmt.setBoolean(4, false);
				stmt.executeUpdate();

				// update hasConflict
				boolean hasConflict = false;
				sql = "select * from "
						+ dataPrefix
						+ "_user_orders_decisions where orderID = ? and isTerm = ? and decision <> ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, order_id);
				stmt.setBoolean(2, false);
				stmt.setString(3, decision);
				rs = stmt.executeQuery();
				if (rs.next()) {
					hasConflict = true;
				}
				sql = "update "
						+ dataPrefix
						+ "_user_orders_decisions set hasConflict = ? where orderID = ? ";
				stmt = conn.prepareStatement(sql);
				stmt.setBoolean(1, hasConflict);
				stmt.setInt(2, order_id);
				stmt.executeUpdate();
				returnValue += "<orderID>" + subOrder.getID() + "</orderID>"
						+ "<hasConflict>" + hasConflict + "</hasConflict>";
			}
			conn.commit();
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:saveOrder",
					exe);
			exe.printStackTrace();
			return "";
		} finally {
			closeConnection(stmt, conn);
			rs.close();
			return returnValue;
		}
	}

	@SuppressWarnings("finally")
	public synchronized boolean saveHierarchyTree(
			ArrayList<StructureNodeBean> nodes, String dataPrefix, User user)
			throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<String, ArrayList<String>> paths = getSavedNodesPath(
				dataPrefix, user);
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "insert into "
					+ dataPrefix
					+ "_user_tags_decisions"
					+ "(tagID, tagPID, isLeaf, name, userid, path, decisionDate, removed, pathWithName) values "
					+ "(?, ?, ?, ?, ?, ?, sysdate(), ?, ?)";
			for (int i = 0; i < nodes.size(); i++) {
				StructureNodeBean node = nodes.get(i);
				ArrayList<String> path_parent = paths.get(Long.toString(node
						.getPID()));
				String pathWithID = path_parent.get(0) + "_"
						+ Long.toString(node.getID());
				String pathWithName = path_parent.get(1) + "-" + node.getName();
				stmt = conn.prepareStatement(sql);
				stmt.setLong(1, node.getID());
				stmt.setLong(2, node.getPID());
				stmt.setBoolean(3, true);
				stmt.setString(4, node.getName());
				stmt.setInt(5, user.getUserId());
				stmt.setString(6, pathWithID);
				stmt.setBoolean(7, node.removeFromSrc());
				stmt.setString(8, pathWithName);
				stmt.executeUpdate();

				// update hasConflict
				String sql_conflict = "select * from "
						+ dataPrefix
						+ "_user_tags_decisions where tagID = ? and userid <> ?"
						+ " and path <> ?";
				stmt = conn.prepareStatement(sql_conflict);
				stmt.setLong(1, node.getID());
				stmt.setInt(2, user.getUserId());
				stmt.setString(3, pathWithID);
				rs = stmt.executeQuery();
				boolean hasConflict = false;
				if (rs.next()) {
					hasConflict = true;
				}
				if (hasConflict) {
					String sql_update = "update "
							+ dataPrefix
							+ "_user_tags_decisions set hasConflict = true where "
							+ "tagID = ?";
					stmt = conn.prepareStatement(sql_update);
					stmt.setLong(1, node.getID());
					stmt.executeUpdate();
				}

				// update parent not isLeaf
				String sql_update = "update " + dataPrefix
						+ "_user_tags_decisions "
						+ "set isLeaf = ? where userid = ? and tagID = ?";
				stmt = conn.prepareStatement(sql_update);
				stmt.setBoolean(1, false);
				stmt.setInt(2, user.getUserId());
				stmt.setLong(3, node.getPID());
				stmt.executeUpdate();
				conn.commit();

				// add this node into hashMap
				ArrayList<String> path = new ArrayList<String>();
				path.add(pathWithID);
				path.add(pathWithName);
				paths.put(Long.toString(node.getID()), path);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:saveGroup",
					exe);
			exe.printStackTrace();
			return false;
		} finally {
			closeConnection(stmt, conn);
			rs = null;
			return returnValue;
		}
	}

	/**
	 * This method is to save categorizing decisions in group term page
	 * 
	 * @param categories
	 * @param dataPrefix
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean saveCategorizingDecisions(
			ArrayList<CategoryBean> categories, String dataPrefix, User user)
			throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			st = conn.createStatement();

			String userid = Integer.toString(user.getUserId());

			// This has to be a transaction
			conn.setAutoCommit(false);
			for (int i = 0; i < categories.size(); i++) {
				CategoryBean category = categories.get(i);
				boolean isRemovedTerms = false;
				String categoryName = category.getName();
				String categoryNameFor_confirmed_category = categoryName;
				if (categoryName.equals("")) {
					isRemovedTerms = true;
					categoryNameFor_confirmed_category = "discarded";
				}

				// new decisions
				// ArrayList<String> terms = category.getTerms();
				ArrayList<Term> terms = category.getChanged_terms();
				for (Term term : terms) {
					// get latest and active decision of current user.
					// if equals to the decision to be saved, no need to save
					String query_sql = "select * from "
							+ dataPrefix
							+ "_user_terms_decisions "
							+ "where userid = ? and isActive = ? and "
							+ "term = ? and decision = ? and hasSyn = ? and isAdditional = ? "
							+ "and relatedTerms = ?";
					pstmt = conn.prepareStatement(query_sql);
					pstmt.setInt(1, user.getUserId());
					pstmt.setBoolean(2, true);
					pstmt.setString(3, term.getTerm());
					pstmt.setString(4, category.getName());
					pstmt.setBoolean(5, term.hasSyn());
					pstmt.setBoolean(6, term.isAdditional());
					pstmt.setString(7, term.getRelatedTerms());
					rset = pstmt.executeQuery();
					if (rset.next()) {
						// no need to save this record, move on to next term
						continue;
					}

					/**
					 * save the decision to _user_terms_decisions table
					 */
					String termName = term.getTerm();

					// update existent isActive in _user_terms_decisions
					String update_sql = "update " + dataPrefix
							+ "_user_terms_decisions set isActive = false "
							+ "where term = '" + termName + "' and userid = "
							+ userid;
					st.executeUpdate(update_sql);

					// update existent isLatest
					update_sql = "update " + dataPrefix
							+ "_user_terms_decisions set isLatest = false "
							+ "where term = '" + termName + "'";
					st.executeUpdate(update_sql);

					// get hasConflict
					String hasConflict = "false";
					query_sql = "select decision from "
							+ dataPrefix
							+ "_user_terms_decisions "
							+ "where term = ? and isActive = ? and decision <> ?";
					pstmt = conn.prepareStatement(query_sql);
					pstmt.setString(1, term.getTerm());
					pstmt.setBoolean(2, true);
					pstmt.setString(3, category.getName());
					rset = pstmt.executeQuery();
					if (rset.next()) {
						hasConflict = "true";
					}

					// insert new decision record
					String hasSyn = (term.hasSyn() ? "true" : "false");
					String isAdditional = (term.isAdditional() ? "true"
							: "false");
					String sql = "insert into "
							+ dataPrefix
							+ "_user_terms_decisions (term, userid, decision, decisiondate, "
							+ "isActive, isLatest, hasConflict, groupid, hasSyn, isAdditional, relatedTerms) "
							+ " values('" + termName + "', " + userid + ", '"
							+ categoryName + "', sysdate(), " + "true, true, "
							+ hasConflict + ", 0, " + hasSyn + ", "
							+ isAdditional + ", \"" + term.getRelatedTerms()
							+ "\")";
					st.executeUpdate(sql);

					// update all to be the same hasConflict
					sql = "update " + dataPrefix + "_user_terms_decisions "
							+ "set hasConflict = " + hasConflict
							+ " where term = '" + termName + "'";
					st.executeUpdate(sql);

					// insert comments for removed terms
					if (isRemovedTerms && !term.getComment().equals("")) {
						sql = "insert into "
								+ dataPrefix
								+ "_comments(term, comments, userid, commentDate) "
								+ "values ('" + termName + "', '"
								+ term.getComment() + "', " + userid
								+ ", sysdate())";
						st.executeUpdate(sql);
					}

					/**
					 * synchronize with _confirmed_category table
					 */
					TermAndExtentionBean termBean = new TermAndExtentionBean(
							term.getTerm());

					// delete active decision in confirmed_category
					update_sql = "delete from " + dataPrefix
							+ "_confirmed_category "
							+ "where termWithIndex = '" + termName
							+ "' and userid = " + userid;
					st.executeUpdate(update_sql);

					// get categoryApproved
					boolean categoryApproved = false;
					sql = "select term, category from "
							+ dataPrefix
							+ "_confirmed_category "
							+ "where term = ? and category = ? and categoryApproved = true";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, termBean.getTerm());
					pstmt.setString(2, categoryNameFor_confirmed_category);
					rset = pstmt.executeQuery();
					if (rset.next()) {
						categoryApproved = true;
					}

					// get isApprovedSynonym
					boolean isApprovedSynonym = false;
					sql = "select term from "
							+ dataPrefix
							+ "_confirmed_category "
							+ "where synonym = ? and category = ? and synonymApproved = true ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, termBean.getTerm());
					pstmt.setString(2, categoryNameFor_confirmed_category);
					rset = pstmt.executeQuery();
					if (rset.next()) {
						isApprovedSynonym = true;
					}

					if (term.hasSyn()) {
						// may insert multiple records
						ArrayList<Term> syns = term.getSyns();
						for (Term syn : syns) {
							// remove index
							TermAndExtentionBean synBean = new TermAndExtentionBean(
									syn.getTerm());

							// get synonymApproved
							boolean synonymApproved = false;
							sql = "select synonymWithIndex from "
									+ dataPrefix
									+ "_confirmed_category "
									+ "where term = ? and category = ? and synonym = ? and synonymApproved = true ";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, termBean.getTerm());
							pstmt.setString(2,
									categoryNameFor_confirmed_category);
							pstmt.setString(3, synBean.getTerm());
							rset = pstmt.executeQuery();
							if (rset.next()) {
								synonymApproved = true;
							}

							sql = "insert into "
									+ dataPrefix
									+ "_confirmed_category "
									+ "(term, category, userid, categoryApproved, isApprovedSynonym, synonymApproved, synonym, "
									+ "termIndex, termWithIndex, synonymWithIndex) "
									+ "values ('" + termBean.getTerm() + "', '"
									+ categoryNameFor_confirmed_category
									+ "', " + userid + ", "
									+ (categoryApproved ? "true" : "false")
									+ ", "
									+ (isApprovedSynonym ? "true" : "false")
									+ ", "
									+ (synonymApproved ? "true" : "false")
									+ ", '" + synBean.getTerm() + "', "
									+ Integer.toString(termBean.getIndex())
									+ ", '" + termBean.getTermWithIndex()
									+ "', '" + synBean.getTermWithIndex()
									+ "')";
							st.executeUpdate(sql);
						}
					} else {
						// insert one record
						sql = "insert into "
								+ dataPrefix
								+ "_confirmed_category "
								+ "(term, category, userid, categoryApproved, isApprovedSynonym, synonymApproved, synonym, "
								+ "termIndex, termWithIndex, synonymWithIndex) "
								+ "values ('" + termBean.getTerm() + "', '"
								+ categoryNameFor_confirmed_category + "', "
								+ userid + ", "
								+ (categoryApproved ? "true" : "false")

								+ ", " + (isApprovedSynonym ? "true" : "false")

								+ ", false, '', "
								+ Integer.toString(termBean.getIndex()) + ", '"
								+ termBean.getTermWithIndex() + "', '')";
						st.executeUpdate(sql);
					}
				}
			}
			conn.commit();
			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: saveCategorizingDecisions",
					exe);
			exe.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					LOGGER.error(
							"Couldn't roll back sqls in CharacterDBAccess: saveCategorizingDecisions",
							ex1);
					ex1.printStackTrace();
				}
			}
		} finally {
			closeConnection(pstmt, rset, conn);
			if (st != null) {
				st.close();
			}
		}
		return returnValue;
	}

	/**
	 * This method saves the decision-group-termslist information to the
	 * database. This needs to be a stored procedure as well!
	 * 
	 * Fengqiong 2012-01-20: replaced by saveCategorizingDecisions
	 * 
	 * @param cbean
	 * @param dataPrefix
	 * @param userid
	 * @return boolean
	 * @throws SQLException
	 *             author: Fengqiong
	 */
	@SuppressWarnings("finally")
	public synchronized boolean saveGroupTerms(CategoryBean category,
			String dataPrefix, User user) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();

			// get latest decision of this term
			// get latest decision of current user
			ArrayList<String> terms = category.getTerms();
			// This has to be a transaction
			conn.setAutoCommit(false);
			String sql = "insert into "
					+ dataPrefix
					+ "_user_terms_decisions (term, userid, decision, decisiondate) "
					+ " values(?, ?, ?, sysdate())";
			stmt = conn.prepareStatement(sql);
			for (String term : terms) {
				stmt.setString(1, term);
				stmt.setInt(2, user.getUserId());
				stmt.setString(3, category.getName());
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:saveGroupTerms",
					exe);
			exe.printStackTrace();
			return false;
		} finally {
			closeConnection(stmt, conn);
			// rs.close();
			return returnValue;
		}
	}

	/**
	 * This function is used to get the decisions user has added terms into
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getProcessedCategories(User user, String dataset) {
		long start = System.currentTimeMillis();
		ArrayList<String> categories = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = "select distinct decision from " + dataset
				+ "_user_terms_decisions order by decision";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			// pstmt.setInt(1, user.getUserId());

			rset = pstmt.executeQuery();
			while (rset.next()) {
				categories.add(rset.getString(1));
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get processed categories", exe);
		} finally {
			try {
				closeConnection(pstmt, rset, conn);
			} catch (SQLException e) {
				LOGGER.error("unable to close connection ", e);
				e.printStackTrace();
			}
		}

		if (evaluateExecutionTime) {
			System.out
					.println("getProcessedCategories costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		}
		return categories;
	}

	/**
	 * get a list of all finalized datasets (only for categorization) which has
	 * the same glossary type with the given dataset
	 * 
	 * @param dataset
	 *            : the return list must have the same glossary type with given
	 *            dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getCategorizationFinalizedDatasets(String dataset)
			throws Exception {
		ArrayList<String> datasets = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = "select distinct prefix from "
				+ "datasetprefix where grouptermsdownloadable = true "
				+ "and glossaryType in (select glossaryType from datasetprefix "
				+ "where prefix = ?)";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				datasets.add(rset.getString(1));
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error(
					"Error in CharacterDBACcess: getCategorizationFinalizedDatasets: ",
					exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return datasets;
	}

	/**
	 * This function is used to get all the saved and processed groups.
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getProcessedGroups(User user, String dataset)
			throws Exception {

		ArrayList<String> processedGroups = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select distinct groupid from " + dataset
				+ "_user_terms_decisions where userid = ? ";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());

			rset = pstmt.executeQuery();
			while (rset.next()) {
				processedGroups.add("Group_" + rset.getInt("groupid"));
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("unable to get processed groups ", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return processedGroups;
	}

	/**
	 * This method will return all the groups that have not been finished
	 * 
	 * @param dataset
	 * @param userid
	 * @return ArrayList<String>
	 * @throws SQLException
	 */

	public ArrayList<String> getAllGroups(String dataset, int userid)
			throws SQLException {
		ArrayList<String> groups = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "select distinct groupid from " + dataset
				+ "_web_grouped_terms";

		ResultSet rset = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				groups.add("Group_" + rset.getInt("groupId"));
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getAllGroups",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);

		}

		return groups;
	}

	/**
	 * This method will return all the groups that have not been processed by
	 * the user so far.
	 * 
	 * @param dataset
	 * @param userid
	 * @param flag
	 * @return
	 * @throws SQLException
	 */
	/* This logic should also contain entropy selection */
	public ArrayList<String> getGroups(String dataset, int userid, boolean flag)
			throws SQLException {
		ArrayList<String> processedGroups = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		/*
		 * The flag is to determine whether we want to show all the unprocessed
		 * groups or all the processed groups
		 */
		String condition = ((flag) ? "in" : "not in");
		/*
		 * String sql = "select distinct groupid from "+ dataset +
		 * "_user_terms_decisions where groupid "+ condition + " (select " +
		 * " distinct groupid from "+ dataset +
		 * "_web_user_grouped_terms where userid = ?) order by groupid";
		 */
		String sql = "select distinct groupid from " + dataset
				+ "_web_grouped_terms where groupid " + condition + " (select "
				+ " distinct groupid from " + dataset
				+ "_web_user_grouped_terms where userid = ?) order by groupid";

		ResultSet rset = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				processedGroups.add("Group_" + rset.getInt("groupId"));
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getProcessedGroups",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);

		}
		return processedGroups;
	}

	/**
	 * Get all orders as a map of <baseOrderID, ArrayList<Order>>
	 * 
	 * @param dataPrefix
	 * @param user
	 * @param baseOrders
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer, ArrayList<Order>> getAllOrders(String dataPrefix,
			User user, ArrayList<Order> baseOrders) throws SQLException {
		HashMap<Integer, ArrayList<Order>> OrdersMap = new HashMap<Integer, ArrayList<Order>>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		ResultSet rset_terms = null;
		try {
			String sql = "select * from " + dataPrefix
					+ "_web_orders where base = ?";
			conn = getConnection();
			for (Order ord : baseOrders) {
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, ord.getID());
				rset = stmt.executeQuery();

				ArrayList<Order> orders = new ArrayList<Order>();
				while (rset.next()) {
					int orderID = rset.getInt("id");
					Order order = new Order(orderID, rset.getString("name"));
					order.setExplanation(rset.getString("explanation"));
					// get terms from decision: order by distance
					ArrayList<Character> savedTerms = new ArrayList<Character>();
					boolean hasOwn = false;
					boolean hasSavedRecords = false;

					// own decision
					String sql_savedTerms = "select * from "
							+ dataPrefix
							+ "_user_orders_decisions "
							+ "where userID = ? and orderID = ? and isTerm = ? order by distance";
					stmt = conn.prepareStatement(sql_savedTerms);
					stmt.setInt(1, user.getUserId());
					stmt.setInt(2, orderID);
					stmt.setBoolean(3, true);
					rset_terms = stmt.executeQuery();
					while (rset_terms.next()) {
						hasOwn = true;
						hasSavedRecords = true;
						Character term = new Character(0,
								rset_terms.getString("termName"));
						term.setDistance(rset_terms.getInt("distance"));
						term.setIsBase(rset_terms.getBoolean("isBase"));
						order.setHasConflict(rset_terms
								.getBoolean("hasConflict"));
						savedTerms.add(term);
					}

					// prepopulate latest decision
					if (!hasOwn) {
						sql_savedTerms = "select * from "
								+ dataPrefix
								+ "_user_orders_decisions "
								+ "where isLatest = ? and orderID = ? and isTerm = ? order by distance";
						stmt = conn.prepareStatement(sql_savedTerms);
						stmt.setBoolean(1, true);
						stmt.setInt(2, orderID);
						stmt.setBoolean(3, true);
						rset_terms = stmt.executeQuery();
						while (rset_terms.next()) {
							hasSavedRecords = true;
							Character term = new Character(0,
									rset_terms.getString("termName"));
							term.setDistance(rset_terms.getInt("distance"));
							term.setIsBase(rset_terms.getBoolean("isBase"));
							order.setHasConflict(rset_terms
									.getBoolean("hasConflict"));
							savedTerms.add(term);
						}
					}
					order.setTerms(savedTerms);

					// get base term
					if (hasSavedRecords) {
						order.setSavedBefore(true);
					} else {
						order.setSavedBefore(false);
						order.setHasConflict(false);
						// get base term for unsaved order
						String sql_getBaseTerm = "select name from "
								+ dataPrefix
								+ "_web_orders_terms where orderID = ? "
								+ " and isBase = ?";
						stmt = conn.prepareStatement(sql_getBaseTerm);
						stmt.setInt(1, orderID);
						stmt.setBoolean(2, true);
						rset_terms = stmt.executeQuery();
						if (rset_terms.next()) {
							String baseTerm = rset_terms.getString("name");
							order.setBaseTermName(baseTerm);
						}
					}
					orders.add(order);
				}
				OrdersMap.put(ord.getID(), orders);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getAllOrders",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, rset, conn);
			if (rset_terms != null) {
				rset_terms.close();
			}

		}
		return OrdersMap;
	}

	/**
	 * This method returns all the base orders for the order page.
	 * 
	 * @param dataPrefix
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Order> getBaseOrders(String dataPrefix)
			throws SQLException {
		ArrayList<Order> orders = new ArrayList<Order>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select * from " + dataPrefix
					+ "_web_orders where isBase = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, true);

			rset = stmt.executeQuery();
			while (rset.next()) {
				int id = rset.getInt("id");
				String name = rset.getString("name");
				String baseTerm = "";
				Order ord = new Order(id, name);

				// get terms
				ArrayList<Character> terms = new ArrayList<Character>();
				String sql_term = "select * from " + dataPrefix
						+ "_web_orders_terms " + "where orderID = ?";
				stmt = conn.prepareStatement(sql_term);
				stmt.setInt(1, id);
				ResultSet rs = stmt.executeQuery();
				int terms_number = 0;
				while (rs.next()) {
					terms_number++;
					String termName = rs.getString("name");
					boolean isBase = rs.getBoolean("isBase");
					Character term = new Character(rs.getInt("id"), termName);
					if (isBase) {
						baseTerm = termName;
					}
					term.setIsBase(rs.getBoolean("isBase"));
					terms.add(term);
				}

				ord.setTermsNumber(terms_number);
				ord.setTerms(terms);
				ord.setBaseTermName(baseTerm);
				orders.add(ord);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getBaseOrders",
					exe);
			exe.printStackTrace();

		} finally {
			closeConnection(stmt, rset, conn);
		}
		return orders;
	}

	/**
	 * This method checks if the datasetTable Exists for a particular dataset.
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public boolean checkIfDataSetTableExist(String dataset) throws SQLException {
		boolean returnFlag = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		String tableName = dataset + "_web_grouped_terms";
		String sql = "SELECT count(*) as count_ FROM information_schema.tables WHERE table_schema = ? "
				+ "AND table_name = ?";
		ResultSet rset = null;

		try {

			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dbName);
			pstmt.setString(2, tableName);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				int count = rset.getInt("count_");
				if (count > 0) {
					returnFlag = true;
				}
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:checkIfDataSetTableExist",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);

		}

		return returnFlag;
	}

	/**
	 * for manage synonyms page, get how many pages are there in total
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public int getPagesCountForManageSynonyms(String dataset)
			throws SQLException {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = "select count(*) as count from (select distinct term, category from "
				+ dataset
				+ "_confirmed_category where categoryApproved = true and "
				+ "synonym is not null and synonym <> '') a;";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt("count");
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getPagesCountForManageSynonyms",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		int rowPerPage = row_per_page;
		if (count % rowPerPage == 0) {
			return count / rowPerPage;
		} else {
			return count / rowPerPage + 1;
		}
	}

	/**
	 * get synonyms decision for admin to approve
	 * 
	 * @param dataset
	 * @param pageNum
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<TermDecision> getTermSynonymsDecisions(String dataset,
			int pageNum) throws SQLException {
		long start = 0;
		if (evaluateExecutionTime)
			start = System.currentTimeMillis();
		ArrayList<TermDecision> tdList = new ArrayList<TermDecision>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset_2 = null;

		int rowPerPage = row_per_page;
		int startRecord = rowPerPage * (pageNum - 1);

		String sql = "select distinct term, category from " + dataset
				+ "_confirmed_category where categoryApproved = true and "
				+ "synonym is not null and synonym <> '' and synonym <> term "
				+ "order by term, category limit "
				+ Integer.toString(startRecord) + ", " + rowPerPage;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				String termName = rset.getString("term");
				String categoryName = rset.getString("category");

				TermDecision td = new TermDecision(termName);
				td.setCategory(categoryName);
				ArrayList<AdminDecisionBean> acceptedDecisions = new ArrayList<AdminDecisionBean>();
				ArrayList<AdminDecisionBean> unconfirmedList = new ArrayList<AdminDecisionBean>();

				// get accepted decisions, could be more than one
				sql = "select term, category, synonym, group_concat(userName) as decidedBy "
						+ "from (select distinct term, category, synonym, userid from "
						+ dataset
						+ "_confirmed_category where term = ? and category = ? and synonym is not null "
						+ "and synonym <> term and synonym <> '' and synonymApproved = ?) a "
						+ "left join (select concat(firstname, ' ', lastname) as userName, userid from users) b "
						+ "on a.userid = b.userid group by term, category, synonym";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, termName);
				pstmt.setString(2, categoryName);

				// get approved list
				pstmt.setBoolean(3, true);
				rset_2 = pstmt.executeQuery();
				while (rset_2.next()) {
					AdminDecisionBean adb = new AdminDecisionBean();
					adb.setSynonym(rset_2.getString("synonym"));
					adb.setDecidedBy(rset_2.getString("decidedBy"));
					acceptedDecisions.add(adb);
				}
				td.setConfirmedDecisionBeans(acceptedDecisions);

				// get unapproved list
				pstmt.setBoolean(3, false);
				rset_2 = pstmt.executeQuery();
				while (rset_2.next()) {
					AdminDecisionBean adb = new AdminDecisionBean();
					adb.setSynonym(rset_2.getString("synonym"));
					adb.setDecidedBy(rset_2.getString("decidedBy"));
					unconfirmedList.add(adb);
				}

				td.setUnconfirmedDecisionsbeans(unconfirmedList);
				tdList.add(td);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getTermSynonymsDecisions",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset_2 != null) {
				rset_2.close();
			}
		}
		if (evaluateExecutionTime) {
			System.out
					.println("getCategoryTermDecisions costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		}
		return tdList;
	}

	/**
	 * get how many pages are there on the manage_category page
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public int getPagesCountForManageCategory(String dataset)
			throws SQLException {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		String sql = "select count(*) as c from "
				+ "(select distinct term from " + dataset
				+ "_confirmed_category) a";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				count = rset.getInt("c");
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getPagesCountForManageCategory",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		int rowPerPage = row_per_page;
		if (count % rowPerPage == 0) {
			return count / rowPerPage;
		} else {
			return count / rowPerPage + 1;
		}
	}

	/**
	 * This method returns all the decisions have been made in specific dataset
	 * so that managers can made final decisions based on these information must
	 * 
	 * @param dataset
	 * @param pageNum
	 *            : page number
	 * @return
	 * @throws SQLException
	 * @author Fengqiong
	 */
	public ArrayList<TermDecision> getCategoryTermDecisions(String dataset,
			int pageNum) throws SQLException {
		long start = 0;
		if (evaluateExecutionTime)
			start = System.currentTimeMillis();
		ArrayList<TermDecision> tdList = new ArrayList<TermDecision>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset_2 = null;

		int rowPerPage = row_per_page;
		int startRecord = rowPerPage * (pageNum - 1);

		String sql = "select distinct term from " + dataset
				+ "_confirmed_category " + "order by term limit "
				+ Integer.toString(startRecord) + ", " + rowPerPage;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				String termName = rset.getString("term");
				TermDecision td = new TermDecision(termName);
				td.setHasConflict(false);
				ArrayList<AdminDecisionBean> acceptedDecisions = new ArrayList<AdminDecisionBean>();
				ArrayList<AdminDecisionBean> unconfirmedList = new ArrayList<AdminDecisionBean>();

				// get accepted decisions, could be more than one
				sql = "select term, category, group_concat(userName) as decidedBy from "
						+ "(select distinct term, category, categoryApproved, userid "
						+ "from "
						+ dataset
						+ "_confirmed_category where term = ? and categoryApproved = ?) a "
						+ "left join (select concat(firstname, ' ', lastname) as userName, userid from users) b "
						+ "on a.userid = b.userid "
						+ "group by term, category;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, termName);

				// get approved list
				pstmt.setBoolean(2, true);
				rset_2 = pstmt.executeQuery();
				while (rset_2.next()) {
					AdminDecisionBean adb = new AdminDecisionBean();
					adb.setCategory(rset_2.getString("category"));
					adb.setDecidedBy(rset_2.getString("decidedBy"));
					acceptedDecisions.add(adb);
				}
				td.setConfirmedDecisionBeans(acceptedDecisions);

				// get unapproved list
				pstmt.setBoolean(2, false);
				rset_2 = pstmt.executeQuery();
				while (rset_2.next()) {
					AdminDecisionBean adb = new AdminDecisionBean();
					adb.setCategory(rset_2.getString("category"));
					adb.setDecidedBy(rset_2.getString("decidedBy"));
					unconfirmedList.add(adb);
				}

				td.setUnconfirmedDecisionsbeans(unconfirmedList);

				if (unconfirmedList.size() > 0 || acceptedDecisions.size() > 0)
					tdList.add(td);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getCategoryTermDecisions",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset_2 != null) {
				rset_2.close();
			}
		}
		if (evaluateExecutionTime) {
			System.out
					.println("getCategoryTermDecisions costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		}
		return tdList;
	}

	public ArrayList<TermDecision> getHierarchyTermDecisions(String dataset)
			throws SQLException {
		ArrayList<TermDecision> tdList = new ArrayList<TermDecision>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rs_confirmed = null, rs_unconfirmed = null;
		String sql = "select distinct tagID, name, hasConflict from " + dataset
				+ "_user_tags_decisions where tagID > 7 order by name";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				ArrayList<String> confirmedList = new ArrayList<String>();
				ArrayList<String> declinedList = new ArrayList<String>();
				ArrayList<String> unconfirmedList = new ArrayList<String>();

				String termName = rset.getString(2);
				TermDecision td = new TermDecision(termName);
				td.setTermID(Integer.toString(rset.getInt(1)));
				td.setHasConflict(rset.getBoolean("hasConflict"));

				// get confirmed decision
				sql = "select pathWithName, accepted from " + dataset
						+ "_confirmed_paths where term = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, termName);
				rs_confirmed = pstmt.executeQuery();
				while (rs_confirmed.next()) {
					if (rs_confirmed.getBoolean(2)) {
						// accepted
						confirmedList.add(rs_confirmed.getString(1));
					} else {
						// declined
						declinedList.add(rs_confirmed.getString(1));
					}
					td.setConfirmed(true);
				}
				td.setAccepedDecisions(confirmedList);
				td.setDeclinedDecisions(declinedList);

				// get unconfirmed decisions
				sql = "select pathWithName, count(pathWithName) from "
						+ dataset
						+ "_user_tags_decisions "
						+ "where name = ? and pathWithName not in (select pathWithName from "
						+ dataset + "_confirmed_paths where term = ?)"
						+ "group by pathWithName";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, termName);
				pstmt.setString(2, termName);
				rs_unconfirmed = pstmt.executeQuery();
				while (rs_unconfirmed.next()) {
					unconfirmedList.add(rs_unconfirmed.getString(1));
				}
				td.setUnconfirmedDecisions(unconfirmedList);

				tdList.add(td);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getHierarchyTermDecisions",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			rs_confirmed = null;
			rs_unconfirmed = null;
		}
		return tdList;
	}

	/**
	 * Fengqiong: This method returns a users list with their decisions of the
	 * order --for managerOrder page
	 * 
	 * @param dataset
	 * @param orderID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<OrderDecisionBean> getSavedDecisionsForOrder(
			String dataset, int orderID) throws SQLException {
		ArrayList<OrderDecisionBean> usersDecisionsList = new ArrayList<OrderDecisionBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rs_d = null;
		try {
			// get users list, get first name and last name
			String sql = "select a.userID, b.firstname, b.lastname from "
					+ dataset
					+ "_user_orders_decisions a join users b on a.userID = b.userid "
					+ "where a.orderID = ? group by userID";
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, orderID);
			rset = pstmt.executeQuery();

			// get character list for each distance
			sql = "select a.termName, a.distance as distance, b.accepted, a.isBase from "
					+ dataset
					+ "_user_orders_decisions a "
					+ "left join "
					+ dataset
					+ "_confirmed_orders b on a.orderID = b.orderID and a.termName = b.term "
					+ "where a.userid = ? and a.orderID = ? and a.isTerm order by distance";
			while (rset.next()) {
				OrderDecisionBean odb = new OrderDecisionBean(
						rset.getString("firstname") + " "
								+ rset.getString("lastname"));
				int userID = rset.getInt("userID");
				HashMap<Integer, ArrayList<Character>> decisionMap = new HashMap<Integer, ArrayList<Character>>();
				// get each mapping: per distance, a list of terms with accept
				// status

				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, userID);
				pstmt.setInt(2, orderID);
				rs_d = pstmt.executeQuery();
				ArrayList<Character> termList = null;
				int lastDistance = -10000, currentDistance = -10000;
				while (rs_d.next()) {
					currentDistance = rs_d.getInt("distance");
					if (currentDistance != lastDistance) {
						if (termList != null) {
							decisionMap.put(lastDistance, termList);
						}
						termList = new ArrayList<Character>();
					}
					Character cha = new Character(rs_d.getString("termName"));
					cha.setDistance(rs_d.getInt("distance"));
					cha.setAccepted(rs_d.getBoolean("accepted")
							|| rs_d.getBoolean("isBase")); // could be
					// null
					termList.add(cha);

					lastDistance = currentDistance;
				}
				if (termList != null) {
					decisionMap.put(lastDistance, termList);
				}

				odb.setDecisions(decisionMap);
				usersDecisionsList.add(odb);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getSavedDecisionsForOrder: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			rs_d = null;
		}

		return usersDecisionsList;
	}

	/**
	 * This method returns decision map for one order: 1st, this map is
	 * organized by terms' distances 2nd, under each distance, there are several
	 * terms, some of them are confirmed, some of them are not
	 * 
	 * @param dataset
	 * @param orderID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<TermDecision> getOrdersTermDecisions(String dataset,
			int orderID) throws SQLException {
		ArrayList<TermDecision> tdList = new ArrayList<TermDecision>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset_confirmed = null;
		try {
			conn = getConnection();
			String sql = "select distinct distance from "
					+ dataset
					+ "_user_orders_decisions where isTerm = true and orderID = ? "
					+ "union (select distance from " + dataset
					+ "_confirmed_orders where orderID = ?) "
					+ "order by distance";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, orderID);
			pstmt.setInt(2, orderID);
			rset = pstmt.executeQuery();

			// for each distance in this order
			while (rset.next()) {
				int distance = rset.getInt(1);
				TermDecision td = new TermDecision(distance);
				ArrayList<String> acceptedList = new ArrayList<String>();
				ArrayList<String> declinedList = new ArrayList<String>();

				// get confirmed (accepted and declined) terms in this distance
				String sql_confirmed = "select term, accepted, distance from "
						+ dataset + "_confirmed_orders "
						+ "where orderID = ? and distance = ?";
				pstmt = conn.prepareStatement(sql_confirmed);
				pstmt.setInt(1, orderID);
				pstmt.setInt(2, distance);
				rset_confirmed = pstmt.executeQuery();
				while (rset_confirmed.next()) {
					if (rset_confirmed.getBoolean("accepted")) {
						// accepted
						acceptedList.add(rset_confirmed.getString(1));
					} else {
						// declined
						declinedList.add(rset_confirmed.getString(1));
					}
				}
				td.setAccepedDecisions(acceptedList);
				td.setDeclinedDecisions(declinedList);

				// add term_decision into tdList
				tdList.add(td);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getOrdersTermDecisions",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset_confirmed != null) {
				rset_confirmed.close();
			}
		}
		return tdList;
	}

	public ArrayList<Order> getDecidedOrders(String dataset)
			throws SQLException {
		ArrayList<Order> odList = new ArrayList<Order>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			// need base term in this order
			conn = getConnection();
			String sql = "select b.id as id, a.hasConflict, b.name, b.explanation, c.name as baseTerm "
					+ " from "
					+ dataset
					+ "_user_orders_decisions a"
					+ " join "
					+ dataset
					+ "_web_orders b"
					+ " on a.orderID = b.id"
					+ " join "
					+ dataset
					+ "_web_orders_terms c"
					+ " on a.orderID = c.orderID where c.isBase = true"
					+ " group by id";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				Order od = new Order(rset.getInt("id"), rset.getString("name"));
				od.setHasConflict(rset.getBoolean("hasConflict"));
				od.setExplanation(rset.getString("explanation"));
				od.setBaseTermName(rset.getString("baseTerm"));
				odList.add(od);
			}
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:getDecidedOrders",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return odList;
	}

	/**
	 * confirm synonym
	 * 
	 * @param mdb
	 * @return
	 * @throws SQLException
	 */
	public boolean confirmSynonym(ManagerDecisionBean mdb) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			String sql;
			// accept
			if (mdb.isAccept()) {
				// add new accepted record
				sql = "update "
						+ mdb.getDataset()
						+ "_confirmed_category set synonymApproved = true, confirmDate = sysdate() where term = ? and category = ? and synonym = ?";

			} else {
				sql = "update "
						+ mdb.getDataset()
						+ "_confirmed_category set synonymApproved = false where term = ? and category = ? and synonym = ?";
			}
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, mdb.getTerm());
			stmt.setString(2, mdb.getCategory());
			stmt.setString(3, mdb.getDecision());
			stmt.executeUpdate();

			// update isApprovedSynonym
			sql = "update "
					+ mdb.getDataset()
					+ "_confirmed_category set isApprovedSynonym = ? where term = ? and category = ?";
			stmt = conn.prepareStatement(sql);
			if (mdb.isAccept()) {
				stmt.setBoolean(1, true);
			} else {
				stmt.setBoolean(1, false);
			}
			stmt.setString(2, mdb.getDecision());
			stmt.setString(3, mdb.getCategory());
			stmt.executeUpdate();

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: confirmSynonym",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, conn);

		}
		return returnValue;
	}

	public boolean revokeCategory(ManagerDecisionBean mdb) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql;
			sql = "select distinct synonym from "
					+ mdb.getDataset()
					+ "_confirmed_category "
					+ "where term = ? and category = ? and synonym is not null and synonym <> ''";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, mdb.getTerm());
			stmt.setString(2, mdb.getDecision());
			rset = stmt.executeQuery();

			sql = "update "
					+ mdb.getDataset()
					+ "_confirmed_category set categoryApproved = false, synonymApproved = false, "
					+ "confirmDate = null where term = ? and category = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, mdb.getTerm());
			stmt.setString(2, mdb.getDecision());
			stmt.executeUpdate();

			// update isApprovedSynonym for possible approved synonyms
			sql = "update " + mdb.getDataset()
					+ "_confirmed_category set isApprovedSynonym = false "
					+ "where term = ? and category = ?";
			stmt = conn.prepareStatement(sql);
			while (rset.next()) {
				stmt.setString(1, rset.getString(1));
				stmt.setString(2, mdb.getDecision());
				stmt.executeUpdate();
			}
			rv = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: revokeCategory",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, rset, conn);
		}
		return rv;
	}

	public boolean confirmCategory(ManagerDecisionBean mdb) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql;
			// accept
			if (mdb.isAccept()) {
				// resolve conflict first
				if (mdb.getDecision().equals("discarded")) {
					// if accept "discarded", revoke all other accepted
					// categories
					sql = "select category from "
							+ mdb.getDataset()
							+ "_confirmed_category where categoryApproved = true "
							+ "and term = ? ";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, mdb.getTerm());
					rset = stmt.executeQuery();
					while (rset.next()) {
						revokeCategory(new ManagerDecisionBean(mdb.getTerm(),
								rset.getString(1), mdb.getDataset()));
					}
				} else {
					// if accept a category, revoke "discarded" if accepted
					sql = "update "
							+ mdb.getDataset()
							+ "_confirmed_category set categoryApproved = false "
							+ "where term = ? and category = ?";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, mdb.getTerm());
					stmt.setString(2, "discarded");
					stmt.executeUpdate();
				}

				// add new accepted record
				sql = "update "
						+ mdb.getDataset()
						+ "_confirmed_category set categoryApproved = true, confirmDate = sysdate() "
						+ "where term = ? and category = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, mdb.getTerm());
				stmt.setString(2, mdb.getDecision());
				stmt.executeUpdate();
			} else {
				revokeCategory(mdb);
			}
			rv = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: confirmCategory",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, rset, conn);
		}
		return rv;
	}

	public synchronized boolean confirmPath(ManagerDecisionBean mdb)
			throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			String sql = "";
			if (mdb.isAccept()) {
				sql = "insert into " + mdb.getDataset() + "_confirmed_paths "
						+ " (term, pathWithName, accepted, confirmDate) "
						+ "values (?, ?, ?, sysdate())";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, mdb.getTerm());
				stmt.setString(2, mdb.getDecision());
				stmt.setBoolean(3, mdb.isAccept());
				stmt.executeUpdate();
			} else {
				sql = "delete from "
						+ mdb.getDataset()
						+ "_confirmed_paths where term = ? and pathWithName = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, mdb.getTerm());
				stmt.setString(2, mdb.getDecision());
				stmt.executeUpdate();
			}
			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess:confirmPath",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, conn);
		}
		return returnValue;
	}

	public synchronized boolean confirmOrder(ManagerDecisionBean mdb)
			throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			String sql = "";
			if (mdb.isAccept()) {
				// delete all other accepted distance
				// one term in one order can only have one distance
				sql = "delete from " + mdb.getDataset() + "_confirmed_orders "
						+ "where term = ? and orderID = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, mdb.getTerm());
				stmt.setInt(2, mdb.getOrderID());
				stmt.executeUpdate();
				sql = "insert into " + mdb.getDataset() + "_confirmed_orders "
						+ " (term, orderID, distance, accepted, confirmDate) "
						+ "values (?, ?, ?, true, sysdate())";
				;
			} else {
				sql = "delete from " + mdb.getDataset() + "_confirmed_orders "
						+ "where term = ? and orderID = ? and distance = ?";
			}
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, mdb.getTerm());
			stmt.setInt(2, mdb.getOrderID());
			stmt.setInt(3, Integer.parseInt(mdb.getDecision()));
			stmt.executeUpdate();

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't update manager's decision on orders: confirmOrder, ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, conn);
		}
		return returnValue;
	}

	/**
	 * save reviewed history for categorizing page
	 * 
	 * @param terms
	 * @param dataPrefix
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean saveReviewedTerms(ArrayList<String> terms,
			String dataPrefix, User user) throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			String sql_query = "select * from " + dataPrefix
					+ "_review_history where userid = ? and term = ?";
			String sql_update = "update " + dataPrefix
					+ "_review_history set reviewTime = sysdate() "
					+ " where userid = ? and term = ?";
			String sql_insert = "insert "
					+ dataPrefix
					+ "_review_history (userid, term, reviewTime) values (?, ?, sysdate())";
			int userid = user.getUserId();
			for (String term : terms) {
				stmt = conn.prepareStatement(sql_query);
				stmt.setInt(1, userid);
				stmt.setString(2, term);
				rs = stmt.executeQuery();
				String sql = sql_insert;
				if (rs.next()) {
					sql = sql_update;
				}
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, userid);
				stmt.setString(2, term);
				stmt.executeUpdate();
			}
			rv = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't insert new category in CharacterDBAccess:saveReviewedTerms",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, rs, conn);
		}
		return rv;
	}

	public synchronized boolean addNewCategory(ArrayList<CategoryBean> cats,
			String dataPrefix) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			String sql = "insert into " + dataPrefix
					+ "_categories(category, definition) values(?, ?)";
			for (int i = 0; i < cats.size(); i++) {
				CategoryBean cat = cats.get(i);
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, cat.getName());
				stmt.setString(2, cat.getDef());
				stmt.executeUpdate();
			}
			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't insert new category in CharacterDBAccess:addNewCategory",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, conn);
		}
		return returnValue;
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
	 * if is to keep the dataset after merge, update the redirection information
	 * and remove data that will no longer be used
	 * 
	 * @param dataPrefix
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public synchronized boolean setMergedInto(String dataPrefix,
			String target_ds, int userId) throws SQLException, IOException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			// update datasetprefix table
			String sql = "update datasetprefix set mergedInto = ? where prefix = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, target_ds);
			stmt.setString(2, dataPrefix);
			stmt.executeUpdate();

			// keep data in case the merged dataset is deleted
			// use other constraints to freeze the data from here
			/*
			 * stmt.execute("delete from " + dataPrefix + "_categories; ");
			 * stmt.execute("delete from " + dataPrefix +
			 * "_user_terms_relations; "); stmt.execute("delete from " +
			 * dataPrefix + "_user_terms_decisions; ");
			 * stmt.execute("delete from " + dataPrefix +
			 * "_comments where term is not null; ");
			 * stmt.execute("delete from " + dataPrefix + "_review_history; ");
			 * stmt.execute("delete from " + dataPrefix + "_term_category; ");
			 * stmt.execute("delete from " + dataPrefix + "_syns; ");
			 * stmt.execute("delete from " + dataPrefix +
			 * "_confirmed_category; ");
			 */

			// user log
			stmt.execute(getUserLogSQL(dataPrefix, "Merged into " + target_ds,
					userId));

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't delete dataset to set " + dataPrefix
					+ " in CharacterDBAccess: updateDatasetAfterMerge: ", exe);
			exe.printStackTrace();
			returnValue = false;
		} finally {
			closeConnection(stmt, conn);
		}

		return returnValue;
	}

	/**
	 * get datasets for user's report (display only those datasets that the user
	 * has made decisions in)
	 * 
	 * @param userid
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> getDatasetsForUserReport(int userid)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset_decision = null;
		ArrayList<String> datasets = new ArrayList<String>();
		try {
			conn = getConnection();
			String[] tables = { "_user_terms_decisions",
					"_user_tags_decisions", "_user_orders_decisions" };

			String sql = "select prefix from datasetprefix";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				// check table1
				String dataset = rset.getString(1);
				for (String table : tables) {
					sql = "select * from " + dataset + table
							+ " where userid = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, userid);
					rset_decision = pstmt.executeQuery();
					if (rset_decision.next()) {
						datasets.add(dataset);
						break;
					}
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't get datasets for user's report in CharacterDBAccess: getDatasetsForUserReport: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
			if (rset_decision != null) {
				rset_decision.close();
			}
		}

		return datasets;
	}

	/**
	 * if the dataset is a merged one, delete those source datasets when
	 * finalize it
	 * 
	 * @param dataset
	 * @param userID
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteSmallerDatasetsWhenFinalize(String dataset, int userID)
			throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select note from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				String note = rset.getString(1);
				if (note != null && !note.equals("") && !note.equals("null")) {
					sql = "select prefix from datasetprefix where mergedInto = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, dataset);
					rset = pstmt.executeQuery();
					while (rset.next()) {
						deleteDataset(rset.getString(1), userID, true, dataset);
					}
				}
			}
			rv = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Error in CharacterDBAccess: deleteSmallerDatasetsWhenFinalize: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
	}

	/**
	 * delte a dataset
	 * 
	 * @param dataPrefix
	 * @param userId
	 *            : who is trying to delete the dataset
	 * @param fromMerge
	 *            : if this delete is initiated from merging datasets
	 * @param mergedDataset
	 *            : if this function is called from merged, what is the merged
	 *            dataset name. This will be used to update those who are
	 *            originally merged into the dataset that is going to be deleted
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public synchronized boolean deleteDataset(String dataPrefix, int userId,
			boolean fromMerge, String mergedDataset) throws SQLException,
			IOException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();

			// delete from datasetprefix
			String sql = "delete from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataPrefix);
			pstmt.executeUpdate();

			// delete from dataset owner
			sql = "delete from dataset_owner where dataset = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataPrefix);
			pstmt.executeUpdate();

			// delete tables
			ArrayList<String> tables = getDatasetTableList();
			for (int i = 0; i < tables.size(); i++) {
				sql = "drop table if exists " + dataPrefix + tables.get(i);
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
			}

			// update mergedInto related to this dataset
			sql = "update datasetprefix set mergedInto = ? where mergedInto = ?";
			pstmt = conn.prepareStatement(sql);
			if (fromMerge) {
				// update mergedInto to be the larger merged dataset
				pstmt.setString(1, mergedDataset);
			} else {
				// update mergedInto to be empty to enable merge in the future
				pstmt.setString(1, "");
			}
			pstmt.setString(2, dataPrefix);
			pstmt.executeUpdate();

			sql = "update glossary_versions set mergedInto = ? where mergedInto = ?";
			pstmt = conn.prepareStatement(sql);
			if (fromMerge) {
				// update mergedInto to be the larger merged dataset
				pstmt.setString(1, mergedDataset);
			} else {
				// update mergedInto to be empty to enable merge in the future
				pstmt.setString(1, "");
			}
			pstmt.setString(2, dataPrefix);
			pstmt.executeUpdate();

			// add deleting log
			String operation = "Delete";
			if (fromMerge) {
				operation += " (from merge)";
			}
			pstmt = conn.prepareStatement(getUserLogSQL(dataPrefix, operation,
					userId));
			pstmt.executeUpdate();

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't delete dataset to set " + dataPrefix
					+ " in CharacterDBAccess: deleteDataset: ", exe);
			exe.printStackTrace();
			returnValue = false;
		} finally {
			closeConnection(pstmt, conn);
		}

		return returnValue;
	}

	public synchronized boolean reopenDataset(String dataPrefix, String type)
			throws SQLException, IOException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		String fieldName = "grouptermsdownloadable"; // the field in
														// datasetprefix table
														// to update
		if (type.equals("1")) {
			fieldName = "grouptermsdownloadable";
		} else if (type.equals("2")) {
			fieldName = "structurehierarchydownloadable";
		} else if (type.equals("3")) {
			fieldName = "termorderdownloadable";
		}

		try {
			conn = getConnection();
			String sql = "update datasetprefix set " + fieldName
					+ " = 0 where prefix = '" + dataPrefix + "'";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			// add timestamp to exiting downloadable files
			long start = 0;
			if (evaluateExecutionTime)
				start = System.currentTimeMillis();

			renameDownloadableFiles(dataPrefix);

			if (evaluateExecutionTime) {
				System.out.println("renameDownloadableFiles costs "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");

			}

			// if has other parts finalized, re-generate downloadable files
			// if (isConfirmed(dataPrefix, 4)) {
			// generateDownloadingFiles_as_sql_and_zip(dataPrefix);
			// }

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't reopen dataset" + dataPrefix
					+ " in CharacterDBAccess: reopenDataset: ", exe);
			exe.printStackTrace();
			returnValue = false;
		} finally {
			closeConnection(stmt, conn);
		}

		return returnValue;
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

	/**
	 * to finalize specific page of a dataset, set the flag in table
	 * datasetprefix to be true
	 * 
	 * @param dataPrefix
	 * @param type
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	public boolean finalizeDataset(String dataPrefix, String type, int userID)
			throws Exception {
		Connection conn = null;
		boolean success = false;
		PreparedStatement stmt = null;
		String fieldName = ""; // the field in datasetprefix table to update
		if (type.equals("1")) {
			fieldName = "grouptermsdownloadable";
			// prepare tables for downloading

			long start = 0;
			if (evaluateExecutionTime)
				start = System.currentTimeMillis();

			// delete possible existing files
			if (configuration.getOs().equals("windows")) {
				success = true;
			} else {
				success = deleteLocalCsvFiles(dataPrefix);
			}

			success = prepareCategoryTablesForDownloading(dataPrefix);

			if (evaluateExecutionTime)
				System.out.println("prepareCategoryTablesForDownloading costs "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");

			// prepare version controled .csv files
			if (success) {
				if (evaluateExecutionTime) {
					start = System.currentTimeMillis();
				}

				success = generateCSVDownload(dataPrefix, userID);

				if (evaluateExecutionTime) {
					System.out.println("generateCSVDownloadWithVersions costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
				}
			}
		} else if (type.equals("2")) {
			fieldName = "structurehierarchydownloadable";
			success = true;
		} else if (type.equals("3")) {
			fieldName = "termorderdownloadable";
			success = true;
		}

		if (success) {
			try {
				conn = getConnection();
				String sql = "update datasetprefix set " + fieldName
						+ " = ? where prefix = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setBoolean(1, true);
				stmt.setString(2, dataPrefix);
				stmt.executeUpdate();
				success = true;

			} catch (Exception exe) {
				LOGGER.error("Couldn't update table datasetprefix to set "
						+ fieldName
						+ " in CharacterDBAccess: finalizeDataset: ", exe);
				exe.printStackTrace();
				success = false;
			} finally {
				closeConnection(stmt, conn);
			}
		}

		// generate zipped files for downloading
		if (success) {
			success = generateDownloadingFiles_as_sql_and_zip(dataPrefix);
			String os = configuration.getOs();
			if (os.equals("windows")) { // window canot execute bash, ignore for
										// testing reasons
				success = true;
			}
		}

		return success;
	}

	/**
	 * delete the _term_category.csv and _syn.csv files for a given dataset
	 * 
	 * @param dataset
	 * @return
	 * @throws IOException
	 */
	public boolean deleteLocalCsvFiles(String dataset) throws IOException {
		String glossaryFilePath = configuration.getGlossaryFilePath();
		String termCategoryFileName = glossaryFilePath + dataset
				+ "_term_category.csv";
		String synFileName = glossaryFilePath + dataset + "_syns.csv";
		boolean returnvalue = deleteFile(termCategoryFileName);
		if (returnvalue) {
			returnvalue = deleteFile(synFileName);
		}
		return returnvalue;
	}

	private boolean deleteFile(String fileName) {
		String cmmd = "rm " + fileName;
		ExecCommmand ec = new ExecCommmand();
		ec.execShellCmd(cmmd);
		return true;
	}

	/**
	 * renmae some files
	 * 
	 * @param dataset
	 * @throws Exception
	 */
	public void renameDownloadableFiles(String dataset) throws Exception {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(Calendar.getInstance().getTime());
		try {
			String filePath = configuration.getGlossaryFilePath();

			// .without date _groupterms.sql
			String old_name = getPrefixWithoutDatePart(dataset) + "_groupterms";
			String new_name = old_name.replaceAll(old_name, old_name
					+ "_archived_at_" + timeStamp);
			old_name = old_name + ".sql";
			new_name = new_name + ".sql";
			String cmmd = "mv " + filePath + old_name + " " + filePath
					+ new_name;
			runCommand(cmmd);

			// .sql
			old_name = dataset + ".sql";
			new_name = old_name.replaceAll(dataset, dataset + "_archived_at_"
					+ timeStamp);
			cmmd = "mv " + filePath + old_name + " " + filePath + new_name;
			runCommand(cmmd);

			// .zip
			old_name = dataset + ".zip";
			new_name = old_name.replaceAll(dataset, dataset + "_archived_at_"
					+ timeStamp);
			cmmd = "mv " + filePath + old_name + " " + filePath + new_name;
			runCommand(cmmd);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * all the tables for one dataset
	 * 
	 * @param datasetName
	 * @return
	 */
	private ArrayList<String> getDatasetTableList() {
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
	 * update note in table datasetprefix
	 * 
	 * @param datasetName
	 * @param note
	 * @return
	 * @throws SQLException
	 */
	public boolean updateNote(String datasetName, String note)
			throws SQLException {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		if (note != null && !note.equals("")) {
			try {
				conn = getConnection();

				// check if dataset already exists, if so, do not create
				String sql = "select * from datasetprefix where prefix = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datasetName);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					// update note
					String oldNote = rset.getString("note");
					if (oldNote != null && !oldNote.equals("")
							&& !oldNote.equals("null")) {
						note = oldNote + ", " + note;
					}

					sql = "update datasetprefix set note = ? where prefix = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, note);
					pstmt.setString(2, datasetName);
					pstmt.executeUpdate();
				}

				rv = true;
			} catch (Exception exe) {
				LOGGER.error("Couldn't create dataset  " + datasetName
						+ " in CharacterDBAccess: updateNote: ", exe);
				exe.printStackTrace();
			} finally {
				closeConnection(pstmt, rset, conn);
			}
		}
		return rv;
	}

	/**
	 * create a new dataset with empty tables if already exist
	 * 
	 * @param datasetName
	 * @param note
	 * @param createrID
	 *            : the owner of this newly created dataset
	 * @param glossaryID
	 * @throws Exception 
	 */
	public boolean createDatasetIfNotExist(String datasetName, String note,
			int createrID, int glossaryID) throws Exception {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			if (note == null) {
				note = "";
			}

			// check if dataset already exists, if so, do not create
			String sql = "select * from datasetprefix where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, datasetName);
			rset = pstmt.executeQuery();

			if (!rset.next()) {
				// dataprefix table

				sql = "insert into datasetprefix (prefix, note, glossaryType) values (?, ?, ?); ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datasetName);
				pstmt.setString(2, note);
				pstmt.setInt(3, glossaryID);
				pstmt.executeUpdate();

				// add dataset owner
				if (createrID > 0) {
					sql = "insert into dataset_owner(dataset, ownerID) values (?, ?); ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datasetName);
					pstmt.setInt(2, createrID);
					pstmt.executeUpdate();
				}

				// add tables
				ArrayList<String> tables = getDatasetTableList();
				for (int i = 0; i < tables.size(); i++) {
					String tableSuffix = tables.get(i);
					sql = "create table " + datasetName + tableSuffix
							+ " like OTO_Demo" + tableSuffix;
					pstmt = conn.prepareStatement(sql);
					pstmt.executeUpdate();
				}

				// add default categories
				sql = "insert into " + datasetName
						+ "_categories select * from categories";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();

				// add user log
				pstmt = conn.prepareStatement(getUserLogSQL(datasetName,
						"Create", createrID));
				pstmt.executeUpdate();
				pstmt.close();
			}
			rv = true;
		} catch (Exception exe) {
			LOGGER.error("Couldn't create dataset  " + datasetName
					+ " in CharacterDBAccess: createDatasetTables: ", exe);
			exe.printStackTrace();
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
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
			LOGGER.error(
					"Couldn't generate note for merged datasets in CharacterDBAccess: generateNoteForMergedDataset: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}

		return note;
	}

	/**
	 * merge datasets into target dataset: 1. The target has been created before
	 * calling this function 2. only deal with merge related stuff in a
	 * transaction
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
		long start = System.currentTimeMillis();
		boolean success = false;
		Connection conn = null;
		Statement st = null, st2 = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;
		int userID = user.getUserId();
		ArrayList<String> toDelete = new ArrayList<String>();
		ArrayList<String> cannotDelete = new ArrayList<String>();
		Hashtable<String, Boolean> hash_sentences = new Hashtable<String, Boolean>();
		String sql = "";

		// remove target_ds from datasets list
		for (int i = 0; i < datasets.size(); i++) {
			if (datasets.get(i).equals(target_ds)) {
				datasets.remove(i);
				break;
			}
		}

		// costruct note (merged from)
		String note = generateNoteForMergedDataset(datasets);

		if (evaluateExecutionTime) {
			System.out
					.println("parepare merge: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
			start = System.currentTimeMillis();
		}

		// everything below should be in a transaction
		try {
			conn = getConnection();
			st = conn.createStatement();
			st2 = conn.createStatement();
			conn.setAutoCommit(false);

			// read in target sentences to avoid inserting duplicate sentence
			rset = st.executeQuery("select originalSent from " + target_ds
					+ "_sentence");
			while (rset.next()) {
				hash_sentences.put(rset.getString(1), true);
			}

			// update target note
			rset = st
					.executeQuery("select * from datasetprefix where prefix = '"
							+ target_ds + "'");

			if (rset.next()) {
				// update note
				String oldNote = rset.getString("note");
				if (oldNote != null && !oldNote.equals("")
						&& !oldNote.equals("null")) {
					note = oldNote + ", " + note;
				}
			}

			st.executeUpdate("update datasetprefix set note = '" + note
					+ "' where prefix = '" + target_ds + "'");

			if (evaluateExecutionTime) {
				System.out.println("update target note: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// insert data from source datasets
			for (String source_ds : datasets) {
				Long dataset_start = System.currentTimeMillis();
				// category
				st.executeUpdate("insert into "
						+ target_ds
						+ "_categories select * from "
						+ source_ds
						+ "_categories where category not in (select category from "
						+ target_ds + "_categories)");

				if (evaluateExecutionTime) {
					System.out.println("\t* _category: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// comments
				st.executeUpdate("insert into "
						+ target_ds
						+ "_comments (comments, term, userid, commentDate, tagID, orderID) "
						+ "select comments, term, userid, commentDate, tagID, orderID from "
						+ source_ds + "_comments");

				if (evaluateExecutionTime) {
					System.out.println("\t* _comments: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// review_history: non duplicate ones
				st.executeUpdate("insert into " + target_ds
						+ "_review_history (userid, term, reviewTime) "
						+ "select a.userid, a.term, a.reviewTime from "
						+ source_ds + "_review_history a "
						+ "left join (select userid, term, 1 as exist from "
						+ target_ds + "_review_history) b "
						+ "on a.userid = b.userid and a.term = b.term "
						+ "where b.exist is null");

				if (evaluateExecutionTime) {
					System.out.println("\t* _review_history: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// sentence
				rset = st2
						.executeQuery("select source, sentence, originalsent, status, tag from "
								+ source_ds + "_sentence");
				while (rset.next()) {
					if (hash_sentences.get(rset.getString("originalSent")) == null) {
						pstmt = conn
								.prepareStatement("insert into "
										+ target_ds
										+ "_sentence (sentid, source, sentence, originalsent, status, tag) "
										+ "values (?, ?, ?, ?, ?, ?)");
						pstmt.setInt(1, 0);
						pstmt.setString(2, rset.getString("source"));
						pstmt.setString(3, rset.getString("sentence"));
						pstmt.setString(4, rset.getString("originalsent"));
						pstmt.setString(5, rset.getString("status"));
						pstmt.setString(6, rset.getString("tag"));
						pstmt.executeUpdate();
						hash_sentences
								.put(rset.getString("originalSent").trim(), true);
					}
				}
				// st.executeUpdate("insert into "
				// + target_ds
				// +
				// "_sentence (sentid, source, sentence, originalsent, status, tag) "
				// +
				// "select sentid, source, sentence, originalsent, status, tag from "
				// + source_ds + "_sentence");

				if (evaluateExecutionTime) {
					System.out.println("\t* _sentence: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// user_terms_relations
				st.executeUpdate("insert into " + target_ds
						+ "_user_terms_relations " + "select * from "
						+ source_ds + "_user_terms_relations");

				if (evaluateExecutionTime) {
					System.out.println("\t* _user_terms_relations: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// web_grouped_terms
				// Step 1: prepare source dataset: if sourceDataset is empty,
				// set to be the datasetname
				// prepare sourceDataset
				st.executeUpdate("update  " + source_ds
						+ "_web_grouped_terms set sourceDataset = '"
						+ source_ds
						+ "' where sourceDataset is null or sourceDataset = ''");
				// insert
				st.executeUpdate("insert into "
						+ target_ds
						+ "_web_grouped_terms(groupId, term, cooccurTerm, sourceDataset) "
						+ "select groupId, term, cooccurTerm, sourceDataset from "
						+ source_ds + "_web_grouped_terms");

				if (evaluateExecutionTime) {
					System.out.println("\t* _web_grouped_terms: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// user_terms_decisions: only insert non-duplicate decisions
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

				if (evaluateExecutionTime) {
					System.out.println("\t* _user_terms_decisions: "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				// _confirmed_category
				// insert non-duplicate records
				if (isMergeIntoSystem) {
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

				if (evaluateExecutionTime) {
					System.out.println("\t* _confirmed_category (insert): "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
					start = System.currentTimeMillis();
				}

				if (evaluateExecutionTime) {
					System.out.println("insert data from "
							+ source_ds
							+ ": "
							+ Long.toString(System.currentTimeMillis()
									- dataset_start) + " ms");
				} else {
					System.out.println("Data inserted from " + source_ds);
				}
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

			if (evaluateExecutionTime) {
				System.out.println("update isActive: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

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

			if (evaluateExecutionTime) {
				System.out.println("update isLatest: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

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

			if (evaluateExecutionTime) {
				System.out.println("update hasConflict: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// remove source datasets when standard applies
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

			if (evaluateExecutionTime) {
				System.out.println("take care of sources after merge: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// till here, merge dataset succeeded
			conn.commit();
			success = true;
		} catch (SQLException exe) {
			LOGGER.error(
					"Couldn't execute sqls in CharacterDBAccess: mergeDatasets",
					exe);
			exe.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex1) {
					LOGGER.error(
							"Couldn't roll back sqls in CharacterDBAccess: mergeDatasetsInTransaction",
							ex1);
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

		// delete tables: cannot rollback, therefore separate from previous
		// changes
		if (success) {
			try {
				conn = getConnection();
				st = conn.createStatement();
				for (String ds : toDelete) {
					ArrayList<String> tables = getDatasetTableList();
					for (int i = 0; i < tables.size(); i++) {
						st.executeUpdate("drop table if exists " + ds
								+ tables.get(i));
					}
				}
			} catch (SQLException exe) {
				LOGGER.error(
						"Couldn't drop tables in CharacterDBAccess: mergeDatasets",
						exe);
				exe.printStackTrace();
			} finally {
				closeConnection(st, conn);
			}
		}

		return success;
	}

	/**
	 * get the sql script to create _term_category table
	 * 
	 * @param dataset
	 * @return
	 */
	private String getSQLOfCreateTermCategoryTable(String dataset) {
		return "create table if not exists "
				+ dataset
				+ "_term_category (term varchar(100), "
				+ "category varchar(100), hasSyn TINYINT(1) default 0, sourceDataset text, "
				+ "termID varchar(100))";
	}

	/**
	 * 1. the file prefix_groupterms.sql should hold two tables,
	 * "prefix_term_category" table and "prefix_syns" table. 2.
	 * prefix_term_category should have columns "term", "category",
	 * "sourceDataset" and "hasSyn". hasSyn is TINYINT(1), holding 1 or 0. 3.
	 * prefix_syns should have columns "term" and "synonym"
	 */
	public synchronized boolean prepareCategoryTablesForDownloading(
			String dataPrefix) throws SQLException {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String fieldName = ""; // the field in datasetprefix table to update
		String sql = "";
		try {
			conn = getConnection();

			long start = 0;
			if (evaluateExecutionTime) {
				start = System.currentTimeMillis();
			}

			// get glossaryType
			int glossaryType = 0;
			sql = "select glossaryType from datasetprefix where prefix = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, dataPrefix);
			rset = stmt.executeQuery();
			if (rset.next()) {
				glossaryType = rset.getInt(1);
			}

			if (evaluateExecutionTime) {
				System.out.println("\t* get glossary type: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// set default sourceDataset in _web_grouped_terms table
			sql = "update  "
					+ dataPrefix
					+ "_web_grouped_terms set sourceDataset = ? where sourceDataset is null or sourceDataset = ''";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, dataPrefix);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out.println("\t* set default sourceDataset: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// insert into dictionary if not exist (uuid is taken care of by
			// table trigger)
			sql = "insert into glossary_dictionary(term, category, glossaryType) "
					+ "select a.term, a.category, "
					+ glossaryType
					+ " as glossaryType from (select distinct term, category from "
					+ dataPrefix
					+ "_confirmed_category where categoryApproved = true and isApprovedSynonym = false and category <> 'discarded') a "
					+ "left join (select term, category, 1 as exist from glossary_dictionary where glossaryType = ?) d "
					+ "on a.term = d.term and a.category = d.category where exist is null";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, glossaryType);
			stmt.executeUpdate();
			if (evaluateExecutionTime) {
				System.out.println("\t* insert into dictionary: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// drop table prefix_term_category
			sql = "drop table if exists " + dataPrefix + "_term_category";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			// generate prefix_term_category table
			// prefix_term_category should have columns "term", "category", and
			// "hasSyn". hasSyn is TINYINT(1), holding 1 or 0.
			sql = getSQLOfCreateTermCategoryTable(dataPrefix);
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			if (evaluateExecutionTime) {
				System.out.println("\t* recreate _term_category table: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// insert data into prefix_term_category table
			// get static ID here too
			sql = "insert into "
					+ dataPrefix
					+ "_term_category(term, category, hasSyn, termID) "
					+ "select distinct a.term, a.category, 0 as hasSyn, d.termID "
					+ "from (select distinct term, category from "
					+ dataPrefix
					+ "_confirmed_category where categoryApproved = true and isApprovedSynonym = false and category <> 'discarded') a "
					+ "left join (select term, category, termID from glossary_dictionary where glossaryType = ?) d "
					+ "on d.term = a.term and d.category = a.category; ";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, glossaryType);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out.println("\t* get data for _term_category table: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// update sourceDataset later because left join with text is
			sql = "update "
					+ dataPrefix
					+ "_term_category a "
					+ "left join "
					+ "(select term, group_concat(sourceDataset) as sourceDataset from "
					+ "(select distinct term, sourceDataset from " + dataPrefix
					+ "_web_grouped_terms) c group by term) b "
					+ "on a.term = b.term "
					+ "set a.sourceDataset = b.sourceDataset;";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out
						.println("\t* update sourceDataset for _term_category table: "
								+ Long.toString(System.currentTimeMillis()
										- start) + " ms");
				start = System.currentTimeMillis();
			}

			// generate prefix_syns talbe
			// drop table prefix_syns
			sql = "drop table if exists " + dataPrefix + "_syns";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			// create table
			// 3. prefix_syns should have columns "term" and "synonym"
			sql = "create table "
					+ dataPrefix
					+ "_syns (term varchar(100), category varchar(100), synonym varchar(100), termID varchar(100))";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out.println("\t* re-create _syns table: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			// insert data into _syns table
			sql = "insert into "
					+ dataPrefix
					+ "_syns (term, category, synonym, termID) "
					+ "select a.term, a.category, a.synonym, d.termID "
					+ "from (select distinct term, category, synonym "
					+ "from "
					+ dataPrefix
					+ "_confirmed_category "
					+ "where categoryApproved = true and synonymApproved = true and category <> 'discarded') a "
					+ "left join "
					+ "(select term, category, termID from glossary_dictionary where glossaryType = ?) d "
					+ "on d.term = a.term and d.category = a.category; ";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, glossaryType);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out.println("\t* get data for _syns table: "
						+ Long.toString(System.currentTimeMillis() - start)
						+ " ms");
				start = System.currentTimeMillis();
			}

			sql = "update "
					+ dataPrefix
					+ "_term_category set hasSyn = 1 where termID in (select distinct termID from "
					+ dataPrefix + "_syns)";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();

			if (evaluateExecutionTime) {
				System.out
						.println("\t* update hasSyn for _term_category table: "
								+ Long.toString(System.currentTimeMillis()
										- start) + " ms");
			}

			returnValue = true;
		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't prepare tables for categorization downloading to set "
							+ fieldName
							+ " in CharacterDBAccess: prepareCategoryTablesForDownloading: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, rset, conn);
		}
		return returnValue;
	}

	public String getPrefixWithoutDatePart(String ds_name) {
		String prefixWithoutDate = ds_name;
		Pattern p = Pattern
				.compile("^(.*)_\\d{4}_?\\d{1,2}_?\\d{1,2}\\d{6,}?$");
		Matcher m = p.matcher(ds_name);
		if (m.matches()) {
			prefixWithoutDate = m.group(1);
		}
		return prefixWithoutDate;
	}

	/**
	 * compute the next glossary version of a dataset
	 * 
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public VersionBean getNextVersion(String dataset) throws SQLException {
		VersionBean version = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select * from glossary_versions where dataset = ? and isLatest = true";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				version = new VersionBean(rset.getInt("primaryVersion"),
						rset.getInt("secondaryVersion") + 1);
			} else {
				version = new VersionBean(0, 1);
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getNextVersion: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return version;
	}

	/**
	 * get a list of meta data for glossary files
	 * 
	 * @param userid
	 * @param dataset
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> prepareMetaData(int userid, String dataset)
			throws SQLException {
		ArrayList<String> metaLines = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			// line 0: version
			metaLines.add("#Version: ");

			String sql = "select glossaryType, note, glossaryName from datasetprefix a "
					+ "left join glossarytypes b "
					+ "on a.glossaryType = b.glossTypeID " + "where prefix = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				// line 1: glossary type
				metaLines.add("#Glossary type: "
						+ rset.getString("glossaryName"));

				// line 2: source
				String source = rset.getString("note");
				if (source == null || source.equals("null")
						|| source.equals("")) {
					metaLines.add("#Source: " + dataset);
				} else {
					metaLines.add("#Source: " + source);
				}
			}

			// line3: Finalized by
			sql = "select concat(firstname, ' ', lastname) from users where userid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				metaLines.add("#Finalized by: " + rset.getString(1));
			}

			// line 4: Date
			metaLines.add("#Date: ");

			// line 5: Reviewers
			sql = "select group_concat(fullname), '', '' "
					+ "from "
					+ "(select c.userid, b.fullname, b.lastname from "
					+ "(select distinct userid from (select distinct userid from "
					+ dataset
					+ "_review_history "
					+ "union select distinct userid from "
					+ dataset
					+ "_user_terms_decisions) a ) c "
					+ "left join (select userid, concat(firstname, ' ', lastname) as fullname, lastname from users) b "
					+ "on c.userid = b.userid " + "order by lastname) d ";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				metaLines.add("#Reviewers: " + rset.getString(1));
			}

			// line 6: Source producer
			sql = "select a.dataset, concat(b.firstname, ' ', b.lastname) as fullname from dataset_owner a "
					+ "left join users b "
					+ "on a.ownerID = b.userID "
					+ "where a.dataset = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				metaLines
						.add("#Source producer: " + rset.getString("fullname"));
			} else {
				metaLines.add("#Source producer: OTO System");
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: getNextVersion: ",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return metaLines;
	}

	/**
	 * generate two .csv files (_term_cateogry and _syns) for OTO web service
	 * -files should be under version control -only consider group terms page
	 * for now
	 * 
	 * @param dataset
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generateCSVDownload(String dataset, int userID)
			throws Exception {
		boolean rv = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int success = 0;
		String filePath = configuration.getGlossaryFilePath();
		String fileType = ".csv";
		DatasetBean datasetInfo = getDataset(dataset);
		String file_term_category = dataset + "_term_category" + fileType;
		String file_syns = dataset + "_syns" + fileType;

		// version management on OTO server
		VersionBean newVersion = getNextVersion(dataset);
		String file_withVersion_term_category = file_term_category;
		String file_withVersion_syns = file_syns;
		if (newVersion != null) {
			file_withVersion_term_category = dataset + "_term_category_"
					+ newVersion.toString() + fileType;
			file_withVersion_syns = dataset + "_syns_" + newVersion.toString()
					+ fileType;
		}

		// prepare meta data for space reservation
		ArrayList<String> metaDataLines = prepareMetaData(userID, dataset);
		String spaceReserveForMetaData = "";
		for (String line : metaDataLines) {
			spaceReserveForMetaData += line;
		}

		try {
			conn = getConnection();

			// for linux, can only select outfile into the tmpdir of mysql,
			// therefore, we should get the tmp path for linux and move
			// files into
			// target path later
			String tmpFilePath = filePath;
			String os = configuration.getOs();
			boolean toMoveFile = false;
			if (os.toLowerCase().equals("linux")) {
				String sql = "show variables like 'tmpdir'";
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					tmpFilePath = rset.getString("Value");
					toMoveFile = true;
				}
			}

			/**
			 * for _term_category table, there are 7 lines of meta data, we need
			 * (sizeof(spaceReserveForMetaData) + 7x2(\r\n) + 2(extra line:
			 * \r\n)) which means extra 16 characters. In the space reserve
			 * line, we already have "","","","",""\r\n which is exactly 16
			 * characters, there for we are good with reserveing spaces
			 */

			// for _term_category table
			/**
			 * get file for _term_category: the order of fields cannot be
			 * changed
			 */
			String reserveSpaceSQL = "select '" + spaceReserveForMetaData
					+ "', '', '', '', '' union ";
			String sql = "select 'term', 'category', 'hasSyn', 'sourceDataset', 'termID' "
					+ "union "
					+ "select term, category, hasSyn, sourceDataset, termID from "
					+ dataset
					+ "_term_category into outfile '"
					+ tmpFilePath
					+ file_term_category
					+ "' "
					+ "FIELDS TERMINATED BY ',' "
					+ "ESCAPED BY '\\\\' "
					+ "OPTIONALLY ENCLOSED BY '\"' "
					+ "LINES TERMINATED BY '\r\n';";
			pstmt = conn.prepareStatement(reserveSpaceSQL + sql);
			pstmt.executeQuery();

			insertMetaDataIntoCsvFile(tmpFilePath + file_term_category,
					metaDataLines);

			if (toMoveFile) {
				String mvCommand = "mv " + tmpFilePath + file_term_category
						+ " " + filePath + file_term_category;
				success = new ExecCommmand().execShellCmd(mvCommand);
			}

			// version management on OTO server
			if (os.equals("windows")) {
				sql = "select 'term', 'category', 'hasSyn', 'sourceDataset', 'termID' "
						+ "union "
						+ "select term, category, hasSyn, sourceDataset, termID from "
						+ dataset
						+ "_term_category into outfile '"
						+ filePath
						+ file_withVersion_term_category
						+ "' "
						+ "FIELDS TERMINATED BY ',' "
						+ "ESCAPED BY '\\\\' "
						+ "OPTIONALLY ENCLOSED BY '\"' "
						+ "LINES TERMINATED BY '\r\n';";
				pstmt = conn.prepareStatement(reserveSpaceSQL + sql);
				pstmt.executeQuery();

				insertMetaDataIntoCsvFile(filePath
						+ file_withVersion_term_category, metaDataLines);
			} else {
				// copy file to withVersion
				String cpCommad = "cp " + filePath + file_term_category + " "
						+ filePath + file_withVersion_term_category;
				success = new ExecCommmand().execShellCmd(cpCommad);
			}

			// get file for _syns, the order of fields cannot be changed
			/**
			 * for _term_category table, there are 7 lines of meta data, we need
			 * (sizeof(spaceReserveForMetaData) + 7x2(\r\n) + 2(extra line:
			 * \r\n)) which means extra 16 characters. In the space reserve
			 * line, we already have "","","",""\r\n which is exactly 13
			 * characters, there for we need three more characters. Here we use
			 * ### to reserve the space
			 */
			reserveSpaceSQL = "select '" + spaceReserveForMetaData + "###"
					+ "', '', '', '' union ";
			sql = "select 'term', 'category', 'synonym', 'termID' union "
					+ "select term, category, synonym, termID from " + dataset
					+ "_syns into outfile '" + tmpFilePath + file_syns + "' "
					+ "FIELDS TERMINATED BY ',' " + "ESCAPED BY '\\\\' "
					+ "OPTIONALLY ENCLOSED BY '\"' "
					+ "LINES TERMINATED BY '\r\n';";
			pstmt = conn.prepareStatement(reserveSpaceSQL + sql);
			pstmt.executeQuery();

			insertMetaDataIntoCsvFile(tmpFilePath + file_syns, metaDataLines);

			if (toMoveFile) {
				String mvCommand = "mv " + tmpFilePath + file_syns + " "
						+ filePath + file_syns;
				success = new ExecCommmand().execShellCmd(mvCommand);
			}

			// version management on OTO server
			if (os.equals("windows")) {
				sql = "select 'term', 'category', 'synonym', 'termID' union "
						+ "select term, category, synonym, termID from "
						+ dataset + "_syns into outfile '" + filePath
						+ file_withVersion_syns + "' "
						+ "FIELDS TERMINATED BY ',' " + "ESCAPED BY '\\\\' "
						+ "OPTIONALLY ENCLOSED BY '\"' "
						+ "LINES TERMINATED BY '\r\n';";
				pstmt = conn.prepareStatement(reserveSpaceSQL + sql);
				pstmt.executeQuery();

				insertMetaDataIntoCsvFile(filePath + file_withVersion_syns,
						metaDataLines);
			} else {
				// copy file to withVersion
				String cpCommad = "cp " + filePath + file_syns + " " + filePath
						+ file_withVersion_syns;
				success = new ExecCommmand().execShellCmd(cpCommad);
			}

			if (success == 0) {
				ArrayList<String> files = new ArrayList<String>();
				try {
					files = commitGlossaryToGit(file_term_category, file_syns,
							dataset);
					rv = true;
				} catch (Exception e) {
					System.out.println("Commit to Git failed: " + e);
				}

				// if dataset is system reserved, send out an email to hong
				if (files.size() > 0) {
					if (new GlossaryNameMapper()
							.isGlossaryReservedDataset(dataset)) {
						rv = new NotifyEmail()
								.sendNewGlossaryCommitNotification(
										configuration
												.getGlossaryCommitRecipient(),
										dataset, files);
					}
				}

				if (rv) {
					// update glossary_versions table
					// update previous latest version to be not latest
					sql = "update glossary_versions set isLatest = false where isLatest = true and dataset = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, dataset);
					pstmt.executeUpdate();

					// store latest version to db
					boolean isForGlossaryDownload = GlossaryNameMapper
							.getInstance().isGlossaryReservedDataset(dataset);
					sql = "insert into glossary_versions(dataset, glossaryType, filename, primaryVersion, "
							+ "secondaryVersion, svnLink, isLatest, isForGlossaryDownload, dateCreated) values "
							+ "(?, ?, ?, ?, ?, ?, ?, ?, now())";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, dataset);
					pstmt.setInt(2, datasetInfo.getGlossaryID());
					pstmt.setInt(4, newVersion.getPrimaryVersion());
					pstmt.setInt(5, newVersion.getSecondaryVersion());
					pstmt.setBoolean(7, true);
					pstmt.setBoolean(8, isForGlossaryDownload);
					pstmt.setString(6, ""); // svn link

					// for _term_category file
					pstmt.setString(3, file_withVersion_term_category); // filename
					pstmt.executeUpdate();

					// for _syns file
					pstmt.setString(3, file_withVersion_syns); // filename
					pstmt.executeUpdate();
					rv = true;
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: generateCSVDownload: ",
					exe);
			exe.printStackTrace();
			return false;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return rv;
	}

	private boolean insertMetaDataIntoCsvFile(String file,
			ArrayList<String> metadata) throws IOException {
		boolean rv = false;
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(new File(file), "rw");
			f.seek(0); // to the beginning

			for (String eachLine : metadata) {
				f.writeBytes(eachLine);
				f.writeBytes("\r\n");
			}
			f.writeBytes("\r\n");
			f.close();
			rv = true;
		} catch (Exception e) {
			System.out.println("insertMetaDataIntoCsvFile failure: " + e);
		} finally {
			if (f != null) {
				f.close();
			}
		}
		return rv;

	}

	/**
	 * commit two glossary files to github
	 * 
	 * @param termCategoryFileName
	 * @param synFileName
	 * @return
	 * @throws Exception
	 */
	private ArrayList<String> commitGlossaryToGit(String termCategoryFileName,
			String synFileName, String dataset) throws Exception {
		ArrayList<String> committedFiles = new ArrayList<String>();
		String glossaryFilePath = configuration.getGlossaryFilePath();

		// get gitClient configuration
		String gitUser = configuration.getGitUser();
		String gitPassword = configuration.getGitPassword();
		String gitAuthorName = configuration.getGitAuthorName();
		String gitAuthorEmail = configuration.getGitAuthorEmail();
		String gitCommitterName = configuration.getGitCommitterName();
		String gitCommitterEmail = configuration.getGitCommitterEmail();
		String gitRepository = configuration.getGitRepository();
		String gitLocalPath = configuration.getGitLocalPath();
		List<String> branches = new LinkedList<String>();
		branches.add("master");
		branches.add("development");

		GitClient gitClient = new GitClient(gitRepository, branches,
				gitLocalPath, gitUser, gitPassword, gitAuthorName,
				gitAuthorEmail, gitCommitterName, gitCommitterEmail);
		if (!termCategoryFileName.equals("")) {
			try {
				AddResult commitResult = gitClient.addFile(glossaryFilePath
						+ termCategoryFileName, termCategoryFileName,
						"development",
						"Regular finalize commit from OTO on termset "
								+ dataset);
				if (!commitResult.getDiffEntries().isEmpty()) {
					committedFiles.add(termCategoryFileName);
				}
			} catch (Exception e) {
				System.out.println("Commit " + termCategoryFileName
						+ "failed: " + e);
			}
		}

		if (!synFileName.equals("")) {
			try {
				AddResult commitResult = gitClient.addFile(glossaryFilePath
						+ synFileName, synFileName, "development",
						"Regular finalize commit from OTO on termset "
								+ dataset);
				if (!commitResult.getDiffEntries().isEmpty()) {
					committedFiles.add(synFileName);
				}
			} catch (Exception e) {
				System.out.println("Commit " + synFileName + "failed: " + e);
			}
		}

		return committedFiles;
	}

	/**
	 * back up the entire database
	 * 
	 * @param isBeforeMerge
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean backupDatabase(boolean isBeforeMerge) throws SQLException,
			IOException {

		long start = 0;
		if (evaluateExecutionTime) {
			start = System.currentTimeMillis();
		}

		boolean rv = false;
		String backupPath = configuration.getBackupFilePath();

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		String filename = "markedupdatasets_"
				+ dateFormat.format(cal.getTime());

		if (isBeforeMerge) {
			filename += "_beforeMerge";
		}

		String os = configuration.getOs();
		ExecCommmand ec = new ExecCommmand(os);

		String command = configuration.getMysqlDumpLocation();
		if (os.equals("windows")) {
			command = "mysqldump";
			rv = true;
		}

		command = command + " --lock-tables=false --user=termsuser "
				+ "--password=termspassword markedupdatasets" + " > "
				+ backupPath + filename + ".sql";

		int fileCreated = ec.execShellCmd(command);
		if (fileCreated == 0) {
			rv = true;
		}

		if (evaluateExecutionTime)
			System.out
					.println("backup database costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		return rv;
	}

	/**
	 * generate downloading files for entire 3 pages, no version control here
	 * keep this function to make it compatible with old CharaParser
	 * 
	 * @param dataset
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean generateDownloadingFiles_as_sql_and_zip(String dataset)
			throws SQLException, IOException {

		long start = 0;
		if (evaluateExecutionTime) {
			start = System.currentTimeMillis();
		}

		boolean rv = false;
		// prepare file
		String filePath = configuration.getGlossaryFilePath();
		String os = configuration.getOs().toLowerCase();
		String shellCommand = configuration.getMysqlDumpLocation()
				+ " --lock-tables=false --user=termsuser "
				+ "--password=termspassword markedupdatasets ";
		ExecCommmand ec = new ExecCommmand();

		if (isConfirmed(dataset, 1)) {
			shellCommand += dataset + "_term_category " + dataset + "_syns ";

			// get prefix_groupterms.sql for CharaParser
			String command1 = shellCommand;
			// replace prefix name
			String prefixWithoutDate = getPrefixWithoutDatePart(dataset);

			if (!prefixWithoutDate.equals(dataset)) {
				command1 += " > " + filePath + prefixWithoutDate
						+ "_groupterms.sql";
				ec.execShellCmd(command1);

				// read in file and replace dataset with prefixWithoutDate
				File f = new File(filePath + prefixWithoutDate
						+ "_groupterms.sql");
				FileInputStream fs = null;
				InputStreamReader in = null;
				BufferedReader br = null;
				StringBuffer sb = new StringBuffer();
				String textinLine;
				try {
					fs = new FileInputStream(f);
					in = new InputStreamReader(fs);
					br = new BufferedReader(in);
					while (true) {
						textinLine = br.readLine();
						if (textinLine == null)
							break;
						textinLine = textinLine.replaceAll(dataset
								+ "_term_category", prefixWithoutDate
								+ "_term_category");
						textinLine = textinLine.replaceAll(dataset + "_syns",
								prefixWithoutDate + "_syns");
						sb.append(textinLine);
						sb.append(System.getProperty("line.separator"));
					}

					fs.close();
					in.close();
					br.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					FileWriter fstream = new FileWriter(f);
					BufferedWriter outobj = new BufferedWriter(fstream);
					outobj.write(sb.toString());
					outobj.close();
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
			} else {
				command1 += " > " + filePath + prefixWithoutDate
						+ "_groupterms.sql";
				ec.execShellCmd(command1);
			}
		}

		if (isConfirmed(dataset, 2)) {
			shellCommand += dataset + "_confirmed_paths ";
		}
		if (isConfirmed(dataset, 3)) {
			shellCommand += dataset + "_confirmed_orders ";
		}

		shellCommand += " > " + filePath + dataset + ".sql";

		int fileCreated = ec.execShellCmd(shellCommand);
		rv = true;

		// create zip file
		// zip zipfilename zipfile1 zipfile2
		if (fileCreated == 0) {
			if (os.equals("linux")) {
				shellCommand = "zip -j " + filePath + dataset + ".zip "
						+ filePath + dataset + ".sql " + filePath
						+ "readme.txt";
				fileCreated = ec.execShellCmd(shellCommand);
			} else {
				try {
					// create a zipfile with timestamp as the name
					CRC32 crc = new CRC32();
					byte[] buffer = new byte[4096]; // Create a buffer for
													// copying
					int bytesRead;
					@SuppressWarnings("resource")
					ZipOutputStream zipout = new ZipOutputStream(
							(OutputStream) new FileOutputStream(filePath
									+ dataset + ".zip"));
					zipout.setLevel(6);
					try {
						// add readme
						FileInputStream zipin = new FileInputStream(filePath
								+ "readme.txt"); // Stream
						ZipEntry entry = new ZipEntry("readme.txt"); // Make a
						// ZipEntry
						entry.setSize((long) buffer.length);
						crc.reset();
						crc.update(buffer);
						entry.setCrc(crc.getValue());
						zipout.putNextEntry(entry); // Store entry
						while ((bytesRead = zipin.read(buffer)) != -1)
							zipout.write(buffer, 0, bytesRead);
						zipin.close();

						// add dataset.sql
						zipin = new FileInputStream(filePath + dataset + ".sql");
						entry = new ZipEntry(dataset + ".sql");
						entry.setSize((long) buffer.length);
						crc.reset();
						crc.update(buffer);
						entry.setCrc(crc.getValue());
						zipout.putNextEntry(entry); // Store entry
						while ((bytesRead = zipin.read(buffer)) != -1)
							zipout.write(buffer, 0, bytesRead);
						zipin.close();

						fileCreated = 1;
					} catch (Throwable t) {
						System.out.println("Failed zipping files: ");
						t.printStackTrace();
					} finally {
						zipout.close();
					}
				} finally {

				}
			}
		}

		if (evaluateExecutionTime)
			System.out
					.println("generateDownloadingFiles_as_sql_and_zip costs "
							+ Long.toString(System.currentTimeMillis() - start)
							+ " ms");
		return rv;
	}

	/**
	 * 
	 * @param dataset
	 * @param type
	 *            : 1-category; 2-hierarchy; 3-orders; 4-any
	 * @return
	 * @throws SQLException
	 */
	public boolean isConfirmed(String dataset, int type) throws SQLException {
		boolean retv = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		String sql = "select * from datasetprefix " + "where prefix = ?";
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, dataset);
			rset = stmt.executeQuery();
			if (rset.next()) {
				if (type == 1) {
					retv = rset.getBoolean("grouptermsdownloadable");
				} else if (type == 2) {
					retv = rset.getBoolean("structurehierarchydownloadable");
				} else if (type == 3) {
					retv = rset.getBoolean("termorderdownloadable");
				} else {
					retv = rset.getBoolean("grouptermsdownloadable")
							|| rset.getBoolean("structurehierarchydownloadable")
							|| rset.getBoolean("termorderdownloadable");
				}
			}

		} catch (Exception exe) {
			LOGGER.error(
					"Couldn't execute db query in CharacterDBAccess: isConfirmed",
					exe);
			exe.printStackTrace();
		} finally {
			closeConnection(stmt, conn);
			rset.close();
		}
		return retv;
	}
}
