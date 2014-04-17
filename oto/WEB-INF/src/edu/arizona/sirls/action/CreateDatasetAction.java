package edu.arizona.sirls.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.sirls.beans.SessionDataManager;
import edu.arizona.sirls.beans.User;
import edu.arizona.sirls.db.CharacterDBAccess;
import edu.arizona.sirls.db.UserDataAccess;
import edu.arizona.sirls.form.GeneralForm;
import edu.arizona.sirls.util.Forwardable;

public class CreateDatasetAction  extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(GetUserReportAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			try {
				GeneralForm gform = (GeneralForm) form;
				String responseText = "";
				String value = gform.getValue();
				String[] dss = value.split(";");
				
				int glossaryID = Integer.parseInt(dss[0]);
				String datasetName = dss[1];
				if (!datasetName.equals("")) {
					CharacterDBAccess cdba = new CharacterDBAccess();
					SessionDataManager sessionDataMgr = getSessionManager(request);
					User user = sessionDataMgr.getUser();
					
					//allow merged into an existing dataset
					//boolean success = cdba.mergeDatasets(datasets, new_name, user, glossaryID);
					boolean success = cdba.createDatasetIfNotExist(datasetName, "", user.getUserId(), glossaryID);
					if (success)
						responseText = "successful";
					else 	
						responseText = "failed";
					
					UserDataAccess uds = new UserDataAccess();
					sessionDataMgr.setUser(uds.updateUserRole(user));
				} else {
					responseText = "Dataset name is empty.";
				}
					
				response.setContentType("text/xml");
		    	response.getWriter().write("<response>" + responseText + "</response>");	
		    	
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in creating dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
            	response.getWriter().write("<response>Problem encountered in creating dataset: " 
    				+ exe.getMessage()+ "</response>");
			}			
			return null;
		} else {
			return null;
		}
	}

}
