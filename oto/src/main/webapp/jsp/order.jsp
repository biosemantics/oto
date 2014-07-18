<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.TagBean"%>
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.StructureNodeBean"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Order"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Character"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/orderStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/context.js"></script>
<script language="javascript" src="js/order.js"></script>
<script language="javascript" src="js/download.js"></script>
<script language="javascript" src="js/reset.js"></script>
</head>

<body onload="order_init()">
	<div id="dragging_mask"></div>
	<div id="glossaryTerm"></div>
	<div id="sourceFiles"></div>

	<!-- Session Validity check -->

	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			boolean flag = true;
			String tdClass = "";
			String dataset = sessionDataManager.getDataset();

			User user = sessionDataManager.getUser();
			CharacterDBAccess cdba = new CharacterDBAccess();
			ArrayList<Order> baseOrders = cdba.getBaseOrders(dataset);
			HashMap<Integer, ArrayList<Order>> orderMap = cdba
					.getAllOrders(dataset, user, baseOrders);
			//HashMap<Integer, Double> entropyScores = cdba.getOrdersEntropyScores(dataset);
			int unSavedOrders = 0;
	%>
	<jsp:include page="header.jsp" />
	<table border="1" width="100%">
		<tr>
			<td><font class="font-text-style" id="dataset_info">Current
					Dataset: <b><%=dataset%></b>
			</font> <%
 	if (dataset.equals("OTO_Demo")) {
 %> <input type="button" class="uiButton uiButtonMedium"
				style="margin-left: 10px"
				title="Reset OTO_Demo dataset by clearing all decisions. Any user can reset this dataset, therefore your decisions in OTO_Demo dataset may be deleted by other users. "
				value="Reset to initial status" onclick="resetOTODemo(3)"></input> <%
 	}
 %></td>
		</tr>
		<%
			if (!cdba.isConfirmed(dataset, 3)) {
		%>
		<tr>
			<td>
				<div class="dragging_part" id="dragging_part">
					<%
						if (baseOrders.size() > 0) {

									for (Order baseOrder : baseOrders) {
										ArrayList<Order> orders = orderMap.get(baseOrder
												.getID());
										int termsNumber = baseOrder.getTermsNumber();
					%>
					<div id="<%=baseOrder.getID()%>" class="order_group">
						<table width="100%" id="group_<%=baseOrder.getID()%>">
							<tr>
								<th width="15%" class="order_base"><%=baseOrder.getName()%>:&nbsp;&nbsp;</th>
								<th class="terms_base" width="85%">
									<table>
										<tr>
											<%
												ArrayList<Character> terms = baseOrder.getTerms();
																for (Character term : terms) {
											%>
											<td id=<%=term.getID()%> class="term_base"><%=term.getName()%></td>
											<%
												}
											%>
											<td class="greenLink"><a href="javascript:void(0)"
												onclick="newTerm(this)">New Term</a></td>
											<td class="greenLink"><a href="javascript:void(0)"
												onclick="newOrder(this)">New Order</a></td>
											<td align="right">
												<form id="<%=baseOrder.getBaseOrderID()%>_form"
													name="generalForm" method="post">
													<input type="button" name="button" value="Save Orders"
														class="uiButton uiButtonSpecial uiButtonMedium"
														style="padding: 0px 1px 1px 1px"
														onclick="save_order(this)"
														onmouseover="document.getElementById('<%=baseOrder.getID()%>_savingMsg').innerHTML='&nbsp;'" />
													<input type="hidden" name="orderID"
														value="<%=baseOrder.getBaseOrderID()%>" /> <input
														type="hidden" name="baseOrderID"
														value="<%=baseOrder.getID()%>" /> <input type="hidden"
														id="<%=baseOrder.getID()%>_hiddenvalue" name="value" />&nbsp;<img
														src="images/green_rot.gif"
														id="<%=baseOrder.getID()%>_processingSaveImage"
														style="visibility: hidden" width="15px;" /><font
														class="savingMsg"
														onmouseover="document.getElementById('<%=baseOrder.getID()%>_savingMsg').innerHTML='&nbsp;'"
														id="<%=baseOrder.getID()%>_savingMsg" style="color: green">&nbsp;</font>
												</form>
											</td>
										</tr>
									</table>
								</th>

							</tr>
							<%
								for (Order order : orders) {
							%>
							<tr class="tr_order" id=<%=order.getID()%>>
								<td class="order" id="<%=order.getName()%>" width="15%"
									onmouseover="displayEditBtn(this)"
									onmouseout="hideEditBtn(this)" orderName="<%=order.getName()%>"
									orderID="<%=order.getID()%>"><label style="color: black"
									title="<%=order.getExplanation()%>"
									onclick="showSelectedTerms(this)"><%=order.getName()%></label><img
									align="bottom" class="editOrderName" src="images/edit.png"
									height="14px" title="Edit Order Name" style="display: none;"
									onclick="editOrderName('<%=baseOrder.getID()%>', '<%=order.getName()%>')" />&nbsp;<a
									href="javascript:void(0)"
									title="View specific order report for <%=order.getName()%>"
									onclick="showReport('<%=order.getID() + ":" + order.getName()%>')"><img
										border="0px" style="vertical-align: middle;"
										src="images/<%=((order.hasConflict()) ? "down.jpg"
										: "view.gif")%>"
										width="12px"></img></a></td>
								<td class="terms_order" width="85%">
									<table>
										<tr class="terms_tr">
											<%
												if (order.savedBefore()) {
																		int positions = 0;
																		ArrayList<Character> order_terms = order
																				.getTerms();
																		int lastPosition = 0, currentPosition = 0;
																		for (int i = 0; i < order_terms.size(); i++) {
																			currentPosition = order_terms.get(i)
																					.getDistance();
																			if (currentPosition == lastPosition) {
																				if (i == 0) {
																					out.print("<td width='40px' class='term_order'>");
																					positions++;
																				}
																				out.print("<div id = '"
																						+ order_terms.get(i)
																								.getName()
																						+ "'"
																						+ (order_terms.get(i)
																								.isBase() ? "rel='base'"
																								: "")
																						+ ">"
																						+ order_terms.get(i)
																								.getName()
																						+ "</div>");
																			} else {
																				positions++;
																				out.print("</td><td width='40px' class='term_order'><div id = '"
																						+ order_terms.get(i)
																								.getName()
																						+ "'"
																						+ (order_terms.get(i)
																								.isBase() ? "rel='base'"
																								: "")
																						+ ">"
																						+ order_terms.get(i)
																								.getName()
																						+ "</div>");
																			}
																			lastPosition = currentPosition;
																		}
																		out.print("</td>");

																		for (int i = 0; i < termsNumber - positions; i++) {
											%><td width="40px" class="term_order">&nbsp;</td>
											<%
												}

																	} else {
																		for (int i = 0; i < termsNumber; i++) {
											%><td width="40px" class="term_order"><%=(i == 0 ? "<div id = '"
												+ order.getBaseTermName()
												+ "' rel='base'>"
												+ order.getBaseTermName()
												+ "</div>" : "&nbsp;")%></td>
											<%
												}
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
					</div>
					<%
						}
								} else {
					%>
					<font class="font-text-style">There is no task for you now.
					</font>
					<%
						}
					%>
				</div>
			</td>
		</tr>
		<%
			} else {
		%>
		<tr>
			<td height="480px" align="center"><font class="font-text-style"
				color="green" style="font-size: 15px"> Dataset <b><%=dataset%></b>
					has been reviewed and finalized! <br></br> <a
					href="gotoDownload.do">Click here</a> to go to the download page.
			</font><br></br> <label id="serverMsg">&nbsp;</label></td>
		</tr>
		<%
			}
		%>
		<tr>
			<td>
				<!-- the context part -->
				<div>
					<table width="100%" cellspacing="0px">
						<tr>
							<th width="15%" class="currentContext" id="context"
								onclick="showContext()"><font>Context</font></th>
							<th width="15%" class="backContext" id="glossary"
								onclick="showGlossary()"><font>Glossaries</font></th>
							<th width="70%" align="left" bgcolor="white"><font
								color="green">&nbsp;</font></th>
						</tr>
						<tr>
							<td width="85%" colspan="3" style="border: 2px solid green">

								<table width="100%">
									<tr>
										<th width="15%" align="left" id="th_context_1">Source</th>
										<th width="85%" align="left" id="th_context_2">Sentence</th>
									</tr>
								</table>
								<div style="width: 100%; height: 80px; overflow: auto;"
									class="border" id="contextSentences">
									<table width="100%" id="contextTable">
										<tr>
											<td width="15%">&nbsp;</td>
											<td width="85%">&nbsp;</td>
										</tr>

										<tr>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>

										<tr>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>

										<tr>
											<td>&nbsp;</td>
											<td>&nbsp;</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<%
		if (unSavedOrders == 0) {
	%>
	<!-- 	<script language="javascript">
		alert("Congratulations! \n\nYou have finished organizing all the existing orders in this page.");
	</script> -->
	<%
		}
		} else {
	%>

	<jsp:include page="loginHeader.jsp" /><font class="font-text-style">
		Your session has timed off. Please <a
		href="<%=request.getContextPath()%>">login</a>
	</font>
	<%
		}
	%>
	<jsp:include page="footer.jsp" />
</body>
<%@ page errorPage="error.jsp"%>
</html>