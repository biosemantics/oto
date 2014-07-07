<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermForBioportalBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/bioportal.js"></script>
</head>
<body>
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			String dataset = sessionDataManager.getDataset();
	%>
	<jsp:include page="header.jsp" />
	<table border="1" width="100%">
		<tr>
			<td><font class="font-text-style" id="dataset_info">Current
					Dataset: <b><%=dataset%></b>
			</font></td>
		</tr>

		<tr>
			<td width="100%">
				<!-- the main body table -->
				<table width="100%">
					<tr>
						<th bgcolor="green" align="left" style="color: white" width="15%"
							height="25px">Terms:</th>
						<th bgcolor="green" align="left" style="color: white" width="85%">Term
							Details:</th>
					</tr>
					<tr>
						<!-- the term list -->
						<td valign="top" width="15%"
							style="border-right: 1px solid green; height: 500px"><jsp:include
								page="bioportal_leftMenu.jsp" flush="true" /></td>

						<!-- the right block: send form, update form, list submissions, or related terms -->
						<td valign="top" width="85%">
							<table width="100%">
								<tr>
									<td width="45%"><font class="font-text-style">
											Select a term from left!</font></td>
									<td width="55%" align="right"><jsp:include
											page="bioportal_links.jsp" flush="true" /></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<%
		} else {
	%>

	<jsp:include page="loginHeader.jsp" /><font class="font-text-style">
		Your session has timed off. Please <a
		href="<%=request.getContextPath()%>">login</a>
	</font>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>