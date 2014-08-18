package edu.arizona.biosemantics.oto.lite.rest.db;

import java.sql.SQLException;
import java.util.List;

import edu.arizona.biosemantics.oto.common.model.lite.Sentence;

public class SentenceDAO extends AbstractDAO {

	private static SentenceDAO instance;
	
	private SentenceDAO() throws Exception { }
	
	public static SentenceDAO getInstance() throws Exception {
		if(instance == null)
			instance = new SentenceDAO();
		return instance;
	}

	public void putSentences(int uploadId, List<Sentence> sentences) throws SQLException {
		this.openConnection();
				
		for(Sentence sentence : sentences) {
			String sql = "INSERT INTO sentences" + " VALUES (" + uploadId + ", " +
					sentence.getSentId() + ",'"  + 
					sentence.getSource() + "','" + 
					sentence.getSentence() + "','" + 
					sentence.getOriginalSentence() + "')";	
			this.executeSQL(sql).close();
		}
		
		this.closeConnection();
	}
}
