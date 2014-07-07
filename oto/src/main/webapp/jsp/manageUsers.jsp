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
		session.setAttribute("manageType", "0");
		UserDataAccess userDataAccess = new UserDataAccess();
		ArrayList<User> users = (ArrayList<User>) userDataAccess.getAllUsers();
%>
<!-- Session Validity check header End -->
<jsp:include page="header.jsp" />
<%
	User manager = sessionDataManager.getUser();
		if (manager.getRole().equals("S") || manager.getRole().equals("A") || manager.getRole().equals("O")) {
%>
<table width="100%" style="border-top: 1px solid green">
	<tr>
		<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
		</td>
		<td width="85%" valign="top">
		<table  style="height: 500px" width="100%">
			<tr>
				<td>
				<table>
					<tr>
						<td><img src="images/users.jpg" width="auto" height="80px"></td>
						<td><font class="font-text-style"><b>This list
						shows the users that are registered on the system and the ones
						that are awaiting your approval. Dare you disapprove them!</b><br></br>
						If you "Revoke" a user, his/her status will be turned inactive.
						Approval makes the system accessible to the user.</font> <label
							id="serverMessage"></label></td>
						<td>

						<div id="mydiv"></div>
						</td>
					</tr>
				</table>

				<table width="100%">
					<tr bgcolor="green">
						<th align="left" width="5%"><font class="font-text-style"
							color="white">#</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Name</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Email</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Affiliation</font></th>
						<th align="left" width="10%"><font class="font-text-style"
							color="white">Role</font></th>
						<th align="left" width="10%"><font class="font-text-style"
							color="white">Action</font></th>
					</tr>

					<%
						int count = 1;
								boolean flag = true;
								String tdClass = "";

								if (users != null) {
									for (User user : users) {
										tdClass = (flag) ? "d0" : "d1";
										flag = (flag) ? false : true;
					%>
					<tr class="<%=tdClass%>">
						<td width="5%"><font class="font-text-style"><%=count++%></font></td>
						<td width="25%"><font class="font-text-style"><%=user.getFirstName()%>&nbsp;<%=user.getLastName()%></font></td>
						<td width="25%"><font class="font-text-style"><%=user.getUserEmail()%></font></td>
						<td width="25%"><font class="font-text-style"><%=user.getAffiliation()%></font></td>
						<td width="10%">
						<%
							if (user.getRole().equals("S") || user.getRole().equals("A")) {
						%> <font class="font-text-style">Admin</font> <%
 	} else {
 %> <font class="font-text-style">User</font> <%
 	}
 %>
						</td>
						<td width="10%"><font class="font-text-style">
						<div id="status<%=count%>">
						<%
							if (user.isActive()) {
						%> <a href="#"
							onclick="updateStatus(<%=user.getUserId()%>, 'status<%=count%>', false)">Revoke</a>
						<%
							} else {
						%> <a href="#"
							onclick="updateStatus(<%=user.getUserId()%>, 'status<%=count%>', true)">Approve</a>
						<%
							}
						%>
						</div>
						</font></td>
					</tr>
					<%
						}
								}
					%>
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
