package edu.arizona.biosemantics.oto.oto.action;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.ReportingDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;
/**
 * get user report action
 * @author Fengqiong
 *
 */
public class GetUserReportAction extends ParserAction{
	private static final Logger LOGGER = Logger
	.getLogger(GetUserReportAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, SQLException {
		if (checkSessionValidity(request)) {
			try {
				GeneralForm gform = (GeneralForm) form;
				SessionDataManager sessionDataMgr = getSessionManager(request);
				User user = sessionDataMgr.getUser();
				
				String responseText = "";
				String value = gform.getValue();
				String[] info = value.split("::");
				ReportingDBAccess rdba = new ReportingDBAccess();
				if (info.length == 2) {
					String dataset = info[0];
					String type = info[1];
					if (type.equals("1")) {
						responseText = rdba.getUserGroupingReport(user, dataset);
					} else if (type.equals("2")) {
						responseText = rdba.getHierarchyTreeReport(user, dataset);
					} else if (type.equals("3")){
						responseText = rdba.getOrderReport(user, dataset);
					} else {
						responseText = "<label>There is an error occurring in this page.</label>";
					}
				} else if (value.equals("USERS_LOG")) {//getting user's log
					responseText = rdba.getUserLog(user);
				}
				response.setContentType("text/xml");
		    	response.getWriter().write(responseText);
		    	
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in getting the report", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
            	response.getWriter().write("<server-response>Problem encountered in getting the report : " 
    				+ exe.getMessage()+ "</server-response>");
			}
			return null;
		} else {
			return null;
		}
	}
}
