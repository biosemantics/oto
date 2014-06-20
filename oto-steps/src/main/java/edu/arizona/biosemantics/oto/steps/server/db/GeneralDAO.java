package edu.arizona.biosemantics.oto.steps.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.oto.steps.server.utilities.Utilities;
import edu.arizona.biosemantics.oto.steps.shared.beans.UploadInfo;

/**
 * this class holds general db functions that might be used by all pages
 * 
 * @author Fengqiong Huang
 * 
 */
public class GeneralDAO extends AbstractDAO {
	private static GeneralDAO instance;

	public static GeneralDAO getInstance() throws Exception {
		if (instance == null) {
			instance = new GeneralDAO();
		}
		return instance;
	}

	public GeneralDAO() throws Exception {
		super();
	}

	public UploadInfo getUploadInfo(int uploadID, String secret)
			throws Exception {
		UploadInfo info = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			String sql = "select * from uploads where uploadID = ? and secret = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setString(2, secret);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				info = new UploadInfo(uploadID);
				info.setBioportalApiKey(rset.getString("bioportalApiKey"));
				info.setBioportalUserID(rset.getString("bioportalUserID"));
				info.setEtcUserName(rset.getString("EtcUser"));
				info.setFinalized(rset.getBoolean("isFinalized"));
				info.setGlossaryType(rset.getInt("glossaryType"));
				info.setGlossaryTypeName(Utilities.getGlossaryNameByID(rset
						.getInt("glossaryType")));
				info.setHasSentToOTO(rset.getBoolean("sentToOTO"));
				info.setPrefixForOTO(rset.getString("prefixForOTO"));
				info.setReadyToDelete(rset.getBoolean("readyToDelete"));
				info.setUploadTime(rset.getDate("uploadTime"));
				info.setSource(rset.getString("source"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
		}
		return info;
	}

	public UploadInfo getUploadInfo(int uploadID) throws Exception {
		UploadInfo info = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		try {
			conn = getConnection();

			String sql = "select * from uploads where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				info = new UploadInfo(uploadID);
				info.setBioportalApiKey(rset.getString("bioportalApiKey"));
				info.setBioportalUserID(rset.getString("bioportalUserID"));
				info.setEtcUserName(rset.getString("EtcUser"));
				info.setFinalized(rset.getBoolean("isFinalized"));
				info.setGlossaryType(rset.getInt("glossaryType"));
				info.setGlossaryTypeName(Utilities.getGlossaryNameByID(rset
						.getInt("glossaryType")));
				info.setHasSentToOTO(rset.getBoolean("sentToOTO"));
				info.setPrefixForOTO(rset.getString("prefixForOTO"));
				info.setReadyToDelete(rset.getBoolean("readyToDelete"));
				info.setUploadTime(rset.getDate("uploadTime"));
				info.setSource(rset.getString("source"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
		}
		return info;
	}

	/**
	 * give an uploadID, return the glossary type of that upload
	 * 
	 * @param uploadID
	 * @return
	 * @throws Exception
	 */
	public int getGlossaryTypeByUploadID(int uploadID) throws Exception {
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		int glossaryType = -1; // default glossary type is -1
		try {
			conn = getConnection();

			// get glossaryType of this upload
			String sql = "select glossaryType from uploads where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				glossaryType = rset.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
		}

		// validate glossaryType
		if (glossaryType == -1) {
			throw new Exception("No valid glossary type for this upload '"
					+ uploadID + "'!");
		}

		return glossaryType;
	}
}
