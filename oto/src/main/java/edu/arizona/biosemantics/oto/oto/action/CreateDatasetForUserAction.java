package edu.arizona.biosemantics.oto.oto.action;

/**
 * this action is creating a dataset in manage datasets page
 */
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class CreateDatasetForUserAction extends ParserAction {
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

				// get dataset prefix
				String prefix = dss[0];

				// get glossary ID
				int glossaryID = Integer.parseInt(dss[1]);// glossary ID

				// get user name
				SessionDataManager sessionDataMgr = getSessionManager(request);
				User user = sessionDataMgr.getUser();
				String username = user.getFirstName() + "_"
						+ user.getLastName();
				username = username.toLowerCase().replaceAll("^(a-z_)", "_");

				// get timestamp
				SimpleDateFormat sdfDate = new SimpleDateFormat(
						"yyyyMMddHHmmss");
				Date now = new Date();
				String strDate = sdfDate.format(now);

				// prepare dataset name
				String datasetName = prefix + "_" + username + "_" + strDate;

				if (!datasetName.equals("")) {
					CharacterDBAccess cdba = new CharacterDBAccess();
					boolean success = cdba.createDatasetIfNotExist(datasetName,
							"", user.getUserId(), glossaryID);
					if (success)
						responseText = datasetName;
					else
						responseText = "error";

					// update user's role as owner
					UserDataAccess uds = new UserDataAccess();
					sessionDataMgr.setUser(uds.updateUserRole(user));
				} else {
					responseText = "error: Dataset name is empty.";
				}

				response.setContentType("text");
				response.getWriter().write(responseText);

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in creating dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>error: Problem encountered in creating dataset: "
								+ exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
