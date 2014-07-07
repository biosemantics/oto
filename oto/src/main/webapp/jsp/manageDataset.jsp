<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.UserStatisticsBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermDecision"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.DatasetStatistics"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.UserStatisticsBean"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/dtree.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/managers.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/managerSave.js"></script>
<script language="javascript">
function checkDeleteNow() {
	
	if (document.getElementById('deleteNow') != null) {
		var datasetToDelete = document.getElementById('deleteNow').getAttribute("name");
		deleteDataset(datasetToDelete);
	}
}
</script>
</head>
<body onload="checkDeleteNow()">
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			session.setAttribute("manageType", "4");
			if (request.getParameter("s") != null) {
				session.setAttribute("selectedDataset",
						request.getParameter("s"));
			}
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<%
		User manager = sessionDataManager.getUser();
			if (manager.getRole().equals("A")
					|| manager.getRole().equals("O")
					|| manager.getRole().equals("S")) {
				String selectedDataset = "";
				if (session.getAttribute("selectedDataset") != null) {
					selectedDataset = session.getAttribute(
							"selectedDataset").toString();
				}

				CharacterDBAccess cdba = new CharacterDBAccess();
				//get info of this dataset: how may decisions, how many terms in each page
				DatasetStatistics stat = cdba.getDataSetStatistics(manager,
						selectedDataset);

				boolean isSystemReserved = GlossaryNameMapper.getInstance()
						.isSystemReservedDataset(selectedDataset);
	%>

	<table width="100%" style="border-top: 1px solid green">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td width="85%" valign="top">
				<%
					if (stat == null) {
				%>
				<table style="border-left: solid green 1px">
					<tr>
						<td height="500" valign="top">
							<h2>
								<font class="font-text-style" color="red">Dataset '<%=selectedDataset%>'
									has been deleted!
								</font>
							</h2>
						</td>
					</tr>
				</table> <%
 	} else {
 				String note = cdba.getDatasetNote(selectedDataset);
 				note = note.equals("") ? "" : " (Merged dataset from: "
 						+ note + ")";
 %>
				<table width="100%">
					<tr>
						<th align="left">Dataset: <font color="green"><%=selectedDataset%></font>
							<font color="gray" style="font-weight: normal;"><%=note%></font></th>
					</tr>
					<tr>
						<td>
							<%
								if (request.getParameter("action") != null) {
												String action = request.getParameter("action")
														.toString();
												if (action.equals("delete")) {
							%>
							<div id="deleteNow" name="<%=selectedDataset%>"></div> <%
 	}
 				}
 %>
						</td>
					</tr>
					<tr>
						<td align="left">
							<form id="deleteDataset" name="generalForm"
								action="deleteDataset.do" method="post">
								<input type="button" name="button" value="Delete this dataset"
									<%if (!cdba.isConfirmed(selectedDataset, 4)
								&& !isSystemReserved) {%>
									class="uiButton uiButtonSpecial uiButtonMedium"
									onclick="deleteDataset('<%=selectedDataset%>')"
									style="padding: 0px 1px 1px 1px" <%} else {%>
									title="<%=(isSystemReserved ? "Cannot delete system reserved dataset."
									: "Cannot delete this dataset because it has finalized data. ")%>"
									style="padding: 0px 1px 1px 1px; background: gray; border-bottom-color:#444"
									onclick="alert('<%=(isSystemReserved ? "Cannot delete system reserved dataset."
									: "Cannot delete this dataset because it has finalized data. ")%>')"
									<%}%> /><img src="images/green_rot.gif" id="processImg"
									style="visibility: hidden" width="15px;" />
							</form>&nbsp;<label id="serverMessage"></label><br />
						</td>
					</tr>
					<tr>
						<th align="left">Dataset Statistics:</th>
					</tr>
					<tr>
						<td>
							<ul>
								<%
									if (stat.getUserStatsInCategorizationWithReview()
														.size() > 0) {
													ArrayList<UserStatisticsBean> userStats = stat
															.getUserStatsInCategorizationWithReview();
								%>
								<li class="title">Categorization Page:</li>
								<li class="subTitle"><font class="number"><%=stat.getNumTotalTerms()%></font>
									terms, <font class="number"><%=stat.getNumUnCategorizedTerms()%></font>
									uncategorized and <font class="number"><%=stat.getNumUnTouchedTerms()%></font>
									untouched. <font class="number"><%=stat.getNumDecisions()%></font>
									decision records and <font class="number"><%=stat.getNumReviews()%></font>
									review records made by <font class="number"><%=userStats.size()%></font>
									users.</li>
								<%
									for (UserStatisticsBean userStat : userStats) {
								%>
								<li class="simpleText"><font class="name"><%=userStat.getUserName()%>:
								</font> touched <font class="number"><%=userStat.getCount()%></font>
									terms, decisions made on <font class="number"><%=userStat.getCount_decidedTerms()%></font>
									terms.</li>
								<%
									}
								%>

								<%
									}
								%>

								<%
									if (stat.getUserStatsInHierarchy().size() > 0) {
													ArrayList<UserStatisticsBean> userStats = stat
															.getUserStatsInHierarchy();
								%>
								<li class="title">Hierarchy Page:</li>
								<li class="subTitle"><font class="number"><%=stat.getNumTotalTags()%></font>
									structure(s), <font class="number"><%=stat.getNumUnTouchedTags()%></font>
									untouched. <font class="number"><%=stat.getNumDecisionsInHierarchy()%></font>
									decision(s) made by <font class="number"><%=userStats.size()%></font>
									user(s).</li>
								<%
									for (UserStatisticsBean userStat : userStats) {
								%>
								<li class="simpleText"><font class="name"><%=userStat.getUserName()%>:
								</font> <font class="number"><%=userStat.getCount()%></font>
									decision(s)</li>
								<%
									}
								%>
								<%
									}
								%>

								<%
									if (stat.getUserStatsInOrders().size() > 0) {
													ArrayList<UserStatisticsBean> userStats = stat
															.getUserStatsInOrders();
								%>
								<li class="title">Order Page:</li>
								<li class="subTitle"><font class="number"><%=stat.getNumTotalTermsInOrders()%></font>
									term(s) in <font class="number"><%=stat.getNumTotalOrders()%></font>
									order(s), <font class="number"><%=stat.getNumDecisionsInOrders()%></font>
									decision(s) made by <font class="number"><%=userStats.size()%></font>
									user(s)</li>
								<%
									for (UserStatisticsBean userStat : userStats) {
								%>
								<li class="simpleText"><font class="name"><%=userStat.getUserName()%>:
								</font> <font class="number"><%=userStat.getCount()%></font>
									decision(s)</li>
								<%
									}
								%>
								<%
									}
								%>
								<%
									if (stat.getUserStatsInComments().size() > 0) {
													ArrayList<UserStatisticsBean> userStats = stat
															.getUserStatsInComments();
								%>
								<li class="title">Comments:</li>
								<li class="subTitle"><font class="number"><%=stat.getNumComments()%></font>
									comment(s) made by <font class="number"><%=userStats.size()%></font>
									user(s)</li>
								<%
									for (UserStatisticsBean userStat : userStats) {
								%>
								<li class="simpleText"><font class="name"><%=userStat.getUserName()%>:
								</font> <font class="number"><%=userStat.getCount()%></font> comment(s)
								</li>
								<%
									}
								%>
								<%
									}
								%>


							</ul>
						</td>
					</tr>

					<tr>
						<td height="200px"></td>
					</tr>
				</table> <%
 	}
 %>
			</td>
		</tr>
	</table>
	<%
		} else {
	%>
	<table>
		<tr>
			<td>
				<h2>
					<font class="font-text-style">You have not been authorized
						for users and decisions management. Please contact us for
						authorization. Thanks. </font>
				</h2>
			</td>
		</tr>
	</table>
	<%
		}
		} else {
	%>

	<jsp:include page="loginHeader.jsp" />
	<font class="font-text-style"> Your session has timed off.
		Please <a href="<%=request.getContextPath()%>">login</a>
	</font>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>
