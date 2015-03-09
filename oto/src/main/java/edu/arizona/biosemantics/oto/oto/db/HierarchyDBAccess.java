package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.TermContext;

public class HierarchyDBAccess extends DatabaseAccess {

	private static HierarchyDBAccess instance;

	public static HierarchyDBAccess getInstance() throws IOException {
		if (instance == null) {
			instance = new HierarchyDBAccess();
		}

		return instance;
	}

	public HierarchyDBAccess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void importStructures(String datasetName,
			List<TermContext> termContexts, String fileName) throws Exception {
		List<String> terms = new LinkedList<String>();
		List<String> contexts = new LinkedList<String>();
		for(TermContext context : termContexts) {
			terms.add(context.getTerm());
			contexts.add(context.getContext());
		}
		this.importStructures(datasetName, terms, fileName, contexts);
	}
	
	/**
	 * delete existing structures and import terms
	 * 
	 * @param dataset
	 * @param termList
	 * @param fileName
	 * @param sentences
	 * @throws Exception 
	 */
	public void importStructures(String dataset, List<String> termList,
			String fileName, List<String> sentences) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			// import structures
			stmt.execute("delete from " + dataset + "_web_tags");

			// insert default 7 terms
			stmt.execute("insert into " + dataset
					+ "_web_tags(tagID, tagName) " + "values "
					+ "(1, 'Plant'), (2, 'Root'), (3, 'Stem'), (4, 'Leaf'), "
					+ "(5, 'Fruit'), (6, 'Seed'), (7, 'Flower');");

			// insert new terms
			for (String term : termList) {
				stmt.execute("insert into " + dataset + "_web_tags "
						+ "(tagName) values ('" + term + "')");
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
