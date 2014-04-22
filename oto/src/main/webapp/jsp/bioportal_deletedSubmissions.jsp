<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermForBioportalBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionsHolderBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/jquery-1.8.3.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/bioportal.js"></script>
</head>
<body>
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			String dataset = sessionDataManager.getDataset();
			String orderby = "";
			if (request.getParameter("orderby") != null) {
				orderby = request.getParameter("orderby").toString();
			}

			CharacterDBAccess cdba = new CharacterDBAccess();
			User user = sessionDataManager.getUser();

			//get all deleted submissions
			ArrayList<BioportalSubmissionBean> submissions = cdba.getDeletedSubmissions(dataset);
	%>
	<jsp:include page="header.jsp" />
	<table border="1" width="100%">
		<tr>
			<td><font class="font-text-style" id="dataset_info">Current
					Dataset: <b><%=dataset%></b>
			</font></td>
		</tr>

		<tr>
			<td width="100%">
				<!-- the main body table -->
				<table width="100%">
					<tr>
						<th bgcolor="green" align="left" style="color: white" width="15%"
							height="25px">Terms:</th>
						<th bgcolor="green" align="left" style="color: white" width="85%">Submissions
						</th>
					</tr>
					<tr>
						<!-- the term list -->
						<td valign="top" width="15%"
							style="border-right: 1px solid green; height: 500px"><jsp:include
								page="bioportal_leftMenu.jsp" flush="true" /></td>

						<!-- the right block: send form, update form, list submissions, or related terms -->
						<td valign="top" width="85%">
							<table width="100%">
								<tr>
									<td width="100%" align="right"><jsp:include
											page="bioportal_links.jsp" flush="true" /></td>
								</tr>
								<tr>
									<td width="100%">
										<table width="100%">
											<tr>
												<td width="100%">
													<!-- the main body table -->
													<table width="100%">
														<tr>
															<!-- the right block: send form, update form, list submissions, or related terms -->
															<td valign="top">
																<table width="100%">
																	<tr>
																		<!-- the related info -->
																		<td width="100%" valign="top">
																			<table style="border-collapse: collapse;"
																				width="100%">
																				<!-- existing submissions -->
																				<%
																					if (submissions != null && submissions.size() > 0) {
																				%>
																				<tr>
																					<th
																						style="color: green; border-bottom: 1px solid green"
																						align="left" width="100%">Deleted
																						Submissions: 
																					</th>
																				</tr>
																				<tr>
																					<td>
																						<div style="width: 100%; height: 450px; overflow: scroll;" id="submission_DIV">
																						<table width="100%"
																							style="padding: 10px 0px 0px 10px">
																							<tr bgcolor="green" title="Order By">
																								<th align="left" width="1%"><font
																									class="font-text-style" color="white">#</font></th>
																								<th align="left" width="5%"><font
																									class="font-text-style" color="white">Term</font></th>
																								<th align="left" width="5%"><font
																									class="font-text-style" color="white">Category</font></th>
																								<th align="left" width="5%"><font
																									class="font-text-style" color="white">Ontology</font></th>
																								<th align="left" width="9%"><font
																									class="font-text-style" color="white">Super Class</font></th>
																								<th align="left" width="20%"><font
																									class="font-text-style" color="white">Definition</font></th>
																								<th align="left" width="10%"><font
																									class="font-text-style" color="white">Synonyms</font></th>
																								<th align="left" width="10%"><font
																									class="font-text-style" color="white">Source</font></th>
																								<th align="left" width="10%"><font
																									class="font-text-style" color="white">Submitted By</font></th>
																								<th align="left" width="10%"><font
																									class="font-text-style" color="white">Deleted By</font></th>
																								<th align="left" width="15%"><font
																									class="font-text-style" color="white">Delete Time</font></th>
																							</tr>
																							<%
																								int count = 1;
																										boolean flag = true;
																										String tdClass = "";

																										for (BioportalSubmissionBean submission : submissions) {
																											tdClass = (flag) ? "d0" : "d1";
																											flag = (flag) ? false : true;
																							%>
																							<tr class="<%=tdClass%>" id="SUBMISSION_TR_<%=submission.getLocalID()%>">
																								<td><font class="font-text-style"><%=count++%></font></td>
																								<td><font class="font-text-style"><%=submission.getTermName()%></font></td>
																								<td><font class="font-text-style"><%=submission.getTemCategory()%></font></td>
																								<td><font class="font-text-style"><%=submission.getOntologyIDs()%></font></td>
																								<td><font class="font-text-style"><%=submission.getSuperClass()%></font></td>
																								<td><font class="font-text-style"><%=submission.getDefinition()%></font></td>
																								<td><font class="font-text-style"><%=submission.getSynonyms()%></font></td>
																								<td><font class="font-text-style"><%=submission.getSource()%></font></td>
																								<td><font class="font-text-style"><%=submission.getUsername()%></font></td>
																								<td><font class="font-text-style"><%=submission.getDeletedBy()%></font></td>
																								<td><font class="font-text-style"><%=submission.getDeleteTime()%></font></td>
																								
																							</tr>
																							<%
																								}
																							%>

																						</table>
																						</div>
																					</td>
																				</tr>
																				<tr>
																					<th width="100%"></th>
																				</tr>
																				<%
																					} else {
																				%>
																				<!-- no submissions yet -->
																				<tr>
																					<td><font class="font-text-style">No
																							deleted submissions.</font></td>
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
												</td>
											</tr>
										</table>

									</td>
								</tr>
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