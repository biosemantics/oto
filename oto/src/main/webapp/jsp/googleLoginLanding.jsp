<html>
<head>
<script language="javascript">

function redirectToAction() {
	var urlParts = window.location.href.split("#access_token=");
	var parameters = urlParts[1].split("&token_type=");
	var accessToken = parameters[0];
	var parameters = parameters[1].split("&expires_in=");
	var tokenType = parameters[0];
	var expiresIn = parameters[1];

	document.getElementById('accessToken').value = accessToken;
	document.getElementById('tokenType').value = tokenType;
	document.getElementById('expiresIn').value = expiresIn;
		
	document.getElementById('form').submit();
}


</script>
</head>

<body onLoad="redirectToAction()">
	<form method="post" action="../validateLoginGoogle.do" id="form" name="loginGoogleForm">
		<input type="hidden" name="accessToken" id="accessToken"/>
		<input type="hidden" name="tokenType" id="tokenType"/>
		<input type="hidden" name="expiresIn" id="expiresIn"/>
	</form>
</body>
</html>