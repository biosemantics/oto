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

import edu.arizona.biosemantics.oto.common.model.NameContext;
import edu.arizona.biosemantics.oto.common.model.TermOrder;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.oto.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.oto.db.HierarchyDBAccess;
import edu.arizona.biosemantics.oto.oto.db.OrderDBAcess;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;

@Path("/populateDataset")
public class PopulateDatasetResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public PopulateDatasetResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("/{datasetName}/groupterms")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	
	public String groupTerms(@PathParam("datasetName") String datasetName, NameContext content) {		
		System.out.println("Received a request to load 'group terms' data for dataset " + datasetName + ".");
		System.out.println("Content: " + content);

		//verify login credentials
		User user = new User();
		user.setUserEmail(content.getLoginData().getUserEmail());
		user.setPassword(content.getLoginData().getUserPassword());

		try {
			UserDataAccess uaccess = new UserDataAccess();
			if(uaccess.validateUser(user)) {
				//group terms
				try {
					CategorizationDBAccess.getInstance().importTerms(datasetName,
							content.getTermList(), "web service upload",
							content.getSentences());
					return "Group Terms data was uploaded correctly.";
				} catch (Exception e) {
					e.printStackTrace();
					return "An error occurred while attempting to populate the dataset. Please try again later.";
				}
			} else { 
				return "Invalid email or password! Please try again.";
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			return "An error occurred while attempting to create the dataset. Please try again later.";
		}
	}
	
	@Path("/{datasetName}/structurehierarchy")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	
	public String structureHierarchy(@PathParam("datasetName") String datasetName, NameContext content) {		
		System.out.println("Received a request to load 'structure hierarchy' data for dataset " + datasetName + ".");
		System.out.println("Content: " + content);
		
		//verify login credentials
		User user = new User();
		user.setUserEmail(content.getLoginData().getUserEmail());
		user.setPassword(content.getLoginData().getUserPassword());

		try {
			UserDataAccess uaccess = new UserDataAccess();
			if(uaccess.validateUser(user)) {
				//import structures.
				try {
					HierarchyDBAccess.getInstance().importStructures(datasetName,
							content.getTermList(), "web service upload", content.getSentences());
					return "Structure Hierachy data was uploaded correctly.";
				} catch (Exception e) {
					e.printStackTrace();
					return "An error occurred while attempting to populate the dataset. Please try again later.";
				}
			} else { 
				return "Invalid email or password! Please try again.";
			}
		} catch (Exception exe) {
			exe.printStackTrace();
			return "An error occurred while attempting to create the dataset. Please try again later.";
		}
	}
	
	@Path("/{datasetName}/termorder")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	
	public String termOrder(@PathParam("datasetName") String datasetName, TermOrder content) {
		System.out.println("Received a request to load 'term order' data for dataset " + datasetName + ".");
		System.out.println("Content: " + content);
		
		//verify login credentials
		User user = new User();
		user.setUserEmail(content.getLoginData().getUserEmail());
		user.setPassword(content.getLoginData().getUserPassword());

		try {
			UserDataAccess uaccess = new UserDataAccess();
			if(uaccess.validateUser(user)) {

				// import orders
				try {
					OrderDBAcess.getInstance().importOrders(datasetName, content.getOrderData());
					return "Term Order data was uploaded correctly.";
				} catch (Exception e) {
					e.printStackTrace();
					return "An error occurred while attempting to populate the dataset. Please try again later.";
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