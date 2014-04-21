<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Date"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<script language="javascript" src="js/group.js"></script>
<script language="javascript">
	function submitData() {
		var val = document.getElementById("value").value;
		if (val == 'select') {
			document.getElementById("serverMessage").innerHTML = 'Please select a dataset';
		} else {
			document.getElementById('generalForm').submit();
		}
	}
</script>
</head>
<body>
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			User user = sessionDataManager.getUser();
			String currentDateset = sessionDataManager.getDataset();
			if (currentDateset == null) {
				currentDateset = "";
			}
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<table width="100%" height="100%" border="1">
		<tr>
			<td>
				<table align="center" width="100%" height="90%">
					<tr>
						<td><font class="font-text-style"> <b>Hello! </b><br></br>
								In the <a
								href="http://sites.google.com/site/biosemanticsproject/"
								target="_blank">Fine-Grained Semantic Markup Project</a>, a
								semi-automatic semantic annotation software called CharaParser
								has been implemented to annotate semi-structured morphological
								descriptions. CharaParser extracts structure and character terms
								from plain text descriptions. These extracted terms are
								presented on this website for you to review and categorize.<br></br>
								Please select a term set below you would like to work on. Then
								you can categorize terms in '<a href="groupTerms.do"
								title="Group the terms as per your discretion">Group Terms</a>'
								page, to assign the hierarchy relationship of terms in '<a
								href="hierarchy.do"
								title="Build the structure hierarchy as per your discretion">Structure
									Hierarchy</a>' page and to order terms '<a href="order.do"
								title="Put character states in order as per your discretion">Term
									Order</a>' page. <br></br> To categorize terms, you can drag terms
								from left column into specific categories on the right. After
								you make your decision, you need to save your decisions. <br></br>
								To assign the hierarchy relationship of terms, you can drag a
								term from left onto any node of the hierarchy tree on the right
								of the web page. The term will be added as a child of the node
								you selected. Decisions CANNOT be changed after being saved. You
								can submit your decisions multiple times. Normally terms on the
								left will be removed after you drag it to the tree. If you want
								to keep it on the left, you can press 'Ctrl' on PC or 'Command'
								on Mac when you are dragging the term.<br></br> To order terms,
								you can drag the term from the base terms into the empty cells
								in specific order.
						</font></td>
						<td><img src="images/plants.jpg" width="auto" height="310" /></td>
					</tr>
					<tr>
						<td>
							<hr></hr>
							<p>Please select a dataset prefix to start working:</p> <%
 	CharacterDBAccess cdba = new CharacterDBAccess();
 		ArrayList<String> datasets = cdba.getUnfinishedDataSets();
 %>

							<form id="generalForm" name="generalForm" action="groupTerms.do"
								method="post">
								<select name="value" id="value" onchange="checkDataset(this)">
									<option value="select" style="font-weight: bold;">Select</option>
									<%
										GlossaryNameMapper nameMapper = new GlossaryNameMapper();
											for (String prefix : datasets) {
												boolean isSystemReserved = nameMapper
														.isGlossaryReservedDataset(prefix);
												if (currentDateset.equals(prefix)) {
									%>
									<option value="<%=prefix%>" selected="selected"
										style="<%=isSystemReserved ? "color: green" : ""%>"><%=isSystemReserved ? prefix
								+ " [System Reserved]" : prefix%></option>
									<%
										} else {
									%>
									<option value="<%=prefix%>"
										style="<%=isSystemReserved ? "color: green" : ""%>"><%=isSystemReserved ? prefix
								+ " [System Reserved]" : prefix%></option>
									<%
										}
											}
									%>
								</select> <input type="button" name="button" value="Select"
									id="submitbutton"
									class="uiButton uiButtonSpecial uiButtonMedium"
									onclick="submitData()" />
							</form> <%
 	String message = (String) request.getAttribute("message");
 		message = (message == null) ? "" : message;
 %> <label id="serverMessage"><%=message%>&nbsp;</label>
						</td>
						<td>&nbsp;</td>
					</tr>

				</table> <br></br> <br></br> <br></br>
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
<%@ page errorPage="error.jsp"%>
</html>