<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermForBioportalBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.BioportalSubmissionBean"%>
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

<%
	//get the term that is going to display
	String term = "";
	if (request.getParameter("term") != null) {
		term = request.getParameter("term").toString();
	} else if (request.getAttribute("term") != null) {
		term = request.getAttribute("term").toString();
	}

	//get the scroll value to restore the location of left menu
	String scroll = "";
	if (request.getParameter("scroll") != null) {
		scroll = request.getParameter("scroll").toString();
	}
%>

<body onload="updateScrollBar_left(<%=scroll%>)">
	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			String dataset = sessionDataManager.getDataset();
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
						<th bgcolor="green" align="left" style="color: white" width="85%"><%=term.equals("") ? "Term Details" : "Detail of Term '"
						+ term + "'"%></th>
					</tr>
					<tr>
						<!-- the term list -->
						<td valign="top" width="15%"
							style="border-right: 1px solid green; height: 500px"><jsp:include
								page="bioportal_leftMenu.jsp" flush="true" /></td>

						<!-- the right block: send form, update form, list submissions, or related terms -->
						<%
							if (!term.equals("")) {
									User user = sessionDataManager.getUser();

									CharacterDBAccess cdba = new CharacterDBAccess();
									TermForBioportalBean termBio = cdba.getTermForBioportal(
											dataset, term);
									DatasetBean datasetInfo = cdba.getDataset(dataset);
						%>
						<td valign="top" width="85%">
							<table width="100%">
								<tr>
									<!-- the related info -->
									<td width="45%" valign="top">
										<table style="border-collapse: collapse;" width="100%">
											<!-- existing submissions -->
											<%
												ArrayList<BioportalSubmissionBean> submissions = termBio
																.getExistingSubmissions();
														if (submissions != null && submissions.size() > 0) {
											%>
											<tr>
												<th style="color: green; border-bottom: 1px solid green"
													align="left" width="100%">Existing Submissions:</th>
											</tr>

											<tr>
												<td style="padding: 10px 0px 0px 10px">
													<table width="100%">
														<tr bgcolor="green">
															<th align="left" width="5%"><font
																class="font-text-style" color="white">#</font></th>
															<th align="left" width="15%"><font
																class="font-text-style" color="white">Status</font></th>
															<th align="left" width="15%"><font
																class="font-text-style" color="white">Term</font></th>
															<th align="left" width="15%"><font
																class="font-text-style" color="white">Category</font></th>
															<th align="left" width="25%"><font
																class="font-text-style" color="white">Dataset</font></th>
															<th align="left" width="30%"><font
																class="font-text-style" color="white">Submitted
																	By</font></th>
														</tr>

														<%
															int count = 1;
																		boolean flag = true;
																		String tdClass = "";

																		for (BioportalSubmissionBean submission : submissions) {
																			tdClass = (flag) ? "d0" : "d1";
																			flag = (flag) ? false : true;
														%>
														<tr class="<%=tdClass%>"
															id="SUBMISSION_TR_<%=submission.getLocalID()%>"
															onclick="submissionDetail('<%=submission.getTermName()%>',
														 '<%=submission.getDefinition()%>', '<%=submission.getSynonyms()%>', '<%=submission.getOntologyIDs()%>',
														  '<%=submission.getSuperClass()%>', '<%=submission.getSource()%>', '<%=submission.getTemCategory()%>', 
														  '<%=submission.getLocalID()%>', '<%=submission.getTmpID()%>', '<%=submission.isAdopted()%>')"
															style="cursor: pointer;">
															<td><font class="font-text-style"><%=count++%></font></td>
															<td><font class="font-text-style"><%=submission.getStatus()%></font></td>
															<td><font class="font-text-style"><%=submission.getTermName()%></font></td>
															<td><font class="font-text-style"><%=submission.getTemCategory()%></font></td>
															<td><font class="font-text-style"><%=submission.getDataset()%></font></td>
															<td><font class="font-text-style"><%=submission.getUsername()%></font></td>
														</tr>
														<%
															}
														%>

													</table>
												</td>
											</tr>
											<tr>
												<th width="100%">&nbsp;</th>
											</tr>
											<%
												}
											%>

											<!-- available categories for submission -->
											<%
												ArrayList<String> categories = termBio
																.getAvailableCategories();
														if (categories != null && categories.size() > 0) {
											%>
											<tr>
												<th style="color: green; border-bottom: 1px solid green"
													align="left">Available Categories for New Submission</th>
											</tr>
											<tr>
												<td><ul>
														<%
															for (String category : categories) {
														%>
														<li style="cursor: pointer; padding: 0px;"
															id="CATEGORY_LI_<%=category%>"
															onclick="setCategory('<%=category%>', '<%=termBio.getSource()%>')"><font
															class="font-text-style"><%=category%></font></li>
														<%
															}
														%>
													</ul></td>
											</tr>
											<%
												}
											%>
										</table>
									</td>
									<td width="55%" valign="top">
										<table width="100%">
											<tr>
												<td align="right" width="100%"><jsp:include
														page="bioportal_links.jsp" flush="true" /></td>
											</tr>
											<tr>
												<td width="90%" valign="top" id="submissonTD"
													style="border-collapse: collapse; visibility: hidden; padding: 40px 40px 40px 40px">
													<form id="submissonForm" method="post"
														action="submitToBioportal.do">
														<input type="hidden" name="dataset" value="<%=dataset%>" />
														<input type="hidden" name="glossaryType"
															value="<%=datasetInfo.getGlossaryID()%>" /> <input
															type="hidden" name="action" id="submission_action"
															value="submit" /> <input type="hidden" name="localID"
															id="localID" value="" /> <input type="hidden"
															name="tmpID" id="tmpID" value="" /> <input type="hidden"
															name="from" value="term" />
														<table width="100%"
															style="padding: 8px; border: 1px solid green;">
															<tr>
																<th colspan="2" align="left"
																	style="border-bottom: 1px solid green"><font
																	color="white" style="background: green" size="2">&nbsp;Bioportal
																		Submission: &nbsp;</font></th>
															</tr>
															<tr>
																<td></td>
															</tr>
															<tr>
																<td align="right" width="50%"><font
																	class="font-text-style" color="red">Term *: </font></td>

																<td align="left"><input type="text" name="termName"
																	readonly="readonly" id="termName" value="<%=term%>" /></td>
															</tr>

															<tr>
																<td align="right" width="50%"><font
																	class="font-text-style" color="red">Definition
																		*: </font></td>

																<td align="left"><input type="text"
																	name="definition" id="definition" value="" /></td>
															</tr>

															<tr>
																<td align="right" width="50%"><font
																	class="font-text-style">Synonyms (Comma
																		separated list): </font></td>

																<td align="left"><input type="text" name="syns"
																	id="syns" value="<%=termBio.getSyns()%>" /></td>
															</tr>

															<tr>
																<td align="right" width="50%"><font color="red"
																	class="font-text-style">Ontology *: </font></td>

																<td align="left"><select name="ontology"
																	id="ontology" onchange="setOntology()">
																		<option value="" selected="selected">Select</option>
																		<option value="PATO">PATO</option>
																		<option value="PO">PO</option>
																		<option value="HAO">HAO</option>
																		<option value="PORO">PORO</option>
																</select></td>
															</tr>

															<tr>
																<td align="right" width="25%"><font
																	class="font-text-style" color="red">Super Class
																		ID *: </font></td>

																<td align="left"><input type="text"
																	name="superClassID" id="superClassID" value="" /><img
																	title="Browse Super Class ID from Ontology Lookup Service"
																	style="vertical-align: text-top; border: 1px solid gray"
																	src="images/locator.png" width="17px"
																	onclick="openOLS()"></img></td>
															</tr>

															<tr>
																<td align="right" width="25%"><font
																	class="font-text-style">Source: </font></td>

																<td align="left"><input type="text" name="source"
																	id="source" value="<%=termBio.getSource()%>" /></td>
															</tr>

															<tr>
																<td align="right" width="25%"><font
																	class="font-text-style" color="red">Category *:
																</font></td>

																<td align="left"><input type="text" name="category"
																	readonly="readonly" id="category" value="" /></td>
															</tr>

															<tr>
																<td>&nbsp;</td>
																<td align="left"><input type="button" name="button"
																	id="btn_submit" value="Submit"
																	class="uiButton uiButtonSpecial uiButtonMedium"
																	onclick="validateSubmission()" />&nbsp;<input
																	type="button" name="button" id="btn_delete"
																	value="Delete"
																	class="uiButton uiButtonSpecial uiButtonMedium"
																	onclick="deleteSubmission()" /></td>
															</tr>

														</table>
													</form>

												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>

						<%
							} else {
						%>
						<td valign="top"><font class="font-text-style"> Select a term from
								left!</font></td>
						<%
							}
						%>
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