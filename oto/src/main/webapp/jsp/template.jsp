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

<% } else { %>

<jsp:include page="loginHeader.jsp" />
Your session has timed off. Please <a href="<%=request.getContextPath()%>">login</a>
<% }%>
<jsp:include page="footer.jsp" />
</body>
</html>
