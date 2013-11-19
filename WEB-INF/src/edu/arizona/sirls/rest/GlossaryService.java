package edu.arizona.sirls.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.sirls.rest.beans.Download;
import edu.arizona.sirls.rest.beans.Upload;
import edu.arizona.sirls.rest.beans.UploadResult;
import edu.arizona.sirls.rest.db.DownloadDAO;
import edu.arizona.sirls.rest.db.UploadDAO;

@Path("/glossary")
public class GlossaryService {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public GlossaryService() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/upload")
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public UploadResult putUpload(Upload upload) {
		try {
			UploadResult result = UploadDAO.getInstance().putUpload(upload);
			return result;
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			return new UploadResult(-1, "");
		}
	}
	
	@Path("/download")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Download getDownload(@QueryParam("uploadId") int uploadId, @QueryParam("secret") String secret) {
		Download result = new Download();
		try {
			if(UploadDAO.getInstance().isValidSecret(uploadId, secret)) {
				result = DownloadDAO.getInstance().getDownload(uploadId);
				UploadDAO.getInstance().setReadyToDelete(uploadId);
			}
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return result;
	}
}