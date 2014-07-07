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
import edu.arizona.biosemantics.oto.oto.beans.CommentBean;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.User;
import edu.arizona.biosemantics.oto.oto.db.ReportingDBAccess;
import edu.arizona.biosemantics.oto.oto.form.GeneralForm;

/**
 * This class will take care of insrting term specific comments.
 * @author Partha
 *
 */
public class InsertCommentAction extends ParserAction {

	private static Logger LOGGER = Logger.getLogger(InsertCommentAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String map = "";
		if (checkSessionValidity(request)) {
			GeneralForm gform = (GeneralForm) form;
			if(gform.getValue() != null) {
				User  user = getUser(request);			
				String value = gform.getValue();
				String type = request.getParameter("type");
				String comments = value.substring(value.indexOf("<comments>") + 10, value.indexOf("</comments>"));			
				String idOrName = value.substring(value.indexOf("<idOrName>") + 10, value.indexOf("</idOrName>"));
				String name = value.substring(value.indexOf("<name>") + 6, value.indexOf("</name>"));
				SessionDataManager sessiondataMgr = getSessionManager(request);
				CommentBean commentBean = new CommentBean(user, idOrName, comments);
				try {
					new ReportingDBAccess().insertComment(commentBean, sessiondataMgr.getDataset(), type);
					if (type.equals("1")) {
						request.setAttribute("term", idOrName);
					} else if (type.equals("2")) {
						request.setAttribute("tag", idOrName + ":" + name);
					} else
						request.setAttribute("order", idOrName + ":" + name);
					map = Forwardable.RELOAD;
				} catch (Exception exe) {
					exe.printStackTrace();
					LOGGER.error("Not able to insert comment", exe);
					map = Forwardable.ERROR;
				}

			} else {
				request.setAttribute("term", request.getParameter("term"));
				map = Forwardable.RELOAD;
			}			
			
		} else {
			map = Forwardable.LOGON;
		}
		return mapping.findForward(map);
	}

}
