package edu.arizona.biosemantics.oto.oto.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryVersionDAO;

@Path("/versions")
public class VersionsResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public VersionsResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/glossaries/{glossaryType}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getLatestVersion(@PathParam("glossaryType") String glossaryType) {
		String result = null;
		try {
			result = GlossaryVersionDAO.getInstance().getLatestVersion(glossaryType);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return result;
	}
}