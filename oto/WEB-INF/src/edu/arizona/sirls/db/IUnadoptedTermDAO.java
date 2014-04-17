package edu.arizona.sirls.db;

import java.sql.SQLException;
import java.util.List;

import edu.arizona.sirls.beans.OTOProvisionalTerm;


public interface IUnadoptedTermDAO {
	
	public List<OTOProvisionalTerm> getUnadoptedStructureTerms() throws SQLException; 
	
	public List<OTOProvisionalTerm> getUnadoptedCharacterTerms() throws SQLException;	
	
	public OTOProvisionalTerm getUnadoptedTerm(String localId) throws SQLException;

	public OTOProvisionalTerm getFirstUnadoptedTerm() throws SQLException;

	public void markNotSent(String localId) throws SQLException;

	public void markSent(String localId) throws SQLException;
	
}
