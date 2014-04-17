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

public class DeleteDatasetAction  extends ParserAction {
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
				
				if (!value.equals("")) {
					SessionDataManager sessionData = getSessionManager(request);
					int userId = sessionData.getUser().getUserId();
					new CharacterDBAccess().deleteDataset(value, userId, false, "");
					
					//clear session if current dataset is this one
					SessionDataManager sessionDataMgr = getSessionManager(request);
					String currentDS = sessionDataMgr.getDataset();
					if (currentDS != null && currentDS.equals(value)) {
						sessionDataMgr.setDataset("");
					}
					
					User user = sessionDataMgr.getUser();
					UserDataAccess uds = new UserDataAccess();
					sessionDataMgr.setUser(uds.updateUserRole(user));

					
					responseText = "Dataset '" + value + "' has been deleted successfully!";
				} else {
					responseText = "Error in deleting dataset '" + value + "', please try again later!";
				}
								
				response.setContentType("text/xml");
		    	response.getWriter().write("<response>" + responseText + "</response>");
		    	
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in deleting dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
            	response.getWriter().write("<response>Problem encountered in getting the report : " 
    				+ exe.getMessage()+ "</response>");
			}			
			return null;
		} else {
			return null;
		}
	}

}
