package edu.arizona.sirls.util;

import java.util.ArrayList;
import edu.arizona.sirls.beans.SpecificReportBean;
import edu.arizona.sirls.db.ReportingDBAccess;

/**
 * This class creates the TermSpecific Report
 * @author Partha
 *
 */
public class SpecificReport {

	private ArrayList<SpecificReportBean> termReportBeans;
	public SpecificReport(String dataset, String idOrName, int type) {
		try {
			if (type == 1) {
				termReportBeans = new ReportingDBAccess().getTermSpecificReport(idOrName, dataset);
			} else if (type == 2) {
				termReportBeans = new ReportingDBAccess().getTagSpecificReport(idOrName, dataset);
			} else {
				termReportBeans = new ReportingDBAccess().getOrderSpecificReport(idOrName, dataset);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<SpecificReportBean> getTermSpecificReport() {
		return termReportBeans;
	}
}
