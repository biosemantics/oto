package edu.arizona.biosemantics.db;

import java.sql.SQLException;
import java.util.List;

import edu.arizona.biosemantics.beans.ProvisionalEntry;


public interface IUnadoptedTermDAO {
	
	public List<ProvisionalEntry> getUnadoptedStructureTerms() throws SQLException; 
	
	public List<ProvisionalEntry> getUnadoptedCharacterTerms() throws SQLException;	
	
	public ProvisionalEntry getUnadoptedTerm(String localId) throws SQLException;

	public ProvisionalEntry getFirstUnadoptedTerm() throws SQLException;

	public void markNotSent(String localId) throws SQLException;

	public void markSent(String localId) throws SQLException;
	
}
