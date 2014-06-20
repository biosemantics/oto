<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.TermDecision"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryGroupBean"%>

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
</head>
<body>
	<!-- Session Validity check -->
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			User user = sessionDataManager.getUser();
			session.setAttribute("manageType", "5");
			int glossType = 0;
			if (request.getParameter("gloss") != null) {
				glossType = Integer.parseInt(request.getParameter("gloss")
						.toString());
			}
	%>
	<jsp:include page="header.jsp" />

	<table width="100%" style="border-top: 1px solid green; height: 500px">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td width="85%" valign="top">
				<div style="width: 100%; padding: 20px;">
					<b>Select an area to work on:</b>
					<%
						for (int i = 1; i < 6; i++) {
					%>
					<a style="padding-left: 30px" href="mergeDatasets.do?gloss=<%=i%>"
						class="<%=glossType == i ? "current_a" : ""%>"> <%=GlossaryNameMapper.getInstance().getGlossaryName(
							i)%></a>
					<%
						}
					%>
				</div> <%
 	if (glossType > 0) {
 			CharacterDBAccess cdba = new CharacterDBAccess();

 			GlossaryGroupBean gloss = cdba.getMergeableDatasets(user,
 					glossType, false);
 			if (gloss != null && gloss.getDatasets().size() > 0) {
 %>

				<table width="100%" style="padding-left: 20px;">
					<!-- the datasets part -->

					<tr>
						<th align="left" width="100%" style="border-top: solid 1px green">Merge
							datasets in <%=gloss.getGlossaryName()%>:
						</th>
					</tr>
					<%
						ArrayList<DatasetBean> datasetsToMerge = gloss
											.getDatasets();
					%>
					<tr>
						<td>
							<ul>
								<%
									for (DatasetBean ds : datasetsToMerge) {
													String note = ds.getNote();
													String noteTitle = "";
													if (note == null || note.equals("")) {
														note = "";
													} else {
														note = " (Merged from: " + note;

														if (note.length() > 50) {
															noteTitle = note + " )";
															note = note.substring(0, 50) + " ... ";
														}
														note = note + ")";

													}
								%>
								<li style="list-style: none;"><input type="checkbox"
									name="datasetsToMerge_<%=gloss.getGlossaryID()%>"
									value="<%=ds.getName()%>" id="DATASET_<%=ds.getName()%>"><font
										class="font-text-style"><%=ds.getName()%></font><font
										class="font-text-style" color="green"><%=ds.isCategorizationFinalized() ? " (Reviewed)"
									: ""%></font><font color="gray" style="font-weight: normal;"
										class="font-text-style" title="<%=noteTitle%>"><%=note%></font></input></li>
								<%
									}
								%>
							</ul>
							<div>
								<label style="color: black; cursor: default;">Input the
									merged dataset's name: </label><input type="text"
									id="new_dataset_name_<%=gloss.getGlossaryID()%>"></input> <input
									type="button" name="button" value="Merge Datasets"
									id="mergedatasets"
									class="uiButton uiButtonSpecial uiButtonMedium"
									onclick="mergeDatasets('<%=gloss.getGlossaryID()%>', '<%=gloss.getGlossaryName()%>')" />&nbsp;<label
									id="serverMessage"></label>
							</div>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
				</table> <%
 	} else {
 %>
				<table>
					<tr>
						<td height="480px" valign="top">
							<h2>
								<font class="font-text-style" style="padding-left: 20px">No
									dataset in this area for merge now. </font>
							</h2>
						</td>
					</tr>
				</table> <%
 	}
 %> <%
 	} else {
 %>
				<div style="height: 500px"></div> <%
 	}
 %> <!-- Session Validity check header End -->
			</td>
		</tr>
	</table>

	<%
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
