package edu.arizona.biosemantics.oto.oto.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class MergeDatasetsAction extends ParserAction {
	private static final Logger LOGGER = Logger
			.getLogger(GetUserReportAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			try {
				SessionDataManager sessionDataMgr = getSessionManager(request);
				GeneralForm gform = (GeneralForm) form;
				String responseText = "";
				String value = gform.getValue();
				String[] dss = value.split(";");

				String mergeType = dss[0];
				boolean isSystemMerge = mergeType.equals("System") ? true
						: false;
				int glossaryID = Integer.parseInt(dss[1]);
				String new_name = dss[2];

				boolean needsToClearSelectedDataset = false;
				String currentDS = sessionDataMgr.getDataset();
				boolean hasCurrentDS = false;
				if (currentDS != null && !currentDS.equals("")) {
					hasCurrentDS = true;
				}

				if (!new_name.equals("")) {
					ArrayList<String> datasets = new ArrayList<String>();
					for (int i = 3; i < dss.length; i++) {
						datasets.add(dss[i]);
						if (!needsToClearSelectedDataset && hasCurrentDS
								&& currentDS.equals(dss[i])) {
							needsToClearSelectedDataset = true;
						}
					}

					// CharacterDBAccess cdba = new CharacterDBAccess();
					User user = sessionDataMgr.getUser();

					HttpSession session = request.getSession();
					session.setAttribute("mergeStatus", "processing");
					boolean success = CategorizationDBAccess.getInstance()
							.mergeDatasets(datasets, new_name, user,
									glossaryID, isSystemMerge);
					// cdba.mergeDatasets(datasets, new_name,
					// user, glossaryID, isSystemMerge);

					if (success) {
						responseText = "success";
						if (needsToClearSelectedDataset) {
							sessionDataMgr.setDataset("");
						}
					} else {
						responseText = "Merge datasets failed. Please try again later. ";
					}
					session.setAttribute("mergeStatus", responseText);

					UserDataAccess uds = new UserDataAccess();
					sessionDataMgr.setUser(uds.updateUserRole(user));
				} else {
					responseText = "Merged dataset name is empty.";
				}

				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>" + responseText + "</response>");

			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in merging dataset", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				response.setContentType("text/xml");
				response.getWriter().write(
						"<response>Problem encountered in merging dataset: "
								+ exe.getMessage() + "</response>");
			}
			return null;
		} else {
			return null;
		}
	}

}
