package edu.arizona.biosemantics.oto.oto.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryTypeDAO;

@Path("/glossaryTypes")
public class GlossaryTypesResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public GlossaryTypesResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> getGlossaryTypes() {
		List<String> result = new ArrayList<String>();
		try {
			result = GlossaryTypeDAO.getInstance().getGlossaryTypes();
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return result;
	}
}