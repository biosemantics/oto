package edu.arizona.biosemantics.oto.steps.server.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.Entity;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.EntityProposals;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.FormalConcept;
import edu.arizona.biosemantics.oto.steps.server.Configuration;
import edu.arizona.biosemantics.oto.steps.server.utilities.Utilities;
import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermGlossary;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.MappingStatus;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyInfo;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyMatch;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecord;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologyRecordType;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.OntologySubmission;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryLists;
import edu.arizona.biosemantics.oto.steps.shared.beans.toontologies.TermCategoryPair;

public class ToOntologiesDAO extends AbstractDAO {
	private static ToOntologiesDAO instance;

	public static ToOntologiesDAO getInstance() throws Exception {
		if (instance == null) {
			instance = new ToOntologiesDAO();
		}
		return instance;
	}

	/**
	 * default constructor
	 * 
	 * @throws Exception
	 */
	public ToOntologiesDAO() throws Exception {
		super();
	}
	
	public String getOntologyTaxonGroup(String prefix) throws SQLException{
		PreparedStatement pstmt_select = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select taxongroup from ontologies where prefix= ?";
			pstmt_select = conn.prepareStatement(sql);
			pstmt_select.setString(1, prefix);
			rset = pstmt_select.executeQuery();
			if(rset.next()) return rset.getString("taxongroup");
		}catch (SQLException e){
			e.printStackTrace();//TODO log errors
			throw e;
		}finally {
			closeConnection(conn);
			close(pstmt_select);
			close(rset);
		}
		return null;
	}
	
	public String getOntologyFileName(String prefix) throws SQLException{
		PreparedStatement pstmt_select = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select filename from ontologies where prefix= ?";
			pstmt_select = conn.prepareStatement(sql);
			pstmt_select.setString(1, prefix);
			rset = pstmt_select.executeQuery();
			if(rset.next()) return rset.getString("filename");
		}catch (SQLException e){
			e.printStackTrace();//TODO log errors
			throw e;
		}finally {
			closeConnection(conn);
			close(pstmt_select);
			close(rset);
		}
		return null;
	}

	public void registerOntology(String userID, String uploadID, String filename, String prefix, String type, String taxongroup) throws SQLException{
		PreparedStatement pstmt_insert = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "insert into ontologies "
					+ "(userid, uploadid, prefix, filename, otype, taxongroup) "
					+ "values (?,?,?, ?, ?,?)";
			pstmt_insert = conn.prepareStatement(sql);
			pstmt_insert.setString(1, userID);
			pstmt_insert.setString(2, uploadID);
			pstmt_insert.setString(3, prefix);
			pstmt_insert.setString(4, filename);
			pstmt_insert.setString(5, type);
			pstmt_insert.setString(6, taxongroup);
			pstmt_insert.executeUpdate();
		}catch (SQLException e){
			e.printStackTrace();//TODO log errors
			throw e;
		}finally {
			closeConnection(conn);
			close(pstmt_insert);
		}
	}
	
	public ArrayList<OntologyInfo> getOntologyInfoWithTaxonGroup(String taxonGroup) throws SQLException{
		ArrayList<OntologyInfo> info = new ArrayList<OntologyInfo>();
		PreparedStatement pstmt_select = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			
			String sql = "select filename, prefix, otype, taxongroup from ontologies where taxongroup = ?";
			pstmt_select = conn.prepareStatement(sql);
			pstmt_select.setString(1, taxonGroup);
			rset = pstmt_select.executeQuery();
			while(rset.next()){
				info.add(new OntologyInfo(rset.getString("filename"), rset.getString("prefix"), rset.getString("otype"), rset.getString("taxongroup")));
			}
		}catch (SQLException e){
			e.printStackTrace();//TODO log errors
			throw e;
		}finally {
			closeConnection(conn);
			close(pstmt_select);
			close(rset);
		}
		return info;
	}

	
	public ArrayList<OntologyInfo> getOntologyInfoWithUserID(String userID) throws SQLException{
		ArrayList<OntologyInfo> info = new ArrayList<OntologyInfo>();
		PreparedStatement pstmt_select = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			
			String sql = "select filename, prefix, otype, taxongroup from ontologies where userid = ?";
			pstmt_select = conn.prepareStatement(sql);
			pstmt_select.setString(1, userID);
			rset = pstmt_select.executeQuery();
			if(rset.next()){
				info.add(new OntologyInfo(rset.getString("filename"), rset.getString("prefix"), rset.getString("otype"), rset.getString("taxongroup")));
			}
		}catch (SQLException e){
			e.printStackTrace();//TODO log errors
			throw e;
		}finally {
			closeConnection(conn);
			close(pstmt_select);
			close(rset);
		}
		return info;
	}
	
	// in db, couldn't save enum type, therefore translate it to integer values
	private int translateOntologyRecordType(OntologyRecordType type) {
		if (type.equals(OntologyRecordType.MATCH)) {
			return 1;
		} else {
			return 2;
		}
	}

	private OntologyRecordType translateToOntologyRecordType(int i) {
		if (i == 1) {
			return OntologyRecordType.MATCH;
		} else {
			return OntologyRecordType.SUBMISSION;
		}
	}

	public OntologyMatch getOntologyMatchByID(int ID) throws SQLException {
		OntologyMatch match = new OntologyMatch();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select * from ontology_matches where Id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, ID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				match = new OntologyMatch(rset.getString("term"));
				match.setMatchingInfo(rset.getString("ontologyID"),
						rset.getString("permanentID"),
						rset.getString("parentTerm"),
						rset.getString("definition"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
		}

		return match;
	}

	public OntologySubmission getOntologySubmissionByID(int ID)
			throws SQLException {
		OntologySubmission submission = new OntologySubmission();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select * from ontology_submissions where ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, ID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				submission.setTerm(rset.getString("term"));
				submission.setCategory(rset.getString("category"));
				submission.setSubmissionID(rset.getString("ID"));
				submission.setSubmittedBy(rset.getString("submittedBy"));
				submission.setLocalID(rset.getString("localID"));
				submission.setTmpID(rset.getString("tmpID"));
				submission.setPermanentID(rset.getString("permanentID"));
				submission.setSuperClass(rset.getString("superClassID"));
				submission.setDefinition(rset.getString("definition"));
				String[] ontoIDs = rset.getString("ontologyID").split("\\s*[ ,]\\s*");
				String ontologyID = "";
				String localOntologyID = "";
				for(String ontoID: ontoIDs){
					if(ontoID.startsWith("ETC_")) localOntologyID += ontoID+",";
					else ontologyID += ontoID+",";
				}
				submission.setOntologyID(ontologyID.replaceFirst(",$", ""));
				submission.setLocalOntologyID(localOntologyID.replaceFirst(",$", ""));
				submission.setSource(rset.getString("source"));
				submission.setSampleSentence(rset.getString("sampleSentence"));
				submission.setSynonyms(rset.getString("synonyms"));
				submission.setOtherID(rset.getString("otherClassIDs4Term"));
				submission.setPartOfClass(rset.getString("partOfClassID"));
				submission.setSubmitAsSynonym(rset.getBoolean("submitAsSynonym"));	
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(pstmt);
			close(rset);
			closeConnection(conn);
		}

		return submission;
	}

	/*public OntologySubmission getOntologySubmissionByPermanentIDForSynonymSubmission(String pID)
			throws SQLException {
		OntologySubmission submission = new OntologySubmission();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select * from ontology_submissions where ( permanentID = ? || otherClassIDs4Term rlike ?) and submitAsSynonym = 0 ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pID);
			pstmt.setString(2, pID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				submission.setTerm(rset.getString("term"));
				submission.setCategory(rset.getString("category"));
				submission.setLocalID(rset.getString("localID"));
				submission.setTmpID(rset.getString("tmpID"));
				submission.setPermanentID(rset.getString("permanentID"));
				submission.setSuperClass(rset.getString("superClassID"));
				submission.setDefinition(rset.getString("definition"));
				String[] ontoIDs = rset.getString("ontologyID").split("\\s*[ ,]\\s*");
				String ontologyID = "";
				String localOntologyID = "";
				for(String ontoID: ontoIDs){
					if(ontoID.startsWith("ETC_")) localOntologyID += ontoID+",";
					else ontologyID += ontoID+",";
				}
				submission.setOntologyID(ontologyID.replaceFirst(",$", ""));
				submission.setLocalOntologyID(localOntologyID.replaceFirst(",$", ""));
				submission.setSource(rset.getString("source"));
				submission.setSampleSentence(rset.getString("sampleSentence"));
				submission.setSynonyms(rset.getString("synonyms"));
				submission.setOtherID(rset.getString("otherClassIDs4Term"));
				submission.setPartOfClass(rset.getString("partOfClassID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(pstmt);
			close(rset);
			closeConnection(conn);
		}

		return submission;
	}*/

	public OntologySubmission getDefaultDataForNewSubmission(int uploadID,
			String term, String category) throws Exception {
		OntologySubmission submission = new OntologySubmission();
		submission.setTerm(term);
		submission.setCategory(category);

		UploadInfo info = GeneralDAO.getInstance().getUploadInfo(uploadID);
		submission.setSource(info.getSource());

		PreparedStatement pstmt_syns = null, pstmt_sentence = null;
		ResultSet rset_syns = null, rset_sentence = null;
		Connection conn = null;
		try {
			conn = getConnection();

			// get synonyms
			String sql = "select synonyms from term_category_pair where uploadID = ? and term = ? and category = ?";
			pstmt_syns = conn.prepareStatement(sql);
			pstmt_syns.setInt(1, uploadID);
			pstmt_syns.setString(2, term);
			pstmt_syns.setString(3, category);
			rset_syns = pstmt_syns.executeQuery();
			if (rset_syns.next()) {
				submission.setSynonyms(rset_syns.getString("synonyms"));
			}

			// get sample sentence
			sql = "select sentence from sentences where uploadID = ? "
					+ "and sentence rlike '^(.*\\s)?" + term + "(\\s.*)?$'";
			pstmt_sentence = conn.prepareStatement(sql);
			pstmt_sentence.setInt(1, uploadID);
			rset_sentence = pstmt_sentence.executeQuery();
			if (rset_sentence.next()) {
				// get the first sentence as sample sentence
				submission.setSampleSentence(rset_sentence.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset_syns);
			close(rset_sentence);
			close(pstmt_syns);
			close(pstmt_sentence);
			closeConnection(conn);
		}

		return submission;
	}

	/**
	 * get all pending submissions in this upload
	 * 
	 * @return
	 * @param uploadID
	 * @param thisUploadOnly
	 * @throws SQLException
	 */
	public ArrayList<OntologySubmission> getPendingOntologySubmissions(
			int uploadID, boolean thisUploadOnly) throws SQLException {
		ArrayList<OntologySubmission> submissions = new ArrayList<OntologySubmission>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "select ID, tmpID from ontology_submissions "
					+ "where permanentID is NULL or permanentID = ''";
			if (thisUploadOnly) {
				sql = "select ID, tmpID from "
						+ "(select a.ID, a.tmpID, b.inUpload from "
						+ "(select * from ontology_submissions "
						+ "where permanentID is NULL or permanentID = '') a "
						+ "left join "
						+ "(select term, category, 1 as inUpload from term_category_pair "
						+ "where uploadID = ?) b "
						+ "on a.term = b.term and a.category = b.category) c "
						+ "where inUpload = 1;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uploadID);
			} else {
				pstmt = conn.prepareStatement(sql);
			}
			rset = pstmt.executeQuery();
			while (rset.next()) {
				OntologySubmission submission = new OntologySubmission();
				submission.setSubmissionID(rset.getString("ID"));
				submission.setTmpID(rset.getString("tmpID"));
				submissions.add(submission);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			closeConnection(conn);
		}

		return submissions;
	}

	/**
	 * update selected match record to a <term, category, glossaryType>
	 * 
	 * @param uploadID
	 * @param term
	 * @param category
	 * @param ID
	 * @param type
	 * @param recordID
	 * @throws Exception
	 */
	public void updateSelectedOntologyRecord(int uploadID, String term,
			String category, OntologyRecordType type, int recordID)
			throws Exception {
		int glossaryType = GeneralDAO.getInstance().getGlossaryTypeByUploadID(
				uploadID);
		PreparedStatement pstmt_del = null, pstmt_insert = null;
		Connection conn = null;

		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			// delete existing record
			String sql = "delete from selected_ontology_records where glossaryType = ? and "
					+ "term = ? and category = ?";
			pstmt_del = conn.prepareStatement(sql);
			pstmt_del.setInt(1, glossaryType);
			pstmt_del.setString(2, term);
			pstmt_del.setString(3, category);
			pstmt_del.executeUpdate();

			// insert new record
			sql = "insert into selected_ontology_records "
					+ "(term, category, glossaryType, recordType, recordID) "
					+ "values (?, ?, ?, ?, ?)";
			pstmt_insert = conn.prepareStatement(sql);
			pstmt_insert.setString(1, term);
			pstmt_insert.setString(2, category);
			pstmt_insert.setInt(3, glossaryType);
			pstmt_insert.setInt(4, translateOntologyRecordType(type));
			pstmt_insert.setInt(5, recordID);
			pstmt_insert.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			close(pstmt_del);
			close(pstmt_insert);
			closeConnection(conn);
		}

	}

	public void clearSelection(int glossaryType, String term, String category)
			throws Exception {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			// delete existing record
			String sql = "delete from selected_ontology_records where glossaryType = ? and "
					+ "term = ? and category = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, glossaryType);
			pstmt.setString(2, term);
			pstmt.setString(3, category);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
		}
	}

	/**
	 * for term_info part, get the ontology match records for a given term
	 * 
	 * @param term
	 * @param glossaryType
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<TermGlossary> getOntologyMatchForTerm(String term,
			int glossaryType) throws SQLException {
		ArrayList<TermGlossary> glossies = new ArrayList<TermGlossary>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;

		try {
			conn = getConnection();
			String sql = "select category, recordType, recordID "
					+ "from selected_ontology_records "
					+ "where glossaryType = ? and term = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, glossaryType);
			pstmt.setString(2, term);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String category = rset.getString("category");
				// get id and definition
				OntologyRecordType recordType = translateToOntologyRecordType(rset
						.getInt("recordType"));
				String id_prefix = "Ontology ID: ";
				if (recordType.equals(OntologyRecordType.SUBMISSION)) {
					id_prefix = "Temporary ID: ";
					sql = "select tmpID as id, definition from ontology_submissions where ID = ?";
				} else {
					sql = "select permanentID as id, definition from ontology_matches where ID = ?";
				}
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, rset.getInt("recordID"));
				rset2 = pstmt.executeQuery();
				if (rset2.next()) {
					String id = rset2.getString(1);
					if (recordType.equals(OntologyRecordType.MATCH)) {
						id = truncatePermanentID(id);
					}
					TermGlossary gloss = new TermGlossary(id_prefix + id,
							category, rset2.getString(2));
					glossies.add(gloss);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			closeConnection(conn);
		}

		return glossies;
	}

	/**
	 * get both matches and submissions as ontology record
	 * 
	 * @param uploadID
	 * @param term
	 * @param category
	 * @return
	 * @throws Exception
	 */
	public ArrayList<OntologyRecord> getOntologyRecords(int uploadID,
			String term, String category) throws Exception {
		ArrayList<OntologyRecord> records = new ArrayList<OntologyRecord>();
		int glossaryType = GeneralDAO.getInstance().getGlossaryTypeByUploadID(
				uploadID);
		PreparedStatement pstmt_selected = null, pstmt_match = null, pstmt_submission = null;
		ResultSet rset_selected = null, rset_match = null, rset_submission = null;
		Connection conn = null;

		try {
			conn = getConnection();

			// get selected ontology record
			boolean hasSelected = false;
			int selectedType = 0; // 1-match, 2-submission
			int selectedID = 0;
			String sql = "select * from selected_ontology_records where glossaryType = ? and "
					+ "term = ? and category = ? limit 1";
			pstmt_selected = conn.prepareStatement(sql);
			pstmt_selected.setInt(1, glossaryType);
			pstmt_selected.setString(2, term);
			pstmt_selected.setString(3, category);
			rset_selected = pstmt_selected.executeQuery();
			if (rset_selected.next()) {
				hasSelected = true;
				selectedType = rset_selected.getInt("recordType");
				selectedID = rset_selected.getInt("recordID");
			}

			// matches: global
			sql = "select * from ontology_matches where term = ?";
			pstmt_match = conn.prepareStatement(sql);
			pstmt_match.setString(1, term);
			rset_match = pstmt_match.executeQuery();
			while (rset_match.next()) {
				OntologyRecord record = new OntologyRecord(term, category);
				int ID = rset_match.getInt("ID");
				record.setType(OntologyRecordType.MATCH);
				record.setId(Integer.toString(ID));
				record.setDefinition(rset_match.getString("definition"));

				// parse permanentID, only the last part
				String pID = truncatePermanentID(rset_match
						.getString("permanentID"));

				record.setOntology(rset_match.getString("ontologyID") + " ["
						+ pID + "]");
				record.setParent(rset_match.getString("parentTerm"));

				// get selected
				if (hasSelected && selectedType == 1 && selectedID == ID) {
					record.setSelected(true);
				}

				records.add(record);
			}

			// submissions: global
			sql = "select * from ontology_submissions where term = ? and category = ?";
			pstmt_submission = conn.prepareStatement(sql);
			pstmt_submission.setString(1, term);
			pstmt_submission.setString(2, category);
			rset_submission = pstmt_submission.executeQuery();
			while (rset_submission.next()) {
				OntologyRecord record = new OntologyRecord(term, category);
				int ID = rset_submission.getInt("ID");
				record.setType(OntologyRecordType.SUBMISSION);
				record.setId(Integer.toString(ID));
				record.setDefinition(rset_submission.getString("definition"));
				record.setParent(rset_submission.getString("superClassID"));
				record.setOntology(rset_submission.getString("ontologyID")
						+ (rset_submission.getBoolean("accepted") ? " [Accepted]"
								: " [Pending]"));

				// get selected
				if (hasSelected && selectedType == 2 && selectedID == ID) {
					record.setSelected(true);
				}

				records.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset_match);
			close(rset_submission);
			close(rset_selected);
			close(pstmt_selected);
			close(pstmt_submission);
			close(pstmt_match);
			closeConnection(conn);
		}

		return records;
	}

	/**
	 * truncate the url part of permanentID
	 * 
	 * @param pID
	 * @return
	 */
	public String truncatePermanentID(String pID) {
		if (pID.lastIndexOf("/") > 0) {
			return pID.substring(pID.lastIndexOf("/") + 1, pID.length());
		} else {
			return pID;
		}
	}

	/**
	 * get term category lists for the page
	 * 
	 * @param uploadID
	 * @return
	 * @throws Exception
	 */
	public TermCategoryLists getTermCategoryPairsLists(int uploadID)
			throws Exception {
		TermCategoryLists lists = new TermCategoryLists();
		int glossaryType = GeneralDAO.getInstance().getGlossaryTypeByUploadID(
				uploadID);
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;
		Connection conn = null;
		try {
			conn = getConnection();

			boolean isGetRemoved = false;
			boolean isGetStructure = true;
			for (int i = 0; i < 4; i++) {
				switch (i) {
				case 0:
					isGetRemoved = false;
					isGetStructure = true;
					break;
				case 1:
					isGetRemoved = false;
					isGetStructure = false;
					break;
				case 2:
					isGetRemoved = true;
					isGetStructure = true;
					break;
				case 3:
					isGetRemoved = true;
					isGetStructure = false;
					break;
				default:
					break;
				}

				String sql = "select ID, term, category, removed from term_category_pair where uploadID = ? ";
				if (isGetStructure) {
					sql += "and category = 'structure' ";
				} else {
					sql += "and category <> 'structure' ";
				}
				sql += "and removed = ? order by term, category";
				ArrayList<TermCategoryPair> tcList = new ArrayList<TermCategoryPair>();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, uploadID);
				pstmt.setBoolean(2, isGetRemoved);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					int pairID = rset.getInt("ID");
					String term = rset.getString("term");
					String category = rset.getString("category");
					TermCategoryPair pair = new TermCategoryPair(
							Integer.toString(pairID), term, category);
					pair.setRemoved(rset.getBoolean("removed"));
					pair.setIsStructure(isGetStructure);

					// get mapping status
					sql = "select * from selected_ontology_records "
							+ "where term = ? and category = ? and glossaryType = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, term);
					pstmt.setString(2, category);
					pstmt.setInt(3, glossaryType);
					rset2 = pstmt.executeQuery();
					if (rset2.next()) {
						if (translateToOntologyRecordType(
								rset2.getInt("recordType")).equals(
								OntologyRecordType.MATCH)) {
							pair.setStatus(MappingStatus.MAPPED_TO_MATCH);
						} else {
							pair.setStatus(MappingStatus.MAPPED_TO_SUBMISSION);
						}
					} else {
						pair.setStatus(MappingStatus.NOT_MAPPED);
					}

					// add the pair to the list
					tcList.add(pair);
				}

				switch (i) {
				case 0:
					lists.setRegularStructures(tcList);
					break;
				case 1:
					lists.setRegularCharacters(tcList);
					break;
				case 2:
					lists.setRemovedStructures(tcList);
					break;
				case 3:
					lists.setRemovedCharacters(tcList);
					break;
				default:
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
			close(rset2);
		}
		return lists;
	}

	/**
	 * either remove the term(category) pair from regular list, or move the pair
	 * back to the regular list
	 * 
	 * @param uploadID
	 * @param termCategoryPairID
	 * @param isRemove
	 * @throws SQLException
	 */
	public void moveTermCategoryPair(int uploadID, int termCategoryPairID,
			boolean isRemove) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "update term_category_pair set removed = ? where uploadID = ? "
					+ "and ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setBoolean(1, isRemove);
			pstmt.setInt(2, uploadID);
			pstmt.setInt(3, termCategoryPairID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
		}
	}

	public void updatePermanentIDOfSubmission(int submissionID)
			throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "update ontology_submissions set permanentID = ? "
					+ "where ID = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, submissionID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
		}
	}

	/**
	 * delete a submission from db
	 * 
	 * @param submissionID
	 * @throws SQLException
	 */
	public void deleteSubmission(int submissionID) throws SQLException {
		PreparedStatement pstmt = null, pstmt_del_selected = null;
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "delete from ontology_submissions where ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, submissionID);
			pstmt.executeUpdate();

			// delete mapped records to this submission
			sql = "delete from selected_ontology_records where recordType = ? "
					+ "and recordID = ?";
			pstmt_del_selected = conn.prepareStatement(sql);
			pstmt_del_selected.setInt(1,
					translateOntologyRecordType(OntologyRecordType.SUBMISSION));
			pstmt_del_selected.setInt(2, submissionID);
			pstmt_del_selected.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			close(pstmt);
			close(pstmt_del_selected);
			closeConnection(conn);
		}
	}

	/**
	 * update existing submissions
	 * 
	 * @param submission
	 * @throws SQLException
	 */
	public void updateSubmission(OntologySubmission submission)
			throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql = "update ontology_submissions set ontologyID = ?, "
					+ "superClassID = ?, " +"partOfClassID = ?, " + "definition = ?,"
					+ "synonyms = ?, " + "source = ?, sampleSentence = ?, term =?, category=?, submitAsSynonym=?, otherClassIDs4Term=? , submittedBy=?"
					+ "where ID = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, (submission.getOntologyID()+","+submission.getLocalOntologyID()).replaceAll("^,|,$", ""));
			pstmt.setString(2, submission.getSuperClass());
			pstmt.setString(3, submission.getPartOfClass());
			pstmt.setString(4, submission.getDefinition());
			pstmt.setString(5, submission.getSynonyms());
			pstmt.setString(6, submission.getSource());
			pstmt.setString(7, submission.getSampleSentence());
			pstmt.setString(8, submission.getTerm());
			pstmt.setString(9, submission.getCategory());
			pstmt.setInt(10, submission.getSubmitAsSynonym()? 1 : 0);
			pstmt.setString(11, submission.getOtherID());
			pstmt.setString(12, submission.getSubmittedBy());
			pstmt.setInt(13, Integer.parseInt(submission.getSubmissionID()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
		}
	}

	public void addSubmission(OntologySubmission submission, int uploadID)
			throws Exception {
		PreparedStatement pstmt = null, pstmt_id = null;
		int submissionID = -1;
		Connection conn = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "insert into ontology_submissions"
					+ "(term, category, ontologyID, submittedBy, localID, "
					+ "tmpID, permanentID, superClassID, partOfClassID, synonyms, definition, "
					+ "source, sampleSentence, accepted, submitAsSynonym, otherClassIDs4Term) " + "values "
					+ "(?, ?, ?, ?, ?, " + "?, ?, ?,?, ?, ?, " + "? ,?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, submission.getTerm());
			pstmt.setString(2, submission.getCategory());
			pstmt.setString(3, (submission.getOntologyID()+","+submission.getLocalOntologyID()).replaceAll("^,|,$", "")); //at least one of them is not empty
			pstmt.setString(4, submission.getSubmittedBy());
			pstmt.setString(5, submission.getLocalID());

			pstmt.setString(6, submission.getTmpID());
			pstmt.setString(7, submission.getPermanentID()==null? "" : submission.getPermanentID());
			pstmt.setString(8, submission.getSuperClass());
			pstmt.setString(9, submission.getPartOfClass());
			pstmt.setString(10, submission.getSynonyms());
			pstmt.setString(11, submission.getDefinition());

			pstmt.setString(12, submission.getSource());
			pstmt.setString(13, submission.getSampleSentence());
			if(!submission.getLocalOntologyID().isEmpty()){
				pstmt.setInt(14, 1); //submission to local is automatically accepted
			}else{
				pstmt.setInt(14, 0);
			}
			pstmt.setInt(15, submission.getSubmitAsSynonym()? 1: 0);
			pstmt.setString(16, submission.getOtherID());
			pstmt.executeUpdate();
			sql = "SELECT LAST_INSERT_ID()";
			pstmt_id = conn.prepareStatement(sql);
			rset = pstmt_id.executeQuery();
			if (rset.next()) {
				submissionID = rset.getInt(1);
			} else {
				if (conn != null) {
					try {
						conn.rollback();
					} catch (SQLException exe) {
						exe.printStackTrace();
						throw exe;
					}
				}
				throw new Exception(
						"Failed to insert submission record to database. ");
				
			}
			// after submissioin, set the new one to be the default mapping
			if (submissionID > 0) {
				updateSelectedOntologyRecord(uploadID, submission.getTerm(),
						submission.getCategory(), OntologyRecordType.SUBMISSION,
						submissionID);
				conn.commit();
			}
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			close(pstmt_id);
			closeConnection(conn);
		}

		
	}

	/**
	 * check if there is new ontology match in this upload
	 * 
	 * @param uploadID
	 * @param glossaryType
	 * @throws Exception
	 */
	public void refreshStatusOfMatches(int uploadID) throws Exception {
		int glossaryType = GeneralDAO.getInstance().getGlossaryTypeByUploadID(
				uploadID);

		// create ontology lookup client
		String ontologyName = Utilities
				.getOntologyNameByGlossaryType(glossaryType);

		// get ontology directory
		//ClassLoader loader = Thread.currentThread().getContextClassLoader();
		/*Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		String ontologyDir = properties.getProperty("ontology_dir");
		String dictDir = properties.getProperty("dict_dir");
		if(ontologyDir.endsWith(File.separator)) ontologyDir = ontologyDir.replaceFirst(File.separator+"$", "");
		if(dictDir.endsWith(File.separator)) dictDir = dictDir.replaceFirst(File.separator+"$", "");*/
		String ontologyDir = Configuration.ontology_dir;
		String dictDir = Configuration.dict_dir;

		OntologyLookupClient olclient = new OntologyLookupClient(ontologyName,
				ontologyDir, dictDir);

		PreparedStatement pstmt = null, pstmt_insert = null;
		Connection conn = null;
		ResultSet rset = null, rset2 = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			String sql = "select term, category from term_category_pair "
					+ "where uploadID = ? and removed = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setBoolean(2, false);
			rset = pstmt.executeQuery();
			while (rset.next()) {

				String term = rset.getString("term");
				String category = rset.getString("category");

				System.out.println("term: " + term + " (category: " + category
						+ ")");

				if (category.equalsIgnoreCase("structure")) {
					// search entity
					ArrayList<EntityProposals> eps = null;
					try {
						eps = olclient.searchStrucutre(term);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (eps != null) {
						for (EntityProposals ep : eps) {
							for (Entity e : ep.getProposals()) {
								if(e.getClassIRI()!=null){
									System.out.println("\tmatch: "
											+ e.getClassIRI());
									// check if e already exist
									sql = "select ID from ontology_matches where permanentID = ?";
									pstmt = conn.prepareStatement(sql);
									pstmt.setString(1, e.getClassIRI());
									rset2 = pstmt.executeQuery();

									if (rset2.next()) {
										// TODO: update?
									} else {
										sql = "insert into ontology_matches "
												+ "(term, ontologyID, permanentID, parentTerm, definition) "
												+ "values (?, ?, ?, ?, ?)";
										pstmt_insert = conn.prepareStatement(sql);
										pstmt_insert.setString(1, term);
										pstmt_insert.setString(2,
												ontologyName.toUpperCase());
										pstmt_insert.setString(3, e.getClassIRI());
										pstmt_insert.setString(4, e.getPLabel());
										pstmt_insert.setString(5, e.getDef());
										pstmt_insert.executeUpdate();
									}
								}
							}
						}
					}
				} else {
					// search character
					ArrayList<FormalConcept> fcs = null;
					try {
						fcs = olclient.searchCharacter(rset.getString("term"));
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (fcs != null) {
						for (FormalConcept fc : fcs) {
							if(fc.getClassIRI()!=null){
								System.out.println("\tmatch: " + fc.getClassIRI());
								// check if e already exist
								sql = "select ID from ontology_matches where permanentID = ?";
								pstmt = conn.prepareStatement(sql);
								pstmt.setString(1, fc.getClassIRI());
								rset2 = pstmt.executeQuery();

								if (rset2.next()) {
									// TODO: update?
								} else {
									sql = "insert into ontology_matches "
											+ "(term, ontologyID, permanentID, parentTerm, definition) "
											+ "values (?, ?, ?, ?, ?)";
									pstmt_insert = conn.prepareStatement(sql);
									pstmt_insert.setString(1, term);
									pstmt_insert.setString(2, "PATO");
									pstmt_insert.setString(3, fc.getClassIRI());
									pstmt_insert.setString(4, fc.getPLabel());
									pstmt_insert.setString(5, fc.getDef());
									pstmt_insert.executeUpdate();
								}
							}
						}
					}
				}
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(rset2);
			close(pstmt);
			close(pstmt_insert);
			closeConnection(conn);
		}
	}




}
