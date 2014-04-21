package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.io.FileReader;
import edu.arizona.biosemantics.oto.oto.Configuration;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;
/**
 * This class will read the corresponding file from the context sentences
 * @author Partha
 *
 */
public class FileAction extends ParserAction {

	
    /** Getting the instance of logger. */
    private static final Logger LOGGER = Logger
            .getLogger(FileAction.class);
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    	
    	if (checkSessionValidity(request)) {
    		try {
            	GeneralForm gform = (GeneralForm) form;
            	String fileName = gform.getValue();
            	response.setContentType("text/xml");
            	String fileText = (new FileReader()).getFileInfo(Configuration.getInstance().getSourceFilePath() + fileName);
            	response.getWriter().write(fileText);    	
            	
    		} catch(Exception exe) {
    			LOGGER.error("unable to get file contents", exe);
    			exe.printStackTrace();    			
    		}    		
    	}     	
    	return null;

    }
}
