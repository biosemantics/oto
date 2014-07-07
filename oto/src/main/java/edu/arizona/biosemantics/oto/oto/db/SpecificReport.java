package edu.arizona.biosemantics.oto.oto.db;

import java.util.ArrayList;

import edu.arizona.biosemantics.oto.oto.beans.SpecificReportBean;

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
