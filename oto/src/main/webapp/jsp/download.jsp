<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.form.RegistrationForm"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/group.js"></script>
<script language="javascript" src="js/download.js"></script>
</head>
<body>
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));
		if (sessionDataManager == null) {
	%>
	<jsp:include page="GotologinHeader.jsp" />
	<%
		} else {
	%>
	<jsp:include page="header.jsp" />
	<%
		}
	%>
	<table width="100%" border="1">
		<tr>
			<td height="500px" valign="top" width="100%">
				<table style="border-collapse: collapse;" width="100%">
					<tr>
						<td width="30%" valign="top"><div style="padding: 10px">
								<font color="green" class="font-text-style"
									style="font-weight: bold;">Download finalized term sets</font>
							</div> <%
 	CharacterDBAccess cdba = new CharacterDBAccess();
 	ArrayList<String> datasets = cdba.getDownloadableDatasets();
 	if (datasets.size() > 0) {
 %>
							<div style="padding: 20px">
								<font class="font-text-style">Select a dataset : </font><select
									name="dataset" id="dataset" onchange="getDownloadableFiles()">
									<option value="">-- Select --</option>
									<%
										for (String prefix : datasets) {
									%>
									<option value="<%=prefix%>"><%=prefix%></option>
									<%
										}
									%>
								</select>
							</div>
							<div style="padding-left: 20px">
								<label id="serverMsg"></label>
							</div></td>
						<td width="70%">
							<div style="padding-top: 20px" class="downloadLinks">

								<div class="title" id="SQL_ZIP_TITLE">Zipped SQL file
									(.zip)</div>
								<div class="links">
									<ul id="SQL_ZIP_LINKS">

									</ul>
								</div>

								<div class="title" id="CSV_CURRENT_TITLE">Download latest
									version of categorization (.csv)</div>
								<div class="links">
									<ul id="CSV_CURRENT_LINKS">

									</ul>
								</div>

								<div class="title" id="CSV_ARCHIVED_TITLE">Old versions of
									categorization (.csv)</div>
								<div class="links">
									<ul id="CSV_ARCHIVED_LINKS">

									</ul>
								</div>
							</div>
						</td>
					</tr>
				</table> <%
 	} else {
 %> <font color="green" class="font-text-style"
				style="font-weight: bold;">No available download now. </font> <%
 	}
 %>
			</td>
		</tr>
	</table>

	<jsp:include page="footer.jsp" />

</body>
<%@ page errorPage="error.jsp"%>
</html>