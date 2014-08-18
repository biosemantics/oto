<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.UserDataAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/dtree.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/cleanup.js"></script>
</head>
<body>
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			session.setAttribute("manageType", "");
			User manager = sessionDataManager.getUser();
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<%
		if (manager.getRole().equals("S")) {

				UserDataAccess userDataAccess = new UserDataAccess();
	%>
	<table width="100%" style="border-top: 1px solid green; height: 500px">
		<tr>
			<td style="vertical-align: top; padding-right: 10px">
				<p>
					Clean up dataset with clean glossary. Usually clean glossary is in
					.csv or .sql files. The clean glossary contains two tables. The
					first is term-category table and the second is synonyms table.
					Term-category table is required and synonyms table is optional. <br />
					<br /> What the cleaning up will do is: <br /> 1. For those terms
					in the clean glossary, remove existing records and re-create
					matching records. <br /> 2. For other terms, leave them as they are
					in the dataset.<br />
					<br /> After clean up, the finalization step will create a valid
					copy of glossary from the clean glossary tables. The valid copy may
					be different from your input in the following ways:<br /> 1. Has
					less records, e.g. only main terms will be there in the final
					term-category table. <br /> 2. Has different termID. OTO will use
					its system-computed termID in the final glossary tables. <br />
					<br /> Before you proceed, make sure you have imported the clean
					glossary tables to the database on the server.
				</p>
				<table style="padding: 5px;">
					<tr>
						<td align="right"><label style="color: red;">Dataset
								Name (*):</label></td>
						<td><input id="datasetname"></input></td>
					</tr>
					<tr>
						<td align="right"><label style="color: red;">Clean
								term-category table name (*):</label></td>
						<td><input id="cleantermcategorytable"></input></td>
					</tr>
					<tr>
						<td align="right"><label style="color: black;">Clean
								synonyms table name:</label></td>
						<td><input id="cleansynstable"></input></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="button" name="button"
							title="Clean up dataset with clean glossary tables"
							value="Clean up dataset"
							class="uiButton uiButtonSpecial uiButtonMedium createDataset"
							onclick="validateInput()" /></td>
					</tr>
					<tr>
						<td colspan="2" id="creationNote" style="display: none;"><label
							id="processingMsg">Cleaning up dataset ... </label><img
							class="processingSign" src="images/green_rot.gif"></img></td>
					</tr>
				</table>

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
						to clean up glossary. Please contact us for authorization. Thanks.
					</font>
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
