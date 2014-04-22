<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<script language="javascript" src="js/group.js"></script>
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
<div style="width: 100%; height: 580px; overflow: auto;"
					class="border">
<table border="1" width="100%">
	<tr>
		<td>
		<table>
			<tr>
			<td><img src="images/Userinfo.jpg" width="100" height="auto"></td>
				<td><font class="font-text-style">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For your security,
				your password has not been displayed. You can change your password
				here, after you correctly enter your current password</font></td>
			</tr>
			<tr><td> &nbsp;
			</td> <td>
		<%String message = (String)request.getAttribute("message") == null?"":(String)request.getAttribute("message"); %>
			<label><%=message%></label>
			</td></tr>
		</table>
		<form action="editsettings.do" method="post">
		<table style="margin-left: 80px">
			<tr>
				<td><font class="font-text-style" color="red"><b>First Name *</b></font></td>
				<td> <input type="text" value="<%=user.getFirstName()%>" name="firstName"></input></td>
				<td>&nbsp;</td>
				</tr>
				<tr>
					<td><font class="font-text-style" color="red"><b>Last Name *</b></font></td>
					<td><input type="text" value="<%=user.getLastName()%>" name="lastName"></input></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><font class="font-text-style" color="red"><b>Email Address *</b></font></td>
					<td><input type="text" value="<%=user.getUserEmail()%>" name="emailId" id="emailId"
					onchange="document.getElementById('emailCheck').innerHTML='', checkEmail(this.value)"></input></td>
					<td> <label id="emailCheck"></label></td>
				</tr>
				<tr>
					<td><font class="font-text-style" color="red"><b>Affiliation *</b></font></td>
					<td><input type="text" value="<%=user.getAffiliation()%>" name="affiliation"></input></td>
					<td>&nbsp;</td>
				</tr>
				
				<tr>
					<td><font class="font-text-style"><b>Bioportal User ID</b></font></td>
					<td><input type="text" value="<%=user.getBioportalUserId()%>" name="bioportalUserId"></input></td>
					<td><font class="font-text-style">For Ontology Lookup only</font></td>
				</tr>
				
				<tr>
					<td><font class="font-text-style"><b>Bioportal API Key</b></font></td>
					<td><input type="text" value="<%=user.getBioportalApiKey()%>" name="bioportalApiKey"></input></td>
					<td><font class="font-text-style">For Ontology Lookup only</font></td>
				</tr>


				<tr>
					<td><font class="font-text-style" color="red"><b>Current Password *</b></font></td>
					<td><input type="password" name="regPassword"></input></td>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td><font class="font-text-style"><b>New Password</b></font></td>
					<td><input type="password" name="newPassword"></input></td>
					<td>&nbsp;</td>
				</tr>
				
				<tr>
					<td><font class="font-text-style"><b>Re-type New Password</b></font></td>
					<td><input type="password" name="confirmPassword"></input></td>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td><input type="submit" name="button" value="Submit"
						class="uiButton uiButtonSpecial uiButtonMedium" /></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
		</table>


		</form>
		</td>
	</tr>
</table>

<%
	} else {
%>

<jsp:include page="loginHeader.jsp" />
<font class="font-text-style">Your session has timed off. Please
<a href="<%=request.getContextPath()%>">login</a> </font>
<%
	}
%>
</div>
<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp" %>
</html>
