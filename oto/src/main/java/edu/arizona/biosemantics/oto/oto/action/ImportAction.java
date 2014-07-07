package edu.arizona.biosemantics.oto.oto.action;

import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import edu.arizona.biosemantics.oto.common.action.Forwardable;
import edu.arizona.biosemantics.oto.oto.beans.SessionDataManager;
import edu.arizona.biosemantics.oto.oto.beans.SimpleOrderBean;
import edu.arizona.biosemantics.oto.oto.db.CategorizationDBAccess;
import edu.arizona.biosemantics.oto.oto.db.HierarchyDBAccess;
import edu.arizona.biosemantics.oto.oto.db.OrderDBAcess;
import edu.arizona.biosemantics.oto.oto.form.ImportForm;
import edu.arizona.biosemantics.oto.oto.parser.CsvParser;

public class ImportAction extends ParserAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// get form
		ImportForm importForm = (ImportForm) form;
		FormFile file = importForm.getFile();
		if (file == null) {
			return mapping.findForward(Forwardable.RELOAD);
		}
		String taskIndex = importForm.getTaskIndex();
		InputStream fileStream = file.getInputStream();
		ArrayList<String> termList = null;
		ArrayList<SimpleOrderBean> orderList = null;

		String datasetName = importForm.getDatasetName();
		String message = "[" + datasetName + "] Import ";

		try {
			if (taskIndex.equals("1")) {// categorization page
				CsvParser parser = new CsvParser(file.getInputStream());
				message += "terms for Group Terms ";
				// import categorization terms
				CategorizationDBAccess.getInstance().importTerms(datasetName,
						parser.getTermList(), file.getFileName(),
						parser.getSentences());
			} else if (taskIndex.equals("2")) { // hierarchy page
				CsvParser parser = new CsvParser(file.getInputStream());
				termList = parser.getStructureList();
				message += "structures for Structure Hierarchy ";
				// import hierarchy terms
				HierarchyDBAccess.getInstance().importStructures(datasetName,
						termList, file.getFileName(), parser.getSentences());
			} else { // order
				orderList = new CsvParser(fileStream).getOrderList();
				message += "Term Order ";
				// import orders
				OrderDBAcess.getInstance().importOrders(datasetName, orderList);
			}

			message += "SUCCESSFULLY. ";

			// set selected dataset to be current dataset
			SessionDataManager sessionDataMgr = getSessionManager(request);
			sessionDataMgr.setDataset(datasetName);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			// todo: error management
			message += "FAILED: " + e.getMessage().replaceAll("[^a-zA-Z\\s\\d]", "");
		} finally {
			if (fileStream != null)
				fileStream.close();
		}

		// return message
		request.setAttribute("message", message);

		return mapping.findForward(Forwardable.RELOAD);
	}

}
