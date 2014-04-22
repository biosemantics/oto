<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>OTO</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<script type="text/javascript" src="../js/swfobject.js"></script>
<script type="text/javascript">
	swfobject.registerObject("swf", "9.0.0", "expressInstall.swf");
</script>
</head>
<%
String filename = "", hint = "";
if (request.getParameter("v") != null) {
	filename = request.getParameter("v").toString();	
}
if (request.getParameter("h") != null) {
	hint = request.getParameter("h").toString();	
}
%>
<body>
<table width="100%">
	<tr>
		<td style="font-family: lucida grande,tahoma,verdana,arial,sans-serif; font-size: 12px"><%=hint %>
		<div style="visibility: hidden;"><object id="swf"
			classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%"
			height="100%" style="visibility: hidden">
			<param name="movie" value="../swfs/<%=filename%>.swf" />
			<!--[if !IE]>--> <object type="application/x-shockwave-flash"
				data="../swfs/<%=filename%>.swf" width="100%" height="600"> <!--<![endif]-->
				<div>

				<p><a href="http://www.adobe.com/go/getflashplayer"><img
					src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"
					alt="Get Adobe Flash player" /></a></p>
				</div>
				<!--[if !IE]>--> </object> <!--<![endif]--> </object></div>
		</td>
	</tr>
</table>
<div></div>
</body>
</html>
