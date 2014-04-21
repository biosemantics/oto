package edu.arizona.biosemantics.oto.oto.action;

import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.client.FilterCriteria;
import edu.arizona.biosemantics.bioportal.client.ProvisionalClassFilter;
import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.beans.ProvisionalEntry;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.OTOProvisionalTermDAO;

public class BioportalCheckApprovedtermsAction extends ParserAction{
	
	private static final Logger LOGGER = Logger.getLogger(ApproveRevokeAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(checkSessionValidity(request)) {
			try {
				SessionDataManager sessionData = getSessionManager(request);
				final User user = sessionData.getUser();
				BioPortalClient bioPortalClient = new BioPortalClient(Configuration.getInstance().getBioportalUrl(), user.getBioportalApiKey());
				bioPortalClient.open();
				
				List<ProvisionalEntry> allPendingEntries = OTOProvisionalTermDAO.getInstance().getAllPendingAdoptions();
				for (ProvisionalEntry provisionalEntry : allPendingEntries) {
					Future<ProvisionalClass> future = bioPortalClient.getProvisionalClass(provisionalEntry.getProvisionalClass().getId());
					ProvisionalClass result = future.get();
					provisionalEntry.setProvisionalClass(result);
					if(!result.getPermanentId().isEmpty()) {
						OTOProvisionalTermDAO.getInstance().updatePermanentID(provisionalEntry, result.getPermanentId());
					}
				}

				Future<List<ProvisionalClass>> result = bioPortalClient.getProvisionalClasses();
				List<ProvisionalClass> list = result.get();
				ProvisionalClassFilter filter = new ProvisionalClassFilter(new FilterCriteria<ProvisionalClass>() {
					@Override
					public boolean filter(ProvisionalClass t) {
						return !t.getCreator().equals(user.getBioportalUserId());
					}
				});
				filter.filter(list);
				bioPortalClient.close();
				
				int count = 0;
				for(ProvisionalClass provisionalClass : list) {
					if(!provisionalClass.getPermanentId().isEmpty())
						count++;
				}
				
				response.setContentType("text/xml");
				if (count >= 0) {
					response.getWriter().write("<response>" + count + "</response>");	
				} else {
					response.getWriter().write("<response>Error in updating approved terms. Please try again later. </response>");
				}
			
			} catch(Exception exe) {
				exe.printStackTrace();
				LOGGER.error("unable to update adopted terms", exe);
				response.getWriter().write("<response>Error: The application encountered an error while processing your request</response>");
			}
		} else {
			response.getWriter().write("<response>Error: Your session has expired</response>");
		}
		return null;
	}

}
