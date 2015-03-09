package edu.arizona.biosemantics.oto.oto.rest;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.common.model.Authentication;
import edu.arizona.biosemantics.oto.common.model.CreateDataset;
import edu.arizona.biosemantics.oto.common.model.GroupTerms;
import edu.arizona.biosemantics.oto.common.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.common.model.TermOrder;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper;
import edu.arizona.biosemantics.oto.oto.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.db.HierarchyDBAccess;
import edu.arizona.biosemantics.oto.oto.db.OrderDBAcess;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;

@Path("/dataset")
public class DatasetResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	private UserDataAccess uaccess;
	
	public DatasetResource() throws IOException {
		logger =  LoggerFactory.getLogger(this.getClass());
		uaccess = new UserDataAccess();
	}	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public String createDataset(CreateDataset createDataset) {
		int glossaryID = GlossaryNameMapper.getInstance().getGlossaryIDByName(createDataset.getTaxonGroup().getDisplayName());
		
		Authentication authentication = createDataset.getAuthentication();
    	try {
        	if(uaccess.validateAuthentication(createDataset.getAuthentication())) {
        		User user = uaccess.getUser(authentication.getEmail());
        		CharacterDBAccess cdba = new CharacterDBAccess();
        		boolean success = cdba.createDatasetIfNotExist(createDataset.getName(),
        				"", user.getUserId(), glossaryID);
        		if (success){
        			return "Your dataset was successfully created.";
        		} else {
        			return "Your dataset was not created. Please try again later.";
        		}
        		
        	} else { 
        		return "Invalid email or password! Please try again.";
        	}
    	} catch (Exception exe) {
    		logger.error("Couldn't create dataset", exe);
    		return "An error occurred while attempting to create the dataset. Please try again later.";
    	}
	}
	
	
	@Path("/{datasetName}/groupterms")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public String groupTerms(@PathParam("datasetName") String datasetName, GroupTerms groupTerms) {		
		Authentication authentication = groupTerms.getAuthentication();
		try {
			if(uaccess.validateAuthentication(authentication)) {
				
				CategorizationDBAccess.getInstance().importTerms(datasetName,
						groupTerms.getTermContexts(), "web service group terms");
				return "Group Terms data was imported successfully.";
			} else { 
				return "Invalid email or password! Please try again.";
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import group terms", exe);
			return "An error occurred while attempting to populate the dataset. Please try again later.";
		}
	}
	
	@Path("/{datasetName}/structurehierarchy")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public String structureHierarchy(@PathParam("datasetName") String datasetName, StructureHierarchy structureHierarchy) {				
		Authentication authentication = structureHierarchy.getAuthentication();
		try {
			if(uaccess.validateAuthentication(authentication)) {
				HierarchyDBAccess.getInstance().importStructures(datasetName,
						structureHierarchy.getTermContexts(), "web service structure hierarchy");
				return "Structure Hierachy data was imported successfully.";
			} else { 
				return "Invalid email or password! Please try again.";
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import structure hierarchy", exe);
			return "An error occurred while attempting to create the dataset. Please try again later.";
		}
	}
	
	@Path("/{datasetName}/termorder")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public String termOrder(@PathParam("datasetName") String datasetName, TermOrder termOrder) {		
		Authentication authentication = termOrder.getAuthentication();
		try {
			if(uaccess.validateAuthentication(authentication)) {
				OrderDBAcess.getInstance().importOrders(datasetName, termOrder.getOrders());
				return "Term Order data was imported successfully.";
			} else { 
				return "Invalid email or password! Please try again.";
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import term order", exe);
			return "An error occurred while attempting to create the dataset. Please try again later.";
		}
	}
}