<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermDecision"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.AdminDecisionBean"%>

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
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/managerSave.js"></script>
<script language="javascript">
	function showTermsReport(term) {
		window
				.open(
						'viewDecision.do?action=viewDecision&term=' + term,
						"",
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
			session.setAttribute("manageType", "1");
			if (request.getParameter("s") != null) {
				session.setAttribute("selectedDataset",
						request.getParameter("s"));
			}

			int currentPage = 1;
			CharacterDBAccess cdba = new CharacterDBAccess();
			int rowPerPage = cdba.getRowPerPage();
			if (request.getParameter("page") != null) {
				currentPage = Integer.parseInt(request.getParameter("page")
						.toString());
			}
	%>
	<!-- Session Validity check header End -->
	<jsp:include page="header.jsp" />
	<%
		User user = sessionDataManager.getUser();
			if (user.getRole().equals("S") || user.getRole().equals("A")
					|| user.getRole().equals("O")) {

				//get dataset name
				String selectedDataset = "";
				if (session.getAttribute("selectedDataset") != null) {
					selectedDataset = session.getAttribute(
							"selectedDataset").toString();
				}

				boolean canModify = true;
				if (!user.getRole().equals("S")
						&& GlossaryNameMapper.getInstance()
								.isGlossaryReservedDataset(selectedDataset)) {
					canModify = false;
				}

				//get tab name
				String selectedTab = "category";
				if (request.getParameter("tab") != null) {
					selectedTab = request.getParameter("tab").toString();
				}
				boolean isInCategoryTab = true;
				if (!selectedTab.equals("category")) {
					isInCategoryTab = false;
				}
	%>
	<div style="display: none;" id="USERNAME"><%=user.getFirstName() + " " + user.getLastName()%></div>
	<table width="100%" style="border-top: 1px solid green">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td width="85%" valign="top">
				<%
					String mergedInto = cdba.getMergedInto(selectedDataset,
									false);
							if (!mergedInto.equals("")) {
				%>
				<p>
					<font class="font-text-style">Dataset <b>'<%=selectedDataset%>'
					</b> has been merged into dataset <b>'<%=mergedInto%>'
					</b>. You may view the merged dataset by clicking <a
						href="manageCategory.do?s=<%=mergedInto%>">Go to '<%=mergedInto%>'
					</a>.
					</font>
				</p> <%
 	} else {
 %> <!-- tabs -->
				<div>
					<table width="100%" cellspacing="0px">
						<tr>
							<%
								if (isInCategoryTab) {
							%>
							<th width="8%" class="currentTab"
								style="border-bottom: 1px solid green">Categories</th>
							<th width="8%" class="backTab"
								style="border-bottom: 1px solid green"><a
								href="manageCategory.do?s=<%=selectedDataset%>&tab=syns">Synonyms</a></th>
							<%
								} else {
							%>
							<th width="8%" class="backTab"
								style="border-bottom: 1px solid green"><a
								href="manageCategory.do?s=<%=selectedDataset%>&tab=category">Categories</a></th>
							<th width="8%" class="currentTab"
								style="border-bottom: 1px solid green">Synonyms</th>
							<%
								}
							%>
							<%
								boolean confirmed = cdba
													.isConfirmed(selectedDataset, 1);
							%>
							<td width="64%"
								style="border-bottom: 1px solid green; text-align: center;"><font
								color="gray">Dataset Name: </font><font color="green"><b><%=selectedDataset%></b></font><font
								color="gray">, Status: </font><font color="green"><b><%=confirmed ? "Finalized" : "Unfinalized"%></b></font></td>

							<%
								if (canModify) {
												if (confirmed) {
							%>
							<td align="right" width="10%"
								style="border-bottom: 1px solid green"><input type="button"
								name="button" value="Reopen this dataset"
								class="uiButton uiButtonSpecial uiButtonMedium"
								style="padding: 0px 1px 1px 1px"
								onclick="reopenDataset('<%=selectedDataset%>', '1')" /></td>
							<%
								} else {
													if (!(GlossaryNameMapper.getInstance()
															.isGlossaryReservedDataset(selectedDataset))) {
							%>
							<td align="right" valign="middle" width="10%"
								style="border-bottom: 1px solid green"><input type="button"
								name="button" value="Approve all System Categories"
								title="Copy all approved categorization and synonyms decisions from system"
								class="uiButton uiButtonSpecial uiButtonMedium"
								style="padding: 0px 1px 1px 1px"
								onclick="copyAcceptedDecisionsTo('<%=selectedDataset%>')" /></td>
							<%
								}
							%>
							<td align="right" valign="middle" width="10%"
								style="border-bottom: 1px solid green"><input type="button"
								name="button" value="Finalize this dataset"
								title="Finalize all approved categorization and synonyms decisions"
								class="uiButton uiButtonSpecial uiButtonMedium"
								style="padding: 0px 1px 1px 1px"
								onclick="finalizeDataset('<%=selectedDataset%>', '1')" /></td>
							<%
								}
											}
							%>
						</tr>
					</table>
				</div> <!-- end of tabs --> <label id="serverMessage"
				style="font-weight: bold"></label> <%
 	int pageNum = 1;
 				if (isInCategoryTab) {
 					pageNum = cdba
 							.getPagesCountForManageCategory(selectedDataset);
 				} else {
 					pageNum = cdba
 							.getPagesCountForManageSynonyms(selectedDataset);
 				}
 %> <!-- page list --> <%
 	if (pageNum > 1) {
 %> <!-- page list -->
				<table width="100%" style="font-family: sans-serif; font-size: 12px">
					<tr>
						<td width="100%" align="right">
							<%
								if (currentPage > 1) {
							%> <a
							href="manageCategory.do?s=<%=selectedDataset%>&page=1&tab=<%=isInCategoryTab ? "category" : "syns"%>">1</a>
							<%
								}

												if (currentPage - 2 > 1) {
							%>&nbsp;...&nbsp;<%
								}

												if (currentPage - 1 > 1) {
							%> <a
							href="manageCategory.do?s=<%=selectedDataset%>&page=<%=currentPage - 1%>&tab=<%=isInCategoryTab ? "category" : "syns"%>"><%=currentPage - 1%></a>
							<%
								}
							%> <a><%=currentPage%></a> <%
 	if (currentPage + 1 <= pageNum) {
 %> <a
							href="manageCategory.do?s=<%=selectedDataset%>&page=<%=currentPage + 1%>&tab=<%=isInCategoryTab ? "category" : "syns"%>"><%=currentPage + 1%></a>
							<%
								}

												if (currentPage + 2 < pageNum) {
							%>&nbsp;...&nbsp;<%
								}

												if (currentPage + 1 < pageNum) {
							%> <a
							href="manageCategory.do?s=<%=selectedDataset%>&page=<%=pageNum%>&tab=<%=isInCategoryTab ? "category" : "syns"%>"><%=pageNum%></a>
							<%
								}
							%>
						</td>
					</tr>
				</table> <%
 	}
 %> <!-- end of page list --> <%
 	if (isInCategoryTab) {
 %>
				<table width="100%">
					<tr bgcolor="green">
						<th align="left" width="5%"><font class="font-text-style"
							color="white">#</font></th>
						<th align="left" width="15%"><font class="font-text-style"
							color="white">Term</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Accepted Decisions</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Other Decisions</font></th>
					</tr>

					<%
						ArrayList<TermDecision> tdlist = null;
										if (!selectedDataset.equals("")) {
											tdlist = cdba.getCategoryTermDecisions(
													selectedDataset, currentPage);
										}
										int count = rowPerPage * (currentPage - 1) + 1;
										boolean flag = true;
										String tdClass = "";

										if (tdlist != null && tdlist.size() > 0) {
											for (TermDecision td : tdlist) {
												tdClass = (flag) ? "d0" : "d1";
												flag = (flag) ? false : true;
					%>
					<tr class="<%=tdClass%>">
						<td><font class="font-text-style"><%=count++%></font></td>
						<td><font class="font-text-style"><%=td.getTermName()%></font>
							<label><a
								href="javascript:showTermsReport('<%=td.getTermName()%>')"
								title="View term specific report for <%=td.getTermName()%>"><img
									border="0px"
									src="images/<%if (td.hasConflict())
										out.print("down.jpg");
									else
										out.print("view.gif");%>"
									height="11px"></img></a></label></td>
						<td style="color: green" style="padding: 0px">
							<table id="<%=td.getTermName()%>_acceptedDecisions">
								<%
									ArrayList<AdminDecisionBean> acceptedList = td
																	.getConfirmedDecisionBeans();
															for (AdminDecisionBean decision : acceptedList) {
								%>
								<tr class="decision_tr">
									<td style="padding: 0px">
										<table id="<%=td.getTermName()%>" term="<%=td.getTermName()%>"
											dataset="<%=selectedDataset%>"
											decision="<%=decision.getCategory()%>"
											decidedBy="By: <%=decision.getDecidedBy()%>">
											<tr>
												<td width="140px" style="padding: 0px"
													title="By: <%=decision.getDecidedBy()%>"><font
													class="font-text-style"
													color="<%=decision.getCategory()
												.equals("discarded") ? "red"
												: ""%>"><%=decision.getCategory()%></font></td>
												<%
													if (canModify && !confirmed) {
												%>
												<td style="padding: 0px"><img src="images/revoke.jpg"
													decidedBy="By: <%=decision.getDecidedBy()%>" height="12px"
													title="Revoke" onclick="revokeCategory(this)"
													style="cursor: pointer;"></img></td>
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
						<td style="padding: 0px">
							<table id="<%=td.getTermName()%>_unConfirmedDecisions">
								<%
									ArrayList<AdminDecisionBean> unconfirmedDecisions = td
																	.getUnconfirmedDecisionsbeans();
															//ArrayList<String> unConfirmedList = td.getUnconfirmedDecisions();
															for (AdminDecisionBean decision : unconfirmedDecisions) {
								%>
								<tr class="decision_tr">
									<td style="padding: 0px">
										<table id="<%=td.getTermName()%>" term="<%=td.getTermName()%>"
											dataset="<%=selectedDataset%>"
											decision="<%=decision.getCategory()%>"
											decidedBy="By: <%=decision.getDecidedBy()%>">
											<tr>
												<td width="140px" style="padding: 0px"
													title="By: <%=decision.getDecidedBy()%>"><font
													class="font-text-style"
													color="<%=decision.getCategory()
												.equals("discarded") ? "red"
												: ""%>"><%=decision.getCategory()%></font></td>
												<%
													if (canModify && !confirmed) {
												%>
												<td style="padding: 0px"><img src="images/accept.jpg"
													decidedBy="By: <%=decision.getDecidedBy()%>" height="13px"
													title="Accept" onclick="acceptCategory(this)"
													style="cursor: pointer;"></img></td>
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
										} else {
					%>
					<tr>
						<td colspan="4"><font class="font-text-style">No
								decision has been made in this data set.</font></td>
					</tr>
					<%
						}
					%>

				</table> <%
 	} else { //synonym page
 %>
				<table width="100%">
					<tr bgcolor="green">
						<th align="left" width="5%"><font class="font-text-style"
							color="white">#</font></th>
						<th align="left" width="10%"><font class="font-text-style"
							color="white">Term</font></th>
						<th align="left" width="10%"><font class="font-text-style"
							color="white">Category</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Accepted Synonyms</font></th>
						<th align="left" width="25%"><font class="font-text-style"
							color="white">Other Synonyms</font></th>
					</tr>
					<%
						ArrayList<TermDecision> tdlist = null;
										if (!selectedDataset.equals("")) {
											tdlist = cdba.getTermSynonymsDecisions(
													selectedDataset, currentPage);
										}
										int count = rowPerPage * (currentPage - 1) + 1;
										boolean flag = true;
										String tdClass = "";

										if (tdlist != null && tdlist.size() > 0) {
											for (TermDecision td : tdlist) {
												tdClass = (flag) ? "d0" : "d1";
												flag = (flag) ? false : true;
					%>
					<tr class="<%=tdClass%>">
						<td><font class="font-text-style"><%=count++%></font></td>
						<td><font class="font-text-style"><%=td.getTermName()%></font>
							<label><a
								href="javascript:showTermsReport('<%=td.getTermName()%>')"
								title="View term specific report for <%=td.getTermName()%>"><img
									border="0px"
									src="images/<%if (td.hasConflict())
										out.print("down.jpg");
									else
										out.print("view.gif");%>"
									height="11px"></img></a></label></td>
						<td><font class="font-text-style"><%=td.getCategory()%></font></td>
						<td style="color: green" style="padding: 0px">
							<table
								id="<%=td.getTermName()%>_<%=td.getCategory()%>_acceptedDecisions">
								<%
									ArrayList<AdminDecisionBean> acceptedList = td
																	.getConfirmedDecisionBeans();
															for (AdminDecisionBean decision : acceptedList) {
								%>
								<tr>
									<td style="padding: 0px">
										<table term="<%=td.getTermName()%>"
											dataset="<%=selectedDataset%>"
											category="<%=td.getCategory()%>"
											decision="<%=decision.getSynonym()%>">
											<tr>
												<td width="140px" style="padding: 0px"
													title="By: <%=decision.getDecidedBy()%>"><font
													class="font-text-style"><%=decision.getSynonym()%></font></td>
												<%
													if (canModify && !confirmed) {
												%>
												<td style="padding: 0px"><img src="images/revoke.jpg"
													decidedBy="By: <%=decision.getDecidedBy()%>" height="12px"
													title="Revoke" onclick="revokeSynonym(this)"
													style="cursor: pointer;"></img></td>
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
						<td style="padding: 0px">
							<table
								id="<%=td.getTermName()%>_<%=td.getCategory()%>_unConfirmedDecisions">
								<%
									ArrayList<AdminDecisionBean> unconfirmedDecisions = td
																	.getUnconfirmedDecisionsbeans();
															//ArrayList<String> unConfirmedList = td.getUnconfirmedDecisions();
															for (AdminDecisionBean decision : unconfirmedDecisions) {
								%>
								<tr>
									<td style="padding: 0px">
										<table term="<%=td.getTermName()%>"
											dataset="<%=selectedDataset%>"
											category="<%=td.getCategory()%>"
											decision="<%=decision.getSynonym()%>">
											<tr>
												<td width="140px" style="padding: 0px"
													title="By: <%=decision.getDecidedBy()%>"><font
													class="font-text-style"><%=decision.getSynonym()%></font></td>
												<%
													if (canModify && !confirmed) {
												%>
												<td style="padding: 0px"><img src="images/accept.jpg"
													decidedBy="By: <%=decision.getDecidedBy()%>" height="13px"
													title="Accept" onclick="acceptSynonym(this)"
													style="cursor: pointer;"></img></td>
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
										} else {
					%>
					<tr>
						<td colspan="4"><font class="font-text-style">No
								synonym decisions are available in this dataset.</font></td>
					</tr>
					<%
						}
					%>

				</table> <%
 	}

 			}
 %>
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
