package edu.arizona.biosemantics.oto.lite.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.lite.beans.OntologyRecordType;
import edu.arizona.biosemantics.oto.lite.beans.TermGlossaryBean;

public class OntologyDBAccess extends AbstractDBAccess {
	private static final Logger LOGGER = Logger
			.getLogger(OntologyDBAccess.class);

	private static OntologyDBAccess instance;

	public static OntologyDBAccess getInstance() {
		if (instance == null) {
			instance = new OntologyDBAccess();
		}
		return instance;
	}

	private OntologyRecordType translateToOntologyRecordType(int i) {
		if (i == 1) {
			return OntologyRecordType.MATCH;
		} else {
			return OntologyRecordType.SUBMISSION;
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
	public ArrayList<TermGlossaryBean> getOntologyMatchForTerm(String term,
			int glossaryType) throws SQLException {
		ArrayList<TermGlossaryBean> glossies = new ArrayList<TermGlossaryBean>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null, rset2 = null;

		try {
			conn = getConnection();

			// get the matched record
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
					TermGlossaryBean gloss = new TermGlossaryBean(id_prefix
							+ id, category, rset2.getString(2));
					glossies.add(gloss);
				}

			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in OntologyDBAccess: getOntologyMatchForTerm "
					+ exe);
			throw exe;
		} finally {
			if (rset2 != null) {
				rset2.close();
			}
			closeConnection(pstmt, rset, conn);
		}

		return glossies;
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
}
