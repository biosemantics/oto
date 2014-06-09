<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.GeneralDBAccess"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.DatasetStatistics"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/manageDatasets.css" />
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/manageDatasets.js"></script>
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
</head>
<%
	String message = (String) request.getAttribute("message");
%>

<body onload="loadMessage('<%=message%>')">
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			User user = sessionDataManager.getUser();

			//get the dataset list
			ArrayList<DatasetStatistics> datasets = GeneralDBAccess
					.getInstance().getDatasetsByUser(user);
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<table border="1" style="width: 100%;">
		<tr>
			<td width="100%">
				<table>
					<tr class="titleline">
						<th width="35%">Create dataset</th>
						<th width="65px">Populate dataset</th>
					</tr>
					<tr>
						<td style="vertical-align: top; padding-right: 10px">
							<p>Importing a dataset takes two steps. By filling out the
								information below, you will create an empty dataset, which you
								will then populate by using the functions provided on the right
								side of the screen.</p>
							<table style="padding: 5px;">
								<tr>
									<td align="right"><label style="color: black;">Dataset
											Name:</label></td>
									<td><input id="datasetPrefix"></input></td>
								</tr>
								<tr>
									<td align="right"><label style="color: black;">Of
											Taxon Group:</label></td>
									<td><select name="value" id="glossaryID">
											<option value="" style="font-weight: bold;">- Select
												-</option>
											<%
												ArrayList<String> glosses = GlossaryNameMapper.getInstance()
															.getGlossaryNames();
													for (String gloss : glosses) {
											%>
											<option
												value="<%=GlossaryNameMapper.getInstance()
							.getGlossaryIDByName(gloss)%>"><%=gloss%></option>

											<%
												}
											%>
									</select></td>
								</tr>
								<tr>
									<td></td>
									<td><input type="button" name="button"
										title="Create an empty dataset. You may import organizing tasks later."
										value="Create Dataset"
										class="uiButton uiButtonSpecial uiButtonMedium createDataset"
										onclick="create_dataset()" /></td>
								</tr>
								<tr>
									<td colspan="2" id="creationNote" style="display: none;"><label>Creating
											dataset ... This may take a while. Please wait ... </label><img
										class="processingSign" src="images/green_rot.gif"></img></td>
								</tr>
							</table>

						</td>
						<td
							style="border-left: 2px solid green; height: 500px; vertical-align: top; padding-left: 10px">
							<%
								if (datasets.size() == 0) {
							%>
							<p>No dataset.</p> <%
 	} else {
 %>
							<div style="overflow: auto; height: 100%;">
								<p>You can import data for "Group Terms", "Structure
									Hierarchy", and/or "Term Order" to a dataset. You can also set
									the privacy level or delete a dataset. Click on a dataset name
									to see available actions.</p>
								<ul class="datasets">
									<%
										for (DatasetStatistics dataset : datasets) {
									%>
									<li style="vertical-align: top;" class="dataset"><img
										class="nodeStatus" title="Click to view tasks"
										src="images/node_closed.png" height="10px" /><img
										class="datasetStatus"
										src="images/<%=(dataset.isPrivate() ? "private" : "public")%>.png"
										title="This is a <%=(dataset.isPrivate() ? "private" : "public")%> dataset"
										height="14px" /><label class="datasetName"><%=dataset.getDatasetName()%></label>
										<%
											if (!GlossaryNameMapper.getInstance().isSystemReservedDataset(dataset
																.getDatasetName())) {
										%> <input type="button"
										value="Make <%=(dataset.isPrivate() ? "Public"
									: "Private")%>"
										class="uiButton uiButtonSpecial uiButtonMedium actionbuttons"
										onclick="setDatasetPrivacy(this, '<%=dataset.getDatasetName()%>','<%=(dataset.isPrivate() ? "0" : "1")%>')"></input>

										<input type="button" value="Delete dataset"
										onclick="deleteDataset(this, '<%=dataset.getDatasetName()%>')"
										title="Delete this dataset"
										class="uiButton uiButtonSpecial uiButtonMedium actionbuttons"></input>
										<%
											} else {
										%><label class="systemReserved">[System Reserved]</label> <%
 	}
 %> <label class="processingNote"
										style="display: none; font-weight: normal;"></label><img
										src="images/green_rot.gif" class="processingSign"
										style="display: none;" />


										<ul class="tasks">

											<!-- import terms task -->
											<li><img src="images/task.png" height="12px" /><label
												class="taskName">Group Terms</label><label
												class="statistics">[<%=dataset.getNumTotalTerms()%>
													terms, <%=dataset.getNumDecisions()%> decisions made]
											</label> <%
 	if (dataset.getNumDecisions() == 0) {
 %> <input type="button" onclick="selectFile(this) "
												<%=(dataset.getNumTotalTerms() > 0 ? "value='Re-import' title='Re-import from a .csv file. This will delete all existing terms.'"
									: "value='import' title='Import terms from .csv file.'")%>
												class="uiButton uiButtonMedium"></input> <a target="_blank"
												href="intro.do#csvFormat_Terms">View Import Format</a>

												<form class="uploadForm" action="import.do" method="post"
													enctype="multipart/form-data">
													<input type="file" class="selectFileInput" accept="csv"
														name="file" /> <input type="hidden" name="datasetName"
														value="<%=dataset.getDatasetName()%>" /><input
														type="hidden" name="taskIndex" value="1" /> <input
														class="submitImportBtn uiButton uiButtonMedium"
														onclick="submitImport(this)" type="button" value="Submit" />
												</form> <%
 	}
 %></li>

											<!-- import structures task -->
											<li><img src="images/task.png" height="12px" /><label
												class="taskName">Structure Hierarchy</label><label
												class="statistics">[<%=dataset.getNumTotalTags()%>
													structures, <%=dataset.getNumDecisionsInHierarchy()%>
													decisions made]
											</label> <%
 	if (dataset.getNumDecisionsInHierarchy() == 0) {
 %> <input type="button" onclick="selectFile(this)"
												<%=(dataset.getNumTotalTags() > 0 ? "value='Re-import' title='Re-import from a .csv file. This will delete all existing terms.'"
									: "value='import' title='Import terms from .csv file.'")%>
												class="uiButton uiButtonMedium"></input> <a target="_blank"
												href="intro.do#csvFormat_Structures">View Import Format</a>

												<form class="uploadForm" action="import.do" method="post"
													enctype="multipart/form-data">
													<input type="file" class="selectFileInput" accept="csv"
														name="file" /> <input type="hidden" name="datasetName"
														value="<%=dataset.getDatasetName()%>" /><input
														type="hidden" name="taskIndex" value="2" /> <input
														class="submitImportBtn uiButton uiButtonMedium"
														onclick="submitImport(this)" type="button" value="Submit" />
												</form> <%
 	}
 %></li>

											<!-- import orders task -->
											<li><img src="images/task.png" height="12px" /><label
												class="taskName">Term Order</label><label class="statistics">[<%=dataset.getNumTotalOrders()%>
													orders, <%=dataset.getNumDecisionsInOrders()%> decisions
													made]
											</label> <%
 	if (dataset.getNumDecisionsInOrders() == 0) {
 %> <input type="button" onclick="selectFile(this)"
												<%=(dataset.getNumTotalOrders() > 0 ? "value='Re-import' title='Re-import from a .csv file. This will delete all existing terms.'"
									: "value='import' title='Import terms from .csv file.'")%>
												class="uiButton uiButtonMedium"></input> <a target="_blank"
												href="intro.do#csvFormat_Orders">View Import Format</a>

												<form class="uploadForm" action="import.do" method="post"
													enctype="multipart/form-data">
													<input type="file" class="selectFileInput" accept="csv"
														name="file" /> <input type="hidden" name="datasetName"
														value="<%=dataset.getDatasetName()%>" /><input
														type="hidden" name="taskIndex" value="3" /> <input
														class="submitImportBtn uiButton uiButtonMedium"
														onclick="submitImport(this)" type="button" value="Submit" />
												</form> <%
 	}
 %></li>
										</ul></li>
									<%
										}
									%>
								</ul>
							</div> <%
 	}
 %>
						</td>
					</tr>
				</table>

			</td>
		</tr>

	</table>

	<%
		} else {
	%>

	<jsp:include page="loginHeader.jsp" />
	<font class="font-text-style">Your session has timed off. Please
		<a href="<%=request.getContextPath()%>">login</a>
	</font>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>
