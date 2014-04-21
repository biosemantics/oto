package edu.arizona.biosemantics.oto.lite.action;
/**
 * @author Partha Pratim Sanyal
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/**
 * This abstract class serves as the base class of all the Action classes.
 * It contains a set of methods that could be used to access the session related 
 * information of the user
 * @author Partha
 *
 */
public abstract class ParserAction extends Action {

	/**
	 * This is an abstract method that has to be implemented by the subclasses as per requirement
	 */
	public abstract ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception;
}
