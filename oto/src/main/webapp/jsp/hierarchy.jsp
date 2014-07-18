<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TagBean"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.StructureNodeBean"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.CharacterGroupBean"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/hierarchyStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/dtree.css" />
<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/greyout.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/hierarchy.js"></script>
<script language="javascript" src="js/context.js"></script>
<script language="javascript" src="js/download.js"></script>
<script language="javascript" src="js/reset.js"></script>
</head>

<body onload="hierarchy_init()">
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
			ArrayList<TagBean> tagsList = cdba.getTagsList(dataset, user);
			int unDecidedTags = 0;
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
				value="Reset to initial status" onclick="resetOTODemo(2)"></input> <%
 	}
 %></td>
		</tr>
		<%
			if (!cdba.isConfirmed(dataset, 2)) {
		%>
		<tr class="dragging_part">
			<td>
				<!-- below is the biggest dragging part table -->
				<table width="100%">
					<tr bgcolor="green">
						<th width="15%" align="left"><font color="white">Structures
								:</font></th>
						<th width="85%">
							<table width="100%">
								<tr>
									<td width="60%" align="left"><font color="white">Hierarchy
											:</font></td>
									<td width="25" align="right">
										<div id="serverMessage" class="success">
											&nbsp;<%
												String message = (String) request.getAttribute("message");
														if (message != null) {
											%><%=message%>
											<%
												}
											%>
										</div>
									</td>
									<td align="right" width="15%">
										<form id="submitForm" name="generalForm" action="saveTree.do"
											method="post">
											<input type="button" name="button"
												style="padding: 0px 1px 0px 1px" value="Save Tree"
												class="uiButton uiButtonSpecial uiButtonMedium"
												onclick="save_tree('submit')"
												onmouseover="document.getElementById('serverMessage').innerHTML='&nbsp;'" />
											<input type="hidden" id="hiddenvalue" name="value" />&nbsp;&nbsp;&nbsp;<img
												src="images/green_rot.gif" id="processingSaveImage"
												style="visibility: hidden" width="15px;" />
										</form>
									</td>
								</tr>
							</table>
						</th>
					</tr>
					<tr>
						<td style="vertical-align: top;">
							<%
								if (tagsList.size() > 0) {
							%>
							<div id="availableTags">
								<table class="tagsTable">
									<%
										for (TagBean tag : tagsList) {
														if (tag != null) {
															flag = true;
															tdClass = (flag) ? "d0" : "d1";
															flag = (flag) ? false : true;
									%>
									<tr style="display: block;">
										<td class="tag">
											<div class="structure" id=<%=tag.getID()%>>
												<!-- <img style="vertical-align: middle;" class="dragme"
													src="images/drag.jpg" width="12px;"></img> -->
												<label class="structure_label"
													style="cursor: pointer; <%if (tag.isDecided())
									out.print("color:grey");
								else
									unDecidedTags++;%> "
													onclick="setTerm('<%=tag.getName()%>')"><%=tag.getName()%></label>
												<img
													title="View specific hierarchy report for <%=tag.getName()%>"
													onclick="showReport('<%=tag.getID() + ":" + tag.getName()%>')"
													src="images/<%if (tag.hasConflict()) {
									out.print("down.jpg");
								} else {
									out.print("view.gif");
								}%>"
													width="12px"
													style="vertical-align: middle; cursor: pointer;"></img>
											</div>
										</td>
									</tr>
									<%
										}
													}
									%>
								</table>
							</div> <%
 	} else {
 %> <!-- 
					<font class="font-text-style">Congratulations! You have organized all the
				terms in this page. <br></br>To view your decisions, go to <a
					href="userSpecificReport.do">Report</a> page, and click '<b>Structure
				Hierarchy</b>' on the left menu. </font> --> <%
 	}
 %>
						</td>

						<td>
							<!-- below is the table of hierarchy tree  -->
							<div id="hierarchyTree"
								style="height: 420px; overflow: auto; padding-left: 8px">
								<div class="dtree">
									<div class="clip"><%=cdba.getHierarchyNode(dataset, user, false)%></div>
								</div>
							</div>
						</td>
					</tr>
				</table>
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
		if (unDecidedTags == 0) {
	%>
	<!-- 	<script language="javascript">
		alert("Congratulations! \n\nYou have finished organizing all the terms in this page.");
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