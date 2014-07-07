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

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
</head>
<body>
<%@ page isErrorPage="true" %>



<!-- Session Validity check -->
<% 

SessionDataManager sessionDataManager = (SessionDataManager) (session
		.getAttribute("sessionDataMgr"));	

   if (sessionDataManager != null) {
	   User user = sessionDataManager.getUser(); 
%>
<!-- Session Validity check header End -->
<jsp:include page="header.jsp" />

<% } else { %>

<jsp:include page="loginHeader.jsp" />
Your session has timed off. Please <a href="<%=request.getContextPath()%>">login</a>
<% }%>
<h2><font class="font-text-style">The Application encountered an internal error while processing your request.
Please click on any of the links on the upper menu. Thanks
</font></h2>
<% String message = (String)request.getAttribute("message") != null? (String)request.getAttribute("message"): "";  %>
<font class="font-text-style">MESSAGE: <%=message%></font> <br></br>
<font class="font-text-style">Technical details of the error : 
<%=exception != null ? exception.toString() : "" %> </font>
<jsp:include page="footer.jsp" />
</body>
</html>