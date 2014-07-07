package edu.arizona.biosemantics.oto.oto.db;
/**
 * @author Partha Pratim Sanyal
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.oto.beans.User;

public class GroupStrategyDBAccess extends DatabaseAccess {
	
	public GroupStrategyDBAccess() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final Logger LOGGER = Logger.getLogger(GroupStrategyDBAccess.class);
	
	/**
	 * 
	 * @param user
	 * @param dataset
	 * @throws Exception
	 */
	public void setUserDatasetStatus(User user, String dataset) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "insert into web_user_decisions values(?,?,?)";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			pstmt.setString(2, dataset);
			pstmt.setString(3, "Y");
			pstmt.execute();
			
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to insert into web_user_decisions" , exe);
			throw exe;
		} finally {
			closeConnection(pstmt, conn);
		}
	}
	
	/**
	 * This method generates the saved terms list as a sql filter
	 * @param dataset
	 * @param user
	 * @param groupid
	 * @return string: where term not in ('term1', 'term2', ...)
	 */
	public String getSavedTermsFilter(User user, String dataset, int groupid) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int userid = user.getUserId();
		
		//get user saved terms list string filter
		String filter = "";
		
		String sql = "select distinct term from " + dataset 
			+ "_user_terms_decisions where userid = ? and groupid = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			pstmt.setInt(2, groupid);
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				if (filter.equals("")) {
					filter = " where term not in ('" + rset.getString("term") + "', ";
				} else {
					filter += "'" + rset.getString("term") + "', ";
				}
			}
			
			if (!filter.equals("")) {
				//delete the last "'" and add ")" 
				filter = filter.substring(0, filter.length() - 2) + ")";
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to get the available terms", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return filter;
	}	
	
	/**
	 * This method returns if there are terms to be categorize in specific group
	 * @param dataset
	 * @param user
	 * @param groupid
	 * @return true or false
	 */
	public boolean hasTermsLeft(User user, String dataset, int groupid) throws Exception {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		//get user saved terms list string filter
		String filter = getSavedTermsFilter(user, dataset, groupid);
		
		String sql = "select term from ( "
				+ "select term from " + dataset + "_web_grouped_terms where groupid = ? " 
				+ "union (select cooccurTerm as term from " 
				+ dataset + "_web_grouped_terms where groupid = ?) ) as a " 
				+ filter;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, groupid);
			pstmt.setInt(2, groupid);
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				returnValue = true; //has record means term saved
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to get the available terms", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnValue;
	}	
	
	/**
	 * This method returns if the term has been categorized by user
	 * @param dataset
	 * @param user
	 * @param groupid
	 * @param term
	 * @return true or false
	 * 
	 */
	public boolean isTermSaved(User user, String dataset, int groupid, String termName) throws Exception {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int userid = user.getUserId();
		String sql = "select term from " + dataset + "_web_user_grouped_terms where userid = ? and groupId = ? and term = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userid);
			pstmt.setInt(2, groupid);
			pstmt.setString(3, termName);
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				returnValue = true; //has record means term saved
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to get the saved terms", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public boolean getUserDatasetStatus (User user, String dataset) throws Exception {
		
		boolean returnValue = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select status from web_user_decisions where userid = ? and dataset = ?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			pstmt.setString(2, dataset);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				String status = rset.getString("status");
				if(status != null && status.equals("Y")) {
					returnValue = true;
				}
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to get the dataset status from the user decision table", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		
		return returnValue;
	}
	
	/**
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	
	public boolean isThereAnUnsavedTerm(User user, String dataset) throws Exception {
		boolean returnValue = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select distinct groupid from " + dataset + "_web_grouped_terms where (term not in " 
			+ "(select term from " + dataset + "_web_user_grouped_terms where userid = ?)) " 
			+ "or (cooccurTerm not in (select term from " + dataset + "_web_user_grouped_terms" 
			+ " where userid = ?))";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			pstmt.setInt(2, user.getUserId());
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				returnValue = true;
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to check the static groups", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnValue;
	}
	
	
	/**
	 * 
	 * @param user
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public boolean isThereAnUnsavedGroup(User user, String dataset) throws Exception {
		boolean returnValue = true;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select count(distinct groupid) as _count from "+dataset+"_web_grouped_terms where groupid not in " +
				"(select distinct groupid from "+dataset+"_web_user_grouped_terms where userid=?)";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user.getUserId());
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				int count = rset.getInt("_count");
				if(count == 0) {
					returnValue = false;
				}
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Unable to check the static groups", exe);
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param dataset
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public boolean isThisGroupNewlyMade(String dataset, int groupId) throws Exception {
		boolean returnValue = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "select count(*) as count_ from "+dataset+"_web_grouped_terms where groupid = ?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, groupId);
			rset = pstmt.executeQuery();
			if(rset.next()){
				int count = rset.getInt("count_");
				if (count > 0) {
					returnValue = true;
				}
			}
		} catch(Exception exe){
			exe.printStackTrace();
			LOGGER.error("Couldn't check whether the group exists in the static table");
			throw exe;
		} finally {
			closeConnection(pstmt, rset, conn);
		}
		
		return returnValue;
	}
}
