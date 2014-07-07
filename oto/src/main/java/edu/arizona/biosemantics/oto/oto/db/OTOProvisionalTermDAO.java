package edu.arizona.biosemantics.oto.oto.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.oto.oto.beans.ProvisionalEntry;

public class OTOProvisionalTermDAO extends AbstractDAO {

	private static OTOProvisionalTermDAO instance;

	private OTOProvisionalTermDAO() throws SQLException,
			ClassNotFoundException, IOException {
		this.openConnection();
		this.closeConnection();
	}

	public static OTOProvisionalTermDAO getInstance() throws SQLException,
			ClassNotFoundException, IOException {
		if (instance == null)
			instance = new OTOProvisionalTermDAO();
		return instance;
	}

	public void addAwaitingAdoption(ProvisionalEntry provisionalEntry)
			throws SQLException {
		this.openConnection();
		this.storeOTOProvisionalTerm("adoption", provisionalEntry);
		this.closeConnection();
	}

	public List<ProvisionalEntry> getAllPendingAdoptions()
			throws SQLException {
		this.openConnection();
		List<ProvisionalEntry> result = this.getFilteredAdoptions("adoption",
				"WHERE permanentId IS NULL or permanentId = ''");
		this.closeConnection();
		return result;
	}

	public List<ProvisionalEntry> getAllStructureAwaitingAdoption()
			throws SQLException {
		this.openConnection();
		List<ProvisionalEntry> result = this.getFilteredAdoptions(
				"awaitingAdoption", "WHERE termType='structure'");
		this.closeConnection();
		return result;
	}

	public List<ProvisionalEntry> getAdoptedStructureTerms()
			throws SQLException {
		this.openConnection();
		List<ProvisionalEntry> result = this.getFilteredAdoptions("adopted",
				"WHERE termType='structure'");
		this.closeConnection();
		return result;
	}

	public List<ProvisionalEntry> getAdoptedCharacterTerms()
			throws SQLException {
		this.openConnection();
		List<ProvisionalEntry> result = this.getFilteredAdoptions("adopted",
				"WHERE termType='character'");
		this.closeConnection();
		return result;
	}

	public List<ProvisionalEntry> getAllCharacterAwaitingAdoption()
			throws SQLException {
		this.openConnection();
		List<ProvisionalEntry> result = this.getFilteredAdoptions(
				"awaitingAdoption", "WHERE termType='character'");
		this.closeConnection();
		return result;
	}

	public void storeAdopted(ProvisionalEntry OTOProvisionalTerm)
			throws SQLException {
		this.openConnection();

		storeOTOProvisionalTerm("adopted", OTOProvisionalTerm);
		this.closeConnection();
	}

	/**
	 * delete pending adoption from OTO
	 * 
	 * @param provisionalEntry
	 * @param userID
	 * @throws SQLException
	 */
	public void deleteAwaitingAdoption(ProvisionalEntry provisionalEntry,
			int userID) throws SQLException {
		this.openConnection();
		// log deletion before delete
		this.logDeletion(provisionalEntry, userID);
		this.deleteTerm("adoption", provisionalEntry);
		this.closeConnection();
	}

	/**
	 * delete awaiting adoption from main fuction in order to clean up
	 * 
	 * @param provisionalEntry
	 * @throws SQLException
	 */
	public void deleteAwaitingAdoption(ProvisionalEntry provisionalEntry)
			throws SQLException {
		this.openConnection();
		this.deleteTerm("adoption", provisionalEntry);
		this.closeConnection();
	}

	/**
	 * log deletion
	 * 
	 * @param term
	 * @throws SQLException
	 */
	private void logDeletion(ProvisionalEntry term, int userID)
			throws SQLException {
		PreparedStatement pstmt = this
				.prepareStatement("insert into bioportal_deleted_submissions ("
						+ "localId, term, temporaryId, permanentId, superClass, submittedBy, definition, "
						+ "ontologyIds, preferredName, synonyms, source, termType, termCategory, dataset, "
						+ "glossaryType, deletedBy, deleteTime) "
						+ "select localId, term, temporaryId, permanentId, superClass, submittedBy, definition, "
						+ "ontologyIds, preferredName, synonyms, source, termType, termCategory, dataset, glossaryType, "
						+ userID + " as deletedBy, now() as deleteTime "
						+ "from bioportal_adoption " + "where localID = ?;");
		pstmt.setLong(1, Long.parseLong(term.getLocalId()));
		pstmt.executeUpdate();
	}

	public void deleteTerm(String table, ProvisionalEntry provisionalEntry)
			throws SQLException {
		this.executeSQL("DELETE FROM bioportal_" + table + " WHERE localID="
				+ provisionalEntry.getLocalId());
	}

	/**
	 * CREATE TABLE IF NOT EXISTS bioportal_adoption ( localId BIGINT not null
	 * auto_increment unique, term varchar(100) not null, temporaryId
	 * varchar(100) NOT NULL, glossaryType int not null, primary key (localID)
	 * );
	 * 
	 * @param tableName
	 * @param provisionalEntry
	 * @throws SQLException
	 */
	private void storeOTOProvisionalTerm(String tableName,
			ProvisionalEntry provisionalEntry) throws SQLException {
		PreparedStatement preparedStatement = this
				.prepareStatement("INSERT INTO bioportal_"
						+ tableName
						+ " (`temporaryId`, `permanentId`, `superClass`, "
						+ "`submittedby`, `definition`, `ontologyids`, `preferredname`, "
						+ "`synonyms`, `source`, `termType`, `termCategory`, `term`, `glossaryType`, `dataset`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		// preparedStatement.setString(1, OTOProvisionalTerm.getLocalId());
		preparedStatement.setString(1, provisionalEntry.getProvisionalClass().getId());
		preparedStatement.setString(2, provisionalEntry.getProvisionalClass().getPermanentId());
		preparedStatement.setString(3, provisionalEntry.getProvisionalClass().getSubclassOf());
		preparedStatement.setString(4, provisionalEntry.getProvisionalClass().getCreator());
		preparedStatement.setString(5, provisionalEntry.getDefinitionsString());
		preparedStatement.setString(6, provisionalEntry.getOntologiesString());
		preparedStatement.setString(7, provisionalEntry.getProvisionalClass().getLabel());
		preparedStatement.setString(8, provisionalEntry.getSynonymsString());
		preparedStatement.setString(9, provisionalEntry.getSource());
		preparedStatement.setString(10, provisionalEntry.getTermType());
		preparedStatement.setString(11, provisionalEntry.getTermCategory());
		preparedStatement.setString(12, provisionalEntry.getProvisionalClass().getLabel());
		preparedStatement.setInt(13, provisionalEntry.getGlossaryType());
		preparedStatement.setString(14, provisionalEntry.getDataset());
		preparedStatement.executeUpdate();
	}

	private void createTableIfNecessary(String tableName) throws SQLException {
		this.executeSQL("CREATE TABLE IF NOT EXISTS bioportal_" + tableName
				+ "(" + " `localId` varchar(100) NOT NULL, "
				+ " `temporaryId` varchar(100) NOT NULL, "
				+ "  `permanentId` varchar(100) DEFAULT NULL, "
				+ " `superClass` varchar(100) DEFAULT NULL, "
				+ "  `submittedBy` varchar(100) DEFAULT NULL, "
				+ "  `definition` text, "
				+ "  `ontologyIds` varchar(100) DEFAULT NULL, "
				+ "  `preferredName` varchar(100) DEFAULT NULL, "
				+ "  `synonyms` varchar(100) DEFAULT NULL, "
				+ "  `source` varchar(100) DEFAULT NULL, "
				+ "  `termType` varchar(100) DEFAULT NULL, "
				+ "  `termCategory` varchar(100) DEFAULT NULL, "
				+ "  PRIMARY KEY (`localId`))");
	}

	public List<ProvisionalEntry> getFilteredAdoptions(String tableName,
			String where) throws SQLException {
		List<ProvisionalEntry> result = new ArrayList<ProvisionalEntry>();
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ where);
		ResultSet resultSet = preparedStatement.getResultSet();
		while (resultSet.next()) {			
			result.add(this.createProvisionalEntry(resultSet));
		}
		return result;
	}

	public static void main(String[] args) {
		/*ProvisionalEntry provisionalEntry = new ProvisionalEntry();
		provisionalEntry.setTemporaryid("tempId");
		provisionalEntry.setDefinition("def");
		provisionalEntry.setTerm("name");
		provisionalEntry.setSubmittedby("submittedby");
		try {
			OTOProvisionalTermDAO.getInstance().addAwaitingAdoption(
					provisionalEntry);
			List<ProvisionalEntry> all = OTOProvisionalTermDAO.getInstance()
					.getAllPendingAdoptions();
			OTOProvisionalTermDAO.getInstance()
					.storeAdopted(provisionalEntry);
			OTOProvisionalTermDAO.getInstance().deleteAwaitingAdoption(
					provisionalEntry);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public ProvisionalEntry getFirstAwaitingTerm() throws SQLException {
		this.openConnection();
		ProvisionalEntry result = getFirst("awaitingadoption");
		this.closeConnection();
		return result;
	}

	public ProvisionalEntry getFirstAdoptedTerm() throws SQLException {
		this.openConnection();
		ProvisionalEntry result = getFirst("adopted");
		this.closeConnection();
		return result;
	}

	public ProvisionalEntry getFirst(String tableName) throws SQLException {
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ "ORDER BY localId");
		ResultSet resultSet = preparedStatement.getResultSet();
		if (!resultSet.next())
			return null;
		
		return this.createProvisionalEntry(resultSet);
	}

	public void updatePermanentID(ProvisionalEntry term, String permanentID)
			throws SQLException {
		this.openConnection();
		PreparedStatement pstmt = this
				.prepareStatement("UPDATE bioportal_adoption SET permanentId = ? where localID = ?");
		pstmt.setString(1, permanentID);
		pstmt.setString(2, term.getLocalId());
		pstmt.executeUpdate();
	}

	public void updateAwaitingAdoption(ProvisionalEntry provisionalEntry)
			throws SQLException {
		this.openConnection();
		PreparedStatement preparedStatement = this
				.prepareStatement("UPDATE bioportal_adoption SET "
						+ "definition = ?, superClass = ?, synonyms = ?, ontologyIds = ?, source = ? WHERE localId = ?");
		preparedStatement.setString(1, provisionalEntry.getDefinitionsString());
		preparedStatement.setString(2, provisionalEntry.getProvisionalClass().getSubclassOf());
		preparedStatement.setString(3, provisionalEntry.getSynonymsString());
		preparedStatement.setString(4, provisionalEntry.getOntologiesString());
		preparedStatement.setString(5, provisionalEntry.getSource());
		preparedStatement.setString(6, provisionalEntry.getLocalId());
		preparedStatement.executeUpdate();

		this.closeConnection();
	}

	public ProvisionalEntry getAdopted(String localId) throws SQLException {
		this.openConnection();
		ProvisionalEntry result = getTerm("adopted", localId);
		this.closeConnection();
		return result;
	}

	public ProvisionalEntry getAwaitingAdoption(String localId)
			throws SQLException {
		this.openConnection();
		ProvisionalEntry result = getTerm("awaitingadoption", localId);
		this.closeConnection();
		return result;
	}

	public ProvisionalEntry getTerm(String tableName, String localId)
			throws SQLException {
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ "WHERE localId = " + localId);
		ResultSet resultSet = preparedStatement.getResultSet();
		resultSet.next();
		
		return createProvisionalEntry(resultSet);
	}

	private ProvisionalEntry createProvisionalEntry(ResultSet resultSet) throws SQLException {
		ProvisionalEntry provisionalEntry = new ProvisionalEntry(
				resultSet.getString("localId"), 
				resultSet.getString("termType"), 
				resultSet.getString("termCategory"), 
				resultSet.getString("source"),
				resultSet.getInt("glossaryType"));			
		ProvisionalClass provisionalClass = new ProvisionalClass(
				resultSet.getString("term"),
				provisionalEntry.getSynonymsFromString(resultSet.getString("synonyms")), 
				provisionalEntry.getDefinitionsFromString(resultSet.getString("definition")), 
				resultSet.getString("superClass"), 
				resultSet.getString("submittedBy"), 
				"",
				resultSet.getString("permanentId"),
				"",
				provisionalEntry.getOntologiesFromString(resultSet.getString("ontologyIds")),
				resultSet.getString("temporaryId"),
				"");
		provisionalEntry.setProvisionalClass(provisionalClass);
		return provisionalEntry;
	}
}
