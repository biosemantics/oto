package edu.arizona.biosemantics.oto.steps.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.arizona.biosemantics.oto.steps.shared.beans.terminfo.TermContext;

public class ContextDAO extends AbstractDAO {

	private static ContextDAO instance;

	public static ContextDAO getInstance() throws Exception {
		if (instance == null) {
			instance = new ContextDAO();
		}
		return instance;
	}

	public ContextDAO() throws Exception {
		super();
	}

	public ArrayList<TermContext> getTermContext(String term, int uploadID)
			throws SQLException, IOException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rset = null;
		ArrayList<TermContext> contexts = new ArrayList<TermContext>();
		try {
			conn = getConnection();

			// notice: mysql regex space is ' ', not \\s
			String sql = "select source, sentence from sentences "
					+ "where uploadID = ? and sentence rlike '^(.* )?" + term
					+ "( .*)?$' limit 10";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				TermContext context = new TermContext(rset.getString(1),
						rset.getString(2));

				contexts.add(context);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnection(conn);
			close(pstmt);
			close(rset);
		}

		return contexts;
	}

}
