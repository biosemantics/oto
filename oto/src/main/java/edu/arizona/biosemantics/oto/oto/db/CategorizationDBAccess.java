package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
		// TODO Auto-generated constructor stub
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

}
