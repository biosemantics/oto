package edu.arizona.biosemantics.oto.steps.client.presenter;

import com.google.gwt.user.client.Window;

public class Validator {
	public static String validateUploadID() throws Exception {
		String uploadID = Window.Location.getParameter("uploadID");
		if (uploadID == null) {
			throw new Exception(
					"Additional parameters are required to retrieve OTO Lite!");
		}
		return uploadID;
	}
	
	public static String validateSecret() throws Exception {
		String secret = Window.Location.getParameter("secret");
		if (secret == null) {
			throw new Exception(
					"Additional parameters are required to retrieve OTO Lite!");
		}
		return secret;
	}
}
