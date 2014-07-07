package edu.arizona.biosemantics.oto.oto.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.arizona.biosemantics.oto.oto.db.OrderDBAcess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

public class EditOrderNameAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GeneralForm gform = (GeneralForm) form;
		String requestStr = gform.getValue();

		//prepare parameters
		String dataset = getSessionManager(request).getDataset();
		String vals[] = requestStr.split(";");
		String groupID = vals[0];
		String oldname = vals[1];
		String replacement = vals[2];

		// modify server
		boolean success = OrderDBAcess.getInstance().changeOrderName(dataset,
				groupID, oldname, replacement);

		response.setContentType("text/xml");
		if (success) {
			response.getWriter().write("success");
		} else {
			response.getWriter().write("failed");
		}

		return null;
	}

}
