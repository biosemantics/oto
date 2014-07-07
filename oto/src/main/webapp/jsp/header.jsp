<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/menu.js"></script>
<%
	User user = ((SessionDataManager) session
			.getAttribute("sessionDataMgr")).getUser();
	System.out.println("user::" + user);
	System.out.println("--" + user.getFirstName());
	System.out.println("--" + user.getLastName());
	System.out.println("--" + user.getRole());
	boolean showAdminPage = false;
	if (user.getRole().equals("S") || user.getRole().equals("A")
			|| user.getRole().equals("O")) {
		showAdminPage = true;
	}
%>
<table style="width: 100%">
	<tr>
		<td width="77%">
			<div id="nav">
				<div id="nav-menu-left"></div>
				<div id="nav-menu">
					<!-- start of navigation -->
					<ul style="padding-left: 5px" class="dropdown">
						<li><a href="gotoWelcomepage.do">Home</a></li>
						<%
							String dataset = ((SessionDataManager) session
									.getAttribute("sessionDataMgr")).getDataset();
							if (dataset != null) {
						%>
						<li><a href="groupTerms.do"
							title="Group the terms as per your discretion"><font
								style="font-weight: 800;">Group Terms</font></a></li>
						<li><a href="hierarchy.do"
							title="Build the structure hierarchy as per your discretion"><font
								style="font-weight: 800;">Structure Hierarchy</font></a></li>
						<li><a href="order.do"
							title="Put character states in order as per your discretion"><font
								style="font-weight: 800;">Term Order</font></a></li>
						<%
							if (user.getRole().equals("S") || user.getRole().equals("A")) {
						%>
						<li><a href="ontologyLookup.do"
							title="Send terms to ontologies"><font
								style="font-weight: 800;">To Ontologies</font></a></li>
						<%
							}
							}
						%>
						<li><a href="userSpecificReport.do"
							title="This report shows all the terms you have assigned a decision">Reports</a></li>
						<li><a href="#">Account</a>
							<ul class="sub_menu" id="sub_menu_accounts">
								<li><a href="account.do"  title="Account Settings">Settings</a></li>
								<li><a href="manageDatasets.do"  title="Manage your datasets">Manage Datasets</a></li>

							</ul></li>
						<li><a href="webService.do"
							title="Download finalized terms sets">Web Service</a></li>
						<%
							if (showAdminPage) {
						%>
						<li><a href="admin.do" title="Approve or Revoke users">Admin
								Tasks</a></li>
						<%
							}
						%>

					</ul>
					<!-- end navigation -->
				</div>
				<div id="nav-menu-right"></div>
			</div>
		</td>
		<td width="1%"><img src="images/garland_logo.gif" height="35"></img></td>
		<td align="right" width="22%"><font class="font-text-style">Welcome!
				<b><%=user.getFirstName() + " " + user.getLastName()%></b>&nbsp;| <a
				href="logout.do">Logout</a>&nbsp;|&nbsp;<a href="intro.do"
				title="Instructions of completing your tasks" target="_blank">Help</a><br><%=user.getUserEmail()%></font>&nbsp;&nbsp;&nbsp;
		</td>
	</tr>
</table>