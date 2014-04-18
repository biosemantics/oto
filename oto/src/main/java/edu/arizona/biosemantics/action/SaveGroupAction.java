package edu.arizona.biosemantics.action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.beans.DecisionHolder;
import edu.arizona.biosemantics.beans.SessionDataManager;
import edu.arizona.biosemantics.beans.User;
import edu.arizona.biosemantics.db.CharacterDBAccess;
import edu.arizona.biosemantics.form.GeneralForm;
import edu.arizona.biosemantics.util.Forwardable;
import edu.arizona.biosemantics.util.Utilities;


/**
 * This class saves the group information along with the decision to the
 * database
 * 
 * @author Partha
 * 
 */
public class SaveGroupAction extends ParserAction {

	private static final Logger LOGGER = Logger
			.getLogger(SaveGroupAction.class);
	/* Create the strategy hook */
	//private GroupSelectionStrategy gStrategy = new GroupSelectionStrategy();

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			String requestXML = gform.getValue();
			if (requestXML == null) {
				return mapping.findForward(Forwardable.RELOAD);
			}
			
			//testing
			//requestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><decisions><category><category_name>coloration</category_name><new_terms><term>lavender</term><term>pink</term><term>purple</term></new_terms></category><category><category_name>count</category_name><changed_terms><term>tan</term></changed_terms></category><relation><mainTerm>denticulate</mainTerm><addTerm>dentate</addTerm><action>add</action><type>syn</type></relation><removed_decisions><term>glabrous</term></removed_decisions></decisions>";
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataPrefix = sessionDataMgr.getDataset();
			Utilities utilities = new Utilities();
			User user = sessionDataMgr.getUser();
			try {
				DecisionHolder dh = utilities.parseGroupingXML(requestXML);
				CharacterDBAccess cdba = new CharacterDBAccess();
				
				/*//check if this action is a resend
				boolean isResend = false;
				if (categories.size() > 0) {
					isResend = cdba.isResendingGroup(dataPrefix, user, categories.get(0), null);
				} else if (trbList.size() > 0) {
					isResend = cdba.isResendingGroup(dataPrefix, user, null, trbList.get(0));
				}*/
				
				if (dh.getNew_categories().size() > 0) {
					cdba.addNewCategory(dh.getNew_categories(), dataPrefix);
				}
				
				if (dh.getReviewed_terms().size() > 0) {
					cdba.saveReviewedTerms(dh.getReviewed_terms(), dataPrefix, user);
				}
				
				//save categorizing decisions
				cdba.saveCategorizingDecisions(dh.getRegular_categories(), dataPrefix, user);

				request.setAttribute("message", "Changes have been successfully saved!");
			} catch (Exception exe) {
				exe.printStackTrace();
				LOGGER.error("Error in saving the group", exe);
				request.setAttribute(Forwardable.ERROR, exe.getCause());
				return mapping.findForward(Forwardable.ERROR);
			}
			return mapping.findForward(Forwardable.RELOAD);
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}

	}

	public static void main(String[] args) throws Exception {

	}

}
