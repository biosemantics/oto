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
<jsp:include page="loginHeader.jsp" />
<% String message = (String)request.getAttribute("message") != null? (String)request.getAttribute("message"): "user";  %>
<font class="font-text-style">Dear <%=message%>!</font> <br>
<font class="font-text-style" color="green"><b>You have registered successfully! It might take a while to approve your registration.
Please be patient we will get back to you shortly. Thanks!</b></font> <br></br>
<font class="font-text-style"><a href="<%=request.getContextPath()%>">Back to Login</a></font>
<img src="images/collage.jpg" id="saveImage" width="auto" height="550" />
<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp" %>
</html>
