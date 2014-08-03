<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<%@page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.TermDecision"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.UserDataAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/dtree.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/managers.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/managerSave.js"></script>
<script language="javascript">
	function showReport(tag) {
		window
				.open(
						'viewDecision.do?action=viewDecision&tag=' + tag,
						'',
						'height=500,width=1000, directories=no, toolbar=no, location=no, menubar=no,resizable=yes,scrollbars=yes, statusbar=no, left=0,top=0');
	}
</script>
</head>
<body>
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			session.setAttribute("manageType", "2");
			if (request.getParameter("s") != null) {
				session.setAttribute("selectedDataset",
						request.getParameter("s"));
			}
			UserDataAccess userDataAccess = new UserDataAccess();
			ArrayList<User> users = (ArrayList<User>) userDataAccess
					.getAllUsers();
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<%
		User manager = sessionDataManager.getUser();
			if (manager.getRole().equals("S")
					|| manager.getRole().equals("A")
					|| manager.getRole().equals("O")) {
				String selectedDataset = "";
				if (session.getAttribute("selectedDataset") != null) {
					selectedDataset = session.getAttribute(
							"selectedDataset").toString();
				}

				boolean canModify = true;
				if (!manager.getRole().equals("S")
						&& GlossaryNameMapper.getInstance()
								.isGlossaryReservedDataset(selectedDataset)) {
					canModify = false;
				}

				CharacterDBAccess cdba = new CharacterDBAccess();
				ArrayList<TermDecision> tdlist = null;
				if (!selectedDataset.equals("")) {
					tdlist = cdba
							.getHierarchyTermDecisions(selectedDataset);
				}
				//HashMap<String, Double> entropyScores = cdba
				//	.getTagsEntropyScores(selectedDataset);
	%>
	<table width="100%" style="border-top: 1px solid green">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td width="85%" valign="top">
				<table width="100%" style="height: 500px;">
					<tr>
						<td valign="top">
							<table>
								<tr>
									<td>
										<%
											boolean confirmed = cdba.isConfirmed(selectedDataset, 2);
													if (!confirmed) {
										%> <font class="font-text-style" id="datasetinfo"><b>This
												list shows the decisions have been made by users in the <font
												color="purple">Structure Hierarchy</font> page. All their
												decisions will be pending before being finalized.<br>
										</b> </font> <%
 	} else {
 %> <font class="font-text-style" id="datasetinfo"><b>The <font
												color="purple">Structure Hierarchy</font> page of this
												dataset has been finalized. The finalized results are
												showing below. <br></b></font> <%
 	}
 %> <label id="serverMessage"></label>
									</td>
									<td align="right">
										<%
											if (canModify) {
														if (!confirmed) {
										%>
										<form id="finishConfirming" name="generalForm"
											action="finishConfirming.do" method="post">
											<img src="images/green_rot.gif" id="downloadTerms"
												style="visibility: hidden" width="15px;" /><input
												type="button" name="button" value="Finalize this term set"
												class="uiButton uiButtonSpecial uiButtonMedium"
												style="padding: 0px 1px 1px 1px"
												onclick="finalizeDataset('<%=selectedDataset%>', '2')" />
										</form> <%
 	} else {
 %>
										<form id="reopenDataset" name="generalForm">
											<img src="images/green_rot.gif" id="reopen"
												style="visibility: hidden" width="15px;" /><input
												type="button" name="button" value="Reopen this term set"
												class="uiButton uiButtonSpecial uiButtonMedium"
												style="padding: 0px 1px 1px 1px"
												onclick="reopenDataset('<%=selectedDataset%>', '2')" />
										</form> <%
 	}
 			}
 %>
									</td>
								</tr>
							</table>

							<table width="100%" style="vertical-align: top;">
								<tr bgcolor="green">
									<th align="left" width="3%"><font class="font-text-style"
										color="white">#</font></th>
									<th align="left" width="12%"><font class="font-text-style"
										color="white">Term</font></th>
									<th align="left" width="40%"><font class="font-text-style"
										color="white">Accepted Paths</font></th>
									<th align="left" width="45%"><font class="font-text-style"
										color="white">Other Paths</font></th>
								</tr>


								<%
									int count = 1;
											boolean flag = true;
											String tdClass = "";

											if (tdlist != null) {
												for (TermDecision td : tdlist) {
													tdClass = (flag) ? "d0" : "d1";
													flag = (flag) ? false : true;
								%>
								<tr class="<%=tdClass%>">
									<td><font class="font-text-style"><%=count++%></font></td>
									<td><font class="font-text-style"><%=td.getTermName()%></font>
										<img
										title="View specific hierarchy report for <%=td.getTermName()%>"
										onclick="showReport('<%=td.getTermID() + ":" + td.getTermID()%>')"
										src="images/<%if (td.hasConflict()) {
								out.print("down.jpg");
							} else {
								out.print("view.gif");
							}%>"
										width="12px" style="vertical-align: middle; cursor: pointer;"></img>
									</td>
									<td style="padding: 0px; color: green" class="font-text-style">
										<table id="<%=td.getTermName()%>_acceptedDecisions">
											<%
												ArrayList<String> acceptedList = td
																		.getAccepedDecisions();
																for (String decision : acceptedList) {
											%>
											<tr>
												<td style="padding: 0px">
													<table id="<%=td.getTermName()%>"
														term="<%=td.getTermName()%>"
														dataset="<%=selectedDataset%>" decision="<%=decision%>">
														<tr>
															<td style="padding: 0px"><font
																class="font-text-style"><%=decision%></font></td>
															<%
																if (canModify && !confirmed) {
															%>
															<td style="padding: 0px"><img
																src="images/revoke.jpg" height="12px" title="Revoke"
																onclick="revokePath(this)" style="cursor: pointer;"></img></td>
															<%
																}
															%>
														</tr>
													</table>
												</td>
											</tr>
											<%
												}
											%>
										</table>
									</td>
									<td style="padding: 0px; color: green" class="font-text-style">
										<table id="<%=td.getTermName()%>_others">
											<%
												ArrayList<String> unConfirmedList = td
																		.getUnconfirmedDecisions();
																for (String decision : unConfirmedList) {
											%>
											<tr>
												<td style="padding: 0px">
													<table id="<%=td.getTermName()%>"
														term="<%=td.getTermName()%>"
														dataset="<%=selectedDataset%>" decision="<%=decision%>">
														<tr>
															<td style="padding: 0px"><font
																class="font-text-style"><%=decision%></font></td>
															<%
																if (canModify && !confirmed) {
															%>
															<td style="padding: 0px"><img
																src="images/accept.jpg" height="13px" title="Accept"
																onclick="acceptPath(this)" style="cursor: pointer;"></img></td>
															<%
																}
															%>
														</tr>
													</table>
												</td>
											</tr>
											<%
												}
											%>
										</table>
									</td>
								</tr>
								<%
									}
											}
								%>

							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<%
		} else {
	%>
	<table>
		<tr>
			<td>
				<h2>
					<font class="font-text-style">You have not been authorized
						for users and decisions management. Please contact us for
						authorization. Thanks. </font>
				</h2>
			</td>
		</tr>
	</table>
	<%
		}
		} else {
	%>

	<jsp:include page="loginHeader.jsp" />
	<font class="font-text-style"> Your session has timed off.
		Please <a href="<%=request.getContextPath()%>">login</a>
	</font>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>
