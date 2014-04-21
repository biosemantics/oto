<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
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
<script language="javascript" src="js/group.js"></script>
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
	if (manager.getRole().equals("A")  || manager.getRole().equals("O")
			 || manager.getRole().equals("S")) {

		UserDataAccess userDataAccess = new UserDataAccess();
		ArrayList<User> users = (ArrayList<User>) userDataAccess.getAllUsers();
%>
<table width="100%" style="border-top: 1px solid green">
	<tr>
		<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
		</td>
		<td width="85%" valign="top">
		<table border="0" width="100%">
			<tr>
				<td valign="top"><font class="font-text-style">Select
				from the left menu</font></td>
			</tr>
			<tr><td height="500px"></td></tr>
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
		<h2><font class="font-text-style">You have not been
		authorized for users and decisions management. Please contact us for
		authorization. Thanks. </font></h2>
		</td>
	</tr>
</table>
<%
	}
	} else {
%>

<jsp:include page="loginHeader.jsp" />
<font class="font-text-style"> Your session has timed off. Please
<a href="<%=request.getContextPath()%>">login</a> </font>
<%
	}
%>
<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>
