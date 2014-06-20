<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<%@page
	import="edu.arizona.biosemantics.oto.oto.beans.GlossaryNameMapper"%>

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
			session.setAttribute("manageType", "6");
			int glossType = 0;
			if (request.getParameter("gloss") != null) {
				glossType = Integer.parseInt(request.getParameter("gloss")
						.toString());
			}
	%>
	<jsp:include page="header.jsp" />

	<table width="100%" style="border-top: 1px solid green">
		<tr>
			<td width="15%" valign="top"><jsp:include page="leftMenu.jsp" />
			</td>
			<td width="85%" valign="top">
				<div style="width: 100%; padding: 20px;">
					<b>Select an area to work on:</b>
					<%
						for (int i = 1; i < 6; i++) {
					%>
					<a style="padding-left: 30px"
						href="mergeIntoSystem.do?gloss=<%=i%>"
						class="<%=glossType == i ? "current_a" : ""%>"> <%=GlossaryNameMapper.getInstance().getGlossaryName(
							i)%></a>
					<%
						}
					%>
				</div> <%
 	if (glossType > 0) {
 			CharacterDBAccess cdba = new CharacterDBAccess();

 			GlossaryGroupBean gloss = cdba.getMergeableDatasets(user,
 					glossType, true);
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
							</ul> <%
 	if (user.getRole().equals("S")) {
 %>
							<div style="padding-left: 50px">
								<input type="button" name="button"
									value="Merge Into System Dataset" id="BTN_mergeIntoSystem"
									class="uiButton uiButtonSpecial uiButtonMedium"
									onclick="mergeIntoSystemDataset('<%=gloss.getGlossaryID()%>', '<%=gloss.getGlossaryName()%>')" />&nbsp;<label
									id="serverMessage"></label>
							</div> <%
 	}
 %>
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
				<div style="height: 500px"></div> <!-- 
				<table width="100%">
					<tr>
						<th align="left" width="100%">Merge datasets:</th>
					</tr>
					<tr>
						<td width="100%">
							<ul>
								<li><font class="font-text-style">Merge dataset will
										only combine the terms and decisions of 'Group Terms' page
										into a new dataset.</font></li>
								<li><font class="font-text-style">The source
										datasets might be deleted after merge, depending on if the
										'Group Terms' page of this dataset is finalized or not.
										Finalized datasets are marked in <font color="green">green</font>.
								</font>
									<ul>
										<li><font class="font-text-style"><b>Rule 1</b>:
												If a source dataset only uses functions in 'Group Terms'
												page, whether to delete this dataset or not depends on Rule
												2 and 3. If the dataset also uses 'Structure Hierarchy' or
												'Term Order' functions, it will <b>NOT</b> be deleted after
												merge. </font></li>
										<li><font class="font-text-style"><b>Rule 2</b>:
												If all source datasets are finalized or none of them is
												finalized, <b>ALL</b> the source datasets will be deleted
												after merge.</font></li>
										<li><font class="font-text-style"><b>Rule 3</b>:
												If only part of the source datasets are finalized, only the
												unfinalized datasets will be deleted after merge. </font></li>
										<li><font class="font-text-style"><b>Rule 4</b>:
												When merge to a new dataset, only when all source datasets
												are finalized, the merged one will be finalized. <br />
												When merge into an existing dataset, only when all source
												datasets and the target dataset are finalized, the system
												will re-finalize the target dataset. Otherwise, the target
												dataset remains in its old status. </font></li>
									</ul></li>
								<li><font class="font-text-style">To merge datasets,
										please select datasets you want to merge below, then type in
										the new dataset name before click button 'Merge Datasets'.</font></li>
							</ul>
						</td>
					</tr>
					<tr>
						<td height="400px">&nbsp;</td>
					</tr>
				</table> 
				 --> <%
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
