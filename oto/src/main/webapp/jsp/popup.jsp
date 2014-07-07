<%@page import="edu.arizona.biosemantics.oto.oto.beans.SynBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.SpecificReport"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SpecificReportBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Term"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.DecisionBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.CommentBean"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.text.SimpleDateFormat"%>
<html>
<head>
<%
	int type = 0; //1-grouping; 2-hierarchy tree; 3-order
	String name, idOrName = "";
	name = request.getParameter("term");
	if (name == null) {
		name = (String) request.getAttribute("term");
	}

	if (name != null && name != "null" && name != "") {
		type = 1;
		idOrName = name;
	}

	if (type == 0) {
		String tag = request.getParameter("tag");
		if (tag == null) {
			tag = (String) request.getAttribute("tag");
		}
		if (tag != null && tag != "null") {
			type = 2;
			idOrName = tag.split(":")[0];
			name = tag.substring(idOrName.length() + 1);
		}
	}

	if (type == 0) {
		String order = request.getParameter("order");
		if (order == null) {
			order = (String) request.getAttribute("order");
		}
		if (order != null && order != "null") {
			type = 3;
			idOrName = order.split(":")[0];
			name = order.substring(idOrName.length() + 1);
		}
	}
%>
<title>Specific Report for <%=name%></title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<script language="javascript" src="js/group.js"></script>
<script language="javascript">
	function checkComment() {
		var comments = stringTrim(document.getElementById('comments').value);

		if (comments != '') {
			comments = '<comments>' + comments + '</comments><idOrName>'
					+ document.getElementById('idOrName').value + '</idOrName>'
					+ '<name>' + document.getElementById('name').value
					+ '</name>';
			document.getElementById('value').value = comments;
			document.getElementById('generalForm').submit();
		}

	}
</script>
</head>
<body>
	<span> <%
 	SessionDataManager sessionDataManager = (SessionDataManager) (session
 			.getAttribute("sessionDataMgr"));
 	SimpleDateFormat format = new SimpleDateFormat("MMM dd, yy");

 	if (sessionDataManager != null) {
 		if (type > 0 && !idOrName.equals("")) {
 			User user = sessionDataManager.getUser();
 			String dataset = sessionDataManager.getDataset();

 			//for managers
 			//System.out.print(request.getRequestURI());
 			if (request.getParameter("action") != null) {
 				if (request.getParameter("action").equals(
 						"viewDecision")) {
 					dataset = session.getAttribute("selectedDataset")
 							.toString();
 				}
 			}
 			ArrayList<SpecificReportBean> tbeans = new SpecificReport(dataset, idOrName, type)
 					.getTermSpecificReport();
 %> <font class="font-text-style">Specific Report for <u><b><font
					color="green"><%=name%></font></b></u></font> <br></br> <input type="hidden"
		value="<%=idOrName%>" id="idOrName"></input> <input type="hidden"
		value="<%=name%>" id="name"></input>
		<table width="100%" border="1">
			<tr>
				<td>
					<table width="100%">
						<%
							boolean flag = true;
									String tdClass = "";
						%>
						<tr bgcolor="green">
							<th width="16%" align="left"><font class="font-text-style"
								color="white">User</font></th>
							<th width="40%" align="left"><font class="font-text-style"
								color="white">Decision (<%=(type == 1 ? "Category, Synonyms" : "Order")%>,
									Date)
							</font></th>
							<th width="44%" align="left"><font class="font-text-style"
								color="white">Comments</font></th>
						</tr>
						<%
							for (SpecificReportBean tbean : tbeans) {
										tdClass = (flag) ? "d0" : "d1";
										flag = (flag) ? false : true;
						%>
						<tr class="<%=tdClass%>">

							<td><font class="font-text-style"><%=tbean.getUser().getFirstName() + " "
								+ tbean.getUser().getLastName()%></font></td>
							<td>
								<ul style="padding-left: 20px; margin-bottom: 6px">
									<%
										ArrayList<DecisionBean> decisions = tbean
															.getDecisions();
													for (DecisionBean decision : decisions) {
														boolean isEmpty = decision.getDecision().equals("");
									%>
									<li><font class="font-text-style" <%=(isEmpty ? "color='red'" : "") %>><%=(isEmpty ? "uncategorized" : decision.getDecision())%></font>
										<font class="font-text-style" color="green"><%=(((decision.getSyns() != null) && (!decision
									.getSyns().equals(""))) ? " ("
									+ decision.getSyns() + ") " : "")%></font>
										<font class="font-text-style" color="grey" size="5px"><%=format.format(decision.getDecisionDate())%></font></li>
									<%
										}
									%>

								</ul>
							</td>
							<td>
								<table width="100%">
									<%
										ArrayList<CommentBean> comments = tbean
															.getUserComments();
													int count = 1;
													for (CommentBean comment : comments) {
														if (comment.isReviewComment()) {
									%>
									<tr>
										<td colspan="2"><font class="font-text-style" color="grey"><%=comment.getComments()%></font>
											<font class="font-text-style" color="grey" size="5px">
												<%=format.format(comment
										.getCommentDate())%></font></td>
									</tr>
									<%
										} else {
									%>
									<tr>
										<td width="5%" valign="middle"><font class="font-text-style"><%=count++%>.</font></td>
										<td width="95%"><font class="font-text-style"><%=comment.getComments()%></font>
											<font class="font-text-style" color="grey" size="5px">
												<%=format.format(comment
										.getCommentDate())%></font></td>
									</tr>
									<%
										}
													}
									%>
								</table>
							</td>
						</tr>
						<%
							}
						%>
					</table>
					<hr></hr>
					<form action="comment.do" method="post" id="generalForm"
						name="generalForm">
						<input type="hidden" name="value" id="value" /><input
							type="hidden" name="type" value="<%=type%>" />
					</form>
					<table>
						<tr class="d1">
							<td><font class="font-text-style" color="green"><b>Comment
										on <%=name%> :</font></b></td>
						</tr>
						<tr class="d1">
							<td><textarea
									class="uiTextareaNoResize uiTextareaAutogrow textBox textBoxContainer DOMControl_placeholder"
									title="Write a comment..." placeholder="Write a comment..."
									name="comments" id="comments" rows="4" cols="50"></textarea></td>
						</tr>
						<tr class="d1">
							<td align="left"><input type="button" name="button"
								value="Comment" class="uiButton uiButtonSpecial uiButtonMedium"
								onclick="checkComment()"></input></td>
						</tr>
					</table>
				</td>
			</tr>
		</table> <%
 	} else {
 %> <font class="font-text-style">You cannot refresh this pop up
			page. You may <a href="void(0)" onclick="window.close()">close</a>
		
				
			nt></b></u></font> <br></br> <%
 	}

 	} else {
 %> <script type="text/javascript">
	window.close();
</script> <jsp:include page="loginHeader.jsp" /> <font class="font-text-style"><b>Your
				session has timed off. Please <a
				href="<%=request.getContextPath()%>">login</a>
		</b></font> <%
 	}
 %>
	</span>
</body>
<%@ page errorPage="error.jsp"%>
</html>

