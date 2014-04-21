package edu.arizona.biosemantics.oto.oto.action;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.ProvisionalEntry;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.OTOProvisionalTermDAO;
import edu.arizona.biosemantics.oto.oto.form.BioportalForm;

public class BioportalSubmitAction extends ParserAction {	
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			final HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (checkSessionValidity(request)) {
			SessionDataManager sessionData = getSessionManager(request);
			User user = sessionData.getUser();
			
			BioPortalClient bioPortalClient = new BioPortalClient(Configuration.getInstance().getBioportalUrl(), user.getBioportalApiKey());
			bioPortalClient.open();
			
			final BioportalForm bform = (BioportalForm) form;
			
			List<String> definitions = new LinkedList<String>();
			definitions.add(bform.getDefinition());
			List<String> ontologies = new LinkedList<String>();
			ontologies.add(bform.getOntology());
			
			ProvisionalClass provisionalClass = new ProvisionalClass();
			provisionalClass.setLabel(bform.getTermName());
			provisionalClass.setCreator(user.getBioportalUserId());
			provisionalClass.setDefinition(definitions);
			provisionalClass.setSubclassOf(bform.getSuperClassID());
			provisionalClass.setOntology(ontologies);
			//? how is the syns string represented in the UI? Comma-seperated?
			//bform.getSyns();
			//bform.getTmpID();
			
			ProvisionalEntry provisionalEntry = new ProvisionalEntry(bform.getLocalID(), "", bform.getCategory(), bform.getSource(), 
					Integer.parseInt(bform.getGlossaryType()), bform.getDataset(), provisionalClass);

			// do submission and update DB
			String action = bform.getAction();
			if (action.equals("submit")) {
				Future<ProvisionalClass> future = bioPortalClient.postProvisionalClass(provisionalClass);
				try {
					ProvisionalClass result = future.get();
					provisionalEntry.setProvisionalClass(result);
					if(result.getId() == null || result.getId().isEmpty()) {
						return errorPost(request, bform, mapping);
					}
					OTOProvisionalTermDAO.getInstance().addAwaitingAdoption(provisionalEntry);
				} catch(Exception e) {
					return errorPost(request, bform, mapping);
				}
			} else if (action.equals("update")) {
				Future<ProvisionalClass> future = bioPortalClient.patchProvisionalClass(provisionalClass);
				try {
					ProvisionalClass result = future.get();
					provisionalEntry.setProvisionalClass(result);
					OTOProvisionalTermDAO.getInstance().updateAwaitingAdoption(provisionalEntry);
				} catch(Exception e) {
					return errorPatch(request, bform, mapping);
				}
			} else if (bform.getAction().equals("delete")) {
				//find id via localId
				Future<ProvisionalClass> future = bioPortalClient.deleteProvisionalClass(provisionalClass.getId());
				try {
					ProvisionalClass result = future.get();
					provisionalEntry.setProvisionalClass(result);
					OTOProvisionalTermDAO.getInstance().deleteAwaitingAdoption(provisionalEntry, user.getUserId());
				} catch(Exception e) {
					return errorDelete(request, bform, mapping);
				}
			}
			bioPortalClient.close();

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

	private ActionForward errorDelete(HttpServletRequest request,
			BioportalForm bform, ActionMapping mapping) {
		request.setAttribute(
				"message",
				"Error in deleting term from bioportal. Please try again later!");
		return mapping.findForward(Forwardable.ERROR);
	}

	private ActionForward errorPatch(HttpServletRequest request,
			BioportalForm bform, ActionMapping mapping) {
		request.setAttribute(
				"message",
				"Error in updating term from bioportal. Please try again later!");
		return mapping.findForward(Forwardable.ERROR);
	}

	private ActionForward errorPost(HttpServletRequest request, BioportalForm bform, ActionMapping mapping) {
		request.setAttribute(
				"message",
				"Error in submitting term on ontolgoy "
						+ bform.getOntology()
						+ ". Please try again later!");
		return mapping.findForward(Forwardable.ERROR);
	}

}
