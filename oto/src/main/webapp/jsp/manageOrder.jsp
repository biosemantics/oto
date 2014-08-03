<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryGroupBean"%>
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.OrderDecisionBean"%>
<%@page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.TermDecision"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Order"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Character"%>
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
	function showReport(order) {
		window
				.open(
						'comment.do?action=viewDecision&order=' + order,
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
			session.setAttribute("manageType", "3");
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
				ArrayList<Order> orderlist = null;
				if (!selectedDataset.equals("")) {
					orderlist = cdba.getDecidedOrders(selectedDataset);
				}
	%>
	<table width="100%" style="height: 500px; border-top: 1px solid green">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td valign="top" width="85%">
				<table width="100%" style="overflow: auto;">
					<tr>
						<td>
							<%
								boolean confirmed = cdba.isConfirmed(selectedDataset, 3);
										if (!confirmed) {
							%> <font class="font-text-style" id="datasetinfo"><b>This
									list shows the decisions have been made by users in the <font
									color="purple">Term Order</font> page. All their decisions will
									be pending before being finalized.<br>
							</b></font> <%
 	} else {
 %> <font class="font-text-style" id="datasetinfo"><b>The <font
									color="purple">Term Order</font> page of this dataset has been
									finalized. The finalized results are showing below. <br></b></font> <%
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
									onclick="finalizeDataset('<%=selectedDataset%>', '3')" />
							</form> <%
 	} else {
 %>
							<form id="reopenDataset" name="generalForm">
								<img src="images/green_rot.gif" id="reopen"
									style="visibility: hidden" width="15px;" /><input
									type="button" name="button" value="Reopen this term set"
									class="uiButton uiButtonSpecial uiButtonMedium"
									style="padding: 0px 1px 1px 1px"
									onclick="reopenDataset('<%=selectedDataset%>', '3')" />
							</form> <%
 	}
 			}
 %>
						</td>
					</tr>
					<tr>
						<td>
							<table width="100%">
								<!-- the order table, each order is one row -->
								<%
									if (orderlist != null && orderlist.size() > 0) {
												for (Order order : orderlist) {
													int startPosition = 0, endPostion = 0;
													//get a user list
													//get a distance-terms(ArrayList<Character>) map for each user
													ArrayList<OrderDecisionBean> odbs = cdba
															.getSavedDecisionsForOrder(selectedDataset,
																	order.getID());

													ArrayList<TermDecision> tdList = cdba
															.getOrdersTermDecisions(selectedDataset,
																	order.getID());
								%>
								<tr>
									<td style="padding-top: 3px">
										<div>
											<table
												style="border-collapse: collapse; border: 1px solid #C0C0C0;"
												width="100%">
												<tr>
													<th style="text-align: left; background-color: #66CC66"><font
														class="font-text-style"
														title="<%=order.getExplanation()%>">&nbsp;<%=order.getName()%></font>
														<label> <a style="color: white"
															href="javascript:showReport('<%=order.getID() + ":" + order.getName()%>')"
															title="View term specific report for <%=order.getName()%>"><img
																src="images/<%=(order.hasConflict() ? "down.jpg"
									: "view.gif")%>"
																width="13px"></img></a></label></th>
												</tr>
												<tr>
													<td style="padding: 0px">
														<table
															style="border: 1px solid #C0C0C0; border-collapse: collapse;">
															<!-- distance table -->
															<tr style="border: 1px solid #C0C0C0">
																<th align="left"
																	style="border: 1px solid #C0C0C0; background-color: #B0F1A0">Position</th>
																<%
																	int index = 0;
																					for (TermDecision td : tdList) {
																%>
																<th
																	style="text-align: center; background-color: #B0F1A0; border: 1px solid #C0C0C0;"><%=index++%></th>
																<%
																	}
																%>
															</tr>
															<!-- accepted row -->
															<tr style="border: 1px solid #C0C0C0"
																id="<%=order.getID()%>" class="acceptedRow">
																<th align="left"
																	style="border: 1px solid #C0C0C0; background-color: #B0F1A0; height: 25px"><font
																	class="font-text-style">Accepted</font></th>
																<%
																	boolean gotStartP = false;
																					for (TermDecision td : tdList) {
																						if (!gotStartP) {
																							startPosition = td.getDistance();
																							endPostion = startPosition;
																							gotStartP = true;
																						}
																						out.print("<td style='border: 1px solid #C0C0C0; vertical-align: top; font-weight: bold' id='"
																								+ td.getDistance()
																								+ "' class='"
																								+ td.getDistance() + "_accepted'>");
																						if (td.getDistance() == 0) {
																%>
																<div
																	style="padding: 0px 2px 0px 2px; color: blue; font-family: lucida grande; font-size: 12px"
																	id="<%=order.getBaseTermName()%>"><%=order.getBaseTermName()%></div>
																<%
																	}
																						ArrayList<String> acceptedList = td
																								.getAccepedDecisions();
																						for (String accepted : acceptedList) {
																%>
																<div
																	style="padding: 0px 2px 0px 2px; font-family: lucida grande; font-size: 12px"
																	id="<%=accepted%>"><%=accepted%>
																	<%
																		if (canModify && !confirmed) {
																	%>
																	<img rel="<%=selectedDataset%>" src="images/revoke.jpg"
																		width="11px" title="Revoke"
																		onclick="revokeTermOFOrder(this)"></img>
																	<%
																		}
																	%>
																</div>
																<%
																	}
																						out.print("</td>");
																						endPostion++;
																					}
																%>

															</tr>
															<!-- each user one line -->
															<%
																for (OrderDecisionBean odb : odbs) {
															%>
															<tr style="border: 1px solid #C0C0C0"
																id="<%=order.getID()%>">
																<td align="left"
																	style="border: 1px solid #C0C0C0; background-color: #B0F1A0;"><font
																	class="font-text-style"><%=odb.getUserName()%></font></td>
																<%
																	for (int i = startPosition; i < endPostion; i++) {
																%>
																<td
																	style='border: 1px solid #C0C0C0; vertical-align: top;'
																	id="<%=i%>">
																	<%
																		HashMap<Integer, ArrayList<Character>> hm = odb
																										.getDecisions();
																								if (hm.get(i) != null) {
																									ArrayList<Character> chas = hm.get(i);
																									for (Character cha : chas) {
																	%>
																	<div
																		style="padding: 0px 2px 0px 2px; color: <%=(cha.accepted() ? "grey"
													: "black")%>; font-family: lucida grande; font-size: 12px"
																		id="<%=cha.getName()%>"
																		class="term_<%=cha.getName()%>"><%=cha.getName()%>
																		<%
																			if (canModify && !confirmed) {
																		%>
																		<img rel="<%=selectedDataset%>"
																			src="images/accept<%=(cha.accepted() ? "_grey"
														: "")%>.jpg"
																			width="11px" title="Accept"
																			onclick="acceptTermOfOrder(this)"></img>
																		<%
																			}
																		%>
																	</div> <%
 	}

 							}
 %>
																</td>
																<%
																	}
																%>
															</tr>
															<%
																}
															%>
														</table>
													</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<%
									}
											} else {
								%>
								<tr>
									<td><font class="font-text-style">No decision has
											been made in this data set.</font></td>
								</tr>
								<tr>
									<td height="400px"></td>
								</tr>
								<%
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
