<html>
<head>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
	
<script language="javascript">
	function Syn() {
		window.returnValue = "syn";
		window.close();
	}
	function Exl() {
		window.returnValue = "exl";
		window.close();
	}
	function Cancel() {
		window.returnValue = "cancel";
		window.close();
	}
</script>
</head>

<body>
<table>
	<tr>
		<td colspan="3" height="50px">Please select the relationship between the two terms:</td>
	</tr>
	<tr>
		<td><input value="Synonym" type="button" onclick="Syn()" class="uiButton uiButtonSpecial uiButtonMedium"></input></td>
		<td><button onclick="Exl()">Exclusive</button></td>
		<td><button onclick="Cancel()">Cancel</button></td>
	</tr>
</table>
</body>
</html>
