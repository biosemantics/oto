package edu.arizona.biosemantics.oto.oto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Set;

import edu.arizona.biosemantics.oto.oto.db.DatabaseAccess;

public class TimePass extends DatabaseAccess {

	public TimePass() throws IOException {
		super();
	}

	/**
	 * @param args
	 */
	//
	public static void main(String[] args) throws Exception {
		HashMap<String, Double> entropies = new HashMap<String, Double>();
		for (int i = 0; i < 10; i++) {
			entropies.put(i + "", 0d);
		}
		System.out.println(entropies);
		String dataset = "blabla";
		StringBuffer sql = new StringBuffer("select term, entropyscore from "
				+ dataset + "_finalized_terms where terms in (");
		Set<String> keys = entropies.keySet();
		int i = 0;
		for (String key : keys) {
			sql.append("?,");
			entropies.put(i + "", 123d);
			i++;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		System.out.println(sql);
		System.out.println(entropies);

	}

	public void abc() throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rset = null;
		String sql = "(select distinct term from markedupdatasets.fna_v19_web_grouped_terms where groupid = ?) "
				+ " union"
				+ "(select distinct cooccurTerm from markedupdatasets.fna_v19_web_grouped_terms where groupid = ?) ";

		conn = getConnection();
		pstmt = conn.prepareStatement(sql);
		pstmt1 = conn
				.prepareStatement("insert into fna_v19_finalized_terms (groupId, term, entropyscore) values (?,?,?)");
		for (int i = 1; i < 15; i++) {
			pstmt.setInt(1, i);
			pstmt.setInt(2, i);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				// list.add(rset.getString(1));
				pstmt1.setInt(1, i);
				pstmt1.setString(2, rset.getString(1));
				pstmt1.setDouble(3, Math.random() * 5);
				pstmt1.execute();

			}

		}

	}

}
