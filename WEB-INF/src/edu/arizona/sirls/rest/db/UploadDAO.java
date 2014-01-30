package edu.arizona.sirls.rest.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.arizona.sirls.rest.beans.Upload;
import edu.arizona.sirls.rest.beans.UploadResult;
import edu.arizona.sirls.util.Encrypt;
import edu.arizona.sirls.util.Utilities;

public class UploadDAO extends AbstractDAO {

	private UploadDAO() throws Exception {
		super();
	}

	private static UploadDAO instance;
	
	public static UploadDAO getInstance() throws Exception {
		if(instance == null)
			instance = new UploadDAO();
		return instance;
	}

	public UploadResult putUpload(Upload upload) throws Exception {
		this.openConnection();				
		
		int glossaryId = Utilities.getGlossaryIDByName(upload.getGlossaryType());
		
		/*
		String sql = "SELECT glossTypeID FROM markedupdatasets.glossarytypes WHERE UPPER(glossaryName)=UPPER('" + upload.getGlossaryType() + "')";
		PreparedStatement statement = this.executeSQL(sql);
		int glossaryId = -1;
		ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
        	glossaryId = resultSet.getInt(1);
        } else {
        	resultSet.close();
        	statement.close();
        }
    	resultSet.close();
    	statement.close();
		*/
		PreparedStatement statement;
		String sql;
		
		sql = "INSERT INTO uploads (uploadTime, sentToOTO, isFinalized, prefixForOTO, readyToDelete, glossaryType, bioportalUserId, bioportalApiKey, EtcUser, source) " +
				"VALUES (NOW(), 0, 0, ?, NULL, ?, ?, ?, ?, ?)";
		statement = this.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, upload.getGlossaryType());
		statement.setInt(2, glossaryId);
		statement.setString(3, upload.getBioportalUserId());
		statement.setString(4, upload.getBioportalAPIKey());
		statement.setString(5, upload.getUser());
		statement.setString(6, upload.getSource());
		statement.executeUpdate();
		int uploadId;
		ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
        	uploadId = generatedKeys.getInt(1);
        } else {
            throw new SQLException("Upload failed, no generated key obtained.");
        }
        generatedKeys.close();
		statement.close();
		
		//set secret
		String secret = Encrypt.getInstance().encrypt(Integer.toString(uploadId));
		sql = "update uploads set secret = '" + secret +
				"' where uploadID = " + uploadId;
		statement = this.executeSQL(sql);
		statement.executeUpdate();
		
//		sql = "SELECT secret FROM uploads WHERE uploadID = " + uploadId;
//		statement = this.executeSQL(sql);
//		String secret = "";
//		resultSet = statement.getResultSet();
//		if(resultSet.next()) {
//			secret = resultSet.getString(1);
//		}
//		resultSet.close();
    	statement.close();
		this.closeConnection();
		
		SentenceDAO.getInstance().putSentences(uploadId, upload.getSentences());
		TermDAO.getInstance().putPossibleStructures(uploadId, upload.getPossibleStructures());
		TermDAO.getInstance().putPossibleCharacters(uploadId, upload.getPossibleCharacters());
		TermDAO.getInstance().putPossibleOtherTerms(uploadId, upload.getPossibleOtherTerms());
		return new UploadResult(uploadId, secret);
	}

	public void setReadyToDelete(int uploadId) throws SQLException {
		this.openConnection();
		String sql = "UPDATE uploads SET readyToDelete = NOW() WHERE uploadID = " + uploadId;
		this.executeSQL(sql).close();
		this.closeConnection();
	}

	public boolean isFinalized(int uploadId) throws SQLException {
		this.openConnection();
		String sql = "SELECT isFinalized FROM uploads WHERE uploadId = " + uploadId;
		PreparedStatement statement = this.executeSQL(sql);
		boolean result = false;
		ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
        	result = resultSet.getBoolean(1);
        } else {
        	resultSet.close();
        	statement.close();
        }
    	resultSet.close();
    	statement.close();
		this.closeConnection();
		return result;
	}
	
	public boolean isValidSecret(int uploadId, String secret) throws SQLException {
		this.openConnection();
		String sql = "SELECT * FROM uploads WHERE uploadID = " + uploadId + " AND secret = '" + secret + "'";
		PreparedStatement statement = this.executeSQL(sql);
		ResultSet resultSet = statement.getResultSet();
		boolean result = false;
        if (resultSet.next()) {
        	result = true;
        } 
    	resultSet.close();
    	statement.close();
		this.closeConnection();
		return result;
	}

}
