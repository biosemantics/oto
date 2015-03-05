package edu.arizona.biosemantics.oto.oto.rest;

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

import edu.arizona.biosemantics.oto.common.model.Login;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;

@Path("/createDataset")
public class CreateDatasetResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public CreateDatasetResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/{datasetName}/{taxonGroup}")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	
	public String createDataset(@PathParam("datasetName") String datasetName, @PathParam("taxonGroup") String taxonGroup, Login loginData) {
		System.out.println("Request to create a dataset named " + datasetName + " of taxon group " + taxonGroup + ".");
		
		//check validity of arguments
		
		int glossaryID = 0; //TODO: change this
		
		//verify login credentials
		User user = new User();
    	user.setUserEmail(loginData.getUserEmail());
    	user.setPassword(loginData.getUserPassword());
    	
    	try {
    		UserDataAccess uaccess = new UserDataAccess();
        	if(uaccess.validateUser(user)) {
        		
        		//create the dataset.
        		CharacterDBAccess cdba = new CharacterDBAccess();
        		boolean success = cdba.createDatasetIfNotExist(datasetName,
        				"", user.getUserId(), glossaryID);
        		if (success){
        			return "Your dataset was successfully created."; //TODO: Fill this out more. 
        		} else {
        			return "Your dataset was not created. Please try again later.";
        		}
        		
        	} else { 
        		return "Invalid email or password! Please try again.";
        	}
    	} catch (Exception exe) {
    		exe.printStackTrace();
    		return "An error occurred while attempting to create the dataset. Please try again later.";
    	}
    	
	}
}