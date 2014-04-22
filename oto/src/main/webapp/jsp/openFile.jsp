<html>
<head>
<title><%=request.getParameter("f") + ".txt"%></title>
<link rel="stylesheet" media="screen" type="text/css"
	href="../css/groupStyles.css" />
</head>
<body>
<span>
<table width="100%">
	<tr>
		<td><script language="javascript">
		document.write("<p><font color=\"green\">"+window.opener.fileText+"</font></p>");
		</script></td>
	</tr>
</table>
</span>
</body>
</html>