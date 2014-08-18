<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
</head>
<body>
	<%@ page isErrorPage="true"%>
	<jsp:include page="header.jsp" />
	<h2>
		<font class="font-text-style">The application encountered an
			internal error while processing your request. Please make sure you
			have the correct upload ID. </font>
	</h2>
	<%
		String message = (String) request.getAttribute("message") != null ? (String) request
				.getAttribute("message") : "";
	%>
	<font class="font-text-style"><%=message%></font>
	<br></br>
	<font class="font-text-style"><% exception.printStackTrace(); %>Technical details of the error : <%=exception.toString()%>
	</font>
	<jsp:include page="footer.jsp" />
</body>
</html>