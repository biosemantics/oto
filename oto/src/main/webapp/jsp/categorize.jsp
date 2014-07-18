<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="edu.arizona.biosemantics.oto.oto.beans.CategoryHolder"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@page import="edu.arizona.biosemantics.oto.oto.beans.CategoryBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.TermsGroup"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.CharacterGroupBean"%>
<%@ page
	import="edu.arizona.biosemantics.oto.oto.beans.SessionDataManager"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.Term"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.User"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.beans.DatasetBean"%>
<%@ page import="edu.arizona.biosemantics.oto.oto.db.CharacterDBAccess"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTO</title>
<link rel="stylesheet" media="screen" type="text/css"
	href="css/groupStyles.css" />
<link rel="stylesheet" media="screen" type="text/css"
	href="css/general.css" />

<meta http-equiv="Content-type" content="text/html;charset=UTF-8" />
<meta http-equiv="cache-control" content="no-cache"></meta>
<meta http-equiv="expires" content="0"></meta>
<meta http-equiv="pragma" content="no-cache"></meta>
<script language="javascript" src="js/greyout.js"></script>
<script language="javascript" src="js/context.js"></script>
<script language="javascript" src="js/fader.js"></script>
<script language="javascript" src="js/categorize.js"></script>
<script language="javascript" src="js/download.js"></script>
<script language="javascript" src="js/reset.js"></script>
</head>

<body onload="drag_init()">
	<div id="dragging_mask"></div>
	<div id="glossaryTerm"></div>
	<div id="sourceFiles"></div>

	<!-- This is a sample copy of new category box, will be used when creating a new category -->
	<div id="new_category_hidden" style="width: 100%">
		<table
			style="border-collapse: collapse; border: 1px solid #B0F1A0; width: 100%">
			<tr bgcolor="#B0F1A0">
				<th class="new_cat_th" style="color: red;"></th>
			</tr>
			<tr>
				<td width="100%">
					<div class="categoryTerms" style="width: 100%">
						<table class="changedDecision" width="100%">
						</table>
						<div class="newDecisions" style="width: 100%"></div>
					</div>
				</td>
			</tr>
		</table>
	</div>

	<!-- Session Validity check -->

	<%
		SessionDataManager sessionDataManager = (SessionDataManager) (session
				.getAttribute("sessionDataMgr"));

		if (sessionDataManager != null) {
			User user = sessionDataManager.getUser();
			String dataset = sessionDataManager.getDataset();
			CharacterDBAccess cdba = new CharacterDBAccess();
			DatasetBean datasetInfo = cdba.getDatasetInfoForCategorizePage(
					user, dataset);
	%>
	<jsp:include page="header.jsp" />
	<table border="1" width="100%">

		<tr>
			<td><font class="font-text-style" id="dataset_info">Current
					Dataset: <b><%=dataset%></b> ( <%=datasetInfo.getNumTermsInCategorizePage()%>
					terms, <%=datasetInfo.getNumTemrsReviewedInCategorizePage()%>
					reviewed by you)
			</font> <%
 	if (dataset.equals("OTO_Demo")) {
 %> <input type="button" class="uiButton uiButtonMedium"
				style="margin-left: 10px"
				title="Reset OTO_Demo dataset by clearing all decisions. Any user can reset this dataset, therefore your decisions in OTO_Demo dataset may be deleted by other users. "
				value="Reset to initial status" onclick="resetOTODemo(1)"></input> <%
 	}
 %></td>
		</tr>
		<%
			if (datasetInfo.isCategorizationFinalized()) {
		%>
		<tr>
			<td height="480px" align="center"><font class="font-text-style"
				color="green" style="font-size: 15px"> Dataset <b><%=dataset%></b>
					has been reviewed and finalized! <br></br> <a
					href="gotoDownload.do">Click here</a> to go to the download page.
			</font><br></br> <label id="serverMsg">&nbsp;</label></td>
		</tr>
		<%
			} else if (datasetInfo.isHasBeenMerged()) {
		%>
		<tr>
			<td height="480px" align="center"><font class="font-text-style"
				color="green" style="font-size: 15px"> Dataset <b><%=dataset%></b>
					has been merged into <b>'<%=datasetInfo.getMergedInto()%>'
				</b>! <br></br> Go to the <a href="gotoWelcomepage.do">home page</a> and
					select '<%=datasetInfo.getMergedInto()%>' to view the merged
					dataset.
			</font><br></br> <label id="serverMsg">&nbsp;</label></td>
		</tr>
		<%
			} else {
		%>
		<tr class="dragging_part" id="dragging_part">
			<td>
				<!-- below is the biggest dragging part table -->
				<table width="100%">
					<tr bgcolor="green">
						<th align="left" width="15%"><font color="white">Terms:</font></th>
						<th width="85%">
							<table width="100%">
								<tr>
									<td width="37.5%" align="left"><font color="white">Categories:</font></td>
									<td width="25%">
										<div id="serverMessage" class="success"
											onmouseover="document.getElementById('serverMessage').innerHTML='&nbsp;'">
											&nbsp;
											<%
												String message = (String) request.getAttribute("message");
														if (message != null) {
											%><%=message%>
											<%
												}
											%>
										</div>
									</td>
									<td width="25%" align="right">
										<form id="submitForm" name="generalForm" action="saveGroup.do"
											method="post">
											<img src="images/green_rot.gif" id="processingSaveImage"
												style="visibility: hidden" width="15px;" /><input
												type="button" name="button"
												value="Save Decisions/Submit Review History"
												class="uiButton uiButtonSpecial uiButtonMedium"
												style="padding: 0px 1px 1px 1px"
												onclick="save_categories('submit')"
												onmouseover="document.getElementById('serverMessage').innerHTML='&nbsp;'" />
											<input type="hidden" id="hiddenvalue" name="value" />
										</form>
									</td>
									<td align="right" valign="middle"><input type="button"
										name="button" value="Copy System Decisions"
										class="uiButton uiButtonSpecial uiButtonMedium"
										style="padding: 0px 1px 1px 1px"
										onclick="copySystemDecisions('<%=dataset%>')" /></td>
									<td width="7.5%" align="right"><input type="button"
										style="padding: 0px 1px 1px 1px" value="New Category"
										onclick="newCategory()" id="newCategoryBtn"
										class="uiButton uiButtonSpecial uiButtonMedium"></input></td>

								</tr>
							</table>
						</th>
					</tr>
					<tr>
						<td>
							<div id="availableTerms">
								<%
									//get the terms list, term has to be unique in the entire list scope
											ArrayList<TermsGroup> tgList = cdba.getTermsGroupList(user,
													dataset);
											ArrayList<CategoryBean> allCategories = cdba
													.getAllCategory(dataset);

											//get all decided terms and put them into sessionDataManager --by f.huang
											ArrayList<String> processedCategories = cdba
													.getProcessedCategories(user, dataset);
											HashMap<String, CategoryHolder> categories_data = new HashMap<String, CategoryHolder>();
											for (String category : processedCategories) {
												CategoryHolder ch = cdba.getTermsDecidedInCategory(
														category, dataset, user);
												categories_data.put(category, ch);
											}

											if (tgList.size() > 0) {
												for (TermsGroup termsGroup : tgList) {
													ArrayList<Term> terms = termsGroup
															.GetTermsInGroup();
													if (terms.size() > 0) {
								%>
								<div class="dragGroupTable" id="<%=termsGroup.getGroup()%>">
									<table>
										<tr>
											<td>
												<table class="termsTable">
													<%
														for (Term term : terms) {
													%>
													<tr class="term_row" id="<%=term.getTerm()%>">
														<td class="term"><input type="checkbox" /><label
															class="dragme"
															style="cursor: pointer; color: <%=(term.isReviewed() ? "grey"
											: "black")%>"
															onclick="setTerm_categorizing('<%=term.getTerm()%>')"><%=term.getTerm()%></label>
															<label><a href="javascript:void(0)"
																title="View term specific report for <%=term.getTerm()%>"
																onclick="showTermsReport('<%=term.getTerm()%>')"><img
																	border="0px"
																	src="images/<%if (term.hasConflict())
										out.print("down.jpg");
									else
										out.print("view.gif");%>"
																	height="11px"></img></a></label></td>
													</tr>
													<%
														}
													%>
												</table>
											</td>
											<!-- <td><img class="dragme" src="images/drag.jpg"
												width="20px;" height="<%=(21 * terms.size())%>px"></img></td>
												 -->
										</tr>
									</table>
								</div>
								<%
									}
												}
											} else {
								%><!-- <font class="font-text-style">Congratulations! You have
				categorized all the terms in this page. <br></br>
				To view your decisions, go to <a href="userSpecificReport.do">Report</a>
				page, and click '<b>Group Terms</b>' on the left menu. </font>  -->
								<%
									}
								%>
							</div>
						</td>

						<td
							style="border-left-color: green; border-left-style: solid; border-left-width: 1px; vertical-align: top;">
							<!-- below is the table of categories  -->
							<div id="categories_div">
								<table id="categories_table" width="100%">
									<%
										int cellsinrow = 6;
												int count = 1;
												for (CategoryBean category : allCategories) {
													if (count % cellsinrow == 1) {
														out.print("<tr class='category_row'>");
													}
													CategoryHolder ch = categories_data.get(category
															.getName());
													boolean isStructure = category.getName().equals(
															"structure");
									%>
									<td class="categoryTable" id="<%=category.getName()%>"
										style="vertical-align: top" width="16.67%">
										<table width="100%"
											style="border-collapse: collapse; border: 1px solid #B0F1A0;">
											<tr bgcolor="<%=(isStructure ? "#66CC33" : "#B0F1A0")%>">
												<th
													title="<%=category.getDef().replaceAll(">", "&gt;")
								.replaceAll("<", "&lt;").replaceAll("\"", "'")%>"
													style="color: <%=(ch == null || ch.isFinishedReviewing() ? "grey"
								: "black")%>"><%=(isStructure ? "STRUCTURE" : category
								.getName())%></th>
											</tr>
											<tr>
												<td width="100%">
													<!-- decided terms -->
													<div class="categoryTerms" style="width: 100%">
														<table class="savedTerms" width="100%">
															<%
																if (ch != null) {
																				ArrayList<Term> savedTerms = ch.getTerms();
																				if (savedTerms != null) {
																					for (Term eachTerm : savedTerms) {
																						if (!eachTerm.isAdditional()) {
															%>
															<tr class="term_row_saved" id="<%=eachTerm.getTerm()%>"
																valign="middle">
																<td valign="middle" class="term_cell_saved">
																	<!-- <img class="dragAfterSave"
																	src="images/drag.jpg" width="10px;"></img> --> <a
																	class="term_label_saved"
																	style="text-decoration: none; color: <%=(eachTerm.isReviewed() ? "grey"
												: "black")%>"
																	href="javascript:void(0)"
																	onclick="setTerm_categorizing('<%=eachTerm.getTerm()%>')"><%=eachTerm.getTerm()%></a>
																	<a href="javascript:void(0)"
																	style="vertical-align: baseline;"
																	title="View term specific report for <%=eachTerm.getTerm()%>"
																	onclick="showTermsReport('<%=eachTerm.getTerm()%>')">
																		<img border="0px"
																		src="images/<%if (eachTerm.hasConflict())
											out.print("down.jpg");
										else
											out.print("view.gif");%>"
																		height="11px"></img>
																</a> <%
 	if (eachTerm.hasSyn()) {
 									ArrayList<Term> syns = eachTerm
 											.getSyns();
 									for (Term eachSyn : syns) {
 %><label class="syn" id="<%=eachSyn.getTerm()%>"
																	style="vertical-align: middle;"><br>&nbsp;&nbsp;&nbsp;&nbsp;<a class="syn_a"
																			style="text-decoration: none; color: <%=(eachSyn.isReviewed() ? "grey"
														: "black")%>"
																			href="javascript:void(0)"
																			onclick="setTerm_categorizing('<%=eachSyn.getTerm()%>')"><%=eachSyn.getTerm()%></a>
																			<a href="javascript:void(0)"
																			title="View term specific report for <%=eachSyn.getTerm()%>"
																			onclick="showTermsReport('<%=eachSyn.getTerm()%>')"><img
																				src="images/<%if (eachSyn.hasConflict())
													out.print("down.jpg");
												else
													out.print("view.gif");%>"
																				height="11px"></img></a> <label
																			onclick="removeTerm(this)"
																			style="vertical-align: middle;" class="delete_cross"><font
																				color='blue'>x</font></label></label> <%
 	}
 								}
 %>
																</td>
															</tr>
															<%
																}
																					}
																				}
																			}
															%>
														</table>
														<!-- changed decisions will be listed here -->
														<table class="changedDecision" width="100%">
														</table>
														<!-- here suppose to list the terms to be saved -->
														<div class="newDecisions" style="width: 100%"></div>
													</div>
												</td>
											</tr>
										</table>
									</td>
									<%
										if (count % cellsinrow == 0)
														out.print("</tr>");
													count++;
												}
												//if (count % cellsinrow == 1)
												//out.print("<tr class='category_row'>");
									%>

									<!-- <input type="button" value="New" onclick="mouse_down_handler"></input></th> -->
									<%
										//if (count % cellsinrow == 0)
												//	out.print("</tr>");
									%>

								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
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
							<th width="10%" class="currentContext" id="termLocations"
								onclick="showTermLocations()"><font>Locations</font></th>
							<th width="10%" class="backContext" id="context"
								onclick="showContext()"><font>Context</font></th>
							<th width="10%" class="backContext" id="glossary"
								onclick="showGlossary()"><font>Glossaries</font></th>
							<th width="70%" align="left" bgcolor="white"
								style="text-align: right"><font color="green">&nbsp;</font><input
								type="button" name="button" value="Search"
								class="uiButton uiButtonSpecial uiButtonMedium"
								style="padding: 0px 1px 1px 1px" onclick="openTermLocator()" /><img
								src="images/locator.png" onclick="openTermLocator()"
								height="14px"></img></th>
						</tr>
						<tr>
							<td width="85%" colspan="4" style="border: 2px solid green">

								<table width="100%">
									<tr>
										<th width="15%" align="left" id="th_context_1"></th>
										<th width="85%" align="left" id="th_context_2"></th>
									</tr>
								</table>
								<div style="width: 100%; height: 80px; overflow: auto;"
									class="border" id="contextSentences">
									<table width="100%" id="contextTable">
										<tr>
											<td width="2%">&nbsp;</td>
											<td width="98%"><font class="font-text-style">Click
													on term's name to view locations of all copies, context and
													glossary.</font></td>
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