package edu.arizona.biosemantics.oto.steps.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import edu.arizona.biosemantics.oto.common.ontologylookup.search.OntologyLookupClient;
import edu.arizona.biosemantics.oto.common.ontologylookup.search.data.SimpleEntity;
import edu.arizona.biosemantics.oto.steps.server.beans.TermForTreePopulation;
import edu.arizona.biosemantics.oto.steps.server.utilities.Utilities;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.Structure;
import edu.arizona.biosemantics.oto.steps.shared.beans.hierarchy.StructureNodeData;

public class HierarchyDAO extends AbstractDAO {
	public static HierarchyDAO instance;

	public static HierarchyDAO getInstance() throws Exception {
		if (instance == null) {
			instance = new HierarchyDAO();
		}

		return instance;
	}

	public HierarchyDAO() throws Exception {
		super();
	}

	/**
	 * recursively get node and its children
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	private StructureNodeData getNode(int id, int uploadID) throws SQLException {
		StructureNodeData node = new StructureNodeData();
		Connection conn = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rset = null, rset2 = null;
		try {
			conn = getConnection();
			String sql = "select a.termID, a.pID, b.term from "
					+ "(select termID, pID from trees where id = ?) a  "
					+ "left join structures b  " + "on a.termID = b.ID";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				node.setTermID(rset.getString("termID"));
				node.setTermName(rset.getString("term"));
				node.setpID(rset.getString("pID"));

				ArrayList<StructureNodeData> children = new ArrayList<StructureNodeData>();
				// get children
				sql = "select ID from trees where uploadID = ? and pID = ?";
				pstmt2 = conn.prepareStatement(sql);
				pstmt2.setInt(1, uploadID);
				pstmt2.setInt(2, id);
				rset2 = pstmt2.executeQuery();
				while (rset2.next()) {
					children.add(getNode(rset2.getInt(1), uploadID));
				}
				node.setChildren(children);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			closeConnection(conn);
		}
		return node;
	}

	/**
	 * recursively save node and its children
	 * 
	 * @param uploadId
	 * @param conn
	 * @param nodeData
	 * @throws SQLException
	 */
	public void saveNode(String uploadID, Connection conn,
			StructureNodeData nodeData, int pID) throws SQLException {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			int generatedID = 0;
			stmt = conn.createStatement();
			String sql = "insert into trees (uploadId, termID, pID) values "
					+ "(" + uploadID + ", " + nodeData.getTermID() + ", "
					+ Integer.toString(pID) + ")";
			stmt.executeUpdate(sql);
			sql = "SELECT LAST_INSERT_ID()";
			rset = stmt.executeQuery(sql);
			if (rset.next()) {
				generatedID = rset.getInt(1);

				if (nodeData.getChildren() != null
						&& nodeData.getChildren().size() > 0) {
					for (StructureNodeData child : nodeData.getChildren()) {
						saveNode(uploadID, conn, child, generatedID);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(stmt);
		}
	}

	/**
	 * use add structure to the upload
	 * 
	 * @param uploadID
	 * @param termName
	 * @return
	 * @throws SQLException
	 */
	public Structure addStructure(String uploadID, String termName)
			throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		Structure structure = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			// delete all records of existing tree
			String sql = "insert into structures (uploadID, term, userCreated) "
					+ "values (" + uploadID + ", '" + termName + "', true)";
			stmt.executeUpdate(sql);

			sql = "SELECT LAST_INSERT_ID()";
			rset = stmt.executeQuery(sql);
			if (rset.next()) {
				structure = new Structure(Integer.toString(rset.getInt(1)),
						"0", termName);
			}

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			throw e;
		} finally {
			close(rset);
			close(stmt);
			closeConnection(conn);
		}

		return structure;
	}

	/**
	 * save the entire tree
	 * 
	 * @param uploadID
	 * @param nodeDataList
	 * @throws SQLException
	 */
	public void saveTree(String uploadID,
			ArrayList<StructureNodeData> nodeDataList) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			// delete all records of existing tree
			String sql = "delete from trees where uploadID = " + uploadID;
			stmt.executeUpdate(sql);

			// save node list
			for (StructureNodeData nodeData : nodeDataList) {
				saveNode(uploadID, conn, nodeData, 0);
			}

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();

			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException exe) {
					exe.printStackTrace();
					throw exe;
				}
			}
			throw e;
		} finally {
			close(rset);
			close(stmt);
			closeConnection(conn);
		}
	}

	/**
	 * get top level nodes list of the tree
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<StructureNodeData> getNodeList(int uploadID)
			throws SQLException {
		ArrayList<StructureNodeData> nodes = new ArrayList<StructureNodeData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select ID from trees where uploadID = ? and (pID is null or pId = 0)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				nodes.add(getNode(rset.getInt("ID"), uploadID));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			closeConnection(conn);
		}
		return nodes;
	}

	/**
	 * get the structures list for the left side of hierarchy page
	 * 
	 * only display those terms not in the tree yet
	 * 
	 * @param uploadID
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Structure> getStructuresList(int uploadID)
			throws SQLException {
		ArrayList<Structure> structures = new ArrayList<Structure>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection();
			String sql = "select ID, term from structures "
					+ "where uploadID = ? and ID not in "
					+ "(select distinct termID from trees where uploadID = ?) "
					+ "order by term";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, uploadID);
			pstmt.setInt(2, uploadID);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				structures.add(new Structure(rset.getString("ID"), "0", rset
						.getString("term")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(pstmt);
			closeConnection(conn);
		}
		return structures;
	}

	/**
	 * query terms with permanentID in its ontology and create hierarchical tree
	 * with their part_of relations
	 * 
	 * @param uploadID
	 * @param glossaryType
	 * @throws SQLException
	 * @throws IOException
	 */
	public void prepopulateTree(String uploadID, int glossaryType)
			throws SQLException, IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		OntologyLookupClient olClient = null;
		String ontologyName = Utilities
				.getOntologyNameByGlossaryType(glossaryType);
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			conn.setAutoCommit(false);

			// empty existing tree
			String sql = "delete from trees where uploadID = " + uploadID;
			stmt.executeUpdate(sql);

			// get candidate structures which has permanentID in ontology
			ArrayList<String> termsToLookup = new ArrayList<String>();
			HashMap<String, TermForTreePopulation> termsDetailMap = new HashMap<String, TermForTreePopulation>();
			sql = "select a.ID, a.term, b.permanentID from "
					+ "(select ID, term from structures where uploadID = "
					+ uploadID + ") a " + "left join "
					+ "(select term, permanentID, 1 as matched "
					+ "from ontology_matches where ontologyID = '"
					+ ontologyName.toUpperCase() + "') b  on a.term = b.term "
					+ "where matched is not null";
			rset = stmt.executeQuery(sql);
			while (rset.next()) {
				String structure = rset.getString("term");
				termsToLookup.add(structure);
				termsDetailMap.put(structure,
						new TermForTreePopulation(rset.getString("ID"),
								structure, rset.getString("permanentID")));
			}

			// create ontology lookup client
			if (termsToLookup.size() > 0) {
				// create ontology lookup client
				ClassLoader loader = Thread.currentThread()
						.getContextClassLoader();
				Properties properties = new Properties();
				properties
						.load(loader.getResourceAsStream("config.properties"));
				String ontologyDir = properties.getProperty("ontology_dir");
				String dictDir = properties.getProperty("dict_dir");

				olClient = new OntologyLookupClient(ontologyName, ontologyDir,
						dictDir);
			}

			// do ontology query until no structure left in the array
			while (termsToLookup.size() > 0) {
				String termName = termsToLookup.get(0);
				// remove it from the list
				termsToLookup.remove(0);

				if (termsDetailMap.get(termName) != null) {
					TermForTreePopulation term = termsDetailMap.get(termName);
					/**
					 * insert structure into trees as a top level node and set
					 * its treeID
					 */
					sql = "insert into trees (uploadID, termID, pID) values ("
							+ uploadID + ", " + term.getTermID() + ", 0)";
					stmt.executeUpdate(sql);

					sql = "SELECT LAST_INSERT_ID()";
					rset = stmt.executeQuery(sql);
					if (rset.next()) {
						term.setTreeID(Integer.toString(rset.getInt(1)));
					}

					// get its parent chain: e.g. petal, corolla, perianth,
					// flower in PO
					ArrayList<SimpleEntity> parentChain = olClient
							.searchParentChain(termsDetailMap.get(termName)
									.getPermanentID());
					TermForTreePopulation lastChildTerm = term;
					for (int i = 0; i < parentChain.size(); i++) {
						String parentTermName = parentChain.get(i).getLabel();

						// check if parent exist in structures
						if (termsToLookup.indexOf(parentTermName) >= 0) {
							if (termsDetailMap.get(parentTermName) == null) {
								termsToLookup.remove(termsToLookup
										.indexOf(parentTermName));
								continue;
							}

							TermForTreePopulation parentTerm = termsDetailMap
									.get(parentTermName);
							// step 1: insert parentTerm as top level node
							sql = "insert into trees (uploadID, termID, pID) values ("
									+ uploadID
									+ ", "
									+ parentTerm.getTermID()
									+ ", 0)";
							stmt.executeUpdate(sql);

							// step 2: get treeID of parentTerm
							sql = "SELECT LAST_INSERT_ID()";
							rset = stmt.executeQuery(sql);
							if (rset.next()) {
								parentTerm.setTreeID(Integer.toString(rset
										.getInt(1)));
							}

							// update last child term to be child of parent node
							sql = "update trees set pID = "
									+ parentTerm.getTreeID() + " where ID = "
									+ lastChildTerm.getTreeID();
							stmt.executeUpdate(sql);

							// remove structures this parent from structures
							termsToLookup.remove(termsToLookup
									.indexOf(parentTermName));

							// set lastValidChild to be this parent
							lastChildTerm = parentTerm;
						}
					}
				}

			}

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(rset);
			close(stmt);
			closeConnection(conn);
		}
	}

}
