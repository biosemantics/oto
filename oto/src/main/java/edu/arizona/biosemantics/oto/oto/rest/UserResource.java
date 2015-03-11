package edu.arizona.biosemantics.oto.oto.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto.common.model.Category;
import edu.arizona.biosemantics.oto.common.model.CreateUserResult;
import edu.arizona.biosemantics.oto.common.model.User;
import edu.arizona.biosemantics.oto.common.security.Encryptor;
import edu.arizona.biosemantics.oto.oto.db.UserDataAccess;
import edu.arizona.biosemantics.oto.oto.rest.db.CategoryDAO;

@Path("/user")
public class UserResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;	
	
	private UserDataAccess uda;
	
	private Logger logger;
	
	public UserResource() throws Exception {
		logger =  LoggerFactory.getLogger(this.getClass());
		uda = new UserDataAccess();
	}	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public CreateUserResult createUser(User user) {
		try {
			boolean result = uda.registerUser(user);
			if(result)
				return new CreateUserResult(true);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return new CreateUserResult(false);
	}
	
	@POST
	@Path("/token")
	public String getAuthenticationToken(User user) {
		try {
			if(uda.validateUser(user)) {
				String token = uda.getAuthenticationToken(user);
				return token;
			}
		} catch(Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
}