package edu.arizona.biosemantics.oto.oto.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.db.ReportingDBAccess;
/**
 * This class's object will hold all the session related data.
 * @author Partha
 *
 */
public class SessionDataManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7986096954687505323L;
	
	private static final Logger LOGGER = Logger.getLogger(SessionDataManager.class);

	private ArrayList<String> groupNames;
	
	private ArrayList<CategoryBean> decisions;
	
	private ArrayList<String> datasets;
	
	private HashMap<String, ArrayList<DecisionBean>> userDecisions;
	
	private String dataset;
	
	private User user;
	
	private HashMap<String, CharacterGroupBean> groups;
	
	private HashMap<String, ArrayList<SpecificReportBean>> termReports;
	
	private HashMap<String, ArrayList<Term>> decisionTerms; //by f.huang: the categorized terms
	
	private ArrayList<String> tagsList;
	
	public ArrayList<String> getTagsList() {
		return tagsList;
	}
	
	public void setTagsList(ArrayList<String> tagsList) {
		this.tagsList = tagsList;
	}
		
	/**
	 * @return the decisionTerms
	 */
	public HashMap<String, ArrayList<Term>> getDecisionTerms(){
		return decisionTerms;
	}
	
	/**
	 * @param decisionTerms the decisionTerms to set
	 */
	public void setDecisionTerms(HashMap<String, ArrayList<Term>> decisionTerms){
		this.decisionTerms = decisionTerms;
		
	}
	
	/**
	 * @return the termReports
	 */
	public HashMap<String, ArrayList<SpecificReportBean>> getTermReports() {
		return termReports;
	}

	/**
	 * @param termReports the termReports to set
	 */
	public void setTermReports(
			HashMap<String, ArrayList<SpecificReportBean>> termReports) {
		this.termReports = termReports;
	}

	/**
	 * @return the userDecisions
	 * @throws IOException 
	 */
	public HashMap<String, ArrayList<DecisionBean>> getUserDecisions() throws IOException {
		if (userDecisions == null) {
			refetchUserDecisions();
		}
		return userDecisions;
	}

	/**
	 * @param userDecisions the userDecisions to set
	 */
	public void setUserDecisions(HashMap<String, ArrayList<DecisionBean>> userDecisions) {
		this.userDecisions = userDecisions;
	}
	
	public SessionDataManager(User user) {
		    this.user = user;
		try {
			if (datasets == null) {
				datasets = getDataSets();
			}
			
			if (groups == null) {
				groups = new HashMap<String, CharacterGroupBean>();
			}

		} catch(Exception exe) {
			exe.printStackTrace();
			LOGGER.error("Couldn't init SessionDataManager", exe);
		}
	}

	private ArrayList<String> getDataSets() throws Exception {
		CharacterDBAccess cdba = new CharacterDBAccess();
		ArrayList<String> datasets = null;
		try {
			datasets = cdba.getDataSets();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datasets;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the groupNames
	 * @throws Exception 
	 */
	public ArrayList<String> getGroupNames() throws Exception {
		if (groupNames == null) {
			groupNames = getGroups(dataset, user.getUserId(), false);
		}
		return groupNames;
	}

	private ArrayList<String> getGroups(String dataset, int userid, boolean flag)
			throws Exception {
		if (dataset != null) {
			CharacterDBAccess cdba = new CharacterDBAccess();
			ArrayList<String> savedGroups = null;
			try {
				savedGroups = cdba.getAllGroups(dataset, userid);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("unable to load savedGroups", e);
			}
			return savedGroups;
		}
		return null;
	}

	/**
	 * @param groupNames the groupNames to set
	 */
	public void setGroupNames(ArrayList<String> groupNames) {
		this.groupNames = groupNames;
	}

	/**
	 * @return the decisions
	 */
	public ArrayList<CategoryBean> getDecisions() {
		return decisions;
	}

	/**
	 * @param decisions the decisions to set
	 */
	public void setDecisions(ArrayList<CategoryBean> decisions) {
		this.decisions = decisions;
	}

	/**
	 * @return the datasets
	 */
	public ArrayList<String> getDatasets() {
		return datasets;
	}

	/**
	 * @param datasets the datasets to set
	 */
	public void setDatasets(ArrayList<String> datasets) {
		this.datasets = datasets;
	}

	/**
	 * @return the dataset
	 */
	public String getDataset() {
		return dataset;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	/**
	 * @return the groups
	 */
	public HashMap<String, CharacterGroupBean> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(HashMap<String, CharacterGroupBean> groups) {
		this.groups = groups;
	}
	
	public void refetchDecisions() throws Exception {
		decisions = getDecisions(dataset);
	}
	
	private ArrayList<CategoryBean> getDecisions(String dataPrefix)
			throws Exception {
		CharacterDBAccess cdba = new CharacterDBAccess();
		ArrayList<CategoryBean> decisions = null;
		try {
			decisions = cdba.getAllCategory(dataPrefix);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("unable to load decisions", e);
		}
		return decisions;
	}


	public void refetchUserDecisions() throws IOException{
		if (userDecisions == null) {
			userDecisions = new HashMap<String, ArrayList<DecisionBean>>();
		}
		try {
			for (String groupDataset : datasets) {
				ArrayList<DecisionBean> userGroupReport = getUserSpecificReport(user, groupDataset);
				if (userGroupReport != null && userGroupReport.size() != 0) {
					userDecisions.put(groupDataset, userGroupReport);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<DecisionBean> getUserSpecificReport(User user,
			String dataset) throws SQLException, IOException {
		ReportingDBAccess rdba = new ReportingDBAccess();
		return rdba.getUserSpecificReport(user, dataset);
	}
	
}
