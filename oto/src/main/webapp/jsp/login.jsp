<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.form.RegistrationForm"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/group.js"></script>
<script language="javascript" src="js/download.js"></script>
<script language="javascript">
function loginCheck(){
	document.getElementById('loginForm').submit();
}

function registrationCheck(){
	document.getElementById('registrationForm').submit();
}

function demoLogoOn() {
	document.getElementById('demoimg').src = "images/demo2.png";
}

function demoLogoOff() {
	document.getElementById('demoimg').src = "images/demo1.png";
}

function openDemo() {
	window
	.open(
			'demo.do',
			fileToShow,
			'height=630,width=1120, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');

}

//interface changes when clicking forget password
function forgetPass() {
	/*
	var it = document.getElementById("forgetPassTxt").innerHTML;
	if (it.indexOf("Forgot") > -1) {
		document.getElementById("getPassbtn").style["visibility"] = "visible";
		document.getElementById("passwordLine").style["visibility"] = "hidden";
		document.getElementById("loginBtn").style["visibility"] = "hidden";
		document.getElementById("forgetPassTxt").innerHTML = "Log in";
	} else {
		document.getElementById("getPassbtn").style["visibility"] = "hidden";
		document.getElementById("passwordLine").style["visibility"] = "visible";
		document.getElementById("loginBtn").style["visibility"] = "visible";
		document.getElementById("forgetPassTxt").innerHTML = "Forgot your password?";
	}*/
	
	if ($("#forgetPassTxt").html().indexOf("Forgot") > -1) {
		$("#getPassbtn").css("visibility", "visible");
		$("#passwordLine").css({"visibility":"hidden"});
		$("#loginBtn").css({"visibility":"hidden"});
		$("#forgetPassTxt").html("Log in");
	} else {
		$("#getPassbtn").css("visibility", "hidden");
		$("#passwordLine").css({"visibility":"visible"});
		$("#loginBtn").css({"visibility":"visible"});
		$("#forgetPassTxt").html("Forgot your password?");
	}
}

//submmit get password
function getPass() {
	var email = document.getElementById("userEmail").value;
	//var email = $("#userEmail").val();
	var XHR;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		XHR = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		XHR = new ActiveXObject("Microsoft.XMLHTTP");
	}
	  if(XHR) {
		  XHR.onreadystatechange = function () {
			  if (XHR.readyState == 4) {
				  if (XHR.status == 200) {
					  alert(XHR.responseXML.getElementsByTagName("response")[0].childNodes[0].nodeValue);
					  forgetPass();
				  }
			  }
		  };
		  XHR.open("POST", 'getPass.do', true);
		  XHR.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	      var x = 'value=' + email;
	      XHR.send(x);
	  }
}

</script>
</head>
<body>
<jsp:include page="loginHeader.jsp" />
<table width="100%" border="1">
	<tr>
		<td>
		<table width="100%">
			<tr>
				<td align="middle" rowspan="10" width="65%" height="550px" background="images/collage.jpg" style="background-size: 100%">
				</td>
				<td align="right" width="35%" style="vertical-align: top; padding-top: 0px">
				<table width="100%" cellpadding="8px">
					<tr>
						<td align="center"><font color="red" class="font-text-style" style="font-weight: bold;">Recommended Browsers: Firefox, Safari, Chrome</font></td>
					</tr>
					<tr>
						<td><font color="green" class="font-text-style" style="font-weight: bold;">Log in</font></td>
					</tr>
					<tr>
						<td
							style="background-image: url(images/header.jpg); background-repeat: no-repeat;">
						<form method="post" action="validateLogin.do" id="loginForm" name="loginForm">
						<table width="100%">
							<tr>
								<td align="right"><font class="font-text-style"
									color="White">Email</font></td>
								<td align="left"><input type="text" name="userEmail" id="userEmail"></input>&nbsp;<input 
								type="button" name="button" value="Get My Password" id="getPassbtn" style="visibility: hidden;"
									class="uiButton uiButtonSpecial uiButtonMedium" onclick="getPass()"></input></td>
							</tr>
							<tr id="passwordLine">
								<td align="right"><font class="font-text-style"
									color="White">Password</font></td>
								<td align="left"><input type="password" name="userPassword" id="userPassword"></input></td>
							</tr>
							<tr>
								<td> &nbsp;<br></br>
								</td>
								<td align="left"><input type="button" name="button" value="Log in" id="loginBtn"
									class="uiButton uiButtonSpecial uiButtonMedium" onclick="loginCheck()">
								<br> </br>
								<font class="font-text-style" color="White"><a href="#"
								onclick="forgetPass()"><font id="forgetPassTxt"
									class="font-text-style" color="White">Forgot your password?</font></a> | </font> <a href="#" onclick="fade('register',1500.0)"
								><font class="font-text-style" color="White" id="registerTxt">Register</font></a></td>
								
							</tr>
						</table>
						</form>
						</td>
					</tr>
					
					<tr>
						<td><% String message = (String)request.getAttribute("message"); %> 
								<%if (message != null) { %><label><b><%=message %></b></label><%} %></td>
					</tr>
					
					<!-- register part -->
					<tr>
						<td>
						<% String show = (String)request.getAttribute("show"); 
						String msopacity = "100"; String cropacity = "0";
						if(show != null) {
							msopacity = "0";
							cropacity = "100";
						} else {
							cropacity ="0";
							msopacity = "100";
						}
						
						RegistrationForm regForm = (RegistrationForm) request.getAttribute("registrationForm");
						String firstName = "", lastName = "", emailId = "", confirmEmail = "", aff ="";
						if(regForm != null) {
							firstName = regForm.getFirstName()== null ? "" : regForm.getFirstName();
							lastName = regForm.getLastName() == null? "" : regForm.getLastName();
							emailId = regForm.getEmailId() == null ?"":regForm.getEmailId();
							confirmEmail = regForm.getConfirmEmailId()==null?"":regForm.getConfirmEmailId();
							aff = regForm.getAffiliation()==null?"":regForm.getAffiliation();
						}
						%>
						<div id="register"
							style="height: 80px; filter: alpha(opacity =<%=msopacity%>); opacity: <%=cropacity%>;">
						<form id="registrationForm" method="post" action="register.do">
						<table width="100%">
							<tr>
								<td align="right" width="25%"><font class="font-text-style">First
								Name</font></td>

								<td align="left"><input type="text" name="firstName" id="firstname" value="<%=firstName%>"></td>
							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Last
								Name</font></td>
								<td align="left"><input type="text" name="lastName" id="lastname" value="<%=lastName%>"></td>
							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Email
								</font></td>
								<td align="left"><input type="text" name="emailId" id="emailId" value="<%=emailId%>" 
								onblur="checkEmail(this.value)"><label> <font class="font-text-style">(This
								is your Username)</font></label></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td align="left"><label id="emailCheck"></label></td>
							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Re-enter
								Email</font></td>
								<td align="left"><input type="text" name="confirmEmailId"
									id="confirmEmail" value="<%=confirmEmail%>"></td>
							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Password</font></td>
								<td align="left"><input type="password" name="regPassword" id="regpassword"></td>

							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Re-enter
								Password</font></td>
								<td align="left"><input type="password" name="confirmPassword"
									id="confirmPassword"></td>
							</tr>
							<tr>
								<td align="right"><font class="font-text-style">Affiliation</font></td>
								<td align="left"><input type="text" name="affiliation"
									id="affiliation" value="<%=aff%>"></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td align="left"><input type="button" name="button" value="Sign Up"
									class="uiButton uiButtonSpecial uiButtonMedium" onclick="registrationCheck()"></td>
								<tr>
									<td>&nbsp;</td>
									<td align="left"><label>All the fields are mandatory</label></td>
								</tr>
							</tr>
						</table>
						</form>
						</div>
						</td>
					</tr>					
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>

<jsp:include page="footer.jsp" />

</body>
<%@ page errorPage="error.jsp" %>
</html>