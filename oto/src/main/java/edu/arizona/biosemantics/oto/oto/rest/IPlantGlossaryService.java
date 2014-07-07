package edu.arizona.biosemantics.oto.oto.rest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.common.model.Category;
import edu.arizona.biosemantics.oto.common.model.iplant.GlossaryDownload;
import edu.arizona.biosemantics.oto.common.model.iplant.TermCategory;
import edu.arizona.biosemantics.oto.common.model.iplant.TermSynonym;
import edu.arizona.biosemantics.oto.oto.rest.db.CategoryDAO;
import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryDownloadDAO;
import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryTypeDAO;
import edu.arizona.biosemantics.oto.oto.rest.git.GlossaryVersionDAO;

/**
 * Kept in place to support the current charaParser iplant implementation
 * @author rodenhausen
 */
@Path("/glossary")
public class IPlantGlossaryService {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public IPlantGlossaryService() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/download")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public GlossaryDownload getDownload(@QueryParam("glossaryType") String glossaryType, @QueryParam("version") String version) {
		GlossaryDownload result = new GlossaryDownload();
		if(version == null || version.isEmpty())
			version = "latest";
		try {
			result = getLegacyGlossaryDownloadForIplant(GlossaryDownloadDAO.getInstance().getGlossaryDownload(glossaryType, version));
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			result.setVersion("Requested version not available");
		}
		return result;
	}
	
	private GlossaryDownload getLegacyGlossaryDownloadForIplant(edu.arizona.biosemantics.oto.common.model.GlossaryDownload newGlossaryDownload) {
		List<edu.arizona.biosemantics.oto.common.model.TermCategory> oldTermCategories = newGlossaryDownload.getTermCategories();
		List<edu.arizona.biosemantics.oto.common.model.TermSynonym> oldTermSynonyms = newGlossaryDownload.getTermSynonyms();
		List<TermCategory> termCategories = new LinkedList<TermCategory>();
		List<TermSynonym> termSynonyms = new LinkedList<TermSynonym>();
		for(edu.arizona.biosemantics.oto.common.model.TermCategory oldTermCategory : oldTermCategories)
			termCategories.add(new TermCategory(oldTermCategory.getTerm(), oldTermCategory.getCategory(), oldTermCategory.isHasSyn()));
		for(edu.arizona.biosemantics.oto.common.model.TermSynonym oldTermSynonym : oldTermSynonyms)
			termSynonyms.add(new TermSynonym(oldTermSynonym.getTerm(), oldTermSynonym.getSynonym()));
		
		GlossaryDownload result = new GlossaryDownload();
		result.setVersion(newGlossaryDownload.getVersion());
		result.setTermCategories(termCategories);
		result.setTermSynonyms(termSynonyms);
		return result;
	}

	@Path("/categories")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Category> getCategories() {
		List<Category> result = new ArrayList<Category>();
		try {
			result = CategoryDAO.getInstance().getCategories();
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	@Path("/glossaryTypes")
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
	
	@Path("/latestVersion")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getLatestVersion(@QueryParam("glossaryType") String glossaryType) {
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