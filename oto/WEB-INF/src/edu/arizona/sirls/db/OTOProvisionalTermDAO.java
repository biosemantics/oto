package edu.arizona.sirls.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.arizona.sirls.beans.OTOProvisionalTerm;

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

	public void addAwaitingAdoption(OTOProvisionalTerm OTOProvisionalTerm)
			throws SQLException {
		this.openConnection();
		this.storeOTOProvisionalTerm("adoption", OTOProvisionalTerm);
		this.closeConnection();
	}

	public List<OTOProvisionalTerm> getAllPendingAdoptions()
			throws SQLException {
		this.openConnection();
		List<OTOProvisionalTerm> result = this.getFilteredAdoptions("adoption",
				"WHERE permanentId IS NULL or permanentId = ''");
		this.closeConnection();
		return result;
	}

	public List<OTOProvisionalTerm> getAllStructureAwaitingAdoption()
			throws SQLException {
		this.openConnection();
		List<OTOProvisionalTerm> result = this.getFilteredAdoptions(
				"awaitingAdoption", "WHERE termType='structure'");
		this.closeConnection();
		return result;
	}

	public List<OTOProvisionalTerm> getAdoptedStructureTerms()
			throws SQLException {
		this.openConnection();
		List<OTOProvisionalTerm> result = this.getFilteredAdoptions("adopted",
				"WHERE termType='structure'");
		this.closeConnection();
		return result;
	}

	public List<OTOProvisionalTerm> getAdoptedCharacterTerms()
			throws SQLException {
		this.openConnection();
		List<OTOProvisionalTerm> result = this.getFilteredAdoptions("adopted",
				"WHERE termType='character'");
		this.closeConnection();
		return result;
	}

	public List<OTOProvisionalTerm> getAllCharacterAwaitingAdoption()
			throws SQLException {
		this.openConnection();
		List<OTOProvisionalTerm> result = this.getFilteredAdoptions(
				"awaitingAdoption", "WHERE termType='character'");
		this.closeConnection();
		return result;
	}

	public void storeAdopted(OTOProvisionalTerm OTOProvisionalTerm)
			throws SQLException {
		this.openConnection();

		storeOTOProvisionalTerm("adopted", OTOProvisionalTerm);
		this.closeConnection();
	}

	/**
	 * delete pending adoption from OTO
	 * 
	 * @param OTOProvisionalTerm
	 * @param userID
	 * @throws SQLException
	 */
	public void deleteAwaitingAdoption(OTOProvisionalTerm OTOProvisionalTerm,
			int userID) throws SQLException {
		this.openConnection();
		// log deletion before delete
		this.logDeletion(OTOProvisionalTerm, userID);
		this.deleteTerm("adoption", OTOProvisionalTerm);
		this.closeConnection();
	}

	/**
	 * delete awaiting adoption from main fuction in order to clean up
	 * 
	 * @param OTOProvisionalTerm
	 * @throws SQLException
	 */
	public void deleteAwaitingAdoption(OTOProvisionalTerm OTOProvisionalTerm)
			throws SQLException {
		this.openConnection();
		this.deleteTerm("adoption", OTOProvisionalTerm);
		this.closeConnection();
	}

	/**
	 * log deletion
	 * 
	 * @param term
	 * @throws SQLException
	 */
	private void logDeletion(OTOProvisionalTerm term, int userID)
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

	public void deleteTerm(String table, OTOProvisionalTerm OTOProvisionalTerm)
			throws SQLException {
		this.executeSQL("DELETE FROM bioportal_" + table + " WHERE localID="
				+ OTOProvisionalTerm.getLocalId());
	}

	/**
	 * CREATE TABLE IF NOT EXISTS bioportal_adoption ( localId BIGINT not null
	 * auto_increment unique, term varchar(100) not null, temporaryId
	 * varchar(100) NOT NULL, glossaryType int not null, primary key (localID)
	 * );
	 * 
	 * @param tableName
	 * @param OTOProvisionalTerm
	 * @throws SQLException
	 */
	private void storeOTOProvisionalTerm(String tableName,
			OTOProvisionalTerm OTOProvisionalTerm) throws SQLException {
		PreparedStatement preparedStatement = this
				.prepareStatement("INSERT INTO bioportal_"
						+ tableName
						+ " (`temporaryId`, `permanentId`, `superClass`, "
						+ "`submittedby`, `definition`, `ontologyids`, `preferredname`, "
						+ "`synonyms`, `source`, `termType`, `termCategory`, `term`, `glossaryType`, `dataset`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		// preparedStatement.setString(1, OTOProvisionalTerm.getLocalId());
		preparedStatement.setString(1, OTOProvisionalTerm.getTemporaryid());
		preparedStatement.setString(2, OTOProvisionalTerm.getPermanentid());
		preparedStatement.setString(3, OTOProvisionalTerm.getSuperclass());
		preparedStatement.setString(4, OTOProvisionalTerm.getSubmittedby());
		preparedStatement.setString(5, OTOProvisionalTerm.getDefinition());
		preparedStatement.setString(6, OTOProvisionalTerm.getOntologyids());
		preparedStatement.setString(7, OTOProvisionalTerm.getTerm());
		preparedStatement.setString(8, OTOProvisionalTerm.getSynonyms());
		preparedStatement.setString(9, OTOProvisionalTerm.getSource());
		preparedStatement.setString(10, OTOProvisionalTerm.getTermType());
		preparedStatement.setString(11, OTOProvisionalTerm.getTermCategory());
		preparedStatement.setString(12, OTOProvisionalTerm.getTerm());
		preparedStatement.setInt(13, OTOProvisionalTerm.getGlossaryType());
		preparedStatement.setString(14, OTOProvisionalTerm.getDataset());
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

	public List<OTOProvisionalTerm> getFilteredAdoptions(String tableName,
			String where) throws SQLException {
		List<OTOProvisionalTerm> result = new ArrayList<OTOProvisionalTerm>();
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ where);
		ResultSet resultSet = preparedStatement.getResultSet();
		while (resultSet.next()) {
			result.add(new OTOProvisionalTerm(resultSet.getString("localId"),
					resultSet.getString("term"), resultSet
							.getString("termType"), resultSet
							.getString("termCategory"), resultSet
							.getString("definition"), resultSet
							.getString("superClass"), resultSet
							.getString("synonyms"), resultSet
							.getString("ontologyIds"), resultSet
							.getString("submittedBy"), resultSet
							.getString("temporaryId"), resultSet
							.getString("permanentId"), resultSet
							.getString("source"), resultSet
							.getInt("glossaryType")));
		}
		return result;
	}

	public static void main(String[] args) {
		OTOProvisionalTerm OTOProvisionalTerm = new OTOProvisionalTerm();
		OTOProvisionalTerm.setTemporaryid("tempId");
		OTOProvisionalTerm.setDefinition("def");
		OTOProvisionalTerm.setTerm("name");
		OTOProvisionalTerm.setSubmittedby("submittedby");
		try {
			OTOProvisionalTermDAO.getInstance().addAwaitingAdoption(
					OTOProvisionalTerm);
			List<OTOProvisionalTerm> all = OTOProvisionalTermDAO.getInstance()
					.getAllPendingAdoptions();
			OTOProvisionalTermDAO.getInstance()
					.storeAdopted(OTOProvisionalTerm);
			OTOProvisionalTermDAO.getInstance().deleteAwaitingAdoption(
					OTOProvisionalTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OTOProvisionalTerm getFirstAwaitingTerm() throws SQLException {
		this.openConnection();
		OTOProvisionalTerm result = getFirst("awaitingadoption");
		this.closeConnection();
		return result;
	}

	public OTOProvisionalTerm getFirstAdoptedTerm() throws SQLException {
		this.openConnection();
		OTOProvisionalTerm result = getFirst("adopted");
		this.closeConnection();
		return result;
	}

	public OTOProvisionalTerm getFirst(String tableName) throws SQLException {
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ "ORDER BY localId");
		ResultSet resultSet = preparedStatement.getResultSet();
		if (!resultSet.next())
			return null;
		return new OTOProvisionalTerm(resultSet.getString("localId"),
				resultSet.getString("term"), resultSet.getString("termType"),
				resultSet.getString("termCategory"),
				resultSet.getString("definition"),
				resultSet.getString("superClass"),
				resultSet.getString("synonyms"),
				resultSet.getString("ontologyIds"),
				resultSet.getString("submittedBy"),
				resultSet.getString("temporaryId"),
				resultSet.getString("permanentId"),
				resultSet.getString("source"), resultSet.getInt("glossaryType"));
	}

	public void updatePermanentID(OTOProvisionalTerm term, String permanentID)
			throws SQLException {
		this.openConnection();
		PreparedStatement pstmt = this
				.prepareStatement("UPDATE bioportal_adoption SET permanentId = ? where localID = ?");
		pstmt.setString(1, permanentID);
		pstmt.setString(2, term.getLocalId());
		pstmt.executeUpdate();
	}

	public void updateAwaitingAdoption(OTOProvisionalTerm OTOProvisionalTerm)
			throws SQLException {
		this.openConnection();
		PreparedStatement preparedStatement = this
				.prepareStatement("UPDATE bioportal_adoption SET "
						+ "definition = ?, superClass = ?, synonyms = ?, ontologyIds = ?, source = ? WHERE localId = ?");
		preparedStatement.setString(1, OTOProvisionalTerm.getDefinition());
		preparedStatement.setString(2, OTOProvisionalTerm.getSuperclass());
		preparedStatement.setString(3, OTOProvisionalTerm.getSynonyms());
		preparedStatement.setString(4, OTOProvisionalTerm.getOntologyids());
		preparedStatement.setString(5, OTOProvisionalTerm.getSource());
		preparedStatement.setString(6, OTOProvisionalTerm.getLocalId());
		preparedStatement.executeUpdate();

		this.closeConnection();
	}

	public OTOProvisionalTerm getAdopted(String localId) throws SQLException {
		this.openConnection();
		OTOProvisionalTerm result = getTerm("adopted", localId);
		this.closeConnection();
		return result;
	}

	public OTOProvisionalTerm getAwaitingAdoption(String localId)
			throws SQLException {
		this.openConnection();
		OTOProvisionalTerm result = getTerm("awaitingadoption", localId);
		this.closeConnection();
		return result;
	}

	public OTOProvisionalTerm getTerm(String tableName, String localId)
			throws SQLException {
		PreparedStatement preparedStatement = this
				.executeSQL("SELECT * FROM bioportal_" + tableName + " "
						+ "WHERE localId = " + localId);
		ResultSet resultSet = preparedStatement.getResultSet();
		resultSet.next();
		return new OTOProvisionalTerm(resultSet.getString("localId"),
				resultSet.getString("term"), resultSet.getString("termType"),
				resultSet.getString("termCategory"),
				resultSet.getString("definition"),
				resultSet.getString("superClass"),
				resultSet.getString("synonyms"),
				resultSet.getString("ontologyIds"),
				resultSet.getString("submittedBy"),
				resultSet.getString("temporaryId"),
				resultSet.getString("permanentId"),
				resultSet.getString("source"), resultSet.getInt("glossaryType"));
	}
}
