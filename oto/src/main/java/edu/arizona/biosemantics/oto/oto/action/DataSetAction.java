package edu.arizona.biosemantics.oto.oto.action;
/**
 * @author Partha Pratim Sanyal
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

/* This class is used to load the group after a dataset is selected*/
public class DataSetAction extends ParserAction {
	/** Getting the instance of logger. */
	private static final Logger LOGGER = Logger.getLogger(DataSetAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (checkSessionValidity(request)) {
			SessionDataManager sessionDataMgr = getSessionManager(request);
			String dataset = "";
			GeneralForm general = (GeneralForm) form;
			if (general != null && general.getValue() != null) {
				dataset = general.getValue();
			}
			
			try {
				if (!dataset.equals("")) {
					sessionDataMgr.setDataset(dataset);
					return mapping.findForward(Forwardable.HOME);
				} else {
					String oldDataset = sessionDataMgr.getDataset();
					if (oldDataset != null && !oldDataset.equals("")) {
						return mapping.findForward(Forwardable.HOME);
					} else {
						request.setAttribute("message", "Please select a dataset");
						return mapping.findForward(Forwardable.RELOAD);
					}
				}
			} catch (Exception exe) {
				LOGGER.error("unable to select a dataset", exe);
				exe.printStackTrace();
				return mapping.findForward(Forwardable.ERROR);
			}
		} else {
			return mapping.findForward(Forwardable.LOGON);
		}
	}

}
