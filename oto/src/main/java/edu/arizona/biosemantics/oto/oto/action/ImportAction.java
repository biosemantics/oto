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
import edu.arizona.biosemantics.oto.common.model.Order;
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
		ArrayList<Order> orderList = null;

		String datasetName = importForm.getDatasetName();
		String message = "[" + datasetName + "] Import ";

		try {
			
			boolean overMax = false;
			if (taskIndex.equals("1")) {// categorization page
				CsvParser parser = new CsvParser(file.getInputStream());
				ArrayList<String> terms = parser.getTermList();
				
				message += "terms for Group Terms ";
				overMax = terms.size() > 2000;
				if(!overMax)
					CategorizationDBAccess.getInstance().importTerms(datasetName,
							terms, file.getFileName(), parser.getSentences(), true);
				
			} else if (taskIndex.equals("2")) { // hierarchy page
				CsvParser parser = new CsvParser(file.getInputStream());
				termList = parser.getStructureList();
				message += "structures for Structure Hierarchy ";
				
				overMax = termList.size() > 2000;
				if(!overMax)
					HierarchyDBAccess.getInstance().importStructures(datasetName,
							termList, file.getFileName(), parser.getSentences(), true);
				
			} else { // order
				orderList = new CsvParser(fileStream).getOrderList();
				
				overMax = orderList.size() > 2000;				
				message += "Term Order ";
				if(!overMax)
					OrderDBAcess.getInstance().importOrders(datasetName, orderList, true);
			}
			
			if(!overMax) {
				message += "SUCCESSFULLY. ";
				
				// set selected dataset to be current dataset
				SessionDataManager sessionDataMgr = getSessionManager(request);
				sessionDataMgr.setDataset(datasetName);
			} else
				message += "FAILED. Can not import more than 2000.";
			
			
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
