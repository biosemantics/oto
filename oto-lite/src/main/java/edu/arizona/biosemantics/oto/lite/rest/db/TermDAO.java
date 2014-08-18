package edu.arizona.biosemantics.oto.lite.rest.db;

import java.sql.SQLException;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.lite.Term;

public class TermDAO extends AbstractDAO {

	private static TermDAO instance = null;
	
	private TermDAO () throws Exception { }

	public static TermDAO getInstance() throws Exception {
		if(instance == null)
			instance = new TermDAO();
		return instance;
	}

	public void putPossibleStructures(int uploadId, List<Term> possibleStructures) throws SQLException {
		this.openConnection();
		for(Term term : possibleStructures) {
			String sql = "INSERT INTO terms" + " VALUES (" + uploadId + ", '" + term.getTerm() + "', 1)";
			this.executeSQL(sql).close();
		}
		this.closeConnection();
	}

	public void putPossibleCharacters(int uploadId, List<Term> possibleCharacters) throws SQLException {
		this.openConnection();
		for(Term term : possibleCharacters) {
			String sql = "INSERT INTO terms" + " VALUES (" + uploadId + ", '" + term.getTerm() + "', 2)";
			this.executeSQL(sql).close();
		}
		this.closeConnection();
	}

	public void putPossibleOtherTerms(int uploadId, List<Term> possibleOtherTerms) throws SQLException {
		this.openConnection();
		for(Term term : possibleOtherTerms) {
			String sql = "INSERT INTO terms" + " VALUES (" + uploadId + ", '" + term.getTerm() + "', 3)";
			this.executeSQL(sql).close();
		}
		this.closeConnection();
	}
	
}
