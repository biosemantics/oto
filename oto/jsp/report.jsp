<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/reportStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/dtree.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<script type="text/javascript" src="js/report.js"></script>

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
</head>
<body>
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			User user = sessionDataManager.getUser();
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<table border="1" width="100%">
		<tr>
			<td>
				<table>
					<tr>
						<td><font class="font-text-style"><b>This report
									gives you a history of the terms that have been grouped by you.
									It will also tell you if your grouping was accepted based on
									inputs of other experts in the field.</b><br></font></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%">
					<tr bgcolor="green">
						<th style="padding: 0px"><font color="white">Dataset
								Prefix</font></th>
						<th style="padding: 0px"><font color="white">Report</font></th>
					</tr>
					<tr>
						<td width="15%" valign="top"
							style="padding-top: 2px; padding-bottom: 5px;">
							<%
								CharacterDBAccess cdba = new CharacterDBAccess();
									ArrayList<String> datasets = cdba.getDatasetsForUserReport(user
											.getUserId());
									for (String dataset : datasets) {
							%>
							<div class="dtree" style="height: 100px">
								<font class="font-text-style"
									style="font-size: 13px; font-weight: bolder; background-color: #B0F1A0;"><%=dataset%></font>
								<div class="clip" id=<%=dataset%> style="padding-top: 3px;">
									<div class="dtree" onclick="getReport(this)" id="1"
										style="cursor: pointer;">
										<img src="images/tree/join.gif"></img><font
											class="font-text-style" style="text-decoration: underline;">Group
											Terms</font>
									</div>
									<div class="dtree" onclick="getReport(this)" id="2"
										style="cursor: pointer;">
										<img src="images/tree/join.gif"></img><font
											class="font-text-style" style="text-decoration: underline;">Structure
											Hierarchy</font>
									</div>
									<div class="dtree" onclick="getReport(this)" id="3"
										style="cursor: pointer;">
										<img src="images/tree/joinbottom.gif"></img><font
											class="font-text-style" style="text-decoration: underline;">Term
											Order</font>
									</div>
								</div>
							</div> <%
 	}
 %>
							<div style="height: 100px">
								<a href="#" onclick="getReport(this)" id="USERS_LOG"><font
									class="font-text-style">Dataset Logs</font></a>
							</div>


						</td>
						<td width="85%" id="reportContent" valign="top"
							style="padding: 0px" height="500px"><font
							class='font-text-style'>Select reports category from left
								to view specific report</font></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<%
		} else {
	%>

	<jsp:include page="loginHeader.jsp" />
	Your session has timed off. Please
	<a href="<%=request.getContextPath()%>">login</a>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
</html>
