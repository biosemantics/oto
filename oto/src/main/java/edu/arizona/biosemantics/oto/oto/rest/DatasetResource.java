package edu.arizona.biosemantics.oto.oto.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.NoSuchPaddingException;
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

import edu.arizona.biosemantics.oto.common.model.CategorizeTerms;
import edu.arizona.biosemantics.oto.common.model.CreateDataset;
import edu.arizona.biosemantics.oto.common.model.GroupTerms;
import edu.arizona.biosemantics.oto.common.model.Order;
import edu.arizona.biosemantics.oto.common.model.StructureHierarchy;
import edu.arizona.biosemantics.oto.common.model.TermContext;
import edu.arizona.biosemantics.oto.common.model.TermOrder;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
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
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public DatasetResource() throws Exception {
		logger =  LoggerFactory.getLogger(this.getClass());
		uaccess = new UserDataAccess();
	}	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public String createDataset(CreateDataset createDataset) {
		int glossaryID = GlossaryNameMapper.getInstance().getGlossaryIDByName(createDataset.getTaxonGroup().getDisplayName());
		
    	try {
        	if(uaccess.validateAuthentication(createDataset.getAuthenticationToken())) {
        		User user = uaccess.getUserFromAuthenticationToken(createDataset.getAuthenticationToken());
        		
				String username = (user.getFirstName() + "_"	+ user.getLastName()).toLowerCase().replaceAll("^(a-z_)", "_");
				String datasetName = createDataset.getName() + "_" + username + "_" + dateFormat.format(new Date());;
        		
        		CharacterDBAccess cdba = new CharacterDBAccess();
        		boolean success = cdba.createDatasetIfNotExist(datasetName,
        				"", user.getUserId(), glossaryID);
        		if (success){
        			return datasetName;
        		}
        	}
    	} catch (Exception exe) {
    		logger.error("Couldn't create dataset", exe);
    	}
    	return null;
	}
	
	
	@Path("/{datasetName}/groupterms")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public GroupTerms.Result groupTerms(@PathParam("datasetName") String datasetName, GroupTerms groupTerms) {		
		try {
			if(uaccess.validateAuthentication(groupTerms.getAuthenticationToken())) {
				List<TermContext> termContexts = groupTerms.getTermContexts();
				if(termContexts.size() <= 2000) {
					return new GroupTerms.Result(CategorizationDBAccess.getInstance().importTerms(datasetName,
							groupTerms.getTermContexts(), "web service group terms", groupTerms.isReplace()));
				}
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import group terms", exe);
		}
		return new GroupTerms.Result(0);
	}
	
	@Path("/{datasetName}/groupterms/categorization")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public void categorizeGroupTerms(@PathParam("datasetName") String datasetName, CategorizeTerms categorization) {
		try { 
			CharacterDBAccess characterDBAccess = new CharacterDBAccess();
			UserDataAccess userDataAccess = new UserDataAccess();
			User user = userDataAccess.getUserFromAuthenticationToken(categorization.getAuthenticationToken());
			characterDBAccess.saveCategorizingDecisions(categorization.getDecisionHolder().getRegular_categories(), datasetName, user);
		} catch (Exception e) {
			logger.error("Could not categorize terms", e);
		}
	}
	
	@Path("/{datasetName}/structurehierarchy")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public StructureHierarchy.Result structureHierarchy(@PathParam("datasetName") String datasetName, StructureHierarchy structureHierarchy) {				
		try {
			if(uaccess.validateAuthentication(structureHierarchy.getAuthenticationToken())) {
				List<TermContext> termContexts = structureHierarchy.getTermContexts();
				if(termContexts.size() <= 2000) {
					return new StructureHierarchy.Result(HierarchyDBAccess.getInstance().importStructures(datasetName,
							structureHierarchy.getTermContexts(), "web service structure hierarchy", structureHierarchy.isReplace()));
				}
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import structure hierarchy", exe);
		}
		return new StructureHierarchy.Result(0);
	}
	
	@Path("/{datasetName}/termorder")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public TermOrder.Result termOrder(@PathParam("datasetName") String datasetName, TermOrder termOrder) {		
		try {
			if(uaccess.validateAuthentication(termOrder.getAuthenticationToken())) {
				List<Order> orders = termOrder.getOrders();
				
				if(orders.size() <= 2000) {
					return new TermOrder.Result(OrderDBAcess.getInstance().importOrders(datasetName, termOrder.getOrders(), termOrder.isReplace()));
				}
			}
		} catch (Exception exe) {
			logger.error("Couldnt' import term order", exe);
		}
		return new TermOrder.Result(0);
	}
}