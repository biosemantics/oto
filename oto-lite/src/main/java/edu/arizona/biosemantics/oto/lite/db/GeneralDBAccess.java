package edu.arizona.biosemantics.oto.lite.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class GeneralDBAccess extends AbstractDBAccess {
	private static final Logger LOGGER = Logger
			.getLogger(GeneralDBAccess.class);

	private static GeneralDBAccess instance;

	public static GeneralDBAccess getInstance() {
		if (instance == null) {
			instance = new GeneralDBAccess();
		}
		return instance;
	}

	protected GeneralDBAccess() {

	}

	public int getGlossaryType(int uploadID) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int glossaryType = 1; // default is 1
		try {
			conn = getConnection();
			String sql = "select glossaryType from uploads where uploadID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				glossaryType = rset.getInt(1);
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in GeneralDBAccess: getGlossaryType " + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return glossaryType;
	}

	public int validateURL(String uploadIDString, String secret)
			throws SQLException {
		int returnvalue = -1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int uploadID = returnvalue;

		try {
			uploadID = Integer.parseInt(uploadIDString);
			if (uploadID <= 0) {
				return returnvalue;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Wrong uploadID: " + e);
		}

		try {
			conn = getConnection();
			String sql = "select uploadID from uploads where uploadID = ? and secret = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setString(2, secret);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				returnvalue = uploadID;
			}
		} catch (SQLException exe) {
			exe.printStackTrace();
			LOGGER.error("Exception in GeneralDBAccess: isValidURL " + exe);
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnvalue;
	}
}
