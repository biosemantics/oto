package edu.arizona.biosemantics.oto.oto.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.common.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryDownloadDAO;

@Path("/glossaries")
public class GlossaryResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public GlossaryResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/{glossaryType}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public GlossaryDownload getGlossary(@PathParam("glossaryType") String glossaryType, @QueryParam("version") String version) {
		GlossaryDownload result = new GlossaryDownload();
		if(version == null || version.isEmpty())
			version = "latest";
		try {
			result = GlossaryDownloadDAO.getInstance().getGlossaryDownload(glossaryType, version);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			result.setVersion("Requested version not available");
		}
		return result;
	}
}