package edu.arizona.biosemantics.action;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.beans.ProvisionalEntry;
import edu.arizona.biosemantics.beans.SessionDataManager;
import edu.arizona.biosemantics.beans.User;
import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.bioportal.client.FilterCriteria;
import edu.arizona.biosemantics.bioportal.client.ProvisionalClassFilter;
import edu.arizona.biosemantics.bioportal.model.ProvisionalClass;
import edu.arizona.biosemantics.db.OTOProvisionalTermDAO;

public class BioportalCheckApprovedtermsAction extends ParserAction{
	
	private static final Logger LOGGER = Logger.getLogger(ApproveRevokeAction.class);
	private String bioportalUrl;
		
	public BioportalCheckApprovedtermsAction() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		properties.load(loader.getResourceAsStream("config.properties"));
		this.bioportalUrl = properties.getProperty("bioportalUrl");
	}
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(checkSessionValidity(request)) {
			try {
				SessionDataManager sessionData = getSessionManager(request);
				final User user = sessionData.getUser();
				BioPortalClient bioPortalClient = new BioPortalClient(bioportalUrl, user.getBioportalApiKey());
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
