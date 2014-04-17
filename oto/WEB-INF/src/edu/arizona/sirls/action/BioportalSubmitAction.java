package edu.arizona.sirls.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import edu.arizona.sirls.beans.OTOProvisionalTerm;
import edu.arizona.sirls.bioportal.TermsToOntologiesClient;
import edu.arizona.sirls.beans.SessionDataManager;
import edu.arizona.sirls.beans.User;
import edu.arizona.sirls.form.BioportalForm;
import edu.arizona.sirls.util.Forwardable;

public class BioportalSubmitAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			SessionDataManager sessionData = getSessionManager(request);
			User user = sessionData.getUser();
			TermsToOntologiesClient bioportalClient = new TermsToOntologiesClient(
					user.getBioportalUserId(), user.getBioportalApiKey());
			// create proisional term
			BioportalForm bform = (BioportalForm) form;
			OTOProvisionalTerm submissionTerm = new OTOProvisionalTerm(
					bform.getLocalID(), bform.getTermName(), "",
					bform.getCategory(), bform.getDefinition(),
					bform.getSuperClassID(), bform.getSyns(),
					bform.getOntology(), Integer.toString(user.getUserId()),
					bform.getTmpID(), "", bform.getSource(),
					Integer.parseInt(bform.getGlossaryType()));
			submissionTerm.setDataset(bform.getDataset());

			// do submission and updae DB
			String action = bform.getAction();
			if (action.equals("submit")) {
				String tmpID = bioportalClient.sendTerm(submissionTerm);
				if (tmpID == null) {
					request.setAttribute(
							"message",
							"Error in submitting term on ontolgoy "
									+ bform.getOntology()
									+ ". Please try again later!");
					return mapping.findForward(Forwardable.ERROR);
				}
			} else if (action.equals("update")) {
				if (!bioportalClient.updateTerm(submissionTerm)) {
					request.setAttribute(
							"message",
							"Error in updating term from bioportal. Please try again later!");
					return mapping.findForward(Forwardable.ERROR);
				}
			} else if (bform.getAction().equals("delete")) {
				if (!bioportalClient.deleteTerm(submissionTerm,
						user.getUserId())) {
					request.setAttribute(
							"message",
							"Error in deleting term from bioportal. Please try again later!");
					return mapping.findForward(Forwardable.ERROR);
				}
			}

			// determine forward string
			String from = bform.getFrom();
			String forwardString = "";
			//HttpSession session = request.getSession();
			if (from.equals("term")) {// from term
				forwardString = Forwardable.RELOAD;
				//session.setAttribute("term", bform.getTermName());
				request.setAttribute("term", bform.getTermName());
			} else { // from submissions
				String show = "my";// default show my submissions
				if (request.getParameter("show") != null) {
					show = request.getParameter("show").toString();
				}
				//session.setAttribute("show", show);
				request.setAttribute("show", show);
				forwardString = Forwardable.SUCCESS;
			}

			return mapping.findForward(forwardString);
		} else {
			return mapping.findForward(Forwardable.HOME);
		}
	}

}
